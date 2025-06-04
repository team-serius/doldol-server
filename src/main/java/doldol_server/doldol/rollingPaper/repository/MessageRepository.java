package doldol_server.doldol.rollingPaper.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import doldol_server.doldol.rollingPaper.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {
}
