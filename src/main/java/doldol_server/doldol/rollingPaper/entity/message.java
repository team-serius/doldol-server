package doldol_server.doldol.rollingPaper.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "MESSAGE")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class message {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "message_id")
	private long id;

	@ManyToOne
	@JoinColumn(name = "paper_id", nullable = false)
	private Paper paper;
	
	@OneToOne
	@JoinColumn(name = "participant_id", nullable = false)
	private Participant participant;

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

	@Column(name = "created_date", nullable = false)
	private LocalDateTime createDate;

	@Column(name = "modified_date")
	private LocalDateTime modifiedDate;
}
