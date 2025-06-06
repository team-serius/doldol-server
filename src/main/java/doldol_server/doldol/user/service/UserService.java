package doldol_server.doldol.user.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.UserErrorCode;
import doldol_server.doldol.user.dto.request.UpdateUserInfoRequest;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public User getById(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
	}

	public void changeInfo(UpdateUserInfoRequest request, Long userId) {
		User user = this.getById(userId);

		user.updateUserInfo(request.name(), passwordEncoder.encode(request.password()));
	}
}
