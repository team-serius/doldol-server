package doldol_server.doldol.common;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.fasterxml.jackson.databind.ObjectMapper;

import doldol_server.doldol.auth.dto.CustomUserDetails;
import doldol_server.doldol.common.config.TestSecurityConfig;

@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
public abstract class ControllerTest {

	@Autowired
	protected MockMvc mockMvc;

	@Autowired
	protected ObjectMapper objectMapper;

	protected String asJsonString(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected RequestPostProcessor mockUser(Long userId) {
		CustomUserDetails userDetails = mock(CustomUserDetails.class);
		when(userDetails.getUserId()).thenReturn(userId);

		Authentication auth = new UsernamePasswordAuthenticationToken(
			userDetails, null, List.of()
		);

		return authentication(auth);
	}
}