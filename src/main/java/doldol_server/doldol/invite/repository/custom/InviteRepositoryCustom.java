package doldol_server.doldol.invite.repository.custom;

import doldol_server.doldol.common.request.CursorPageRequest;
import doldol_server.doldol.invite.dto.response.InviteResponse;

import java.util.List;

public interface InviteRepositoryCustom {

    List<InviteResponse> findInvitesByUserId(Long userId, CursorPageRequest request);
}

