package es.onebox.event.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.dto.ProducerDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.events.enums.TaxModeDTO;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.simulation.record.ProductRecord;
import es.onebox.event.products.dao.ProductChannelDao;
import es.onebox.event.products.dao.ProductCommunicationElementCouchDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductDeliveryPointRelationDao;
import es.onebox.event.products.dao.ProductEventDao;
import es.onebox.event.products.dao.ProductEventDeliveryPointDao;
import es.onebox.event.products.dao.ProductLanguageDao;
import es.onebox.event.products.dao.ProductSessionDao;
import es.onebox.event.products.dao.ProductVariantDao;
import es.onebox.event.products.dao.ProductVariantStockCouchDao;
import es.onebox.event.products.dao.couch.ProductCommunicationElement;
import es.onebox.event.products.dao.couch.ProductCommunicationElementDetail;
import es.onebox.event.products.dao.couch.ProductCommunicationElementDocument;
import es.onebox.event.products.domain.ProductChannelRecord;
import es.onebox.event.products.domain.ProductDeliveryPointRelationRecord;
import es.onebox.event.products.domain.ProductEventRecord;
import es.onebox.event.products.domain.ProductLanguageRecord;
import es.onebox.event.products.dto.CreateProductDTO;
import es.onebox.event.products.dto.ProductsDTO;
import es.onebox.event.products.dto.SearchProductFilterDTO;
import es.onebox.event.products.dto.UpdateProductDTO;
import es.onebox.event.products.enums.ProductCommunicationElementTextsType;
import es.onebox.event.products.enums.ProductCommunicationElementsImagesType;
import es.onebox.event.products.enums.ProductState;
import es.onebox.event.products.enums.ProductStockType;
import es.onebox.event.products.enums.ProductType;
import es.onebox.event.products.enums.ProductVariantStatus;
import es.onebox.event.products.helper.ProductHelper;
import es.onebox.event.sessions.dao.TaxDao;
import es.onebox.event.surcharges.product.ProductSurchargesService;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantRecord;
import es.onebox.utils.ObjectRandomizer;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceTest {
    private static final Long ENTITY_ID = 1L;
    private static final Long PRODUCT_ID = 1L;
    private static final Long TAX_ID = 1L;
    private static final Long SURCHARGE_TAX_ID = 2L;
    private static final Long AVAILABLE_TAX_ID = 22L;
    private static final Long PRODUCER_ID = 1L;
    private static final String PRODUCT_NAME = "New Product";
    private static final String OLD_PRODUCT_VARIANT_NAME = "Old Product variant name";
    private static final String TAX_NAME = "Tax";
    private static final String SURCHARGE_TAX_NAME = "SurchargeTax";
    private static final String PRODUCER_NAME = "Producer";

    @Mock
    private ProductDao productDao;
    @Mock
    private TaxDao taxDao;

    @Mock
    private EntitiesRepository entitiesRepository;
    @Mock
    private ProductVariantDao productVariantDao;
    @Mock
    private ProductVariantStockCouchDao productVariantStockCouchDao;
    @Mock
    private ProductLanguageDao productLanguageDao;
    @Mock
    private ProductCommunicationElementCouchDao productCommunicationElementCouchDao;
    @Mock
    private ProductEventDeliveryPointDao productEventDeliveryPointDao;
    @Mock
    private ProductChannelDao productChannelDao;
    @Mock
    private ProductEventDao productEventDao;
    @Mock
    private ProductDeliveryPointRelationDao productDeliveryPointRelationDao;
    @Mock
    private RefreshDataService refreshDataService;
    @Mock
    private ProductSessionDao productSessionDao;
    @Mock
    private ProductHelper productHelper;
    @Mock
    private ProductSurchargesService productSurchargesService;
    @Mock
    private WebhookService webhookService;
    @InjectMocks
    private ProductService productService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProduct() {
        Mockito.doNothing().when(refreshDataService).refreshProduct(Mockito.anyLong());
        CreateProductDTO request = new CreateProductDTO();
        request.setName(PRODUCT_NAME);
        request.setEntityId(ENTITY_ID);
        request.setProducerId(PRODUCER_ID);
        request.setStockType(ProductStockType.UNBOUNDED);
        request.setProductType(ProductType.SIMPLE);
        request.setCurrencyId(1L);

        EntityDTO entityDTO = new EntityDTO();
        entityDTO.setId(ENTITY_ID.intValue());

        CpanelProductRecord productRecord = new CpanelProductRecord();
        productRecord.setState(ProductState.INACTIVE.getId());
        productRecord.setType(ProductType.SIMPLE.getId());
        productRecord.setName(request.getName());
        productRecord.setEntityid(request.getEntityId().intValue());
        productRecord.setProducerid(request.getProducerId().intValue());
        productRecord.setType(ProductType.SIMPLE.getId());
        productRecord.setIdcurrency(request.getCurrencyId().intValue());

        CpanelProductRecord expectedProductRecord = new CpanelProductRecord();
        expectedProductRecord.setProductid(PRODUCT_ID.intValue());
        expectedProductRecord.setType(ProductType.SIMPLE.getId());

        when(productDao.findProducts(anyLong(), anyString())).thenReturn(null);
        when(productDao.insert(any())).thenReturn(expectedProductRecord);
        when(entitiesRepository.getEntity(ENTITY_ID.intValue())).thenReturn(entityDTO);
        when(entitiesRepository.getProducer(PRODUCER_ID.intValue())).thenReturn(new ProducerDTO());
        productSurchargesService.initProductSurcharges(productRecord);
        Long productId = productService.createProduct(request);

        assertEquals(PRODUCT_ID.longValue(), productId);
    }

    @Test
    void createProductMissingProducerIdException() {
        Mockito.doNothing().when(refreshDataService).refreshProduct(Mockito.anyLong());
        CreateProductDTO request = new CreateProductDTO();
        request.setName(PRODUCT_NAME);
        request.setEntityId(ENTITY_ID);

        OneboxRestException exception =
                Assertions.assertThrows(OneboxRestException.class, () -> productService.createProduct(request));

        assertEquals(MsEventErrorCode.ENTITY_NOT_FOUND.name(), exception.getErrorCode());
        assertEquals(MsEventErrorCode.ENTITY_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void updateProductMissingProductException() {
        Mockito.doNothing().when(refreshDataService).refreshProduct(Mockito.anyLong());
        UpdateProductDTO request = new UpdateProductDTO();

        OneboxRestException exception =
                Assertions.assertThrows(OneboxRestException.class, () ->
                        productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), exception.getErrorCode());
        assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getMessage(), exception.getMessage());
    }

    @Test
    void updateTaxOnActiveProductException() {
        Mockito.doNothing().when(refreshDataService).refreshProduct(anyLong());
        UpdateProductDTO request = new UpdateProductDTO();
        request.setTaxId(TAX_ID);

        ProductRecord productRecord = new ProductRecord();
        productRecord.setEntityid(1);
        productRecord.setType(2);
        productRecord.setState(ProductState.ACTIVE.getId());

        when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(productRecord);
        when(productDao.findProducts(anyLong(), anyString())).thenReturn(null);

        OneboxRestException exception =
                Assertions.assertThrows(OneboxRestException.class, () ->
                        productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.TAX_NOT_UPDATABLE_ON_PRODUCT.getErrorCode(), exception.getErrorCode());
        assertEquals(MsEventErrorCode.TAX_NOT_UPDATABLE_ON_PRODUCT.getMessage(), exception.getMessage());
    }

    @Test
    void updateTaxModeOnActiveProductException() {
        Mockito.doNothing().when(refreshDataService).refreshProduct(anyLong());
        UpdateProductDTO request = new UpdateProductDTO();
        request.setTaxMode(es.onebox.event.products.enums.TaxModeDTO.ON_TOP);

        ProductRecord productRecord = new ProductRecord();
        productRecord.setEntityid(1);
        productRecord.setType(2);
        productRecord.setState(ProductState.ACTIVE.getId());
        productRecord.setTaxmode(es.onebox.event.products.enums.TaxModeDTO.INCLUDED.getId());

        when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(productRecord);
        when(productDao.findProducts(anyLong(), anyString())).thenReturn(null);

        OneboxRestException exception =
                Assertions.assertThrows(OneboxRestException.class, () ->
                        productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.PRODUCT_UPDATE_TAX_MODE_INVALID_PRODUCT_STATUS.getErrorCode(), exception.getErrorCode());
        assertEquals(MsEventErrorCode.PRODUCT_UPDATE_TAX_MODE_INVALID_PRODUCT_STATUS.getMessage(), exception.getMessage());
    }

    @Test
    void updateTaxModeOnProductWithSalesException() {
        Mockito.doNothing().when(refreshDataService).refreshProduct(anyLong());
        UpdateProductDTO request = new UpdateProductDTO();
        request.setTaxMode(es.onebox.event.products.enums.TaxModeDTO.INCLUDED);

        ProductRecord productRecord = new ProductRecord();
        productRecord.setEntityid(1);
        productRecord.setType(2);
        productRecord.setState(ProductState.ACTIVE.getId());
        productRecord.setTaxmode(es.onebox.event.products.enums.TaxModeDTO.ON_TOP.getId());

        when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(productRecord);
        when(productDao.findProducts(anyLong(), anyString())).thenReturn(null);
        when(productHelper.checkProductOrVariantSales(anyList())).thenReturn(true);

        OneboxRestException exception =
                Assertions.assertThrows(OneboxRestException.class, () ->
                        productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.TAX_MODE_NOT_UPDATABLE_ON_PRODUCT.getErrorCode(), exception.getErrorCode());
        assertEquals(MsEventErrorCode.TAX_MODE_NOT_UPDATABLE_ON_PRODUCT.getMessage(), exception.getMessage());
    }

    @Test
    void updateProduct() {
        ProductEventRecord productEventRecord1 = new ProductEventRecord();
        productEventRecord1.setEventid(1);
        productEventRecord1.setProductid(2);
        productEventRecord1.setProducteventid(12);
        productEventRecord1.setSessionsselectiontype(0);
        Mockito.when(productEventDao.findByProductIdAndEventId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(productEventRecord1);
        Mockito.doNothing().when(refreshDataService).refreshProduct(Mockito.anyLong());
        UpdateProductDTO request = new UpdateProductDTO();
        request.setProductState(ProductState.ACTIVE);
        request.setName(PRODUCT_NAME);
        request.setTaxId(TAX_ID);

        CpanelProductVariantRecord oldProductVariant = new CpanelProductVariantRecord();
        oldProductVariant.setProductid(PRODUCT_ID.intValue());
        oldProductVariant.setVariantid(12);
        oldProductVariant.setName(OLD_PRODUCT_VARIANT_NAME);

        List<Long> taxList = List.of(TAX_ID);

        ProductRecord productRecord = getProductRecord();
        productRecord.setStocktype(ProductStockType.UNBOUNDED.getId());
        productRecord.setTaxmode(TaxModeDTO.INCLUDED.getId());
        when(productDao.findProducts(anyLong(), anyString())).thenReturn(null);
        when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(productRecord);
        when(taxDao.getTaxesByEntity(ENTITY_ID)).thenReturn(taxList);
        when(productDao.insert(any())).thenReturn(productRecord);

        when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(getProductLanguageRecords(PRODUCT_ID, "es-ES"));
        when(productCommunicationElementCouchDao.get(Mockito.anyString())).thenReturn(getProductCommunicationElements(PRODUCT_ID, "es-ES"));
        when(productChannelDao.findByProductId(PRODUCT_ID)).thenReturn(getProductChannelRecords(PRODUCT_ID, 2));
        when(productEventDao.findByProductId(PRODUCT_ID.intValue(), false)).thenReturn(getProductEventRecords(PRODUCT_ID, 2));
        when(productVariantDao.getProductVariantsByProductId(PRODUCT_ID)).thenReturn(getProductVariants(PRODUCT_ID, 1, ProductVariantStatus.ACTIVE));
        productService.updateProduct(PRODUCT_ID, request);
    }

    @Test
    void updateProductNoVariantFoundException() {
        Mockito.doNothing().when(refreshDataService).refreshProduct(Mockito.anyLong());
        UpdateProductDTO request = new UpdateProductDTO();
        request.setProductState(ProductState.ACTIVE);
        request.setName(PRODUCT_NAME);
        request.setTaxId(TAX_ID);
        request.setSurchargeTaxId(SURCHARGE_TAX_ID);

        List<Long> taxList = List.of(TAX_ID);

        ProductRecord productRecord = getProductRecord();
        productRecord.setStocktype(ProductStockType.UNBOUNDED.getId());
        productRecord.setTaxmode(TaxModeDTO.INCLUDED.getId());
        when(productDao.findProducts(anyLong(), anyString())).thenReturn(null);
        when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(productRecord);
        when(productVariantDao.getProductVariantsByProductId(PRODUCT_ID)).thenReturn(getProductVariants(PRODUCT_ID, 2, ProductVariantStatus.ACTIVE));
        when(taxDao.getTaxesByEntity(ENTITY_ID)).thenReturn(taxList);
        when(productDao.insert(any())).thenReturn(productRecord);

        when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(getProductLanguageRecords(PRODUCT_ID, "es-ES"));
        when(productCommunicationElementCouchDao.get(Mockito.anyString())).thenReturn(getProductCommunicationElements(PRODUCT_ID, "es-ES"));
        when(productChannelDao.findByProductId(PRODUCT_ID)).thenReturn(getProductChannelRecords(PRODUCT_ID, 2));
        when(productEventDao.findByProductId(PRODUCT_ID.intValue(), false)).thenReturn(getProductEventRecords(PRODUCT_ID, 2));

        OneboxRestException exception =
                Assertions.assertThrows
                        (OneboxRestException.class, () -> productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.ILLEGAL_VARIANT_AMOUNT.name(), exception.getErrorCode());
        assertEquals(MsEventErrorCode.ILLEGAL_VARIANT_AMOUNT.getMessage(), exception.getMessage());
    }

    @Test
    void updateProductNotValidTaxException() {
        Mockito.doNothing().when(refreshDataService).refreshProduct(Mockito.anyLong());
        UpdateProductDTO request = new UpdateProductDTO();
        request.setTaxId(AVAILABLE_TAX_ID);

        List<Long> taxList = List.of(TAX_ID);

        ProductRecord productRecord = new ProductRecord();
        productRecord.setProductid(PRODUCT_ID.intValue());
        productRecord.setState(ProductState.INACTIVE.getId());
        productRecord.setEntityid(ENTITY_ID.intValue());
        productRecord.setProducerid(PRODUCER_ID.intValue());
        productRecord.setName(PRODUCT_NAME);
        productRecord.setTaxid(TAX_ID.intValue());
        productRecord.setTaxName(TAX_NAME);

        when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(productRecord);
        when(productDao.findProducts(anyLong(), anyString())).thenReturn(null);
        when(taxDao.getTaxesByEntity(ENTITY_ID)).thenReturn(taxList);

        OneboxRestException exception =
                Assertions.assertThrows(OneboxRestException.class, () ->
                        productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.INVALID_ENTITY_TAX.getErrorCode(), exception.getErrorCode());
        assertEquals(MsEventErrorCode.INVALID_ENTITY_TAX.getMessage(), exception.getMessage());
    }

    @Test
    void updateProductNotValidSurchargeTaxException() {
        UpdateProductDTO request = new UpdateProductDTO();
        request.setSurchargeTaxId(AVAILABLE_TAX_ID);

        List<Long> taxList = List.of(TAX_ID);

        ProductRecord productRecord = new ProductRecord();
        productRecord.setProductid(PRODUCT_ID.intValue());
        productRecord.setState(ProductState.INACTIVE.getId());
        productRecord.setEntityid(ENTITY_ID.intValue());
        productRecord.setProducerid(PRODUCER_ID.intValue());
        productRecord.setName(PRODUCT_NAME);
        productRecord.setSurchagetaxid(SURCHARGE_TAX_ID.intValue());
        productRecord.setSurchargeTaxName(SURCHARGE_TAX_NAME);

        when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(productRecord);
        when(productDao.findProducts(anyLong(), anyString())).thenReturn(null);
        when(taxDao.getTaxesByEntity(ENTITY_ID)).thenReturn(taxList);

        OneboxRestException exception =
                Assertions.assertThrows(OneboxRestException.class, () ->
                        productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.INVALID_ENTITY_SURCHARGE_TAX.getErrorCode(), exception.getErrorCode());
        assertEquals(MsEventErrorCode.INVALID_ENTITY_SURCHARGE_TAX.getMessage(), exception.getMessage());
    }

    @Test
    void activateProductValidations() {
        ProductEventRecord productEventRecord1 = new ProductEventRecord();
        productEventRecord1.setEventid(1);
        productEventRecord1.setProductid(2);
        productEventRecord1.setProducteventid(12);
        productEventRecord1.setSessionsselectiontype(0);
        Mockito.when(productEventDao.findByProductIdAndEventId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(productEventRecord1);
        Mockito.doNothing().when(refreshDataService).refreshProduct(Mockito.anyLong());
        UpdateProductDTO request = new UpdateProductDTO();
        request.setProductState(ProductState.ACTIVE);

        ProductRecord productRecord = new ProductRecord();
        productRecord.setProductid(PRODUCT_ID.intValue());
        productRecord.setState(ProductState.INACTIVE.getId());
        productRecord.setEntityid(ENTITY_ID.intValue());
        productRecord.setProducerid(PRODUCER_ID.intValue());
        productRecord.setName(PRODUCT_NAME);
        productRecord.setStocktype(ProductStockType.BOUNDED.getId());
        productRecord.setType(ProductType.SIMPLE.getId());

        when(productDao.findProducts(anyLong(), anyString())).thenReturn(null);
        when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(productRecord);

        OneboxRestException exception = Assertions.assertThrows(OneboxRestException.class, () ->
                productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.INVALID_PRODUCT_TAX_MODE.getErrorCode(), exception.getErrorCode());

        productRecord.setTaxmode(TaxModeDTO.INCLUDED.getId());

        exception = Assertions.assertThrows(OneboxRestException.class, () ->
                productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.PRODUCT_TAX_NOT_FOUND.getErrorCode(), exception.getErrorCode());

        productRecord.setTaxid(1);

        exception = Assertions.assertThrows(OneboxRestException.class, () ->
                productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.PRODUCT_SURCHARGE_TAX_NOT_FOUND.getErrorCode(), exception.getErrorCode());

        productRecord.setSurchagetaxid(2);

        exception = Assertions.assertThrows(OneboxRestException.class, () ->
                productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.PRODUCT_DELIVERY_TYPE_REQUIRED.getErrorCode(), exception.getErrorCode());

        productRecord.setDeliverytype(1);

        exception = Assertions.assertThrows(OneboxRestException.class, () ->
                productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.PRODUCT_DELIVERY_PERIOD_REQUIRED.getErrorCode(), exception.getErrorCode());

        productRecord.setDeliverystarttimeunit(1);
        productRecord.setDeliveryendtimeunit(1);
        productRecord.setDeliverystarttimevalue(10);
        productRecord.setDeliveryendtimevalue(10);

        productRecord.setDeliverytype(2);
        exception = Assertions.assertThrows(OneboxRestException.class, () ->
                productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.PRODUCT_DELIVERY_POINT_RELATION_NOT_FOUND.getErrorCode(), exception.getErrorCode());

        when(productDeliveryPointRelationDao.findByProductId(PRODUCT_ID)).thenReturn(getProductDeliveryPointRelationRecord(PRODUCT_ID, 2));

        productRecord.setDeliverytype(1);
        when(productEventDeliveryPointDao.existsProductEventWithoutDelivery(PRODUCT_ID.intValue())).thenReturn(true);
        exception = Assertions.assertThrows(OneboxRestException.class, () ->
                productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.PRODUCT_EVENT_DELIVERY_POINT_DEFAULT_REQUIRED.getErrorCode(), exception.getErrorCode());

        when(productEventDeliveryPointDao.existsProductEventWithoutDelivery(PRODUCT_ID.intValue())).thenReturn(false);
        exception = Assertions.assertThrows(OneboxRestException.class, () ->
                productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.PRODUCT_LANGUAGE_DEFAULT_REQUIRED.getErrorCode(), exception.getErrorCode());

        when(productLanguageDao.findByProductId(PRODUCT_ID)).thenReturn(getProductLanguageRecords(PRODUCT_ID, "es-ES"));

        when(productCommunicationElementCouchDao.get(Mockito.anyString())).thenReturn(getProductCommunicationElements(PRODUCT_ID, "es-ES"));
        exception = Assertions.assertThrows(OneboxRestException.class, () ->
                productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.PRODUCT_CHANNELS_NOT_FOUND.getErrorCode(), exception.getErrorCode());

        when(productChannelDao.findByProductId(PRODUCT_ID)).thenReturn(getProductChannelRecords(PRODUCT_ID, 2));
        exception = Assertions.assertThrows(OneboxRestException.class, () ->
                productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.PRODUCT_EVENTS_REQUIRED.getErrorCode(), exception.getErrorCode());

        when(productEventDao.findByProductId(PRODUCT_ID.intValue(), false)).thenReturn(getProductEventRecords(PRODUCT_ID, 2));
        exception = Assertions.assertThrows(OneboxRestException.class, () ->
                productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.PRODUCT_VARIANT_NOT_FOUND.getErrorCode(), exception.getErrorCode());

        productRecord.setType(ProductType.VARIANT.getId());
        when(productVariantDao.getProductVariantsByProductId(PRODUCT_ID)).thenReturn(getProductVariants(PRODUCT_ID, 1, ProductVariantStatus.INACTIVE));
        exception = Assertions.assertThrows(OneboxRestException.class, () ->
                productService.updateProduct(PRODUCT_ID, request));

        assertEquals(MsEventErrorCode.PRODUCT_MISSING_ACTIVE_VARIANT.getErrorCode(), exception.getErrorCode());

        when(productVariantDao.getProductVariantsByProductId(PRODUCT_ID)).thenReturn(getProductVariants(PRODUCT_ID, 1, ProductVariantStatus.ACTIVE));
        when(productVariantStockCouchDao.get(anyLong(), anyLong())).thenReturn(1L);

        productService.updateProduct(PRODUCT_ID, request);
    }


    @Test
    void deleteProduct() {
        Mockito.doNothing().when(refreshDataService).refreshProduct(anyLong());
        when(productDao.findById(any())).thenReturn(null);
        try {
            productService.deleteProduct(anyLong());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelProductRecord productRecord = new CpanelProductRecord();
        productRecord.setName(PRODUCT_NAME);
        productRecord.setProductid(PRODUCT_ID.intValue());
        productRecord.setState(ProductState.INACTIVE.getId());

        when(productDao.findById(any())).thenReturn(productRecord);

        productService.deleteProduct(PRODUCT_ID);

        verify(productDao, times(1)).update(productRecord);
    }

    @Test
    void searchProducts() {

        Mockito.when(productDao.getProducts(any())).thenReturn(null);

        SearchProductFilterDTO filter = ObjectRandomizer.random(SearchProductFilterDTO.class);
        try {
            productService.searchProducts(filter);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        List<ProductRecord> result = getProductRecords();

        Mockito.when(productDao.getProducts(any())).thenReturn(result);

        SearchProductFilterDTO searchProductFilterDTO = ObjectRandomizer.random(SearchProductFilterDTO.class);
        ProductsDTO productsDTO = productService.searchProducts(searchProductFilterDTO);

        assertNotNull(productsDTO);
        assertNotNull(productsDTO.getData());
        assertEquals(2, productsDTO.getData().size());
    }

    private static List<ProductRecord> getProductRecords() {
        List<ProductRecord> result = new ArrayList<>();
        ProductRecord productRecord = new ProductRecord();
        productRecord.setName("Product 1");
        productRecord.setProductid(1);
        productRecord.setEntityid(1);
        productRecord.setProducerid(1);
        productRecord.setState(1);
        productRecord.setType(1);
        productRecord.setIdcurrency(1);
        productRecord.setStocktype(1);
        result.add(productRecord);
        ProductRecord productRecord2 = new ProductRecord();
        productRecord2.setName("Product 2");
        productRecord2.setProductid(2);
        productRecord2.setState(1);
        productRecord2.setEntityid(2);
        productRecord2.setProducerid(1);
        productRecord2.setType(1);
        productRecord2.setStocktype(1);
        productRecord2.setIdcurrency(1);
        result.add(productRecord2);

        return result;
    }

    private static ProductRecord getProductRecord() {
        ProductRecord productRecord = new ProductRecord();
        productRecord.setProductid(PRODUCT_ID.intValue());
        productRecord.setState(ProductState.INACTIVE.getId());
        productRecord.setEntityid(ENTITY_ID.intValue());
        productRecord.setName(PRODUCT_NAME);
        productRecord.setType(ProductType.SIMPLE.getId());
        productRecord.setTaxid(TAX_ID.intValue());
        productRecord.setTaxName(TAX_NAME);
        productRecord.setSurchagetaxid(SURCHARGE_TAX_ID.intValue());
        productRecord.setTaxName(SURCHARGE_TAX_NAME);
        productRecord.setProducerid(PRODUCER_ID.intValue());
        productRecord.setProducerName(PRODUCER_NAME);
        productRecord.setStocktype(1);
        productRecord.setDeliverytype(1);
        productRecord.setDeliverystarttimeunit(1);
        productRecord.setDeliverystarttimevalue(10);
        productRecord.setDeliveryendtimeunit(1);
        productRecord.setDeliveryendtimevalue(10);
        return productRecord;
    }

    private List<ProductChannelRecord> getProductChannelRecords(Long productId, int amount) {
        List<ProductChannelRecord> result = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            ProductChannelRecord p = new ProductChannelRecord();
            p.setProductid(productId.intValue());
            p.setChannelid(i + 1);
            result.add(p);
        }
        return result;
    }

    private List<ProductEventRecord> getProductEventRecords(Long productId, int amount) {
        List<ProductEventRecord> result = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            ProductEventRecord p = new ProductEventRecord();
            p.setProductid(productId.intValue());
            p.setEventid(i + 1);
            p.setSessionsselectiontype(0);
            result.add(p);
        }
        return result;
    }

    private List<CpanelProductVariantRecord> getProductVariants(Long productId, int variantsAmount, ProductVariantStatus variantStatus) {
        List<CpanelProductVariantRecord> variantRecords = new ArrayList<>();
        for (int i = 0; i < variantsAmount; i++) {
            CpanelProductVariantRecord variant = new CpanelProductVariantRecord();
            variant.setProductid(productId.intValue());
            variant.setVariantid(i + 1);
            variant.setStatus(variantStatus.getId());
            variant.setPrice(10D + i);
            variantRecords.add(variant);
        }
        return variantRecords;
    }

    private List<ProductLanguageRecord> getProductLanguageRecords(Long productId, String... langs) {
        List<ProductLanguageRecord> result = new ArrayList<>();
        boolean first = true;
        for (String l : langs) {
            ProductLanguageRecord productLanguageRecord = new ProductLanguageRecord();
            productLanguageRecord.setProductid(productId.intValue());
            productLanguageRecord.setCode(l);
            if (first) {
                productLanguageRecord.setDefaultlanguage((byte) 1);
                first = false;
            }
            result.add(productLanguageRecord);
        }
        return result;
    }

    private ProductCommunicationElementDocument getProductCommunicationElements(Long productId, String... langs) {
        ProductCommunicationElementDocument result = new ProductCommunicationElementDocument();
        result.setProductId(PRODUCT_ID);
        result.setLanguageElements(new HashMap<>());
        for (String l : langs) {
            result.getLanguageElements().put(l, new ProductCommunicationElement());
            result.getLanguageElements().get(l).setTexts(new ArrayList<>());
            result.getLanguageElements().get(l).getTexts().add(new ProductCommunicationElementDetail());
            result.getLanguageElements().get(l).getTexts().get(0).setType(ProductCommunicationElementTextsType.PRODUCT_NAME.name());
            result.getLanguageElements().get(l).getTexts().get(0).setValue(RandomStringUtils.random(10));

            result.getLanguageElements().get(l).setImages(new ArrayList<>());
            result.getLanguageElements().get(l).getImages().add(new ProductCommunicationElementDetail());
            result.getLanguageElements().get(l).getImages().get(0).setType(ProductCommunicationElementsImagesType.LANDSCAPE.name());
            result.getLanguageElements().get(l).getImages().get(0).setValue(RandomStringUtils.random(10));
            result.getLanguageElements().get(l).getImages().get(0).setPosition(1);
            result.getLanguageElements().get(l).getImages().add(new ProductCommunicationElementDetail());
            result.getLanguageElements().get(l).getImages().get(1).setType(ProductCommunicationElementsImagesType.LANDSCAPE.name());
            result.getLanguageElements().get(l).getImages().get(1).setValue(RandomStringUtils.random(10));
            result.getLanguageElements().get(l).getImages().get(1).setPosition(2);
        }
        return result;
    }

    private List<ProductDeliveryPointRelationRecord> getProductDeliveryPointRelationRecord(Long productId, int amount) {
        List<ProductDeliveryPointRelationRecord> result = new ArrayList<>();
        for (int i = 0; i < amount; i++) {
            ProductDeliveryPointRelationRecord p = new ProductDeliveryPointRelationRecord();
            p.setProductid(productId.intValue());
            p.setDeliverypointid(i + 1);
            result.add(p);
        }
        return result;
    }


}
