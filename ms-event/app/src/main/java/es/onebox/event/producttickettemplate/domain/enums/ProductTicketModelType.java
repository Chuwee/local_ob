package es.onebox.event.producttickettemplate.domain.enums;

import java.util.Arrays;

public enum ProductTicketModelType {
	PDF(1), ZPL(2);

	private final int modelTypeId;

	public int getModelTypeId() {
		return modelTypeId;
	}

	ProductTicketModelType(int modelTypeId) {
		this.modelTypeId = modelTypeId;
	}

	public static ProductTicketModelType fromId(int typeId) {
		return Arrays.stream(ProductTicketModelType.values())
				.filter(modelType -> modelType.modelTypeId == typeId)
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException(
						"No value found for ProductTicketModelType with id: " + typeId));
	}
}