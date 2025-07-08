package doldol_server.doldol.auth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.auth.dto.request.EmailCodeSendRequest;
import doldol_server.doldol.auth.dto.request.EmailCodeVerifyRequest;
import doldol_server.doldol.auth.dto.request.IdCheckRequest;
import doldol_server.doldol.auth.dto.request.OAuthRegisterRequest;
import doldol_server.doldol.auth.dto.request.PasswordInfoCheckRequest;
import doldol_server.doldol.auth.dto.request.RegisterInfoRequest;
import doldol_server.doldol.auth.dto.request.RegisterRequest;
import doldol_server.doldol.auth.dto.request.ReissueTokenRequest;
import doldol_server.doldol.auth.dto.request.UserInfoIdCheckRequest;
import doldol_server.doldol.auth.dto.response.ReissueTokenResponse;
import doldol_server.doldol.auth.dto.response.UserLoginIdResponse;
import doldol_server.doldol.auth.service.AuthService;
import doldol_server.doldol.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "회원가입")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/check-id")
	@Operation(
		summary = "아이디 중복 확인 API",
		description = "이이디 중복 확인")
	public ApiResponse<Void> checkIdDuplicate(@RequestBody @Valid IdCheckRequest idCheckRequest) {
		authService.checkIdDuplicate(idCheckRequest.id());
		return ApiResponse.noContent();
	}

	@PostMapping("/check-register-info")
	@Operation(
		summary = "이메일, 전화번호 중복 확인 API",
		description = "이메일, 전화번호 중복 확인")
	public ApiResponse<Void> checkRegisterInfoDuplicate(
		@RequestBody @Valid RegisterInfoRequest registerInfoRequest) {
		authService.checkRegisterInfoDuplicate(registerInfoRequest.email(), registerInfoRequest.phone());
		return ApiResponse.noContent();
	}

	@PostMapping("/email/send-code")
	@Operation(
		summary = "이메일 인증 코드 전송 API",
		description = "이메일 인증 코드 전송")
	public ApiResponse<Void> sendVerificationCode(
		@RequestBody @Valid EmailCodeSendRequest emailCodeSendRequest) {
		authService.sendVerificationCode(emailCodeSendRequest.email());
		return ApiResponse.noContent();
	}

	@PostMapping("/email/verify-code")
	@Operation(
		summary = "이메일 인증 코드 검증 API",
		description = "이메일 인증 코드 검증")
	public ApiResponse<Void> validateVerificationCode(
		@RequestBody @Valid EmailCodeVerifyRequest emailCodeVerifyRequest) {
		authService.validateVerificationCode(emailCodeVerifyRequest.email(), emailCodeVerifyRequest.code());
		return ApiResponse.noContent();
	}

	@PostMapping("/register")
	@Operation(
		summary = "자체 서비스 회원가입 API",
		description = "자체 서비 회원가입")
	public ApiResponse<Void> register(@RequestBody @Valid RegisterRequest registerRequest) {
		authService.register(registerRequest);
		return ApiResponse.noContent();
	}

	@PostMapping("/oauth/register")
	@Operation(
		summary = "소셜 회원가입 완료 API",
		description = "소셜 회원가입 완료")
	public ApiResponse<Void> oauthRegister(
		@RequestBody @Valid OAuthRegisterRequest oAuthRegisterRequest) {
		authService.oauthRegister(oAuthRegisterRequest);
		return ApiResponse.noContent();
	}

	@PostMapping("/reissue")
	@Operation(
		summary = "토큰 재발급 API",
		description = "리프레시 토큰으로 새로운 액세스 토큰 발급")
	public ApiResponse<ReissueTokenResponse> reissue(@RequestBody ReissueTokenRequest request) {
		ReissueTokenResponse reissueTokenResponse = authService.reissue(request.refreshToken());
		return ApiResponse.ok(reissueTokenResponse);
	}

	@PostMapping("/withdraw")
	@Operation(
		summary = "회원 탈퇴 API",
		description = "회원 탈퇴",
		security = {@SecurityRequirement(name = "jwt")})
	public ApiResponse<Void> withdraw(@RequestParam("userId") Long userId,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		authService.withdraw(userId);
		return ApiResponse.noContent();
	}

	@PostMapping("/validate/user/info")
	@Operation(
		summary = "사용자 정보 검증 API",
		description = "사용자 정보 검증")
	public ApiResponse<Void> validateUserInfo(
		@RequestBody UserInfoIdCheckRequest userInfoIdCheckRequest) {
		authService.validateUserInfo(userInfoIdCheckRequest.name(), userInfoIdCheckRequest.email(),
			userInfoIdCheckRequest.phone());
		return ApiResponse.noContent();
	}

	@GetMapping("/find/id")
	@Operation(
		summary = "아이디 찾기 API",
		description = "아이디 찾기")
	public ApiResponse<UserLoginIdResponse> validateUserInfo(@RequestParam("email") String email) {
		UserLoginIdResponse loginId = authService.getLoginId(email);
		return ApiResponse.ok(loginId);
	}

	@PatchMapping("/reset/password")
	@Operation(
		summary = "비밀번호 초기화 API",
		description = "비밀번호 초기화")
	public ApiResponse<Void> resetPassword(@RequestParam("email") String email) {
		authService.resetPassword(email);
		return ApiResponse.noContent();
	}

	@PostMapping("/check-email")
	@Operation(
		summary = "이메일 확인 API",
		description = "이메일 확인")
	public ApiResponse<Void> checkEmailDuplicate(@RequestParam("email") String email) {
		authService.checkEmailExists(email);
		return ApiResponse.noContent();
	}

	@PostMapping("/check-password-info")
	@Operation(
		summary = "아이디, 이메일 확인 API",
		description = "아이디, 이메일 확인")
	public ApiResponse<Void> validatePasswordInfo(@RequestBody PasswordInfoCheckRequest passwordInfoCheckRequest) {
		authService.validatePasswordInfo(passwordInfoCheckRequest.id(), passwordInfoCheckRequest.email());
		return ApiResponse.noContent();
	}
}
