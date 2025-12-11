package es.onebox.event.datasources.ms.entity.dto;

import java.util.List;

public record PurchaseLimit(Customer customer) {
	public record Customer(
			List<CustomerCondition> allowedConditions,
			int maxAllowed,
			CustomerCondition defaultCondition,
			int defaultValue) {
	}

}