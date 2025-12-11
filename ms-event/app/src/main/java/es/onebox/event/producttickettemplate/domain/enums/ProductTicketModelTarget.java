package es.onebox.event.producttickettemplate.domain.enums;

import java.util.Arrays;

public enum ProductTicketModelTarget {
	FILE(1), PRINTER(2);

	private final int targetTypeId;

	ProductTicketModelTarget(int targetTypeId) {
		this.targetTypeId = targetTypeId;
	}

	public int getTargetTypeId() {
		return targetTypeId;
	}

	public static ProductTicketModelTarget fromId(int typeId) {
		return Arrays.stream(ProductTicketModelTarget.values())
				.filter(target -> target.targetTypeId == typeId)
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException(
						"No value found for ProductTicketModelTarget with id: " + typeId));
	}
}