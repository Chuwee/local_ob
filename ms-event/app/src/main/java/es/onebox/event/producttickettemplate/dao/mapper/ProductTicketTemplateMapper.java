package es.onebox.event.producttickettemplate.dao.mapper;

import org.jooq.Record;
import org.jooq.RecordMapper;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketModelDTO;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateDTO;
import es.onebox.event.producttickettemplate.domain.enums.ProductTicketModelTarget;
import es.onebox.event.producttickettemplate.domain.enums.ProductTicketModelType;

public class ProductTicketTemplateMapper implements RecordMapper<Record, ProductTicketTemplateDTO> {
	@Override
	public ProductTicketTemplateDTO map(Record row) {
		IdNameDTO entity = new IdNameDTO();
		entity.setName(row.get("entityName", String.class));
		entity.setId(row.get("entityId", Long.class));

		ProductTicketModelDTO model = new ProductTicketModelDTO(row.get("productModelId", Long.class),
				row.get("productModelName", String.class), row.get("description", String.class),
				ProductTicketModelType.fromId(row.get("modelType", Integer.class)),
				ProductTicketModelTarget.fromId(row.get("targetType", Integer.class)),
				row.get("fileName", String.class));
		return new ProductTicketTemplateDTO(row.get("id", Long.class), row.get("name", String.class), entity,
				model);
	}
}
