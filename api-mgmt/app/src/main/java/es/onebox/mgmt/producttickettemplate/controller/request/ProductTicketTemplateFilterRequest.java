package es.onebox.mgmt.producttickettemplate.controller.request;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonProperty;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.producttickettemplate.domain.enums.ProductTicketModelType;

public class ProductTicketTemplateFilterRequest extends BaseRequestFilter {

	private SortOperator<String> sort;
	@JsonProperty("q")
	private String freeSearch;
	@JsonProperty("model_type")
	private ProductTicketModelType modelType;

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public SortOperator<String> getSort() {
		return sort;
	}

	public void setSort(SortOperator<String> sort) {
		this.sort = sort;
	}

	public void setFreeSearch(String freeSearch) {
		this.freeSearch = freeSearch;
	}

	public String getFreeSearch() {
		return freeSearch;
	}

	public ProductTicketModelType getModelType() {
		return modelType;
	}

	public void setModelType(ProductTicketModelType modelType) {
		this.modelType = modelType;
	}
}
