package doldol_server.doldol.auth.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.auth.dto.request.TempJoinRequest;
import doldol_server.doldol.auth.dto.response.TempSignupResponse;
import doldol_server.doldol.auth.util.GeneratorRandomUtil;
import doldol_server.doldol.common.exception.AuthErrorCode;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

	private final EmailService emailService;
	private final UserRepository userRepository;
	private final RedisTemplate<String, Object> redisTemplate;
	private final PasswordEncoder passwordEncoder;

	public void checkIdDuplicate(String id) {
		boolean isIdExists = userRepository.existsByLoginId(id);

		if (isIdExists) {
			throw new CustomException(AuthErrorCode.ID_DUPLICATED);
		}
	}

	@Transactional
	public void tempJoin(@Valid TempJoinRequest tempJoinRequest) {

		boolean isEmailExists = userRepository.existsByEmail(tempJoinRequest.email());

		if (isEmailExists) {
			throw new CustomException(AuthErrorCode.EMAIl_DUPLICATED);
		}

		boolean isPhoneNumberExists = userRepository.existsByPhoneNumber(tempJoinRequest.phone());

		if (isPhoneNumberExists) {
			throw new CustomException(AuthErrorCode.PHONE_NUMBER_DUPLICATED);
		}

		TempSignupResponse tempSignupResponse = TempSignupResponse.getTempSignupDate(tempJoinRequest);
		redisTemplate.opsForValue().set(tempJoinRequest.email(), tempSignupResponse, 10, TimeUnit.MINUTES);
	}

	@Transactional
	public void sendVerificationCode(String email) {
		TempSignupResponse tempSignupResponse = (TempSignupResponse)redisTemplate.opsForValue().get(email);

		if (tempSignupResponse == null) {
			throw new CustomException(AuthErrorCode.EMAIL_NOT_FOUND);
		}

		String verificationCode = GeneratorRandomUtil.generateRandomNum();

		tempSignupResponse.initVerificationCode(verificationCode);
		redisTemplate.opsForValue().set(email, tempSignupResponse, 5, TimeUnit.MINUTES);

		emailService.sendEmailVerificationCode(email, verificationCode);
	}

	@Transactional
	public void validateVerificationCode(String email, String code) {
		TempSignupResponse tempSignupResponse = (TempSignupResponse)redisTemplate.opsForValue().get(email);

		if (tempSignupResponse == null) {
			throw new CustomException(AuthErrorCode.EMAIL_NOT_FOUND);
		}

		if (!tempSignupResponse.getVerificationCode().equals(code)) {
			throw new CustomException(AuthErrorCode.VERIFICATION_CODE_WRONG);
		}

		tempSignupResponse.updateVerificationStatus();
		redisTemplate.opsForValue().set(email, tempSignupResponse, 30, TimeUnit.MINUTES);
	}

	@Transactional
	public void join(String email) {
		TempSignupResponse tempSignupResponse = (TempSignupResponse)redisTemplate.opsForValue().get(email);

		if (tempSignupResponse == null) {
			throw new CustomException(AuthErrorCode.EMAIL_NOT_FOUND);
		}

		if (!tempSignupResponse.isVerified()) {
			throw new CustomException(AuthErrorCode.UNVERIFIED_EMAIL);
		}

		redisTemplate.delete(email);

		User user = User.builder()
			.loginId(tempSignupResponse.getLoginId())
			.password(passwordEncoder.encode(tempSignupResponse.getPassword()))
			.email(email)
			.name(tempSignupResponse.getName())
			.phoneNumber(tempSignupResponse.getPhoneNumber())
			.build();

		userRepository.save(user);
	}
}
