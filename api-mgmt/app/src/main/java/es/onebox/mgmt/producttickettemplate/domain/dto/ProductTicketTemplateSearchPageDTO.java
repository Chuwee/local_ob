package es.onebox.mgmt.producttickettemplate.domain.dto;

import java.util.List;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;

public class ProductTicketTemplateSearchPageDTO extends BaseResponseCollection<ProductTicketTemplateSearchDTO, Metadata> {

	public ProductTicketTemplateSearchPageDTO(List<ProductTicketTemplateSearchDTO> data, Metadata metadata) {
		super(data, metadata);
	}
}

