package com.hermes.Interceptor;

import com.hermes.annotation.NeedTokenApi;
import com.hermes.common.constant.JwtTokenClaimConstant;
import com.hermes.common.exception.BaseException;
import com.hermes.common.helper.BaseContextHelper;
import com.hermes.common.helper.JwtTokenHelper;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class JwtTokenInterceptor implements HandlerInterceptor {
    @Value("${hermes.service.jwt-token.token-name}")
    private String tokenName;

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        log.info("Intercepted request {}", request.getRequestURI());

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        if (!handlerMethod.hasMethodAnnotation(NeedTokenApi.class)) {
            return true;
        }

        String token = request.getHeader(tokenName);
        try {
            Claims claims = JwtTokenHelper.parseToken(token);
            String uid = String.valueOf(claims.get(JwtTokenClaimConstant.USER_ID));
            BaseContextHelper.setCurrentUid(uid);
        } catch (Exception e) {
            log.error("Parsing jwt token error: {}", e.getMessage());
            throw new BaseException("parsing jwt token error" + e.getMessage());
        }
        return true;
    }

    @Override
    public void afterCompletion(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) throws Exception {
        BaseContextHelper.removeCurrentUid();
    }
}
