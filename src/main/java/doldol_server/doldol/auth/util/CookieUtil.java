package doldol_server.doldol.auth.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CookieUtil {

    @Value("${cookie.same-site}")
    private static String sameSite;

    @Value("${cookie.domain}")
    private static String domain;

    public static ResponseCookie createCookie(String name, String value, long cookieExpiration) {
        return ResponseCookie.from(name, value)
            .maxAge(cookieExpiration)
            .path("/")
            .domain(domain)
            .sameSite(sameSite)
            .httpOnly(true)
            .build();
    }

    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie cookie = findCookieByName(request, name);
        return cookie != null ? cookie.getValue() : null;
    }

    private static Cookie findCookieByName(HttpServletRequest request, String name) {
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