package com.hermes.service.Impl;

import com.hermes.common.enum_type.MailFilterAction;
import com.hermes.common.enum_type.TemplateType;
import com.hermes.entity.MailSendConfig;
import com.hermes.entity.MailSendFilter;
import com.hermes.entity.UserInfo;
import com.hermes.mapper.MailSendConfigMapper;
import com.hermes.mapper.MailSendFilterMapper;
import com.hermes.mapper.UserMapper;
import com.hermes.service.MailSendService;
import com.hermes.vo.MailTemplateDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MailSendServiceImpl implements MailSendService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private MailSendConfigMapper mailSendConfigMapper;
    @Autowired
    private MailSendFilterMapper mailSendFilterMapper;
    @Autowired
    private UserMapper userMapper;

    @Value("${spring.mail.username}")
    private String from;
    @Override
    public void sendTextMail(List<String> emails, String subject, String text) {
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailSender.createMimeMessage(), false);
            messageHelper.setFrom(from);
            messageHelper.setTo(emails.toArray(new String[0]));
            String tos = String.join(",", emails);
            messageHelper.setSubject(subject);
            messageHelper.setText(text);
            messageHelper.setSentDate(new Date());
            mailSender.send(messageHelper.getMimeMessage());
            log.info("subject: <{}> send from {} to {} success", subject, from, tos);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> filterEmailsByTemplateType(String templateType, List<String> emails) {
        List<UserInfo> usersBeforeFilter = userMapper.selectUsersByEmailAccounts(emails);
        List<MailSendFilter> mailSendFilters = mailSendFilterMapper.selectMailSendFilterByTemplateType(templateType);

        List<MailSendFilter> acceptFilters = mailSendFilters.stream()
                .filter(filter -> filter.getAction().equals(MailFilterAction.ACCEPT.getCode()))
                .collect(Collectors.toList());
        List<MailSendFilter> rejectFilters = mailSendFilters.stream()
                .filter(filter -> filter.getAction().equals(MailFilterAction.REJECT.getCode()))
                .collect(Collectors.toList());

        /*
          filter logic:
          - accept:
                if user hits any acceptFilter, it can be added to the list.
                Otherwise, it cannot be added to the list unless acceptFilters is empty!
          - reject:
                if user hits any rejectFilter, it should be removed from the list
         */
        List<UserInfo> usersAfterFilter = new ArrayList<>();
        if (!acceptFilters.isEmpty()) {
            for (UserInfo userInfo: usersBeforeFilter) {
                for (MailSendFilter acceptFilter: acceptFilters) {
                    if (filterMatches(acceptFilter, userInfo)) {
                        usersAfterFilter.add(userInfo);
                        log.info("mail send accept-filter <{}> accept user <{}>", acceptFilter, userInfo);
                    }
                    log.info("mail send accept-filter <{}> ignore user <{}>", acceptFilter, userInfo);
                }
            }
        } else {
            usersAfterFilter.addAll(usersBeforeFilter);
        }

        for (int i = usersAfterFilter.size()-1; i >= 0 ; i--) {
            UserInfo userInfo = usersAfterFilter.get(i);
            for (MailSendFilter rejectFilter: rejectFilters) {
                if (filterMatches(rejectFilter, userInfo)) {
                    usersAfterFilter.remove(i);
                    log.info("mail send reject-filter <{}> reject user <{}>", rejectFilter, userInfo);
                }
            }
        }
        return usersAfterFilter.stream()
                .map(UserInfo::getEmailAccount)
                .collect(Collectors.toList());
    }

    private Boolean filterMatches(MailSendFilter mailSendFilter, UserInfo user) {
        if (user.getUid().equals(mailSendFilter.getUid())) {
            return true;
        }
        if (user.getEmailAccount().equals(mailSendFilter.getEmailAccount())) {
            return true;
        }
        if (user.getUserRole().equals(mailSendFilter.getUserRole())) {
            return true;
        }
        return false;
    }
    @Override
    public MailTemplateDetailVO getMailTemplateDetailByTemplateType(TemplateType templateType) {
        MailSendConfig mailSendConfig = mailSendConfigMapper.selectMailSendConfigByTemplateType(templateType.getType());
        return MailTemplateDetailVO.builder().
                templateType(mailSendConfig.getTemplateType()).
                subject(mailSendConfig.getTemplateSubject()).
                content(mailSendConfig.getTemplateContent()).build();
    }
}
