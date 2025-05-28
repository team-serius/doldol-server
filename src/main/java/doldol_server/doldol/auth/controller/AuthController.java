package doldol_server.doldol.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import doldol_server.doldol.auth.dto.request.EmailCodeSendRequest;
import doldol_server.doldol.auth.dto.request.EmailCodeVerifyRequest;
import doldol_server.doldol.auth.dto.request.FinalJoinRequest;
import doldol_server.doldol.auth.dto.request.IdCheckRequest;
import doldol_server.doldol.auth.dto.request.TempJoinRequest;
import doldol_server.doldol.auth.dto.request.OAuthTempJoinRequest;
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
	public ResponseEntity<ApiResponse<Void>> checkIdDuplicate(@RequestBody IdCheckRequest idCheckRequest) {
		authService.checkIdDuplicate(idCheckRequest.id());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
	}

	@PostMapping("/temp-join")
	@Operation(
		summary = "임시 회원가입 API",
		description = "임시 회원가입")
	public ResponseEntity<ApiResponse<Void>> tempJoin(@RequestBody @Valid TempJoinRequest tempJoinRequest) {
		authService.tempJoin(tempJoinRequest);
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

	@PostMapping("/join")
	@Operation(
		summary = "회원가입 완료 API",
		description = "회원가입 완료")
	public ResponseEntity<ApiResponse<Void>> join(@RequestBody @Valid FinalJoinRequest finalJoinRequest) {
		authService.join(finalJoinRequest.email());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
	}

	@PostMapping("/oauth/temp-join")
	@Operation(
		summary = "임시 소셜 회원가입 API",
		description = "임시 소셜 회원가입")
	public ResponseEntity<ApiResponse<Void>> oauthJoin(@RequestBody @Valid OAuthTempJoinRequest OAuthTempJoinRequest) {
		authService.tempOAuthJoin(OAuthTempJoinRequest);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.noContent());
	}
}
