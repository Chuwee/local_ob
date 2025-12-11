package es.onebox.mgmt.producttickettemplate.converter;

import es.onebox.mgmt.producttickettemplate.controller.request.CreateProductTicketTemplate;
import es.onebox.mgmt.producttickettemplate.controller.request.ProductTicketTemplateFilterRequest;
import es.onebox.mgmt.producttickettemplate.controller.request.UpdateProductTicketTemplate;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.CreateProductTicketTemplateRequest;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketModelResponse;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketTemplateFilter;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketTemplateLanguage;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketTemplateLanguages;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketTemplatePageResponse;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.ProductTicketTemplateResponse;
import es.onebox.mgmt.producttickettemplate.datasource.ms.event.dto.UpdateProductTicketTemplateRequest;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketModelDTO;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketTemplateDTO;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketTemplateDetailDTO;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketTemplateLanguageDTO;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketTemplateLanguagesDTO;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketTemplatePageDTO;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketTemplateSearchDTO;
import es.onebox.mgmt.producttickettemplate.domain.dto.ProductTicketTemplateSearchPageDTO;

import java.util.List;

public class ProductTicketTemplateConverter {

	private ProductTicketTemplateConverter() {
	}

	public static ProductTicketTemplateDTO toDomain(ProductTicketTemplateResponse response) {

		return new ProductTicketTemplateDTO(response.id(), response.name(), response.entity(),
				toDomain(response.model()), response.isDefault(), response.defaultLanguage(), response.selectedLanguageIds());
	}

	public static ProductTicketTemplateSearchPageDTO toSearchDomain(ProductTicketTemplatePageResponse response) {

		return new ProductTicketTemplateSearchPageDTO(
				response.getData().stream().map(ProductTicketTemplateConverter::toSearchDomain).toList(),
				response.getMetadata());
	}

	public static ProductTicketTemplateSearchDTO toSearchDomain(ProductTicketTemplateResponse response) {

		return new ProductTicketTemplateSearchDTO(response.id(), response.name(), response.entity(),
				toDomain(response.model()));
	}

	public static ProductTicketTemplateDetailDTO toDetailDomain(ProductTicketTemplateResponse response) {

		return new ProductTicketTemplateDetailDTO(response.id(), response.name(), response.entity(),
				toDomain(response.model()), response.defaultLanguage(), response.selectedLanguageIds());
	}

	public static ProductTicketModelDTO toDomain(ProductTicketModelResponse model) {

		return new ProductTicketModelDTO(model.id(), model.name(), model.description(), model.type(), model.target(),
				model.fileName());
	}

	public static ProductTicketTemplateFilter toMS(ProductTicketTemplateFilterRequest request) {
		ProductTicketTemplateFilter filter = new ProductTicketTemplateFilter();
		filter.setSort(request.getSort());
		filter.setFreeSearch(request.getFreeSearch());
		filter.setOffset(request.getOffset());
		filter.setLimit(request.getLimit());
		filter.setModelType(request.getModelType());
		return filter;
	}

	public static CreateProductTicketTemplateRequest toMS(CreateProductTicketTemplate createRequest, Long defaultLanguage) {
		return new CreateProductTicketTemplateRequest(createRequest.name(), createRequest.entityId(),
				createRequest.modelId(), defaultLanguage.intValue());
	}

	public static UpdateProductTicketTemplateRequest toMS(UpdateProductTicketTemplate updateRequest) {
		return new UpdateProductTicketTemplateRequest(updateRequest.name(), updateRequest.modelId(), null, null);
	}

	public static UpdateProductTicketTemplateRequest toMS(UpdateProductTicketTemplate updateRequest, Long defaultLanguageId,
														  List<Long> selectedLanguageIds) {
		return new UpdateProductTicketTemplateRequest(updateRequest.name(), updateRequest.modelId(), defaultLanguageId,
				selectedLanguageIds);
	}

	public static ProductTicketTemplateLanguagesDTO toDto(ProductTicketTemplateLanguages productTicketTemplateLanguages) {
		if(productTicketTemplateLanguages == null || productTicketTemplateLanguages.isEmpty()) {
			return null;
		}
		ProductTicketTemplateLanguagesDTO productTicketTemplateLanguagesDTO = new ProductTicketTemplateLanguagesDTO();
		for (ProductTicketTemplateLanguage productLanguage : productTicketTemplateLanguages) {
			ProductTicketTemplateLanguageDTO productTicketTemplateLanguageDTO = new ProductTicketTemplateLanguageDTO();
			productTicketTemplateLanguageDTO.setCode(productLanguage.getCode());
			productTicketTemplateLanguageDTO.setProductId(productLanguage.getTemplateId());
			productTicketTemplateLanguageDTO.setLanguageId(productLanguage.getLanguageId());
			productTicketTemplateLanguageDTO.setIsDefault(productLanguage.getIsDefault());
			productTicketTemplateLanguagesDTO.add(productTicketTemplateLanguageDTO);
		}
		return productTicketTemplateLanguagesDTO;
	}
}
