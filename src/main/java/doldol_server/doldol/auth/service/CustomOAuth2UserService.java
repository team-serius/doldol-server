package doldol_server.doldol.auth.service;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.auth.dto.OAuth2Response;
import doldol_server.doldol.common.exception.errorCode.AuthErrorCode;
import doldol_server.doldol.common.exception.errorCode.CommonErrorCode;
import doldol_server.doldol.common.exception.CustomOAuth2Exception;
import doldol_server.doldol.user.entity.SocialType;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private static final String STATE = "state";
	private static final String USER_ID_OPT = "_userId:";

	private final OAuthSeperator oAuthSeperator;
	private final UserRepository userRepository;

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

		if (socialLinkedUser.isPresent() && !socialLinkedUser.get().isDeleted()) {
			return handleExistingUser(socialLinkedUser.get(), oAuth2User, oAuth2Response.getSocialId());
		}

		else if (socialLinkedUser.isPresent() && socialLinkedUser.get().isDeleted()) {
			log.warn("탈퇴한 계정으로 소셜 로그인 시도: socialId={}, socialType={}",
				oAuth2Response.getSocialId(), registrationId);
			throw new CustomOAuth2Exception(AuthErrorCode.ALREADY_WITHDRAWN);
		}

		if (isAccountLinking) {
			return handleAccountLinking(userId, oAuth2Response, oAuth2User, registrationId);
		} else {
			return handleNewUser(oAuth2User, oAuth2Response.getSocialId());
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

	private OAuth2User handleExistingUser(User user, OAuth2User oAuth2User, String registrationId) {
		return new CustomUserDetails(user.getId(), oAuth2User.getAttributes(), registrationId);
	}

	private OAuth2User handleAccountLinking(String userId, OAuth2Response oAuth2Response,
		OAuth2User oAuth2User, String registrationId) {

		validateUserId(userId);

		User existingUser = userRepository.findById(Long.parseLong(userId))
			.orElseThrow(() -> new CustomOAuth2Exception(AuthErrorCode.USER_NOT_FOUND));

		existingUser.updateSocialInfo(oAuth2Response.getSocialId(), SocialType.getSocialType(registrationId));
		userRepository.save(existingUser);

		return new CustomUserDetails(Long.parseLong(userId), oAuth2User.getAttributes(), oAuth2Response.getSocialId());
	}

	private OAuth2User handleNewUser(OAuth2User oAuth2User, String socialId) {
		return new CustomUserDetails(oAuth2User.getAttributes(), socialId);
	}

	private void validateUserId(String userId) {
		try {
			Long.parseLong(userId);
		} catch (NumberFormatException e) {
			throw new CustomOAuth2Exception(CommonErrorCode.INVALID_ARGUMENT);
		}
	}
}
