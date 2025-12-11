package es.onebox.event.products.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.products.domain.ProductEventDeliveryPointRecord;
import es.onebox.event.products.dto.ProductEventDeliveryPointDTO;
import es.onebox.event.products.dto.ProductEventDeliveryPointsDTO;

import java.util.List;

public class ProductEventDeliveryPointConverter {

    public static ProductEventDeliveryPointsDTO toEntity(List<ProductEventDeliveryPointRecord> productEventDeliveryPointRecords) {
        if(productEventDeliveryPointRecords == null) {
            return null;
        }
        ProductEventDeliveryPointsDTO result = new ProductEventDeliveryPointsDTO();
        for(ProductEventDeliveryPointRecord productEventDeliveryPointRecord : productEventDeliveryPointRecords) {
            result.add(toEntity(productEventDeliveryPointRecord));
        }
        return result;
    }

    public static ProductEventDeliveryPointDTO toEntity(ProductEventDeliveryPointRecord productEventDeliveryPointRecord) {
        ProductEventDeliveryPointDTO productDeliveryPointRelationDetailDTOS = new ProductEventDeliveryPointDTO();
        productDeliveryPointRelationDetailDTOS.setDeliveryPoint(new IdNameDTO(productEventDeliveryPointRecord.getDeliverypointid().longValue(), productEventDeliveryPointRecord.getProductDeliveryPointName()));
        productDeliveryPointRelationDetailDTOS.setEvent(new IdNameDTO(productEventDeliveryPointRecord.getEventId().longValue(), productEventDeliveryPointRecord.getEventName()));
        productDeliveryPointRelationDetailDTOS.setProduct(new IdNameDTO(productEventDeliveryPointRecord.getProductId().longValue(), productEventDeliveryPointRecord.getProductName()));
        productDeliveryPointRelationDetailDTOS.setIsDefault(ConverterUtils.isByteAsATrue(productEventDeliveryPointRecord.getDefaultdeliverypoint()));
        return productDeliveryPointRelationDetailDTOS;
    }

}
