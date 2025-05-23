package doldol_server.doldol.rollingPaper.entity;

import doldol_server.doldol.user.User;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "participant")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Participant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 참여자 아이디
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	// 롤링페이퍼 아이디
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "paper_id")
	private Paper paper;

	// 참여자가 쓴 메시지 아이디
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "meesage_id")
	private Message message;

	// 방장 여부
	@Column(name = "is_master", nullable = false)
	private boolean isMaster;
}
