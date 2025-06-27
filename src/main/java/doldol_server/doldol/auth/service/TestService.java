package doldol_server.doldol.auth.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import doldol_server.doldol.auth.util.EncryptionUtil;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

	private final EncryptionUtil encryptionUtil;
	private final UserRepository userRepository;

	public Map<String, String> test(String email) {
		String encrypt = encryptionUtil.encrypt(email);
		log.info("email: {}", encrypt);
		User byEmail = userRepository.findByEmail(encrypt);
		Map<String, String> result = new HashMap<>();
		result.put("email", encryptionUtil.decrypt(byEmail.getEmail()));

		return result;
	}
}
