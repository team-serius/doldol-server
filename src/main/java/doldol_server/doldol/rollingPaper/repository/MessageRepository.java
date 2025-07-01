package doldol_server.doldol.rollingPaper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import doldol_server.doldol.rollingPaper.entity.Message;
import doldol_server.doldol.rollingPaper.entity.Paper;
import doldol_server.doldol.rollingPaper.repository.custom.MessageRepositoryCustom;
import doldol_server.doldol.user.entity.User;

public interface MessageRepository extends JpaRepository<Message, Long>, MessageRepositoryCustom {
	long countByPaperAndFromAndToAndIsDeletedFalse(Paper paper, User fromUser, User toUser);
}
