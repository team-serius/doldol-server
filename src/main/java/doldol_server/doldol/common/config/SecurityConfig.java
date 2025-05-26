package doldol_server.doldol.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;

import doldol_server.doldol.auth.filter.CustomLogoutFilter;
import doldol_server.doldol.auth.filter.CustomUserLoginFilter;
import doldol_server.doldol.auth.filter.JwtAuthenticationFilter;
import doldol_server.doldol.auth.handler.CustomAccessDeniedHandler;
import doldol_server.doldol.auth.handler.CustomAuthenticationEntryPoint;
import doldol_server.doldol.auth.jwt.TokenProvider;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private static final String[] WHITELIST = {
		"/auth/login",
		"/swagger-ui/**",
		"/v3/api-docs/**",
		"/swagger-resources/**",
		"/webjars/**",
		"/auth/join",
		"/papers/invite"
	};

	private final TokenProvider tokenProvider;
	private final ObjectMapper objectMapper;
	private final Validator validator;
	private final CorsConfigurationSource corsConfigurationSource;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager)
		throws Exception {
		http
			.cors(cors -> cors.configurationSource(corsConfigurationSource))
			.csrf(AbstractHttpConfigurer::disable)
			.formLogin(AbstractHttpConfigurer::disable)
			.httpBasic(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(WHITELIST).permitAll()
				.anyRequest().authenticated())
			.addFilterAt(new CustomUserLoginFilter(authenticationManager, tokenProvider, objectMapper, validator),
				UsernamePasswordAuthenticationFilter.class)
			.addFilterAfter(new JwtAuthenticationFilter(tokenProvider, WHITELIST, objectMapper),
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

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
