package doldol_server.doldol.invite.entity;

import doldol_server.doldol.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "invite_comment")
@Entity
public class InviteComment extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invite_id", nullable = false)
    private Invite invite;

    private String author;

    private String content;

    @Builder
    private InviteComment(String author, String content) {
        this.author = author;
        this.content = content;
    }

    void assignInvite(Invite invite) {
        this.invite = invite;
    }
}

