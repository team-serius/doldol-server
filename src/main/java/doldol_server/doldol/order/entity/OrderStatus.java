package doldol_server.doldol.order.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {
	READY("준비중"),
	SHIPPING("배송중"),
	DONE("배송 완료")
	;

	private final String displayName;
}
