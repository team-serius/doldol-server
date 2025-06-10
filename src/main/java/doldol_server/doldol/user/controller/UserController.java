package doldol_server.doldol.user.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.common.response.ApiResponse;
import doldol_server.doldol.user.dto.request.UpdateUserInfoRequest;
import doldol_server.doldol.user.dto.response.UserResponse;
import doldol_server.doldol.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "사용자")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PatchMapping("/info")
	@Operation(
		summary = "개인정보 수정 API",
		description = "개인정보 수정",
		security = {@SecurityRequirement(name = "jwt")})
	public ApiResponse<Void> updateUserInfo(
		@RequestBody @Valid UpdateUserInfoRequest request,
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		userService.changeInfo(request, userDetails.getUserId());
		return ApiResponse.noContent();
	}

	@GetMapping("/info")
	@Operation(
		summary = "사용자 본인 정보 조회 API",
		description = "본인 정보 조회",
		security = {@SecurityRequirement(name = "jwt")})
	public ApiResponse<UserResponse> getUserInfo(
		@AuthenticationPrincipal CustomUserDetails userDetails) {
		UserResponse myInfo = userService.getUserInfo(userDetails.getUserId());
		return ApiResponse.ok(myInfo);
	}

}
