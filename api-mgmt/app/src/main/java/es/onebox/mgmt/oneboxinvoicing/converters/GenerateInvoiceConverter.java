package es.onebox.mgmt.oneboxinvoicing.converters;

import es.onebox.mgmt.datasources.ms.entity.dto.GenerateOneboxInvoiceRequest;
import es.onebox.mgmt.oneboxinvoicing.dto.GenerateOneboxInvoiceRequestDTO;


public class GenerateInvoiceConverter {
    private GenerateInvoiceConverter() {
    }

    public static GenerateOneboxInvoiceRequest toMs(GenerateOneboxInvoiceRequestDTO in) {
        GenerateOneboxInvoiceRequest out = new GenerateOneboxInvoiceRequest();
        out.setEmail(in.getEmail());
        out.setEntitiesId(in.getEntitiesId());
        out.setEventIds(in.getEventIds());
        out.setEntityCode(in.getEntityCode());
        out.setFrom(in.getFrom());
        out.setTo(in.getTo());
        out.setOperatorId(in.getOperatorId());
        out.setUserId(in.getUserId());
        out.setOrderPerspective(in.getOrderPerspective());
        return out;
    }
}
