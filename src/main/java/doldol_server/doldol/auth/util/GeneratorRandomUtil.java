package doldol_server.doldol.auth.util;

import java.security.SecureRandom;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneratorRandomUtil {

	private static final SecureRandom RANDOM = new SecureRandom();

	public static String generateRandomString() {

		StringBuilder sb = new StringBuilder(10);
		for (int i = 0; i < 10; i++) {
			char randomChar = (char) (RANDOM.nextInt(94) + 33);
			sb.append(randomChar);
		}
		return sb.toString();
	}

	public static String generateRandomNum() {
		int randomNumber = RANDOM.nextInt(1000000);
		return String.format("%06d", randomNumber);
	}
}