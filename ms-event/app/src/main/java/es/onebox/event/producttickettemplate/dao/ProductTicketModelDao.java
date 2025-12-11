package es.onebox.event.producttickettemplate.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import es.onebox.event.producttickettemplate.dao.mapper.ProductTicketModelMapper;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketModelDTO;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelProductTicketModel;
import es.onebox.jooq.cpanel.tables.records.CpanelProductTicketModelRecord;
import es.onebox.jooq.dao.DaoImpl;

@Repository
public class ProductTicketModelDao extends DaoImpl<CpanelProductTicketModelRecord, Integer> {

	private static final CpanelProductTicketModel MODEL = CpanelProductTicketModel.CPANEL_PRODUCT_TICKET_MODEL
			.as("model");
	private static final ProductTicketModelMapper MAPPER = new ProductTicketModelMapper();

	protected ProductTicketModelDao() {
		super(Tables.CPANEL_PRODUCT_TICKET_MODEL);
	}

	public List<ProductTicketModelDTO> getAllModelsSorted() {
		return dsl.select(MODEL.fields()).from(MODEL).orderBy(MODEL.NAME.asc())
				.fetch(MAPPER);
	}

	public boolean exists(Integer modelId) {

		return dsl.fetchExists(dsl.selectOne().from(MODEL).where(MODEL.MODELID.eq(modelId)));
	}
}
