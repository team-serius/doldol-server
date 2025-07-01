package doldol_server.doldol.order.entity;

import java.util.ArrayList;
import java.util.List;

import doldol_server.doldol.common.entity.BaseEntity;
import doldol_server.doldol.rollingPaper.entity.Paper;
import doldol_server.doldol.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "paper_id")
	private Paper paper;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderMessage> orderMessages = new ArrayList<>();

	@Column(name = "count")
	private Long count;

	@Column(name = "is_deleted")
	private boolean isDeleted = false;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private OrderStatus orderStatus = OrderStatus.READY;

	@Builder
	public Order(List<OrderMessage> orderMessages, Long count, boolean isDeleted,
		OrderStatus orderStatus, Paper paper, User user) {
		this.orderMessages = orderMessages != null ? orderMessages : new ArrayList<>();
		this.count = count;
		this.isDeleted = isDeleted;
		this.orderStatus = orderStatus;
		this.paper = paper;
		this.user = user;
	}

	public void addOrderMessage(OrderMessage orderMessage) {
		this.orderMessages.add(orderMessage);
	}
}