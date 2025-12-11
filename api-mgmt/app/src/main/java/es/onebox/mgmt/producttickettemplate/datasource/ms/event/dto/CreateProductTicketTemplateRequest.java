package es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto;

public record CreateProductTicketTemplateRequest(
		String name,
		Integer entityId,
		Integer modelId,
		Integer defaultLanguageId) {
}
