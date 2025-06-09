package doldol_server.doldol.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import doldol_server.doldol.auth.filter.CustomLogoutFilter;
import doldol_server.doldol.auth.filter.CustomUserLoginFilter;
import doldol_server.doldol.auth.filter.JwtAuthenticationFilter;
import doldol_server.doldol.auth.handler.CustomAccessDeniedHandler;
import doldol_server.doldol.auth.handler.CustomAuthenticationEntryPoint;
import doldol_server.doldol.auth.handler.CustomOAuth2FailureHandler;
import doldol_server.doldol.auth.handler.CustomOAuth2SuccessHandler;
import doldol_server.doldol.auth.jwt.TokenProvider;
import doldol_server.doldol.auth.resolver.CustomOAuth2ParameterResolver;
import doldol_server.doldol.auth.service.CustomOAuth2UserService;
import doldol_server.doldol.user.entity.Role;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private static final String[] WHITELIST = {
		"/auth/**",
		"/swagger-ui/**",
		"/v3/api-docs/**",
		"/swagger-resources/**",
		"/webjars/**",
		"/actuator/**",
		"/papers/invite"
	};

	private static final String[] BLACKLIST = {
		"/auth/logout",
		"/auth/withdraw"
	};

	private final TokenProvider tokenProvider;
	private final ObjectMapper objectMapper;
	private final CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
	private final CustomOAuth2FailureHandler customOAuth2FailureHandler;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final CustomOAuth2ParameterResolver customOAuth2ParameterResolver;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager)
		throws Exception {
		http
			.cors(AbstractHttpConfigurer::disable)
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.oauth2Login(oauth2 -> oauth2
				.authorizationEndpoint(authorization -> authorization
					.authorizationRequestResolver(customOAuth2ParameterResolver)
				)
				.userInfoEndpoint(userInfo -> userInfo
					.userService(customOAuth2UserService)
				)
				.successHandler(customOAuth2SuccessHandler)
				.failureHandler(customOAuth2FailureHandler)
			)
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(WHITELIST).permitAll()
				.requestMatchers(BLACKLIST).authenticated()

				.requestMatchers(HttpMethod.POST, "/reports").permitAll()
				.requestMatchers("/auth/withdraw", "/reports/**").hasAuthority(Role.ADMIN.getRole())
				.anyRequest().authenticated())
			.addFilterAt(new CustomUserLoginFilter(authenticationManager, tokenProvider, objectMapper),
				UsernamePasswordAuthenticationFilter.class)
			.addFilterAfter(new JwtAuthenticationFilter(tokenProvider, WHITELIST, BLACKLIST, objectMapper),
				CustomUserLoginFilter.class)
			.addFilterBefore(new CustomLogoutFilter(tokenProvider, objectMapper), LogoutFilter.class)

			.exceptionHandling(exceptions -> exceptions
				.authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper))
				.accessDeniedHandler(new CustomAccessDeniedHandler(objectMapper)));

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
}
