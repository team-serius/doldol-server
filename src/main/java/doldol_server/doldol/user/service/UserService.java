package doldol_server.doldol.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.UserErrorCode;
import doldol_server.doldol.user.dto.request.UpdateUserInfoRequest;
import doldol_server.doldol.user.dto.response.UserResponse;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public User getById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> {
				log.warn("존재하지 않는 사용자 조회 시도: userId={}", userId);
				return new CustomException(UserErrorCode.USER_NOT_FOUND);
			});
	}

	@Transactional
	public void changeInfo(UpdateUserInfoRequest request, Long userId) {
		User user = getById(userId);

		boolean nameUpdated = false;
		boolean passwordUpdated = false;

		if (request.name() != null) {
			user.updateUserName(request.name());
			nameUpdated = true;
		}
		if (request.password() != null) {
			user.updateUserPassword(passwordEncoder.encode(request.password()));
			passwordUpdated = true;
		}

		if (nameUpdated && passwordUpdated) {
			log.info("사용자 정보 수정 완료: userId={}, 수정항목=이름+비밀번호", userId);
		} else if (nameUpdated) {
			log.info("사용자 정보 수정 완료: userId={}, 수정항목=이름", userId);
		} else if (passwordUpdated) {
			log.info("사용자 정보 수정 완료: userId={}, 수정항목=비밀번호", userId);
		} else {
			log.warn("사용자 정보 수정 요청했으나 변경사항 없음: userId={}", userId);
		}
	}

	public UserResponse getUserInfo(Long userId) {
		User user = getById(userId);
		return UserResponse.of(user);
	}
}
