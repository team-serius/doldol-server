package doldol_server.doldol.auth.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import doldol_server.doldol.common.exception.CustomException;
import doldol_server.doldol.common.exception.errorCode.MailErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender emailSender;
	private final SpringTemplateEngine templateEngine;

	@Async
	public void sendEmailVerificationCode(String email, String verificationCode) {
		if (!StringUtils.hasText(email)) {
			throw new CustomException(MailErrorCode.MISSING_EMAIL);
		}
		try {
			sendToEmailCode(email, verificationCode);
		} catch (MessagingException e) {
			throw new CustomException(MailErrorCode.EMAIL_SENDING_ERROR);
		}
	}

	@Async
	public void sendEmailTempPassword(String email, String password) {
		if (!StringUtils.hasText(email)) {
			throw new CustomException(MailErrorCode.MISSING_EMAIL);
		}
		try {
			sendToEmailTempPassword(email, password);
		} catch (MessagingException e) {
			throw new CustomException(MailErrorCode.EMAIL_SENDING_ERROR);
		}
	}

	private void sendToEmailCode(String to, String verificationCode) throws MessagingException {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		helper.setTo(to);
		helper.setSubject("인증 코드 안내");

		Context context = new Context();
		context.setVariable("code", verificationCode);

		String htmlContent = templateEngine.process("verificationCode", context);
		helper.setText(htmlContent, true);

		helper.addInline("logo", new ClassPathResource("static/images/logo.png"));

		emailSender.send(message);
	}

	private void sendToEmailTempPassword(String to, String password) throws MessagingException {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

		helper.setTo(to);
		helper.setSubject("임시 비밀번호 안내");

		Context context = new Context();
		context.setVariable("temporaryPassword", password);

		String htmlContent = templateEngine.process("tempPassword", context);
		helper.setText(htmlContent, true);

		helper.addInline("logo", new ClassPathResource("static/images/logo.png"));

		emailSender.send(message);
	}
}