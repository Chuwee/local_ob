package es.onebox.event.products.converter;

import es.onebox.event.products.dto.CreateProductAttributeDTO;
import es.onebox.event.products.dto.ProductAttributeDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductAttributeConverterTest {

    private List<CpanelProductAttributeRecord> productAttributeRecords;
    private CpanelProductAttributeRecord productAttributeRecord;
    private CreateProductAttributeDTO createProductAttributeDTO;

    @BeforeEach
    public void setUp() {
        // Mock data setup
        productAttributeRecords = new ArrayList<>();
        productAttributeRecord = new CpanelProductAttributeRecord();
        productAttributeRecord.setAttributeid(1);
        productAttributeRecord.setProductid(1);
        productAttributeRecord.setName("Attribute 1");
        productAttributeRecords.add(productAttributeRecord);

        createProductAttributeDTO = new CreateProductAttributeDTO("New Attribute", 0);
    }

    @Test
    void testFromRecord() {
        ProductAttributeDTO productAttribute = ProductAttributeConverter.fromRecord(productAttributeRecord);
        assertNotNull(productAttribute);
        assertEquals(productAttributeRecord.getAttributeid().longValue(), productAttribute.getAttributeId());
        assertEquals(productAttributeRecord.getName(), productAttribute.getName());
        assertEquals(productAttributeRecord.getPosition(), productAttribute.getPosition());
    }

    @Test
    void testToEntity() {
        ProductAttributeDTO productAttribute = ProductAttributeConverter.toEntity(productAttributeRecord);
        assertNotNull(productAttribute);
        assertEquals(productAttributeRecord.getAttributeid().longValue(), productAttribute.getAttributeId());
        assertEquals(productAttributeRecord.getName(), productAttribute.getName());
        assertEquals(productAttributeRecord.getPosition(), productAttribute.getPosition());
    }

    @Test
    void testToEntityList() {
        List<ProductAttributeDTO> productAttributes = ProductAttributeConverter.toEntity(productAttributeRecords);
        assertNotNull(productAttributes);
        assertEquals(productAttributeRecords.size(), productAttributes.size());

        ProductAttributeDTO productAttribute = productAttributes.get(0);
        assertEquals(productAttributeRecord.getAttributeid().longValue(), productAttribute.getAttributeId());
        assertEquals(productAttributeRecord.getName(), productAttribute.getName());
        assertEquals(productAttributeRecord.getPosition(), productAttribute.getPosition());
    }

    @Test
    void testToRecord() {
        CpanelProductAttributeRecord convertedRecord = ProductAttributeConverter.toRecord(1L, createProductAttributeDTO);
        assertNotNull(convertedRecord);
        assertEquals(1L, convertedRecord.getProductid().longValue());
        assertEquals(createProductAttributeDTO.getName(), convertedRecord.getName());
        assertEquals(createProductAttributeDTO.getPosition(), convertedRecord.getPosition());
    }
}
