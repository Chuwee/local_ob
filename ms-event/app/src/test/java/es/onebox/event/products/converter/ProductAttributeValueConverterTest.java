package es.onebox.event.products.converter;

import es.onebox.event.products.dto.CreateProductAttributeValueDTO;
import es.onebox.event.products.dto.ProductAttributeValueDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeValueRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductAttributeValueConverterTest {
    private List<CpanelProductAttributeValueRecord> productAttributeValueRecords;
    private CpanelProductAttributeValueRecord productAttributeValueRecord;
    private CreateProductAttributeValueDTO createProductAttributeValueDTO;

    @BeforeEach
    public void setUp() {
        // Mock data setup
        productAttributeValueRecords = new ArrayList<>();
        productAttributeValueRecord = new CpanelProductAttributeValueRecord();
        productAttributeValueRecord.setProductattributeid(1);
        productAttributeValueRecord.setValueid(1);
        productAttributeValueRecord.setName("New attribute value");
        productAttributeValueRecord.setPosition(1);
        productAttributeValueRecords.add(productAttributeValueRecord);

        createProductAttributeValueDTO = new CreateProductAttributeValueDTO("New Attribute value", 1);
    }

    @Test
    void testFromRecord() {
        ProductAttributeValueDTO productAttributeValueDTO = ProductAttributeValueConverter.fromRecord(productAttributeValueRecord);
        assertNotNull(productAttributeValueDTO);
        assertEquals(productAttributeValueRecord.getName(), productAttributeValueDTO.getName());
        assertEquals(productAttributeValueRecord.getPosition(), productAttributeValueDTO.getPosition());
        assertEquals(productAttributeValueRecord.getProductattributeid().longValue(), productAttributeValueDTO.getAttributeId());
        assertEquals(productAttributeValueRecord.getValueid().longValue(), productAttributeValueDTO.getValueId());
    }

    @Test
    void testToEntityList() {
        List<ProductAttributeValueDTO> productAttributeValues = ProductAttributeValueConverter.toDTOs(productAttributeValueRecords);
        assertNotNull(productAttributeValues);
        assertEquals(productAttributeValueRecords.size(), productAttributeValues.size());

        ProductAttributeValueDTO productAttribute = productAttributeValues.get(0);
        assertEquals(productAttributeValueRecord.getName(), productAttribute.getName());
        assertEquals(productAttributeValueRecord.getPosition(), productAttribute.getPosition());
        assertEquals(productAttributeValueRecord.getProductattributeid().longValue(), productAttribute.getAttributeId());
        assertEquals(productAttributeValueRecord.getValueid().longValue(), productAttribute.getValueId());
    }

    @Test
    void testToRecord() {
        CpanelProductAttributeValueRecord convertedRecord = ProductAttributeValueConverter.toRecord(1L, createProductAttributeValueDTO);
        assertNotNull(convertedRecord);
        assertEquals(1L, convertedRecord.getProductattributeid().longValue());
        assertEquals(createProductAttributeValueDTO.getName(), convertedRecord.getName());
        assertEquals(createProductAttributeValueDTO.getPosition(), convertedRecord.getPosition());
    }
}
