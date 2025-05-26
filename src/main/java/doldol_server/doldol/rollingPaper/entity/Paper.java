package doldol_server.doldol.rollingPaper.entity;

import java.time.LocalDateTime;

import doldol_server.doldol.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Paper extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "paper_id")
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "open_date", nullable = false)
	private LocalDateTime openDate;

	@Column(name = "link", nullable = false, unique = true)
	private String link;

	@Column(name = "participants_count")
	private Long participantsCount;

	@Column(name = "message_count")
	private Long messageCount;

	@Column(name = "is_deleted")
	private boolean isDeleted = false;
}
