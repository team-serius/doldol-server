package doldol_server.doldol.report.entity;

import doldol_server.doldol.common.entity.BaseEntity;
import doldol_server.doldol.rollingPaper.entity.Message;
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

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "report_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "message_id")
	private Message message;

	@Column(name = "answer")
	private String answer;

	@Column(name = "is_solved", nullable = false)
	private boolean isSolved;

	@Builder
	public Report(Message message, boolean isSolved) {
		this.message = message;
		this.isSolved = isSolved;
	}
}
