package doldol_server.doldol.rollingPaper.entity;

import doldol_server.doldol.user.User;
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
@NoArgsConstructor
@Getter
public class Participant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "paper_id")
	private Paper paper;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "meesage_id")
	private Message message;

	@Column(name = "is_master", nullable = false)
	private boolean isMaster;
}
