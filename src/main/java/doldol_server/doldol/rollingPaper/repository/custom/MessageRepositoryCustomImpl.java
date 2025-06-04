package doldol_server.doldol.rollingPaper.repository.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;

import doldol_server.doldol.rollingPaper.entity.Message;
import doldol_server.doldol.rollingPaper.entity.QMessage;
import doldol_server.doldol.user.entity.QUser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MessageRepositoryCustomImpl implements MessageRepositoryCustom{

	private final JPAQueryFactory queryFactory;

	@Override
	public Message getMessage(Long messageId, Long userId) {
		QMessage message = QMessage.message;
		QUser fromUser = new QUser("fromUser");
		QUser toUser = new QUser("toUser");

		return queryFactory
			.selectFrom(message)
			.join(message.from, fromUser).fetchJoin()
			.join(message.to, toUser).fetchJoin()
			.where(
				message.id.eq(messageId)
					.and(
						message.from.id.eq(userId)
							.or(message.to.id.eq(userId))
					)
			)
			.fetchOne();
	}
}
