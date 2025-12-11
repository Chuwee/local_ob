package es.onebox.event.products.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.priceengine.simulation.record.ProductRecord;
import es.onebox.event.products.dto.CreateProductDTO;
import es.onebox.event.products.dto.ProductDTO;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.ProductStockType;
import es.onebox.event.products.enums.ProductType;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductRecordConverterTest {
    private static final Long ENTITY_ID = 1L;
    private static final Long PRODUCER_ID = 1L;
    private static final String PRODUCT_NAME = "New Product";
    private static final String TAX_NAME = "Tax";
    private static final String PRODUCER_NAME = "Producer";
    private static final String ENTITY_NAME = "Entity";
    private static final Integer CURRENCY_ID = 1;
    private static final int PRODUCT_ID = 1;
    private static final int TAX_ID = 1;

    @Test
    void convertProductDTOToProductRecord() {
        CreateProductDTO product = new CreateProductDTO();

        product.setEntityId(ENTITY_ID);
        product.setName(PRODUCT_NAME);
        product.setProducerId(PRODUCER_ID);
        product.setStockType(ProductStockType.UNBOUNDED);
        product.setProductType(ProductType.SIMPLE);
        product.setCurrencyId(1L);

        CpanelProductRecord cpanelProductRecord = ProductRecordConverter.toRecord(product);

        assertEquals(cpanelProductRecord.getProducerid(), product.getProducerId().intValue());
        assertEquals(cpanelProductRecord.getType(), ProductType.SIMPLE.getId());
        assertEquals(cpanelProductRecord.getState(), ProductState.INACTIVE.getId());
        assertEquals(cpanelProductRecord.getName(), product.getName());
        assertEquals(cpanelProductRecord.getEntityid(), product.getEntityId().intValue());
        assertEquals(cpanelProductRecord.getStocktype(), ProductStockType.UNBOUNDED.getId());
    }

    @Test
    void convertRecordToProductDTO() {
        ProductRecord productRecord = getProductRecord();

        ProductDTO productDTO = ProductRecordConverter.fromEntity(productRecord);

        assertEquals(productDTO.getName(), productRecord.getName());
        assertEquals(productDTO.getProductId(), productRecord.getProductid().longValue());
        assertEquals(productDTO.getProductType().getId(), productRecord.getType());
        assertEquals(productDTO.getProductState().getId(), productRecord.getState());
        assertEquals(productDTO.getCreateDate(),
                CommonUtils.timestampToZonedDateTime(productRecord.getCreateDate()));
        assertEquals(productDTO.getUpdateDate(),
                CommonUtils.timestampToZonedDateTime((productRecord.getUpdateDate())));
        assertEquals(productDTO.getProducer(),
                new IdNameDTO(productRecord.getProducerid().longValue(), productRecord.getProducerName()));
        assertEquals(productDTO.getTax(),
                new IdNameDTO(productRecord.getTaxid().longValue(), productRecord.getTaxName()));
        assertEquals(productDTO.getEntity(),
                new IdNameDTO(productRecord.getEntityid().longValue(), productRecord.getEntityName()));
    }

    @NotNull
    private static ProductRecord getProductRecord() {
        ProductRecord productRecord = new ProductRecord();

        productRecord.setType(ProductType.SIMPLE.getId());
        productRecord.setProducerid(PRODUCER_ID.intValue());
        productRecord.setProducerName(PRODUCER_NAME);
        productRecord.setName(PRODUCT_NAME);
        productRecord.setProductid(PRODUCT_ID);
        productRecord.setTaxid(TAX_ID);
        productRecord.setTaxName(TAX_NAME);
        productRecord.setEntityid(ENTITY_ID.intValue());
        productRecord.setEntityName(ENTITY_NAME);
        productRecord.setState(ProductState.INACTIVE.getId());
        productRecord.setStocktype(ProductStockType.UNBOUNDED.getId());
        productRecord.setIdcurrency(CURRENCY_ID);
        productRecord.setCreateDate(new Timestamp((new Date().getTime())));
        productRecord.setUpdateDate(new Timestamp((new Date().getTime())));
        return productRecord;
    }
}
