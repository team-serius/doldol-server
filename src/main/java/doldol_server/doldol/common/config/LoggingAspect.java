package doldol_server.doldol.common.config;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MeterRegistry meterRegistry;

    @Autowired
    private Environment environment;

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

        Timer.Sample sample = Timer.start(meterRegistry);
        long startAt = System.currentTimeMillis();

        try {
            Object returnValue = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());
            long endAt = System.currentTimeMillis();
            long responseTime = endAt - startAt;

            if (isProdProfile()) {
                recordMetrics(request, startAt, endAt, "SUCCESS");

                log.info("REQUEST_METRICS method={} uri={} response_time_ms={} status=SUCCESS timestamp={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    responseTime,
                    startAt);
            }

            log.info(getCommunicationData(request, cachingRequest, startAt, endAt, returnValue, null));

            return returnValue;
        } catch (Exception e) {
            long endAt = System.currentTimeMillis();
            long responseTime = endAt - startAt;

            if (isProdProfile()) {
                recordMetrics(request, startAt, endAt, "ERROR");

                log.error("ERROR_METRICS method={} uri={} response_time_ms={} status=ERROR timestamp={} error_class={} error_message=\"{}\"",
                    request.getMethod(),
                    request.getRequestURI(),
                    responseTime,
                    startAt,
                    e.getClass().getSimpleName(),
                    e.getMessage());
            }

            log.error(getCommunicationData(request, cachingRequest, startAt, endAt, null, e));

            throw e;
        } finally {
            if (isProdProfile()) {
                sample.stop(Timer.builder("http_request_duration_seconds")
                    .tag("method", request.getMethod())
                    .tag("uri", getSimplifiedUri(request.getRequestURI()))
                    .register(meterRegistry));
            }
        }
    }

    @Around("doldol_server.doldol.common.config.LoggingAspect.controllerErrorLevelExecute()")
    public Object requestErrorLevelLogging(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        final ContentCachingRequestWrapper cachingRequest = (ContentCachingRequestWrapper) request;
        long startAt = System.currentTimeMillis();

        Object returnValue = proceedingJoinPoint.proceed(proceedingJoinPoint.getArgs());

        long endAt = System.currentTimeMillis();
        long responseTime = endAt - startAt;

        if (isProdProfile()) {
            log.error("ERROR_METRICS method={} uri={} response_time_ms={} status=ERROR timestamp={} handler=GlobalExceptionHandler",
                request.getMethod(),
                request.getRequestURI(),
                responseTime,
                startAt);
        }

        log.error(getCommunicationData(request, cachingRequest, startAt, endAt, returnValue, null));
        return returnValue;
    }

    private String getCommunicationData(
        HttpServletRequest request,
        ContentCachingRequestWrapper cachingRequest,
        long startAt,
        long endAt,
        Object returnValue,
        Exception exception
    ) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append(START_LOG);
        sb.append(String.format("====> Request: %s %s (%dms)\n====> Headers = %s\n",
            request.getMethod(),
            request.getRequestURL(),
            endAt - startAt,
            getHeaders(request)));
        sb.append("====> Content-Type: ").append(request.getContentType()).append("\n");

        if ("POST".equalsIgnoreCase(request.getMethod()) && cachingRequest.getContentAsByteArray().length > 0) {
            try {
                sb.append(String.format("====> Body: %s\n", objectMapper.readTree(cachingRequest.getContentAsByteArray())));
            } catch (Exception e) {
                sb.append("====> Body: [Unable to parse JSON]\n");
            }
        }

        if (returnValue != null) {
            sb.append(String.format("====> Response: %s\n", returnValue));
        }

        if (exception != null) {
            sb.append(String.format("====> Exception: %s\n", exception.getClass().getSimpleName()));
            sb.append(String.format("====> Error Message: %s\n", exception.getMessage()));

            StackTraceElement[] stackTrace = exception.getStackTrace();
            if (stackTrace.length > 0) {
                sb.append("====> Stack Trace (top 5):\n");
                int limit = Math.min(5, stackTrace.length);
                for (int i = 0; i < limit; i++) {
                    sb.append(String.format("      at %s\n", stackTrace[i].toString()));
                }
            }
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

    private void recordMetrics(HttpServletRequest request, long startAt, long endAt, String status) {
        String method = request.getMethod();
        String uri = getSimplifiedUri(request.getRequestURI());

        Counter.builder("http_requests_total")
            .description("Total HTTP requests")
            .tag("method", method)
            .tag("uri", uri)
            .tag("status", status)
            .register(meterRegistry)
            .increment();

        Timer.builder("http_request_duration_seconds")
            .description("HTTP request duration in seconds")
            .tag("method", method)
            .tag("uri", uri)
            .tag("status", status)
            .register(meterRegistry)
            .record(endAt - startAt, java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    private String getSimplifiedUri(String requestUri) {
        return requestUri.replaceAll("/\\d+", "/{id}")
            .replaceAll("/[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}", "/{uuid}");
    }

    private boolean isProdProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("prod".equals(profile)) {
                return true;
            }
        }
        return false;
    }
}