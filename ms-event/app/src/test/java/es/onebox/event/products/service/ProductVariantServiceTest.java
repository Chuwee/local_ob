package es.onebox.event.products.service;

import es.onebox.utils.ObjectRandomizer;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.simulation.record.ProductRecord;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductVariantDao;
import es.onebox.event.products.dao.ProductVariantStockCouchDao;
import es.onebox.event.products.domain.ProductVariantRecord;
import es.onebox.event.products.dto.ProductVariantDTO;
import es.onebox.event.products.dto.UpdateProductVariantDTO;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.ProductType;
import es.onebox.event.products.enums.ProductVariantStatus;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ProductVariantServiceTest {
    private static final String SKU = "1234-1234-1234";
    private static final Long PRODUCT_ID = 1L;
    private static final Long VARIANT_ID = 1L;
    private static final String PRODUCT_NAME = "New Product";
    private static final String VARIANT_NAME = "New Variant";
    private static final double PRICE = 1.0;

    @Mock
    private ProductVariantDao productVariantDao;
    @Mock
    private ProductVariantStockCouchDao productVariantStockCouchDao;
    @Mock
    private ProductDao productDao;
    @Mock
    RefreshDataService refreshDataService;

    @InjectMocks
    private ProductVariantService productVariantService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductVariant() {
        ProductVariantRecord productVariant = getProductVariantRecord();
        ProductRecord productRecord = new ProductRecord();
        productRecord.setState(1);
        productRecord.setType(ProductType.SIMPLE.getId());

        when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(productRecord);
        when(productVariantDao.findById(PRODUCT_ID, VARIANT_ID)).thenReturn(productVariant);
        when(productVariantStockCouchDao.get(PRODUCT_ID, VARIANT_ID)).thenReturn(any());

        ProductVariantDTO productVariantDTO = productVariantService.getProductVariant(PRODUCT_ID, VARIANT_ID);

        assertEquals(productVariantDTO.getId(), productVariant.getProductid().longValue());
        assertEquals(productVariantDTO.getName(), productVariant.getName());
        assertEquals(productVariantDTO.getSku(), productVariant.getSku());
        assertEquals(productVariantDTO.getPrice(), productVariant.getPrice());
        assertEquals(productVariantDTO.getCreateDate(),
                CommonUtils.timestampToZonedDateTime(productVariant.getCreateDate()));
        assertEquals(productVariantDTO.getUpdateDate(),
                CommonUtils.timestampToZonedDateTime(productVariant.getUpdateDate()));

    }

    @Test
    void updateProductVariantExceptions() {
        Mockito.doNothing().when(refreshDataService).refreshProduct(Mockito.anyLong());
        UpdateProductVariantDTO updateProductVariantDTO = ObjectRandomizer.random(UpdateProductVariantDTO.class);

        //Not found product
        OneboxRestException productNotFound =
                Assertions.assertThrows(OneboxRestException.class, () ->
                        productVariantService.updateProductVariant(PRODUCT_ID, VARIANT_ID, updateProductVariantDTO));

        assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), productNotFound.getErrorCode());
        assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getMessage(), productNotFound.getMessage());

        //Product deleted
        CpanelProductRecord productRecord = new CpanelProductRecord();
        productRecord.setState(ProductState.DELETED.getId());
        productRecord.setType(ProductType.VARIANT.getId());
        productRecord.setProductid(PRODUCT_ID.intValue());

        when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(productRecord);

        OneboxRestException productDeleted =
                Assertions.assertThrows(OneboxRestException.class, () ->
                        productVariantService.updateProductVariant(PRODUCT_ID, VARIANT_ID, updateProductVariantDTO));

        assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), productDeleted.getErrorCode());
        assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getMessage(), productDeleted.getMessage());

        //ProductVariant trying to update price in active products
        updateProductVariantDTO.setPrice(1.0);
        updateProductVariantDTO.setStatus(ProductVariantStatus.ACTIVE);
        productRecord.setState(ProductState.ACTIVE.getId());

        ProductVariantRecord productVariantRecord = new ProductVariantRecord();
        productVariantRecord.setVariantid(VARIANT_ID.intValue());
        productVariantRecord.setProductid(PRODUCT_ID.intValue());
        productVariantRecord.setStatus(1);

        when(productVariantDao.findById(PRODUCT_ID, VARIANT_ID)).thenReturn(productVariantRecord);

        OneboxRestException updateProductPrice =
                Assertions.assertThrows(OneboxRestException.class, () ->
                        productVariantService.updateProductVariant(PRODUCT_ID, VARIANT_ID, updateProductVariantDTO));

        assertEquals(MsEventErrorCode.PRODUCT_VARIANT_PRICE_AND_STOCK_NOT_UPDATABLE.getErrorCode(), updateProductPrice.getErrorCode());
        assertEquals(MsEventErrorCode.PRODUCT_VARIANT_PRICE_AND_STOCK_NOT_UPDATABLE.getMessage(), updateProductPrice.getMessage());

        //ProductVariant trying to update stock in active products
        updateProductVariantDTO.setStock(1L);
        productRecord.setState(ProductState.ACTIVE.getId());

        OneboxRestException updateProductStock =
                Assertions.assertThrows(OneboxRestException.class, () ->
                        productVariantService.updateProductVariant(PRODUCT_ID, VARIANT_ID, updateProductVariantDTO));

        assertEquals(MsEventErrorCode.PRODUCT_VARIANT_PRICE_AND_STOCK_NOT_UPDATABLE.getErrorCode(), updateProductStock.getErrorCode());
        assertEquals(MsEventErrorCode.PRODUCT_VARIANT_PRICE_AND_STOCK_NOT_UPDATABLE.getMessage(), updateProductStock.getMessage());
    }

    private static ProductVariantRecord getProductVariantRecord() {
        ProductVariantRecord productVariant = new ProductVariantRecord();
        productVariant.setProductName(PRODUCT_NAME);
        productVariant.setProductid(PRODUCT_ID.intValue());
        productVariant.setVariantid(VARIANT_ID.intValue());
        productVariant.setName(VARIANT_NAME);
        productVariant.setSku(SKU);
        productVariant.setPrice(PRICE);
        productVariant.setCreateDate(new Timestamp(((new Date().getTime()))));
        productVariant.setUpdateDate(new Timestamp(((new Date().getTime()))));

        return productVariant;
    }
}
