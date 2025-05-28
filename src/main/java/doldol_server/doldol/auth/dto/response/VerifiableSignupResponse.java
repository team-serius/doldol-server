package doldol_server.doldol.auth.dto.response;

public interface VerifiableSignupResponse {
    String getVerificationCode();
    void initVerificationCode(String code);
    void updateVerificationStatus();
    boolean isVerified();
    String getEmail();
}