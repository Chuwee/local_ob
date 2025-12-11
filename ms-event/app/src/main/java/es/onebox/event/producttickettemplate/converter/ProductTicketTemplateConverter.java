package es.onebox.event.producttickettemplate.converter;

import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.producttickettemplate.controller.request.CreateProductTicketTemplate;
import es.onebox.event.producttickettemplate.controller.request.UpdateProductTicketTemplate;
import es.onebox.event.producttickettemplate.domain.enums.ProductTicketTemplateStatus;
import es.onebox.jooq.cpanel.tables.records.CpanelProductTicketTemplateRecord;

public class ProductTicketTemplateConverter {

	private ProductTicketTemplateConverter() {
	}

	public static CpanelProductTicketTemplateRecord toRecord(CreateProductTicketTemplate request) {

		CpanelProductTicketTemplateRecord entity = new CpanelProductTicketTemplateRecord();
		entity.setEntityid(request.entityId());
		entity.setModelid(request.modelId());
		entity.setName(request.name());

		return entity;
	}

	public static CpanelProductTicketTemplateRecord toRecord(CpanelProductTicketTemplateRecord found,
			ProductTicketTemplateStatus status) {

		ConverterUtils.updateField(found::setStatus, status.getId().byteValue());

		return found;
	}

	public static CpanelProductTicketTemplateRecord toRecord(UpdateProductTicketTemplate request,
			CpanelProductTicketTemplateRecord found) {

		ConverterUtils.updateField(found::setName, request.name());
		ConverterUtils.updateField(found::setModelid, request.modelId());

		return found;
	}
}
