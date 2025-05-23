package doldol_server.doldol.common.constants;

import org.springframework.beans.factory.annotation.Value;

public class CorsConstant {
    @Value("${cors.allow.origins}")
    public static String ALLOWED_ORIGINS;
    public static final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS";
    public static final String ALLOWED_HEADERS = "X-Requested-With, Content-Type, Accept";
    public static final boolean ALLOWED_CREDENTIALS = true;
    public static final long MAX_AGE = 3600L;
}
