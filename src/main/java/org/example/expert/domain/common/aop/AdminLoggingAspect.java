package org.example.expert.domain.common.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AdminLoggingAspect {

    private final ObjectMapper objectMapper;

    @Around("execution(* org.example.expert.domain.comment.controller.CommentAdminController.deleteComment(..)) || " +
            "execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
    public Object logAdminApiAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

       Long userId = (Long) request.getAttribute("userId");

       //요청 로그
        String requestBody = getArgsAsJson(joinPoint.getArgs());

        log.info("[API request] userId: {}, url: {}, time: {}, request: {}",
                userId, request.getRequestURI(), LocalDateTime.now(), requestBody);

        // API 메서드 실행
        Object result = joinPoint.proceed();

        //응답 로그
        String responseBody = objectMapper.writeValueAsString(result);

        log.info("[API response] userId: {}, url: {}, time: {}, response: {}",
                userId, request.getRequestURI(), LocalDateTime.now(), responseBody);

        return result;
    }

    private String getArgsAsJson(Object[] args) {
        try {
            return objectMapper.writeValueAsString(args);
        } catch (JsonProcessingException e) {
            return "요청 본문 로깅 실패";
        }
    }

}
