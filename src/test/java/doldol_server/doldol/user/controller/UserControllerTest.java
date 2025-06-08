package doldol_server.doldol.user.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import doldol_server.doldol.common.ControllerTest;
import doldol_server.doldol.user.dto.request.UpdateUserInfoRequest;
import doldol_server.doldol.user.dto.response.UserResponse;
import doldol_server.doldol.user.service.UserService;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest extends ControllerTest {

	@MockitoBean
	private UserService userService;

	@Test
	@DisplayName("사용자 정보 갱신 - 성공")
	void updateUserInfo_Success() throws Exception {
		// given
		UpdateUserInfoRequest request = new UpdateUserInfoRequest("김둘둘", "doldol2345!");
		doNothing().when(userService).changeInfo(any(UpdateUserInfoRequest.class), anyLong());

		// when & then
		mockMvc.perform(patch("/user/info")
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(request))
				.with(mockUser(1L)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(204));

		verify(userService).changeInfo(any(UpdateUserInfoRequest.class), anyLong());
	}

	@Test
	@DisplayName("사용자 정보 조회 - 성공")
	void getMyInfo_Success() throws Exception {
		// given
		UserResponse mockResponse = UserResponse.builder()
			.name("김돌돌")
			.phone("01012341234")
			.email("doldol@test.com")
			.socialId("1233244124")
			.socialType("kakao")
			.build();

		when(userService.getMyInfo(1L)).thenReturn(mockResponse);

		// when & then
		mockMvc.perform(get("/user/me")
				.with(mockUser(1L)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.status").value(200))
			.andExpect(jsonPath("$.data.name").value("김돌돌"))
			.andExpect(jsonPath("$.data.email").value("doldol@test.com"))
			.andExpect(jsonPath("$.data.phone").value("01012341234"))
			.andExpect(jsonPath("$.data.socialType").value("kakao"));

		// verify
		verify(userService).getMyInfo(1L);
	}
}
