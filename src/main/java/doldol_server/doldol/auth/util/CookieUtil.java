package doldol_server.doldol.auth.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CookieUtil {

    public static ResponseCookie createCookie(String name, String value, long cookieExpiration) {
        return ResponseCookie.from(name, value)
                .maxAge(cookieExpiration)
                .path("/")
                .sameSite("Strict")
                .httpOnly(true)
                .build();
    }

    public static Cookie findCookieByName(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie;
                }
            }
        }
        return null;
    }
}
