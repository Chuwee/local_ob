package es.onebox.event.products.service;

import es.onebox.utils.ObjectRandomizer;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.ProductAttributeContentsCouchDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductLanguageDao;
import es.onebox.event.products.domain.ProductLanguageRecord;
import es.onebox.event.products.dto.ProductLiteralDTO;
import es.onebox.event.products.dto.ProductLiteralsDTO;
import es.onebox.event.products.dto.ProductValueLiteralDTO;
import es.onebox.event.products.dto.ProductValueLiteralsDTO;
import es.onebox.event.products.lock.HazelcastLockRepository;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductContentServiceTest {
    @Mock
    ProductAttributeContentsCouchDao productAttributeContentsCouchDao;
    @Mock
    ProductLanguageDao productLanguageDao;
    @Mock
    ProductDao productDao;
    @Mock
    RefreshDataService refreshDataService;
    @Mock
    HazelcastLockRepository hazelcastLockRepository;

    @InjectMocks
    ProductContentService productContentService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductAttributeContents() {
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        try {
            productContentService.getProductAttributeLiterals(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setProductid(1);
        cpanelProductRecord.setEntityid(2);
        cpanelProductRecord.setName("dfsdf");
        cpanelProductRecord.setProducerid(3);
        cpanelProductRecord.setState(1);
        cpanelProductRecord.setTaxid(4);
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(cpanelProductRecord);

        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(null);
        try {
            productContentService.getProductAttributeLiterals(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        List<ProductLanguageRecord> cpanelProductLanguageRecord = new ArrayList<>();
        ProductLanguageRecord productLanguageRecord = new ProductLanguageRecord();
        productLanguageRecord.setCode("ca_ES");
        productLanguageRecord.setLanguageid(1);
        productLanguageRecord.setProductid(2);
        productLanguageRecord.setDefaultlanguage(Byte.valueOf("0"));
        cpanelProductLanguageRecord.add(productLanguageRecord);
        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(cpanelProductLanguageRecord);
        productContentService.getProductAttributeLiterals(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), ObjectRandomizer.randomString());
    }

    @Test
    void updateProductAttributeContents() {
        Mockito.doNothing().when(refreshDataService).refreshProduct(Mockito.anyLong());
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        ProductLiteralDTO productLiteralDTO = ObjectRandomizer.random(ProductLiteralDTO.class);
        ProductLiteralsDTO productLiteralsDTO = new ProductLiteralsDTO();
        productLiteralsDTO.add(productLiteralDTO);
        try {
            productContentService.createOrUpdateProductAttributeLiterals(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), productLiteralsDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setProductid(1);
        cpanelProductRecord.setEntityid(2);
        cpanelProductRecord.setName("dfsdf");
        cpanelProductRecord.setProducerid(3);
        cpanelProductRecord.setState(1);
        cpanelProductRecord.setTaxid(4);
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(cpanelProductRecord);

        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(null);
        try {
            productContentService.createOrUpdateProductAttributeLiterals(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), productLiteralsDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        ProductLanguageRecord productLanguageRecord = new ProductLanguageRecord();
        productLanguageRecord.setLanguageid(1);
        productLanguageRecord.setProductid(2);
        productLanguageRecord.setDefaultlanguage(Byte.valueOf("1"));
        Mockito.when(productLanguageDao.findByProductId(Mockito.any())).thenReturn(List.of(productLanguageRecord));

        productContentService.createOrUpdateProductAttributeLiterals(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), productLiteralsDTO);
    }

    @Test
    void getProductValueContents() {
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        try {
            productContentService.getProductValueLiterals(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setProductid(1);
        cpanelProductRecord.setEntityid(2);
        cpanelProductRecord.setName("dfsdf");
        cpanelProductRecord.setProducerid(3);
        cpanelProductRecord.setState(1);
        cpanelProductRecord.setTaxid(4);
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(cpanelProductRecord);

        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(null);
        try {
            productContentService.getProductValueLiterals(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        List<ProductLanguageRecord> cpanelProductLanguageRecord = new ArrayList<>();
        ProductLanguageRecord productLanguageRecord = new ProductLanguageRecord();
        productLanguageRecord.setCode("ca_ES");
        productLanguageRecord.setLanguageid(1);
        productLanguageRecord.setProductid(2);
        productLanguageRecord.setDefaultlanguage(Byte.valueOf("0"));
        cpanelProductLanguageRecord.add(productLanguageRecord);
        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(cpanelProductLanguageRecord);
        productContentService.getProductValueLiterals(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), ObjectRandomizer.randomString());
    }

    @Test
    void getProductBulkValueContents() {
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        try {
            productContentService.getProductBulkValueLiterals(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setProductid(1);
        cpanelProductRecord.setEntityid(2);
        cpanelProductRecord.setName("dfsdf");
        cpanelProductRecord.setProducerid(3);
        cpanelProductRecord.setState(1);
        cpanelProductRecord.setTaxid(4);
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(cpanelProductRecord);

        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(null);
        try {
            productContentService.getProductBulkValueLiterals(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        List<ProductLanguageRecord> cpanelProductLanguageRecord = new ArrayList<>();
        ProductLanguageRecord productLanguageRecord = new ProductLanguageRecord();
        productLanguageRecord.setCode("ca_ES");
        productLanguageRecord.setLanguageid(1);
        productLanguageRecord.setProductid(2);
        productLanguageRecord.setDefaultlanguage(Byte.valueOf("0"));
        cpanelProductLanguageRecord.add(productLanguageRecord);
        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(cpanelProductLanguageRecord);
        productContentService.getProductBulkValueLiterals(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), ObjectRandomizer.randomString());
    }

    @Test
    void updateProductValueContents() {
        Mockito.doNothing().when(refreshDataService).refreshProduct(Mockito.anyLong());
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        ProductLiteralDTO productLiteralDTO = ObjectRandomizer.random(ProductLiteralDTO.class);
        ProductLiteralsDTO productLiteralsDTO = new ProductLiteralsDTO();
        productLiteralsDTO.add(productLiteralDTO);
        try {
            productContentService.createOrUpdateProductValueLiterals(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), productLiteralsDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setProductid(1);
        cpanelProductRecord.setEntityid(2);
        cpanelProductRecord.setName("dfsdf");
        cpanelProductRecord.setProducerid(3);
        cpanelProductRecord.setState(1);
        cpanelProductRecord.setTaxid(4);
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(cpanelProductRecord);

        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(null);
        try {
            productContentService.createOrUpdateProductValueLiterals(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), productLiteralsDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        ProductLanguageRecord productLanguageRecord = new ProductLanguageRecord();
        productLanguageRecord.setLanguageid(1);
        productLanguageRecord.setProductid(2);
        productLanguageRecord.setDefaultlanguage(Byte.valueOf("1"));
        Mockito.when(productLanguageDao.findByProductId(Mockito.any())).thenReturn(List.of(productLanguageRecord));

        productContentService.createOrUpdateProductValueLiterals(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), productLiteralsDTO);
    }

    @Test
    void updateProductBulkValueContents() {
        Mockito.doNothing().when(refreshDataService).refreshProduct(Mockito.anyLong());
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        ProductValueLiteralDTO productValueLiteralDTO = ObjectRandomizer.random(ProductValueLiteralDTO.class);
        ProductValueLiteralsDTO productValueLiteralsDTO = new ProductValueLiteralsDTO();
        productValueLiteralsDTO.add(productValueLiteralDTO);
        try {
            productContentService.createOrUpdateProductBulkValueLiterals(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), productValueLiteralsDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setProductid(1);
        cpanelProductRecord.setEntityid(2);
        cpanelProductRecord.setName("dfsdf");
        cpanelProductRecord.setProducerid(3);
        cpanelProductRecord.setState(1);
        cpanelProductRecord.setTaxid(4);
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(cpanelProductRecord);

        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(null);
        try {
            productContentService.createOrUpdateProductBulkValueLiterals(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), productValueLiteralsDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        ProductLanguageRecord productLanguageRecord = new ProductLanguageRecord();
        productLanguageRecord.setLanguageid(1);
        productLanguageRecord.setProductid(2);
        productLanguageRecord.setDefaultlanguage(Byte.valueOf("1"));
        Mockito.when(productLanguageDao.findByProductId(Mockito.any())).thenReturn(List.of(productLanguageRecord));

        productContentService.createOrUpdateProductBulkValueLiterals(ObjectRandomizer.randomLong(), ObjectRandomizer.randomLong(), productValueLiteralsDTO);
    }

}
