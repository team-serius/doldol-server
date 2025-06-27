package doldol_server.doldol.auth.service;

import java.util.List;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import doldol_server.doldol.rollingPaper.entity.Message;
import doldol_server.doldol.rollingPaper.repository.MessageRepository;
import doldol_server.doldol.user.entity.User;
import doldol_server.doldol.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

	private final UserRepository userRepository;
	private final MessageRepository messageRepository;
	private final StringEncryptor encryptor;

	@Transactional
	public void updateAll() {
		log.info("🚀 전체 데이터 암호화 시작");

		// 1. User 테이블 암호화
		encryptUserData();

		// 2. Message 테이블 암호화
		encryptMessageData();

		log.info("✅ 전체 데이터 암호화 완료");
	}

	@Transactional
	public void encryptUserData() {
		log.info("👤 User 테이블 암호화 시작");

		List<User> users = userRepository.findAll();
		log.info("총 {} 명의 사용자 데이터를 암호화합니다.", users.size());

		for (User user : users) {
			try {
				boolean needsUpdate = false;

				// loginId 암호화
				if (user.getLoginId() != null) {
					user.updateLoginId(encryptor.encrypt(user.getLoginId()));
					needsUpdate = true;
				}

				// phone 암호화
				if (user.getPhone() != null) {
					user.updatePhone(encryptor.encrypt(user.getPhone()));
					needsUpdate = true;
				}

				// email 암호화
				if (user.getEmail() != null) {
					user.updateEmail(encryptor.encrypt(user.getEmail()));
					needsUpdate = true;
				}

				// socialId 암호화
				if (user.getSocialId() != null) {
					user.updateSocialId(encryptor.encrypt(user.getSocialId()));
					needsUpdate = true;
				}

				if (needsUpdate) {
					userRepository.save(user);
					log.info("User ID {} 암호화 완료", user.getId());
				}

			} catch (Exception e) {
				log.error("User ID {} 암호화 실패: {}", user.getId(), e.getMessage());
			}
		}

		log.info("✅ User 테이블 암호화 완료: {} 명 처리", users.size());
	}

	@Transactional
	public void encryptMessageData() {
		log.info("💬 Message 테이블 암호화 시작");

		List<Message> messages = messageRepository.findAll();
		log.info("총 {} 개의 메시지 데이터를 암호화합니다.", messages.size());

		for (Message message : messages) {
			try {
				// content 암호화
				if (message.getContent() != null) {
					message.updateContent(encryptor.encrypt(message.getContent()));
					messageRepository.save(message);
					log.info("Message ID {} 암호화 완료", message.getId());
				}

			} catch (Exception e) {
				log.error("Message ID {} 암호화 실패: {}", message.getId(), e.getMessage());
			}
		}

		log.info("✅ Message 테이블 암호화 완료: {} 개 처리", messages.size());
	}

}