package doldol_server.doldol.invite.repository;

import doldol_server.doldol.invite.entity.Invite;
import doldol_server.doldol.invite.repository.custom.InviteRepositoryCustom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteRepository extends JpaRepository<Invite, Long>, InviteRepositoryCustom {

    @EntityGraph(attributePaths = {"comments"})
    Optional<Invite> findWithCommentsByInviteCode(String inviteCode);

    Optional<Invite> findByInviteCode(String inviteCode);
}

