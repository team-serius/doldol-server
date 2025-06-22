package doldol_server.doldol.rollingPaper.repository.custom;

import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.rollingPaper.dto.response.MessageResponse;
import doldol_server.doldol.rollingPaper.entity.Message;
import doldol_server.doldol.rollingPaper.entity.MessageType;
import doldol_server.doldol.rollingPaper.entity.QMessage;
import doldol_server.doldol.user.entity.QUser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageRepositoryCustomImpl implements MessageRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	public MessageResponse getMessage(Long messageId, Long userId) {
		QMessage message = QMessage.message;

		Message result = queryFactory
			.selectFrom(message)
			.join(message.from).fetchJoin()
			.where(
				message.id.eq(messageId),
				message.from.id.eq(userId),
				message.isDeleted.eq(false)
			)
			.fetchOne();

		if (result == null) {
			return null;
		}

		MessageType messageType = MessageType.SEND;

		return MessageResponse.of(result, result.getFrom().getId(), messageType);
	}

	@Override
	public Message getMessageEntity(Long messageId, Long userId) {
		QMessage message = QMessage.message;
		QUser fromUser = new QUser("fromUser");

		return queryFactory
			.selectFrom(message)
			.join(message.from, fromUser)
			.where(
				message.id.eq(messageId),
				message.from.id.eq(userId),
				message.isDeleted.eq(false)
			)
			.fetchOne();
	}

	@Override
	public List<MessageResponse> getReceivedMessages(Long paperId, Long userId, CursorPageRequest request) {
		QMessage message = QMessage.message;

		BooleanExpression cursorCondition = null;
		if (request.cursorId() != null) {
			cursorCondition = message.id.lt(request.cursorId());
		}

		List<Message> messages = queryFactory
			.selectFrom(message)
			.join(message.to).fetchJoin()
			.where(
				message.paper.id.eq(paperId),
				message.to.id.eq(userId),
				message.isDeleted.eq(false),
				cursorCondition
			)
			.orderBy(message.id.desc())
			.limit(request.size() + 1L)
			.fetch();

		return messages.stream()
			.map(msg -> MessageResponse.of(msg, msg.getFrom().getId(), MessageType.RECEIVE))
			.toList();
	}

	@Override
	public List<MessageResponse> getSentMessages(Long paperId, Long userId, CursorPageRequest request) {
		QMessage message = QMessage.message;

		BooleanExpression cursorCondition = null;
		if (request.cursorId() != null) {
			cursorCondition = message.id.lt(request.cursorId());
		}

		List<Message> messages = queryFactory
			.selectFrom(message)
			.join(message.from).fetchJoin()
			.where(
				message.paper.id.eq(paperId),
				message.from.id.eq(userId),
				message.isDeleted.eq(false),
				cursorCondition
			)
			.orderBy(message.id.desc())
			.limit(request.size() + 1L)
			.fetch();

		return messages.stream()
			.map(msg -> MessageResponse.of(msg, msg.getFrom().getId(), MessageType.SEND))
			.toList();
	}

	@Override
	public Long getReceivedMessagesCount(Long paperId, Long userId) {
		QMessage message = QMessage.message;

		return queryFactory
			.select(message.count())
			.from(message)
			.where(
				message.paper.id.eq(paperId),
				message.to.id.eq(userId),
				message.isDeleted.eq(false)
			)
			.fetchOne();
	}

	@Override
	public Long getSentdMessagesCount(Long paperId, Long userId) {
		QMessage message = QMessage.message;

		return queryFactory
			.select(message.count())
			.from(message)
			.where(
				message.paper.id.eq(paperId),
				message.from.id.eq(userId),
				message.isDeleted.eq(false)
			)
			.fetchOne();
	}
}
