package doldol_server.doldol.invite.repository;

import doldol_server.doldol.invite.entity.InviteComment;
import doldol_server.doldol.invite.repository.custom.InviteCommentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteCommentRepository extends JpaRepository<InviteComment, Long>, InviteCommentRepositoryCustom {
}

