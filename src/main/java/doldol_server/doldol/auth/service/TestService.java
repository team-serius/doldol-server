package doldol_server.doldol.auth.service;

import java.util.List;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.stereotype.Service;

import doldol_server.doldol.rollingPaper.entity.Message;
import doldol_server.doldol.rollingPaper.repository.MessageRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

	private final MessageRepository messageRepository;
	private final StringEncryptor encryptor;

	@Transactional
	public void updateAll() {
		log.info("🚀 전체 데이터 암호화 시작");

		encryptMessageData();

		log.info("✅ 전체 데이터 암호화 완료");
	}


	@Transactional
	public void encryptMessageData() {
		log.info("💬 Message 테이블 암호화 시작");

		List<Message> messages = messageRepository.findAll();
		log.info("총 {} 개의 메시지 데이터를 암호화합니다.", messages.size());

		for (Message message : messages) {
			try {
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