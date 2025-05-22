package doldol_server.doldol.auth.jwt.dto;

public record UserTokenResponse(String accessToken, String refreshToken) {
}