package doldol_server.doldol.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import doldol_server.doldol.common.ServiceTest;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.UserErrorCode;
import doldol_server.doldol.user.dto.request.UpdateUserInfoRequest;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;

@DisplayName("User 서비스 통합 테스트")
class UserServiceTest extends ServiceTest {

	@Autowired
	private UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private User user;

	private User createAndSaveUser(String loginId, String name, String email, String phone, String password) {
		User dummyUser = User.builder()
			.loginId(loginId)
			.name(name)
			.email(email)
			.phone(phone)
			.password(password)
			.build();
		return userRepository.save(dummyUser);
	}

	@BeforeEach
	void setUp() {
		user = createAndSaveUser("doldol", "김돌돌", "kimdoldol@test.com", "01012345678", "doldol1234!");
	}

	@Test
	@DisplayName("사용자 정보 수정 - 성공")
	void updateAllUserInfo_Success() {
		// given
		UpdateUserInfoRequest request = new UpdateUserInfoRequest("김둘둘", "doldol2345!");

		// when
		assertDoesNotThrow(() -> userService.changeInfo(request, user.getId()));

		// then
		User updatedUser = userRepository.findById(user.getId()).orElseThrow();
		assertThat(updatedUser.getName()).isEqualTo("김둘둘");
		assertThat(passwordEncoder.matches("doldol2345!", updatedUser.getPassword())).isTrue();

	}

	@Test
	@DisplayName("사용자 이름만 수정 - 성공")
	void updateUserNameOnly_Success() {
		// given
		UpdateUserInfoRequest request = new UpdateUserInfoRequest("김둘둘", null);
		String originalEncodedPassword = user.getPassword();

		// when
		assertDoesNotThrow(() -> userService.changeInfo(request, user.getId()));

		// then
		User updatedUser = userRepository.findById(user.getId()).orElseThrow();
		assertThat(updatedUser.getName()).isEqualTo("김둘둘");
		assertThat(updatedUser.getPassword()).isEqualTo(originalEncodedPassword);
	}

	@Test
	@DisplayName("사용자 비밀번호만 수정 - 성공")
	void updateUserPasswordOnly_Success() {
		// given
		UpdateUserInfoRequest request = new UpdateUserInfoRequest(null, "doldol2345!");
		String originalUserName = user.getName();

		// when
		assertDoesNotThrow(() -> userService.changeInfo(request, user.getId()));

		// then
		User updatedUser = userRepository.findById(user.getId()).orElseThrow();
		assertThat(updatedUser.getName()).isEqualTo(originalUserName);
		assertThat(passwordEncoder.matches("doldol2345!", updatedUser.getPassword())).isTrue();
	}

	@Test
	@DisplayName("사용자 정보 수정 - 사용자를 찾을 수 없음")
	void updateUserInfo_ThrowsException_UserNotFound() {
		// given
		Long nonExistentUserId = 999L;
		UpdateUserInfoRequest request = new UpdateUserInfoRequest("김둘둘", "doldol2345!");

		// when & then
		CustomException exception = assertThrows(CustomException.class,
			() -> userService.changeInfo(request, nonExistentUserId));

		assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.USER_NOT_FOUND);
	}
}
