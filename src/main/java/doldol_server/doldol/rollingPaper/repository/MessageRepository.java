package doldol_server.doldol.rollingPaper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import doldol_server.doldol.rollingPaper.entity.Message;
import doldol_server.doldol.rollingPaper.repository.custom.MessageRepositoryCustom;

public interface MessageRepository extends JpaRepository<Message, Long>, MessageRepositoryCustom {
}
