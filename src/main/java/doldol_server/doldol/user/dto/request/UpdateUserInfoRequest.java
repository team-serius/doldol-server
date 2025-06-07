package doldol_server.doldol.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(name = "UpdateUserInfoRequest: 개인정보 수정 요청 Dto")
public record UpdateUserInfoRequest(

	@Schema(description = "변경할 이름", example = "김둘둘")
	String name,

	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,16}$",
		message = "비밀번호는 8~16자리로 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.")
	@Schema(description = "변경할 비밀번호", example = "doldol2345!")
	String password) {

}
