package doldol_server.doldol.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import doldol_server.doldol.auth.dto.request.EmailCheckRequest;
import doldol_server.doldol.auth.dto.request.EmailCodeSendRequest;
import doldol_server.doldol.auth.dto.request.EmailCodeVerifyRequest;
import doldol_server.doldol.auth.dto.request.IdCheckRequest;
import doldol_server.doldol.auth.dto.request.PhoneCheckRequest;
import doldol_server.doldol.auth.dto.request.RegisterRequest;
import doldol_server.doldol.auth.dto.request.OAuthRegisterRequest;
import doldol_server.doldol.auth.service.AuthService;
import doldol_server.doldol.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
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
	public ResponseEntity<ApiResponse<Void>> checkIdDuplicate(@RequestBody @Valid IdCheckRequest idCheckRequest) {
		authService.checkIdDuplicate(idCheckRequest.id());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
	}

	@PostMapping("/check-email")
	@Operation(
		summary = "이메일 중복 확인 API",
		description = "이메일 중복 확인")
	public ResponseEntity<ApiResponse<Void>> checkEmailDuplicate(
		@RequestBody @Valid EmailCheckRequest emailCheckRequest) {
		authService.checkEmailDuplicate(emailCheckRequest.email());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
	}

	@PostMapping("/check-phone")
	@Operation(
		summary = "이메일 중복 확인 API",
		description = "이메일 중복 확인")
	public ResponseEntity<ApiResponse<Void>> checkphoneDuplicate(
		@RequestBody @Valid PhoneCheckRequest phoneCheckRequest) {
		authService.checkPhoneDuplicate(phoneCheckRequest.phone());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
	}

	@PostMapping("/email/send-code")
	@Operation(
		summary = "이메일 인증 코드 전송 API",
		description = "이메일 인증 코드 전송")
	public ResponseEntity<ApiResponse<Void>> sendVerificationCode(
		@RequestBody @Valid EmailCodeSendRequest emailCodeSendRequest) {
		authService.sendVerificationCode(emailCodeSendRequest.email());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
	}

	@PostMapping("/email/verify-code")
	@Operation(
		summary = "이메일 인증 코드 검증 API",
		description = "이메일 인증 코드 검증")
	public ResponseEntity<ApiResponse<Void>> validateVerificationCode(
		@RequestBody @Valid EmailCodeVerifyRequest emailCodeVerifyRequest) {
		authService.validateVerificationCode(emailCodeVerifyRequest.email(), emailCodeVerifyRequest.code());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
	}

	@PostMapping("/register")
	@Operation(
		summary = "자체 서비스 회원가입 API",
		description = "자체 서비 회원가입")
	public ResponseEntity<ApiResponse<Void>> register(@RequestBody @Valid RegisterRequest registerRequest) {
		authService.register(registerRequest);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
	}

	@PostMapping("/oauth/register")
	@Operation(
		summary = "소셜 회원가입 완료 API",
		description = "소셜 회원가입 완료")
	public ResponseEntity<ApiResponse<Void>> oauthRegister(
		@RequestBody @Valid OAuthRegisterRequest oAuthRegisterRequest) {
		authService.oauthRegister(oAuthRegisterRequest);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
	}
}
