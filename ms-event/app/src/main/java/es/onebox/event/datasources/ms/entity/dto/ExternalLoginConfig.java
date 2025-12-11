package es.onebox.event.datasources.ms.entity.dto;

import es.onebox.event.events.enums.Provider;

import java.io.Serializable;

public record ExternalLoginConfig(
		Provider provider,
		Login login,
		PurchaseLimit purchaseLimit) implements Serializable {
}
