package es.onebox.mgmt.producttickettemplate.domain.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import es.onebox.mgmt.producttickettemplate.domain.enums.ProductTicketModelTarget;
import es.onebox.mgmt.producttickettemplate.domain.enums.ProductTicketModelType;

public record ProductTicketModelDTO(Long id,
		String name,
		String description,
		ProductTicketModelType type,
		ProductTicketModelTarget target,
		@JsonProperty("file_name") String fileName) implements Serializable {
}
