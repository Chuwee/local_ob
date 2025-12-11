package es.onebox.event.producttickettemplate.dao.mapper;

import es.onebox.event.common.utils.ConverterUtils;
import org.jooq.RecordMapper;

import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateLanguageDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelProductTicketTemplateLanguageRecord;

public class ProductTicketTemplateLanguageMapper
		implements RecordMapper<CpanelProductTicketTemplateLanguageRecord, ProductTicketTemplateLanguageDTO> {

	@Override
	public ProductTicketTemplateLanguageDTO map(CpanelProductTicketTemplateLanguageRecord row) {
		return new ProductTicketTemplateLanguageDTO(row.getTemplateid(), row.getLanguageid(), null,
				ConverterUtils.isByteAsATrue(row.getIsdefault()));
	}
}
