package doldol_server.doldol.auth.dto.response;

import doldol_server.doldol.auth.dto.request.OAuthTempJoinRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OAuthTempSignupResponse implements VerifiableSignupResponse {

	private String email;
	private String name;
	private String phoneNumber;
	private String socialId;
	private String socialType;
	private String verificationCode;
	private boolean verified = false;

	public static OAuthTempSignupResponse getOAuthTempSignupDate(OAuthTempJoinRequest oAuthTempJoinRequest) {
		return OAuthTempSignupResponse.builder()
			.email(oAuthTempJoinRequest.email())
			.name(oAuthTempJoinRequest.name())
			.phoneNumber(oAuthTempJoinRequest.phone())
			.socialId(oAuthTempJoinRequest.socialId())
			.socialType(oAuthTempJoinRequest.socialType())
			.build();
	}

	@Override
	public String getVerificationCode() {
		return this.verificationCode;
	}

	@Override
	public void initVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	@Override
	public void updateVerificationStatus() {
		this.verified = true;
	}

	@Override
	public boolean isVerified() {
		return this.verified;
	}

	@Override
	public String getEmail() {
		return this.email;
	}

	@Builder
	private OAuthTempSignupResponse(String email, String name, String phoneNumber, String socialId, String socialType,
		String verificationCode) {
		this.email = email;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.socialId = socialId;
		this.socialType = socialType;
		this.verificationCode = verificationCode;
	}
}