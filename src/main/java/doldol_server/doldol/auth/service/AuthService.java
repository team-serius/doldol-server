package doldol_server.doldol.auth.service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.auth.dto.request.OAuthRegisterRequest;
import doldol_server.doldol.auth.dto.request.RegisterRequest;
import doldol_server.doldol.auth.util.GeneratorRandomUtil;
import doldol_server.doldol.common.exception.AuthErrorCode;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.user.entity.SocialType;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

	private static final String EMAIL_VERIFIED_KEY = "verified";

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

	public void checkEmailDuplicate(String email) {
		boolean isEmailExists = userRepository.existsByEmail(email);

		if (isEmailExists) {
			throw new CustomException(AuthErrorCode.EMAIl_DUPLICATED);
		}
	}

	public void checkPhoneDuplicate(String phone) {
		boolean isPhoneExists = userRepository.existsByPhone(phone);

		if (isPhoneExists) {
			throw new CustomException(AuthErrorCode.PHONE_DUPLICATED);
		}
	}

	@Transactional
	public void sendVerificationCode(String email) {
		String verificationCode = GeneratorRandomUtil.generateRandomNum();

		redisTemplate.opsForValue().set(email, verificationCode, 5, TimeUnit.MINUTES);

		emailService.sendEmailVerificationCode(email, verificationCode);
	}

	@Transactional
	public void validateVerificationCode(String email, String code) {
		Object result = redisTemplate.opsForValue().get(email);

		if (result == null) {
			throw new CustomException(AuthErrorCode.EMAIL_NOT_FOUND);
		}

		String verificationCode = result.toString();

		if (!verificationCode.equals(code)) {
			throw new CustomException(AuthErrorCode.VERIFICATION_CODE_WRONG);
		}

		redisTemplate.opsForValue().set(email, EMAIL_VERIFIED_KEY, 30, TimeUnit.MINUTES);
	}

	@Transactional
	public void register(RegisterRequest registerRequest) {
		validateAndDeleteEmailVerification(registerRequest.email());

		User user = User.builder()
			.loginId(registerRequest.id())
			.password(passwordEncoder.encode(registerRequest.password()))
			.phone(registerRequest.phone())
			.email(registerRequest.email())
			.name(registerRequest.name())
			.build();

		userRepository.save(user);
	}

	@Transactional
	public void oauthRegister(@Valid OAuthRegisterRequest oAuthRegisterRequest) {
		validateAndDeleteEmailVerification(oAuthRegisterRequest.email());

		User user = User.builder()
			.phone(oAuthRegisterRequest.phone())
			.email(oAuthRegisterRequest.email())
			.name(oAuthRegisterRequest.name())
			.socialId(oAuthRegisterRequest.socialId())
			.socialType(SocialType.getSocialType(oAuthRegisterRequest.socialType()))
			.build();

		userRepository.save(user);
	}

	@Transactional
	protected void validateAndDeleteEmailVerification(String email) {
		String isVerified = Optional.ofNullable(redisTemplate.opsForValue().get(email))
			.map(Object::toString)
			.orElseThrow(() -> new CustomException(AuthErrorCode.UNVERIFIED_EMAIL));

		if (!isVerified.equals(EMAIL_VERIFIED_KEY)) {
			throw new CustomException(AuthErrorCode.UNVERIFIED_EMAIL);
		}

		redisTemplate.delete(email);
	}
}