package es.onebox.event.products.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.products.domain.ProductEventRecord;
import es.onebox.event.products.dto.ProductEventDTO;
import es.onebox.event.products.dto.ProductEventData;
import es.onebox.event.products.dto.ProductEventsDTO;
import es.onebox.event.products.dto.ProductEventsFilterDTO;
import es.onebox.event.products.enums.ProductEventStatus;
import es.onebox.event.products.enums.SelectionType;

import java.util.Collections;
import java.util.List;

public class ProductEventConverter {
    private ProductEventConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static ProductEventsDTO toEntity(List<ProductEventRecord> productEventRecordList) {
        if (productEventRecordList == null) {
            return new ProductEventsDTO();
        }

        ProductEventsDTO productEventsDTO = new ProductEventsDTO();

        for(ProductEventRecord productEventRecord : productEventRecordList) {
            productEventsDTO.add(toEntity(productEventRecord));
        }
        return productEventsDTO;
    }

    public static ProductEventDTO toEntity(ProductEventRecord productEventRecord) {
        ProductEventDTO productEventDTO = new ProductEventDTO();

        ProductEventData productEventData = new ProductEventData();
        productEventData.setId(productEventRecord.getEventid().longValue());
        productEventData.setName(productEventRecord.getEventName());
        productEventData.setStartDate(productEventRecord.getStartDate());

        productEventDTO.setEvent(productEventData);
        productEventDTO.setStatus(ProductEventStatus.get(productEventRecord.getStatus()));
        productEventDTO.setProduct(new IdNameDTO(productEventRecord.getProductid().longValue(), productEventRecord.getProductName()));
        productEventDTO.setSessionsSelectionType(SelectionType.get(productEventRecord.getSessionsselectiontype()));
        return productEventDTO;
    }
}
