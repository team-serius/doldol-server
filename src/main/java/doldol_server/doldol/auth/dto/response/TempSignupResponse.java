package doldol_server.doldol.auth.dto.response;

import doldol_server.doldol.auth.dto.request.TempJoinRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TempSignupResponse {

	private String email;
	private String loginId;
	private String password;
	private String name;
	private String phoneNumber;
	private String verificationCode;
	private boolean verified = false;

	public static TempSignupResponse getTempSignupDate(TempJoinRequest tempJoinRequest) {
		return TempSignupResponse.builder()
			.email(tempJoinRequest.email())
			.name(tempJoinRequest.name())
			.loginId(tempJoinRequest.id())
			.phoneNumber(tempJoinRequest.phone())
			.password(tempJoinRequest.password())
			.build();
	}

	public void initVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public void updateVerificationStatus() {
		this.verified = true;
	}

	@Builder
	private TempSignupResponse(String email, String loginId, String name, String password, String phoneNumber,
		String verificationCode) {
		this.email = email;
		this.loginId = loginId;
		this.name = name;
		this.password = password;
		this.phoneNumber = phoneNumber;
		this.verificationCode = verificationCode;
	}
}