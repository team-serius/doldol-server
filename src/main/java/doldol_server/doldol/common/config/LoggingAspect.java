package doldol_server.doldol.common.config;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Pointcut("execution(* doldol_server.doldol.common.exception.GlobalExceptionHandler.handle*(..))")
    public void controllerErrorLevelExecute() {
    }

    @Around("doldol_server.doldol.common.config.LoggingAspect.controllerErrorLevelExecute()")
    public Object requestErrorLevelLogging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        final ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;
        long startAt = System.currentTimeMillis();

        Object returnValue = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        long endAt = System.currentTimeMillis();

        logErrorDetails(request, cachingRequest, startAt, endAt, returnValue, null);
        return returnValue;
    }

    private void logErrorDetails(
        HttpServletRequest request,
        ContentCachingRequestWrapper cachingRequest,
        long startAt,
        long endAt,
        Object returnValue,
        Exception exception
    ) {
        // 시작 구분선
        log.error("================================================ERROR===============================================");

        // 기본 요청 정보
        log.error("Request: {} {} ({}ms)",
            request.getMethod(),
            request.getRequestURL(),
            endAt - startAt);

        // 헤더 정보
        Map<String, Object> headers = getHeaders(request);
        log.error("Headers: {}", headers);

        // Content-Type
        log.error("Content-Type: {}", request.getContentType());

        // POST 요청 Body 처리
        if ("POST".equalsIgnoreCase(request.getMethod()) && cachingRequest != null) {
            try {
                byte[] content = cachingRequest.getContentAsByteArray();
                if (content.length > 0) {
                    log.error("Body: {}", objectMapper.readTree(content));
                } else {
                    log.error("Body: [Empty]");
                }
            } catch (Exception e) {
                log.error("Body: [Unable to parse JSON]");
            }
        }

        // 응답 정보
        if (returnValue != null) {
            log.error("Response: {}", returnValue);
        }

        // 예외 정보 (있는 경우)
        if (exception != null) {
            log.error("Exception: {}", exception.getClass().getSimpleName());
            log.error("Error Message: {}", exception.getMessage());

            StackTraceElement[] stackTrace = exception.getStackTrace();
            if (stackTrace.length > 0) {
                log.error("Stack Trace (top 5):");
                int limit = Math.min(5, stackTrace.length);
                for (int i = 0; i < limit; i++) {
                    log.error("  at {}", stackTrace[i].toString());
                }
            }
        }

        // 종료 구분선
        log.error("================================================END===============================================");
    }

    private Map<String, Object> getHeaders(HttpServletRequest request) {
        Map<String, Object> headerMap = new HashMap<>();
        try {
            Enumeration<String> headerArray = request.getHeaderNames();
            while (headerArray.hasMoreElements()) {
                String headerName = headerArray.nextElement();
                headerMap.put(headerName, request.getHeader(headerName));
            }
        } catch (Exception e) {
            headerMap.put("error", "Unable to read headers: " + e.getMessage());
        }
        return headerMap;
    }
}