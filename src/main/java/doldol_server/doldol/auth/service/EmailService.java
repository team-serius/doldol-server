package doldol_server.doldol.auth.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.MailErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender emailSender;
	private final SpringTemplateEngine templateEngine;
	private final RedisTemplate<String, String> redisTemplate;

	@Async
	public void sendEmailVerificationCode(String email, String verificationCode) {
		if (!StringUtils.hasText(verificationCode)) {
			throw new CustomException(MailErrorCode.MISSING_EMAIL);
		}
		try {
			sendToEmail(email, verificationCode);
		} catch (MessagingException e) {
			throw new CustomException(MailErrorCode.EMAIL_SENDING_ERROR);
		}
	}

	private void sendToEmail(String to, String verificationCode) throws MessagingException {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		helper.setTo(to);
		helper.setSubject("인증 코드 안내");

		Context context = new Context();
		context.setVariable("code", verificationCode);

		String htmlContent = templateEngine.process("mail", context);
		helper.setText(htmlContent, true);

		emailSender.send(message);
	}
}