package doldol_server.doldol.invite.repository.custom;

import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.invite.dto.response.InviteCommentResponse;

import java.util.List;

public interface InviteCommentRepositoryCustom {

    List<InviteCommentResponse> findCommentsByInviteCode(String inviteCode, CursorPageRequest request);
}

