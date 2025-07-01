package doldol_server.doldol.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import doldol_server.doldol.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
