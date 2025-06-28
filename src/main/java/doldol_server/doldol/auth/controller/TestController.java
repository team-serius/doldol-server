package doldol_server.doldol.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import doldol_server.doldol.auth.service.TestService;
import doldol_server.doldol.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

	private final TestService testService;

	@PostMapping()
	public ApiResponse<Void> resetPassword() {
		testService.updateAll();
		return ApiResponse.noContent();
	}
}