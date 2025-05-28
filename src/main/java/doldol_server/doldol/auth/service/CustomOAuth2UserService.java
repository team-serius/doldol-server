package doldol_server.doldol.auth.service;

import static doldol_server.doldol.auth.util.GeneratorRandomUtil.*;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.auth.dto.OAuth2Response;
import doldol_server.doldol.common.exception.AuthErrorCode;
import doldol_server.doldol.common.exception.CommonErrorCode;
import doldol_server.doldol.common.exception.CustomOAuth2Exception;
import doldol_server.doldol.user.entity.SocialType;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private static final String STATE = "state";
	private static final String USER_ID_OPT = "_userId:";

	private final OAuthSeperator oAuthSeperator;
	private final UserRepository userRepository;

	@Value("${oauth2.temp-user.prefix}")
	private String tempUserPrefix;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		String userId = extractUserIdFromState();

		boolean isAccountLinking = false;

		if (userId != null) {
			isAccountLinking = true;
		}

		OAuth2Response oAuth2Response = oAuthSeperator.createResponse(registrationId, oAuth2User.getAttributes());

		Optional<User> socialLinkedUser = userRepository.findBySocialId(oAuth2Response.getSocialId());
		if (socialLinkedUser.isPresent()) {
			return handleExistingUser(socialLinkedUser.get(), oAuth2User, registrationId,
				oAuth2Response.getSocialType());
		}

		if (isAccountLinking) {
			return handleAccountLinking(userId, oAuth2Response, oAuth2User, registrationId,
				oAuth2Response.getSocialType());
		} else {
			return handleNewUser(oAuth2Response, oAuth2User, registrationId, oAuth2Response.getSocialType());
		}
	}

	private String extractUserIdFromState() {
		ServletRequestAttributes attrs = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if (attrs != null) {
			HttpServletRequest request = attrs.getRequest();
			String state = request.getParameter(STATE);

			if (state != null && state.contains(USER_ID_OPT)) {
				String[] parts = state.split(USER_ID_OPT);
				if (parts.length > 1) {
					String userId = parts[1];
					return userId;
				}
			}
		}
		return null;
	}

	private OAuth2User handleExistingUser(User user, OAuth2User oAuth2User, String registrationId,
		SocialType socialType) {
		return new CustomUserDetails(user, oAuth2User.getAttributes(), registrationId, socialType);
	}

	private OAuth2User handleAccountLinking(String userId, OAuth2Response oAuth2Response,
		OAuth2User oAuth2User, String registrationId, SocialType socialType) {

		validateUserId(userId);

		User existingUser = userRepository.findById(Long.parseLong(userId))
			.orElseThrow(() -> new CustomOAuth2Exception(AuthErrorCode.USER_NOT_FOUND));

		existingUser.updateSocialInfo(oAuth2Response.getSocialId(), SocialType.getSocialType(registrationId));
		User savedUser = userRepository.save(existingUser);

		return new CustomUserDetails(savedUser, oAuth2User.getAttributes(), registrationId, socialType);
	}

	private OAuth2User handleNewUser(OAuth2Response oAuth2Response, OAuth2User oAuth2User, String registrationId,
		SocialType socialType) {

		User tempUser = User.builder()
			.loginId(tempUserPrefix + generateRandomString())
			.email(oAuth2Response.getEmail())
			.socialId(oAuth2Response.getSocialId())
			.socialType(socialType)
			.build();

		return new CustomUserDetails(tempUser, oAuth2User.getAttributes(), registrationId, socialType);
	}

	private void validateUserId(String userId) {
		try {
			Long.parseLong(userId);
		} catch (NumberFormatException e) {
			throw new CustomOAuth2Exception(CommonErrorCode.INVALID_ARGUMENT);
		}
	}
}