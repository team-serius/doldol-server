package doldol_server.doldol.auth.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import doldol_server.doldol.auth.util.EncryptionUtil;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TestService {

	private final EncryptionUtil encryptionUtil;
	private final UserRepository userRepository;

	public Map<String, String> test(String email) {
		User byEmail = userRepository.findByEmail(encryptionUtil.encrypt(email));
		Map<String, String> result = new HashMap<>();
		result.put("email", encryptionUtil.decrypt(byEmail.getEmail()));

		return result;
	}
}
