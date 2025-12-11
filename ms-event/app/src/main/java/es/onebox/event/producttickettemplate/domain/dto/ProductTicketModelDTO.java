package es.onebox.event.producttickettemplate.domain.dto;

import es.onebox.event.producttickettemplate.domain.enums.ProductTicketModelTarget;
import es.onebox.event.producttickettemplate.domain.enums.ProductTicketModelType;

public record ProductTicketModelDTO(Long id,
		String name,
		String description,
		ProductTicketModelType type,
		ProductTicketModelTarget target,
		String fileName) {
}
