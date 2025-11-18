package doldol_server.doldol.invite.entity;

import doldol_server.doldol.common.entity.BaseEntity;
import doldol_server.doldol.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "invite")
@Entity
public class Invite extends BaseEntity {

    private static final int TITLE_MAX_LENGTH = 40;
    private static final int LOCATION_MAX_LENGTH = 120;
    private static final int SENDER_MAX_LENGTH = 60;
    private static final int CODE_MAX_LENGTH = 36;
    private static final int THEME_MAX_LENGTH = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inviteId;

    @Column(nullable = false)
    private LocalDateTime eventDateTime;

    @Column(nullable = false, length = LOCATION_MAX_LENGTH)
    private String location;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false, length = TITLE_MAX_LENGTH)
    private String title;

    @Column(nullable = false, length = SENDER_MAX_LENGTH)
    private String sender;

    @Column(nullable = false, unique = true, length = CODE_MAX_LENGTH)
    private String inviteCode;

    @Column(length = THEME_MAX_LENGTH)
    private String theme;

    @Column(name = "font_style", nullable = false)
    private String fontStyle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "invite", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<InviteComment> comments = new ArrayList<>();

    @Builder
    private Invite(LocalDateTime eventDateTime, String location, String content, String title, String sender,
                   String inviteCode, String theme, String fontStyle, User user) {
        this.eventDateTime = eventDateTime;
        this.location = location;
        this.content = content;
        this.title = title;
        this.sender = sender;
        this.inviteCode = inviteCode;
        this.theme = theme;
        this.fontStyle = fontStyle;
        this.user = user;
    }

    public void addComment(InviteComment comment) {
        comments.add(comment);
        comment.assignInvite(this);
    }
}

