package doldol_server.doldol.complaint.entity;

import doldol_server.doldol.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "complaint")
public class Complaint {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 신고자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	// 관리자
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User admin;

	// 롤링페이퍼 id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "paper_id")
	private Paper paper;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "message_id")
	private Message message;

	// 내용
	@Column(name = "content", nullable = false)
	private String content;

	// 답변
	@Column(name = "answer")
	private String answer;

	// 해결 유무
	@Column(name = "is_solved", nullable = false)
	private boolean isSolved;
}
