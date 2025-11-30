package doldol_server.doldol.invite.repository;

import doldol_server.doldol.invite.entity.Invite;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InviteRepository extends JpaRepository<Invite, Long> {

    @EntityGraph(attributePaths = {"comments"})
    Optional<Invite> findWithCommentsByInviteId(Long inviteId);
}

