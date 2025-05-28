package doldol_server.doldol.auth.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.auth.dto.request.TempJoinRequest;
import doldol_server.doldol.auth.dto.request.OAuthTempJoinRequest;
import doldol_server.doldol.auth.dto.response.OAuthTempSignupResponse;
import doldol_server.doldol.auth.dto.response.TempSignupResponse;
import doldol_server.doldol.auth.dto.response.VerifiableSignupResponse;
import doldol_server.doldol.auth.util.GeneratorRandomUtil;
import doldol_server.doldol.common.exception.AuthErrorCode;
import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.user.entity.SocialType;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
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
	public void tempJoin(TempJoinRequest tempJoinRequest) {
		validateDuplicatedEmailAndPhoneNumber(tempJoinRequest.email(), tempJoinRequest.phone());

		TempSignupResponse tempSignupResponse = TempSignupResponse.getTempSignupDate(tempJoinRequest);
		redisTemplate.opsForValue().set(tempJoinRequest.email(), tempSignupResponse, 10, TimeUnit.MINUTES);
	}

	@Transactional
	public void tempOAuthJoin(OAuthTempJoinRequest oAuthTempJoinRequest) {
		validateDuplicatedEmailAndPhoneNumber(oAuthTempJoinRequest.email(), oAuthTempJoinRequest.phone());

		OAuthTempSignupResponse oAuthTempSignupResponse = OAuthTempSignupResponse.getOAuthTempSignupDate(oAuthTempJoinRequest);
		redisTemplate.opsForValue().set(oAuthTempJoinRequest.email(), oAuthTempSignupResponse, 10, TimeUnit.MINUTES);
	}

	@Transactional
	public void sendVerificationCode(String email) {
		Object response = redisTemplate.opsForValue().get(email);

		if (response == null) {
			throw new CustomException(AuthErrorCode.EMAIL_NOT_FOUND);
		}

		if (!(response instanceof VerifiableSignupResponse)) {
			throw new CustomException(AuthErrorCode.EMAIL_NOT_FOUND);
		}

		VerifiableSignupResponse verifiableResponse = (VerifiableSignupResponse) response;
		String verificationCode = GeneratorRandomUtil.generateRandomNum();

		verifiableResponse.initVerificationCode(verificationCode);
		redisTemplate.opsForValue().set(email, response, 5, TimeUnit.MINUTES);

		emailService.sendEmailVerificationCode(email, verificationCode);
	}

	@Transactional
	public void validateVerificationCode(String email, String code) {
		Object response = redisTemplate.opsForValue().get(email);

		if (response == null) {
			throw new CustomException(AuthErrorCode.EMAIL_NOT_FOUND);
		}

		if (!(response instanceof VerifiableSignupResponse)) {
			throw new CustomException(AuthErrorCode.EMAIL_NOT_FOUND);
		}

		VerifiableSignupResponse verifiableResponse = (VerifiableSignupResponse) response;

		if (!verifiableResponse.getVerificationCode().equals(code)) {
			throw new CustomException(AuthErrorCode.VERIFICATION_CODE_WRONG);
		}

		verifiableResponse.updateVerificationStatus();
		redisTemplate.opsForValue().set(email, response, 30, TimeUnit.MINUTES);
	}

	@Transactional
	public void join(String email) {
		Object response = redisTemplate.opsForValue().get(email);

		if (response == null) {
			throw new CustomException(AuthErrorCode.EMAIL_NOT_FOUND);
		}

		if (!(response instanceof VerifiableSignupResponse)) {
			throw new CustomException(AuthErrorCode.EMAIL_NOT_FOUND);
		}

		VerifiableSignupResponse verifiableResponse = (VerifiableSignupResponse) response;

		if (!verifiableResponse.isVerified()) {
			throw new CustomException(AuthErrorCode.UNVERIFIED_EMAIL);
		}

		if (response instanceof TempSignupResponse) {
			createRegularUser((TempSignupResponse) response);
		} else if (response instanceof OAuthTempSignupResponse) {
			createOAuthUser((OAuthTempSignupResponse) response);
		}

		redisTemplate.delete(email);
	}

	private void createRegularUser(TempSignupResponse tempSignupResponse) {
		User user = User.builder()
			.loginId(tempSignupResponse.getLoginId())
			.password(passwordEncoder.encode(tempSignupResponse.getPassword()))
			.email(tempSignupResponse.getEmail())
			.name(tempSignupResponse.getName())
			.phoneNumber(tempSignupResponse.getPhoneNumber())
			.build();

		userRepository.save(user);
	}

	private void createOAuthUser(OAuthTempSignupResponse oAuthTempSignupResponse) {
		User user = User.builder()
			.email(oAuthTempSignupResponse.getEmail())
			.name(oAuthTempSignupResponse.getName())
			.phoneNumber(oAuthTempSignupResponse.getPhoneNumber())
			.socialId(oAuthTempSignupResponse.getSocialId())
			.socialType(SocialType.getSocialType(oAuthTempSignupResponse.getSocialType()))
			.build();

		userRepository.save(user);
	}

	private void validateDuplicatedEmailAndPhoneNumber(String email, String phoneNumber) {
		boolean isEmailExists = userRepository.existsByEmail(email);

		if (isEmailExists) {
			throw new CustomException(AuthErrorCode.EMAIl_DUPLICATED);
		}

		boolean isPhoneNumberExists = userRepository.existsByPhoneNumber(phoneNumber);

		if (isPhoneNumberExists) {
			throw new CustomException(AuthErrorCode.PHONE_NUMBER_DUPLICATED);
		}
	}
}