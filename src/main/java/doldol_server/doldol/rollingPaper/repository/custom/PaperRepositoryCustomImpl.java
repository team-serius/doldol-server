package doldol_server.doldol.rollingPaper.repository.custom;

import java.util.List;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.common.request.SortDirection;
import doldol_server.doldol.rollingPaper.dto.response.PaperResponse;
import doldol_server.doldol.rollingPaper.entity.Paper;
import doldol_server.doldol.rollingPaper.entity.QPaper;
import doldol_server.doldol.rollingPaper.entity.QParticipant;
import doldol_server.doldol.user.entity.QUser;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaperRepositoryCustomImpl implements PaperRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public List<PaperResponse> getPapers(
		Long userId,
		CursorPageRequest request,
		SortDirection sortDirection
	) {
		QPaper paper = QPaper.paper;
		QParticipant participant = QParticipant.participant;

		BooleanExpression cursorCondition = null;
		if (request.cursorId() != null) {
			cursorCondition = (sortDirection == SortDirection.LATEST)
				? paper.id.lt(request.cursorId())
				: paper.id.gt(request.cursorId());
		}

		OrderSpecifier<Long> orderSpecifier = (sortDirection == SortDirection.LATEST)
			? paper.id.desc()
			: paper.id.asc();

		List<Paper> papers = queryFactory
			.selectFrom(paper)
			.join(participant).on(participant.paper.eq(paper))
			.where(
				participant.user.id.eq(userId),
				cursorCondition
			)
			.orderBy(orderSpecifier)
			.limit(request.size() + 1L)
			.fetch();

		return papers.stream().map(PaperResponse::of).toList();
	}

	@Override
	public PaperResponse findPaperWithUserByInvitationCode(String invitationCode) {
		QPaper paper = QPaper.paper;
		QUser user = QUser.user;
		QParticipant participant = QParticipant.participant;

		return queryFactory
			.select(Projections.constructor(PaperResponse.class,
				paper.id,
				paper.name,
				paper.description,
				paper.participantsCount,
				paper.messageCount,
				paper.openDate,
				paper.paperType,
				user.id
			))
			.from(paper)
			.leftJoin(participant).on(participant.paper.eq(paper).and(participant.isMaster.isTrue()))
			.leftJoin(user).on(participant.user.eq(user))
			.where(paper.invitationCode.eq(invitationCode)
				.and(paper.isDeleted.isFalse()))
			.fetchOne();
	}
}
