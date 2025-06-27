package doldol_server.doldol.auth.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import doldol_server.doldol.auth.service.TestService;
import doldol_server.doldol.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

	private final TestService testService;

	@GetMapping
	public ApiResponse<Map<String, String>> test(@RequestParam String email) {
		Map<String, String> test = testService.test(email);
		return ApiResponse.ok(test);
	}
}
