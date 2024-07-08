package com.hermes.controller;

import com.hermes.common.Result;
import com.hermes.common.enum_type.TemplateType;
import com.hermes.common.enum_type.VerifyTarget;
import com.hermes.common.exception.ParamValidFailedException;
import com.hermes.common.helper.StringTemplateHelper;
import com.hermes.dto.MailTemplateDetailDTO;
import com.hermes.service.MailSendService;
import com.hermes.service.StringCacheService;
import com.hermes.vo.GetVerifyCodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/common")
public class CommonController {
    @Value("${hermes.common.verify-code.duration}")
    private Long verifyCodeDuration;
    @Autowired
    private StringCacheService stringCacheService;
    @Autowired
    private MailSendService mailSendService;
    @GetMapping("/verify_code")
    public Result getVerifyCode(
            @RequestParam(name = "identifier") String identifier,
            @RequestParam(name = "verify_target") String verifyTarget
    ) {
        GetVerifyCodeVO getVerifyCodeVO = new GetVerifyCodeVO();
        getVerifyCodeVO.setIdentifier(identifier);
        getVerifyCodeVO.setVerifyTarget(verifyTarget);
        return new HandlerTemplate<GetVerifyCodeVO, Result>() {
            @Override
            protected void validParam(GetVerifyCodeVO request) {
                String identifier = request.getIdentifier();
                String verifyTarget = request.getVerifyTarget();
                if (identifier == null || identifier.isEmpty() || verifyTarget == null || verifyTarget.isEmpty()) {
                    throw new ParamValidFailedException("param can not be null");
                }
                try {
                    verifyTarget = verifyTarget.toUpperCase();
                    VerifyTarget.valueOf(verifyTarget);
                } catch (IllegalArgumentException e) {
                    throw new ParamValidFailedException("invalid verify target: " + verifyTarget);
                }
            }
            @Override
            protected Result doProcess(GetVerifyCodeVO request) {
                String verifyCode = String.format("%06d", new Random().nextInt(1000000));
                String key = request.getVerifyTarget() + ":" + request.getIdentifier();
                stringCacheService.set(key, verifyCode, Duration.ofMillis(verifyCodeDuration));
                LocalDateTime expiredAt = LocalDateTime.now().plus(Duration.ofMillis(verifyCodeDuration));

                MailTemplateDetailDTO mailTemplateDetailDTO = mailSendService.getMailTemplateDetailByTemplateType(TemplateType.VERIFY_CODE);
                /* BAD CODE: hard code template value */
                Map<String, Object> vars = new HashMap<>();
                vars.put("verify_code", verifyCode);
                vars.put("verify_code_expired_at", expiredAt);
                String text = StringTemplateHelper.parse(mailTemplateDetailDTO.getContent(), vars);

                try {
                    mailSendService.sendTextMail(
                            Collections.singletonList(request.getIdentifier()),
                            mailTemplateDetailDTO.getSubject(), text
                    );
                } catch (MessagingException e) {
                    return new Result(Result.CODE_EMAIL_SEND_FAILED, e.getMessage());
                }
                return Result.Success();
            }
        }.process(getVerifyCodeVO);
    }
}
