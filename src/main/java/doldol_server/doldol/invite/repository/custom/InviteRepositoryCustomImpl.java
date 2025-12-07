package doldol_server.doldol.invite.repository.custom;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.invite.dto.response.InviteResponse;
import doldol_server.doldol.invite.entity.Invite;
import doldol_server.doldol.invite.entity.QInvite;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class InviteRepositoryCustomImpl implements InviteRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<InviteResponse> findInvitesByUserId(Long userId, CursorPageRequest request) {
        QInvite invite = QInvite.invite;

        BooleanExpression cursorCondition = null;
        if (request.cursorId() != null) {
            cursorCondition = invite.inviteId.lt(request.cursorId());
        }

        List<Invite> invites = queryFactory
            .selectFrom(invite)
            .where(
                invite.user.id.eq(userId),
                cursorCondition
            )
            .orderBy(invite.inviteId.desc())
            .limit(request.size() + 1L)
            .fetch();

        return invites.stream()
            .map(InviteResponse::from)
            .toList();
    }
}

