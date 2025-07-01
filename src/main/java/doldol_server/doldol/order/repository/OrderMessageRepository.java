package doldol_server.doldol.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import doldol_server.doldol.order.entity.OrderMessage;

public interface OrderMessageRepository extends JpaRepository<OrderMessage, Long> {
}
