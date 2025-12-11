package es.onebox.event.products.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.products.domain.ProductDeliveryPointRelationRecord;
import es.onebox.event.products.dto.ProductDeliveryPointRelationDTO;
import es.onebox.event.products.dto.ProductDeliveryPointRelationDetailDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelProductDeliveryPointRecord;

import java.util.ArrayList;
import java.util.List;

public class ProductDeliveryPointRecordRelationConverter {

    public static CpanelProductDeliveryPointRecord toRecord(Long productId, Long deliveryPointId) {
        CpanelProductDeliveryPointRecord productDeliveryPointRecord = new CpanelProductDeliveryPointRecord();

        productDeliveryPointRecord.setProductid(productId.intValue());
        productDeliveryPointRecord.setDeliverypointid(deliveryPointId.intValue());
        return productDeliveryPointRecord;
    }

    public static List<ProductDeliveryPointRelationDetailDTO> toDtoList(List<ProductDeliveryPointRelationRecord> productDeliveryPointRelationRecords) {
        List<ProductDeliveryPointRelationDetailDTO> productDeliveryPointRelationDetailDTOS = new ArrayList<>();
        productDeliveryPointRelationRecords.forEach(r -> productDeliveryPointRelationDetailDTOS.add(toDtoDetail(r)));
        return productDeliveryPointRelationDetailDTOS;
    }

    public static ProductDeliveryPointRelationDetailDTO toDtoDetail(ProductDeliveryPointRelationRecord productDeliveryPointRelationRecord) {
        ProductDeliveryPointRelationDetailDTO productDeliveryPointRelationDetailDTO = new ProductDeliveryPointRelationDetailDTO();
        productDeliveryPointRelationDetailDTO.setId(productDeliveryPointRelationRecord.getId().longValue());
        productDeliveryPointRelationDetailDTO.setDeliveryPoint(new IdNameDTO(productDeliveryPointRelationRecord.getDeliverypointid().longValue(), productDeliveryPointRelationRecord.getProductDeliveryPointName()));
        return productDeliveryPointRelationDetailDTO;
    }

    public static ProductDeliveryPointRelationDTO toDto(ProductDeliveryPointRelationRecord productDeliveryPointRelationRecord) {
        ProductDeliveryPointRelationDTO productDeliveryPointRelationDTO = new ProductDeliveryPointRelationDTO();
        productDeliveryPointRelationDTO.setId(productDeliveryPointRelationRecord.getId().longValue());
        productDeliveryPointRelationDTO.setDeliveryPoint(new IdNameDTO(productDeliveryPointRelationRecord.getDeliverypointid().longValue(), productDeliveryPointRelationRecord.getProductDeliveryPointName()));
        productDeliveryPointRelationDTO.setProduct(new IdNameDTO(productDeliveryPointRelationRecord.getProductid().longValue(), productDeliveryPointRelationRecord.getProductName()));
        return productDeliveryPointRelationDTO;
    }
}
