package doldol_server.doldol.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(name = "LoginRequest: 로그인 요청 Dto")
public record LoginRequest(

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Pattern(regexp = "^[a-z0-9]{4,20}$", message = "아이디는 영어 소문자와 숫자만 사용하여 4~20자리여야 합니다.")
    @Schema(description = "아이디는 입력되어야 합니다. 아이디는 영어 소문자와 숫자만 사용하여 4~20자리입니다", example = "doldol1234")
    String id,

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[$@$!%*#?&])[A-Za-z\\d$@$!%*#?&]{8,16}$",
        message = "비밀번호는 8~16자리로 영문 대소문자, 숫자, 특수문자를 포함해야 합니다.")
    @Schema(description = "비밀번호는 입력되어야 합니다. 비밀번호는 8~16자리 수 입니다. 영문 대소문자, 숫자, 특수문자를 포함합니다.", example = "doldol1234!")
    String password

) {}