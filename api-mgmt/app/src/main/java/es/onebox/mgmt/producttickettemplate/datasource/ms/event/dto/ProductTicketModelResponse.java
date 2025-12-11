package es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto;

import java.io.Serializable;

import es.onebox.mgmt.producttickettemplate.domain.enums.ProductTicketModelTarget;
import es.onebox.mgmt.producttickettemplate.domain.enums.ProductTicketModelType;

public record ProductTicketModelResponse(Long id,
		String name,
		String description,
		ProductTicketModelType type,
		ProductTicketModelTarget target,
		String fileName) implements Serializable {
}
