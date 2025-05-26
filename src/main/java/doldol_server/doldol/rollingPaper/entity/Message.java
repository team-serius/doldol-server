package doldol_server.doldol.rollingPaper.entity;

import doldol_server.doldol.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "paper_id", nullable = false)
	private Paper paper;

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
}
