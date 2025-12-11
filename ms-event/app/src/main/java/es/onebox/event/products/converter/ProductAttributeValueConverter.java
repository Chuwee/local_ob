package es.onebox.event.products.converter;

import es.onebox.event.priceengine.simulation.record.ProductRecord;
import es.onebox.event.products.dto.CreateProductAttributeValueDTO;
import es.onebox.event.products.dto.ProductAttributeValueDTO;
import es.onebox.event.products.dto.ProductAttributeValuesDTO;
import es.onebox.event.products.dto.ProductDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeValueRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductAttributeValueConverter {
    private ProductAttributeValueConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static ProductAttributeValueDTO fromRecord(CpanelProductAttributeValueRecord productAttributeValueRecord) {
        ProductAttributeValueDTO result = new ProductAttributeValueDTO();
        result.setValueId(productAttributeValueRecord.getValueid().longValue());
        result.setAttributeId(productAttributeValueRecord.getProductattributeid().longValue());
        result.setName(productAttributeValueRecord.getName());
        result.setPosition(productAttributeValueRecord.getPosition());

        return result;
    }

    public static List<ProductAttributeValueDTO> toDTOs(List<CpanelProductAttributeValueRecord> productAttributeValueRecords) {
        return productAttributeValueRecords.stream()
                .map(ProductAttributeValueConverter::toDTO)
                .collect(Collectors.toList());
    }

    public static ProductAttributeValueDTO toDTO(CpanelProductAttributeValueRecord productAttributeValueRecord) {
        ProductAttributeValueDTO productAttributeValueDTO = new ProductAttributeValueDTO();
        productAttributeValueDTO.setName(productAttributeValueRecord.getName());
        productAttributeValueDTO.setAttributeId(productAttributeValueRecord.getProductattributeid().longValue());
        productAttributeValueDTO.setValueId(productAttributeValueRecord.getValueid().longValue());
        productAttributeValueDTO.setPosition(productAttributeValueRecord.getPosition());

        return productAttributeValueDTO;
    }

    public static CpanelProductAttributeValueRecord toRecord(Long attributeId,
                                                             CreateProductAttributeValueDTO productAttributeValueDTO) {
        CpanelProductAttributeValueRecord productAttributeValueRecord = new CpanelProductAttributeValueRecord();
        productAttributeValueRecord.setName(productAttributeValueDTO.getName());
        productAttributeValueRecord.setPosition(productAttributeValueDTO.getPosition());
        productAttributeValueRecord.setProductattributeid(attributeId.intValue());

        return productAttributeValueRecord;
    }
}
