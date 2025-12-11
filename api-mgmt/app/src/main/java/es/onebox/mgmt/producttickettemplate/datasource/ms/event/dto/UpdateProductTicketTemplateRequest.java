package es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UpdateProductTicketTemplateRequest(
		String name,
		Integer modelId,
		Long defaultLanguageId,
		List<Long> selectedLanguageIds) {
}
