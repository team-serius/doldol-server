package doldol_server.doldol.rollingPaper.entity;

import java.time.LocalDate;

import doldol_server.doldol.common.entity.BaseEntity;
import doldol_server.doldol.rollingPaper.dto.request.PaperType;
import doldol_server.doldol.user.entity.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
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

	@Column(name = "description")
	private String description;

	@Column(name = "open_date", nullable = false)
	private LocalDate openDate;

	@Column(name = "invitation_code", nullable = false)
	private String invitationCode;

	@Column(name = "participants_count")
	private int participantsCount = 0;

	@Column(name = "message_count")
	private int messageCount = 0;

	@Column(name = "is_deleted")
	private boolean isDeleted = false;

	@Enumerated(value = EnumType.STRING)
	@Column(name = "type")
	private PaperType paperType;

	@Builder
	public Paper(String name, String description, LocalDate openDate, String invitationCode, PaperType paperType) {
		this.name = name;
		this.description = description;
		this.openDate = openDate;
		this.invitationCode = invitationCode;
		this.paperType = paperType;
	}

	public void addParticipant() {
		participantsCount++;
	}

	public void addMessage() {
		messageCount++;
	}

	public void deleteMessage() {
		messageCount--;
	}
}
