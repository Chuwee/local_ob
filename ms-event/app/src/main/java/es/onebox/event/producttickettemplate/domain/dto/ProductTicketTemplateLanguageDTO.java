package es.onebox.event.producttickettemplate.domain.dto;

import java.io.Serializable;

public record ProductTicketTemplateLanguageDTO(
		Integer templateId,
		Integer languageId,
		String code,
		Boolean isDefault) implements Serializable {

}
