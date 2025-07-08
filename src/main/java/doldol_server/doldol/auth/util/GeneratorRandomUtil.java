package doldol_server.doldol.auth.util;

import java.security.SecureRandom;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GeneratorRandomUtil {

	private static final SecureRandom RANDOM = new SecureRandom();

	public static String generateRandomString() {
		String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String lowercase = "abcdefghijklmnopqrstuvwxyz";
		String numbers = "0123456789";
		String specialChars = "$@!%*#?&";

		int length = 8 + RANDOM.nextInt(9);
		StringBuilder password = new StringBuilder(length);

		password.append(uppercase.charAt(RANDOM.nextInt(uppercase.length())));
		password.append(lowercase.charAt(RANDOM.nextInt(lowercase.length())));
		password.append(numbers.charAt(RANDOM.nextInt(numbers.length())));
		password.append(specialChars.charAt(RANDOM.nextInt(specialChars.length())));

		String allChars = uppercase + lowercase + numbers + specialChars;
		for (int i = 4; i < length; i++) {
			password.append(allChars.charAt(RANDOM.nextInt(allChars.length())));
		}

		char[] chars = password.toString().toCharArray();
		for (int i = chars.length - 1; i > 0; i--) {
			int j = RANDOM.nextInt(i + 1);
			char temp = chars[i];
			chars[i] = chars[j];
			chars[j] = temp;
		}

		return new String(chars);
	}

	public static String generateRandomNum() {
		int randomNumber = RANDOM.nextInt(1000000);
		return String.format("%06d", randomNumber);
	}
}