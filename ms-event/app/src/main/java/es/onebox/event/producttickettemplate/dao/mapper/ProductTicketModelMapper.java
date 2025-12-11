package es.onebox.event.producttickettemplate.dao.mapper;

import org.jooq.Record;
import org.jooq.RecordMapper;

import es.onebox.event.producttickettemplate.domain.dto.ProductTicketModelDTO;
import es.onebox.event.producttickettemplate.domain.enums.ProductTicketModelTarget;
import es.onebox.event.producttickettemplate.domain.enums.ProductTicketModelType;
import es.onebox.jooq.cpanel.tables.CpanelProductTicketModel;

public class ProductTicketModelMapper implements RecordMapper<Record, ProductTicketModelDTO> {

	private static final CpanelProductTicketModel MODEL = CpanelProductTicketModel.CPANEL_PRODUCT_TICKET_MODEL
			.as("model");

	@Override
	public ProductTicketModelDTO map(Record entity) {

		return new ProductTicketModelDTO(
				entity.getValue(MODEL.MODELID).longValue(),
				entity.getValue(MODEL.NAME),
				entity.getValue(MODEL.DESCRIPTION),
				ProductTicketModelType.fromId(entity.getValue(MODEL.MODELTYPE)),
				ProductTicketModelTarget.fromId(entity.getValue(MODEL.TARGETTYPE)),
				entity.getValue(MODEL.FILENAME));
	}
}