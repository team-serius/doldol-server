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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
public class LoggingAspect {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String START_LOG = "================================================NEW===============================================\n";
    private static final String END_LOG = "================================================END===============================================\n";

    @Pointcut("execution(* doldol_server.doldol.*.controller..*(..)) || ( execution(* doldol_server.doldol.common.exception..*(..)) && !execution(* doldol_server.doldol.common.exception.GlobalExceptionHandler.handle*(..)))")
    public void controllerInfoLevelExecute() {
    }

    @Pointcut("execution(* doldol_server.doldol.common.exception.GlobalExceptionHandler.handle*(..))")
    public void controllerErrorLevelExecute() {
    }

    @Around("doldol_server.doldol.common.config.LoggingAspect.controllerInfoLevelExecute()")
    public Object requestInfoLevelLogging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        final ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;
        long startAt = System.currentTimeMillis();
        Object returnValue = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        long endAt = System.currentTimeMillis();

        log.info(getCommunicationData(request, cachingRequest, startAt, endAt, returnValue));
        return returnValue;
    }

    @Around("doldol_server.doldol.common.config.LoggingAspect.controllerErrorLevelExecute()")
    public Object requestErrorLevelLogging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        final ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;
        long startAt = System.currentTimeMillis();
        Object returnValue = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
        long endAt = System.currentTimeMillis();

        log.error(getCommunicationData(request, cachingRequest, startAt, endAt, returnValue));
        return returnValue;
    }

    private String getCommunicationData(
            HttpServletRequest request,
            ContentCachingRequestWrapper cachingRequest,
            long startAt,
            long endAt,
            Object returnValue
    ) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append(START_LOG);
        sb.append(String.format("====> Request: %s %s ({%d}ms)\n====> *Header = {%s}\n", request.getMethod(), request.getRequestURL(), endAt - startAt, getHeaders(request)));
        sb.append("=================> content type is ").append(request.getContentType()).append("\n");
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            sb.append(String.format("====> application/json Body: {%s}\n", objectMapper.readTree(cachingRequest.getContentAsByteArray())));
        }
        if (returnValue != null) {
            sb.append(String.format("====> Response: {%s}\n", returnValue));
        }
        sb.append(END_LOG);
        return sb.toString();
    }

    private Map<String, Object> getHeaders(HttpServletRequest request) {
        Map<String, Object> headerMap = new HashMap<>();

        Enumeration<String> headerArray = request.getHeaderNames();
        while (headerArray.hasMoreElements()) {
            String headerName = headerArray.nextElement();
            headerMap.put(headerName, request.getHeader(headerName));
        }
        return headerMap;
    }
}
