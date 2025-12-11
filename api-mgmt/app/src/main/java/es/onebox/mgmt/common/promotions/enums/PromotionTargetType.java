package es.onebox.mgmt.common.promotions.enums;

import java.util.stream.Stream;

public enum PromotionTargetType {
	ALL(0), RESTRICTED(1);

	private Integer id;

	private PromotionTargetType(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}

	public static PromotionTargetType fromId(Integer id) {
		return Stream.of(values()).filter(p -> p.id.equals(id)).findAny().orElse(null);
	}
}
