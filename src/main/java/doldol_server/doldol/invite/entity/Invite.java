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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inviteId;

    @Column(nullable = false)
    private LocalDateTime eventDateTime;

    @Column(nullable = false)
    private String location;

    @Column(name = "location_link")
    private String locationLink;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String sender;

    @Column(nullable = false, unique = true)
    private String inviteCode;

    private String theme;

    @Column(name = "font_style", nullable = false)
    private String fontStyle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "invite", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<InviteComment> comments = new ArrayList<>();

    @Builder
    private Invite(LocalDateTime eventDateTime, String location, String locationLink, String content, String title, String sender,
                   String inviteCode, String theme, String fontStyle, User user) {
        this.eventDateTime = eventDateTime;
        this.location = location;
        this.locationLink = locationLink;
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

    public void update(String title, LocalDateTime eventDateTime, String location, String locationLink,
                    String content, String sender, String theme, String fontStyle) {
        this.title = title;
        this.eventDateTime = eventDateTime;
        this.location = location;
        this.locationLink = locationLink;
        this.content = content;
        this.sender = sender;
        this.theme = theme;
        this.fontStyle = fontStyle;
    }
}

