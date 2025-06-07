package doldol_server.doldol.common.constants;

import java.util.List;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CorsConstant {
	public static final List<String> ALLOWED_ORIGINS = List.of("http://localhost:3000", "http://localhost:3001",
		"https://dev.doldol.wha1eson.co.kr");
	public static final List<String> ALLOWED_METHODS = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS");
	public static final List<String> ALLOWED_HEADERS = List.of("X-Requested-With, Content-Type, Accept");
	public static final boolean ALLOWED_CREDENTIALS = true;
	public static final long MAX_AGE = 3600L;
}
