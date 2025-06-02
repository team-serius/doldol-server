package doldol_server.doldol.rollingPaper.repository.custom;

import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import doldol_server.doldol.rollingPaper.dto.response.ParticipantResponse;
import doldol_server.doldol.rollingPaper.entity.QParticipant;
import doldol_server.doldol.user.entity.QUser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ParticipantRepositoryImpl implements ParticipantRepositoryCustom{

	private final JPAQueryFactory queryFactory;

	@Override
	public boolean existsByUserAndPaper(Long userId, Long paperId) {
		QParticipant participant = QParticipant.participant;

		return queryFactory
			.selectOne()
			.from(participant)
			.where(
				participant.user.id.eq(userId),
				participant.paper.id.eq(paperId)
			)
			.fetchFirst() != null;
	}

	@Override
	public List<ParticipantResponse> findAllByPaperAndUserExceptMe(Long paperId, Long userId) {
		QParticipant participant = QParticipant.participant;
		QUser user = QUser.user;

		return queryFactory
			.select(Projections.constructor(
				ParticipantResponse.class,
				participant.id,
				user.id,
				user.name
			))
			.from(participant)
			.join(participant.user, user)
			.where(
				participant.paper.id.eq(paperId),
				participant.user.id.ne(userId)
			)
			.orderBy(user.name.asc())
			.fetch();
	}
}
