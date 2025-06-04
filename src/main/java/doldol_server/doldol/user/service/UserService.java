package doldol_server.doldol.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.UserErrorCode;
import doldol_server.doldol.user.dto.request.UpdateUserInfoRequest;
import doldol_server.doldol.user.dto.response.UpdateUserInfoResponse;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public User getById(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
	}

	public UpdateUserInfoResponse changeInfo(UpdateUserInfoRequest request, Long userId) {
		UpdateUserInfoResponse updateUserInfoResponse = new UpdateUserInfoResponse(request.name(), request.password());
		return updateUserInfoResponse;
	}
}
