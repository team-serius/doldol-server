package doldol_server.doldol.rollingPaper.entity;

import doldol_server.doldol.common.entity.BaseEntity;
import doldol_server.doldol.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "message_id")
	private long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "content", nullable = false)
	private String content;

	@Column(name = "font_style", nullable = false)
	private String fontStyle;

	@Column(name = "background_color", nullable = false)
	private String backgroundColor;

	@Column(name = "is_deleted")
	private boolean isDeleted = false;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_user_id", referencedColumnName = "user_id")
	private User from;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_user_id", referencedColumnName = "user_id")
	private User to;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "paper_id")
	private Paper paper;

	@Builder
	public Message(String backgroundColor, String content, String fontStyle, User from, boolean isDeleted, String name,
		Paper paper, User to) {
		this.backgroundColor = backgroundColor;
		this.content = content;
		this.fontStyle = fontStyle;
		this.from = from;
		this.isDeleted = isDeleted;
		this.name = name;
		this.paper = paper;
		this.to = to;
	}

	public void updateDeleteStatus() {
		this.isDeleted = true;
	}

	public void updateContent(String content) {
		this.content = content;
	}

	public void updateFontStyle(String fontStyle) {
		this.fontStyle = fontStyle;
	}

	public void updateBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public void updateName(String fromName) {
		this.name = fromName;
	}
}
