package es.onebox.event.products.converter;

import es.onebox.event.products.dto.CreateProductAttributeDTO;
import es.onebox.event.products.dto.ProductAttributeDTO;
import es.onebox.event.products.dto.ProductAttributesDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeRecord;

import java.util.List;

public class ProductAttributeConverter {
    private ProductAttributeConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static ProductAttributeDTO fromRecord(CpanelProductAttributeRecord productAttributeRecord) {
        ProductAttributeDTO result = new ProductAttributeDTO();
        result.setAttributeId(productAttributeRecord.getAttributeid().longValue());
        result.setName(productAttributeRecord.getName());
        result.setPosition(productAttributeRecord.getPosition());

        return result;
    }

    public static ProductAttributesDTO toEntity(List<CpanelProductAttributeRecord> productAttributeRecords) {
        ProductAttributesDTO productAttributesDTO = new ProductAttributesDTO();

        for(CpanelProductAttributeRecord productAttributeRecord : productAttributeRecords) {
            productAttributesDTO.add(toEntity(productAttributeRecord));
        }
        return productAttributesDTO;
    }

    public static ProductAttributeDTO toEntity(CpanelProductAttributeRecord productAttributeRecord) {
        ProductAttributeDTO productAttribute = new ProductAttributeDTO();
        productAttribute.setName(productAttributeRecord.getName());
        productAttribute.setPosition(productAttributeRecord.getPosition());
        productAttribute.setAttributeId(productAttributeRecord.getAttributeid().longValue());

        return productAttribute;
    }

    public static CpanelProductAttributeRecord toRecord(Long productId, CreateProductAttributeDTO productAttributeDTO) {
        CpanelProductAttributeRecord productAttributeRecord = new CpanelProductAttributeRecord();
        productAttributeRecord.setName(productAttributeDTO.getName());
        productAttributeRecord.setPosition(productAttributeDTO.getPosition());
        productAttributeRecord.setProductid(productId.intValue());

        return productAttributeRecord;
    }
}
