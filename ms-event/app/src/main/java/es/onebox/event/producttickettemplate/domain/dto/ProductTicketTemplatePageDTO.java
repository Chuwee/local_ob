package es.onebox.event.producttickettemplate.domain.dto;

import java.util.List;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;

public class ProductTicketTemplatePageDTO extends BaseResponseCollection<ProductTicketTemplateDTO, Metadata> {

	public ProductTicketTemplatePageDTO() {
	}

	public ProductTicketTemplatePageDTO(List<ProductTicketTemplateDTO> data, Metadata metadata) {
		super(data, metadata);
	}
}
