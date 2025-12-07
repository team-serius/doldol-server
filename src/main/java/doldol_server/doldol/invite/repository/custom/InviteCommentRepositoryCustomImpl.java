package doldol_server.doldol.invite.repository.custom;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.invite.dto.response.InviteCommentResponse;
import doldol_server.doldol.invite.entity.InviteComment;
import doldol_server.doldol.invite.entity.QInvite;
import doldol_server.doldol.invite.entity.QInviteComment;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class InviteCommentRepositoryCustomImpl implements InviteCommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<InviteCommentResponse> findCommentsByInviteCode(String inviteCode, CursorPageRequest request) {
        QInviteComment comment = QInviteComment.inviteComment;
        QInvite invite = QInvite.invite;

        BooleanExpression cursorCondition = null;
        if (request.cursorId() != null) {
            cursorCondition = comment.commentId.lt(request.cursorId());
        }

        List<InviteComment> comments = queryFactory
            .selectFrom(comment)
            .join(comment.invite, invite)
            .where(
                invite.inviteCode.eq(inviteCode),
                cursorCondition
            )
            .orderBy(comment.commentId.desc())
            .limit(request.size() + 1L)
            .fetch();

        return comments.stream()
            .map(InviteCommentResponse::from)
            .toList();
    }
}

