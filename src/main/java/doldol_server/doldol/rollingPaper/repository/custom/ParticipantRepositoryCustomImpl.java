package doldol_server.doldol.rollingPaper.repository.custom;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import doldol_server.doldol.rollingPaper.dto.request.GetParticipantsRequest;
import doldol_server.doldol.rollingPaper.dto.response.ParticipantResponse;
import doldol_server.doldol.rollingPaper.entity.QParticipant;
import doldol_server.doldol.user.entity.QUser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParticipantRepositoryCustomImpl implements ParticipantRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<ParticipantResponse> getParticipants(Long paperId, GetParticipantsRequest request) {
		QParticipant participant = QParticipant.participant;
		QUser user = QUser.user;

		BooleanExpression cursorCondition = null;
		if (request.cursorName() != null && request.cursorId() != null) {
			cursorCondition = user.name.gt(request.cursorName())
				.or(user.name.eq(request.cursorName())
					.and(user.id.gt(request.cursorId())));
		}

		return queryFactory
			.select(Projections.constructor(
				ParticipantResponse.class,
				participant.id,
				user.id,
				Expressions.stringTemplate(
					"concat({0}, '(', substring({1}, -4), ')')",
					user.name, user.phone
				)
			))
			.from(participant)
			.join(participant.user, user)
			.where(
				participant.paper.id.eq(paperId),
				cursorCondition
			)
			.orderBy(user.name.asc(), user.id.asc())
			.limit(request.size() + 1L)
			.fetch();
	}
}
