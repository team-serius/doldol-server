package doldol_server.doldol.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import doldol_server.doldol.auth.dto.request.JoinRequest;
import doldol_server.doldol.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "회원가입")
@RestController
@RequestMapping("/auth")
public class AuthController {

	@PostMapping("/join")
	@Operation(
		summary = "회원가입 API",
		description = "회원가입")
	public ApiResponse<Void> join(
		@RequestBody @Valid JoinRequest request) {
		return ApiResponse.noContent();
	}
}
