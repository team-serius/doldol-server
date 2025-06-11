package doldol_server.doldol.rollingPaper.repository.custom;

import java.util.List;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.rollingPaper.entity.Participant;
import doldol_server.doldol.rollingPaper.entity.QParticipant;
import doldol_server.doldol.user.entity.QUser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParticipantRepositoryCustomImpl implements ParticipantRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<Participant> getParticipants(Long paperId, CursorPageRequest request) {
		QParticipant participant = QParticipant.participant;
		QUser user = QUser.user;

		BooleanExpression cursorCondition = null;
		if (request.cursorId() != null) {
			cursorCondition = participant.id.gt(request.cursorId());
		}

		return queryFactory
			.selectFrom(participant)
			.join(participant.user, user).fetchJoin()
			.where(
				participant.paper.id.eq(paperId),
				cursorCondition
			)
			.orderBy(
				participant.user.name.asc(),
				participant.user.id.asc())
			.limit(request.size() + 1L)
			.fetch();
	}
}
