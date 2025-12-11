package es.onebox.event.products.service;

import es.onebox.utils.ObjectRandomizer;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.ProductAttributeContentsCouchDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductLanguageDao;
import es.onebox.event.products.dao.couch.ProductContentDocument;
import es.onebox.event.products.domain.ProductLanguageRecord;
import es.onebox.event.products.dto.UpdateProductLanguageDTO;
import es.onebox.event.products.dto.UpdateProductLanguagesDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelProductLanguageRecord;
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

class ProductLanguageServiceTest {
    @Mock
    ProductLanguageDao productLanguageDao;
    @Mock
    ProductDao productDao;
    @Mock
    ProductAttributeContentsCouchDao productContentsCouchDao;
    @Mock
    RefreshDataService refreshDataService;
    @Mock
    WebhookService webhookService;

    @InjectMocks
    ProductLanguageService productLanguageService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductLanguages() {
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        try {
            productLanguageService.getProductLanguages(ObjectRandomizer.randomLong());
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
            productLanguageService.getProductLanguages(ObjectRandomizer.randomLong());
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
        productLanguageService.getProductLanguages(ObjectRandomizer.randomLong());
    }

    @Test
    void updateProductLanguages() {
        Mockito.doNothing().when(refreshDataService).refreshProduct(Mockito.anyLong());
        Mockito.doNothing().when(webhookService).sendProductNotification(Mockito.anyLong(), Mockito.any());
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        try {
            productLanguageService.updateProductLanguages(ObjectRandomizer.randomLong(), ObjectRandomizer.random(UpdateProductLanguagesDTO.class));
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
        cpanelProductRecord.setType(1);
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(cpanelProductRecord);

        CpanelProductLanguageRecord cpanelProductLanguageRecord = new CpanelProductLanguageRecord();
        cpanelProductLanguageRecord.setLanguageid(1);
        cpanelProductLanguageRecord.setProductid(2);
        cpanelProductLanguageRecord.setDefaultlanguage(Byte.valueOf("1"));
        Mockito.doNothing().when(productLanguageDao).deleteByProduct(Mockito.anyLong());
        Mockito.when(productLanguageDao.insert(Mockito.any())).thenReturn(cpanelProductLanguageRecord);

        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(null);

        UpdateProductLanguagesDTO updateProductLanguagesDTO = new UpdateProductLanguagesDTO();
        UpdateProductLanguageDTO productLanguageDTO = new UpdateProductLanguageDTO();
        productLanguageDTO.setCode("CA-ES");
        productLanguageDTO.setLanguageId(1L);
        productLanguageDTO.setDefault(true);
        updateProductLanguagesDTO.add(productLanguageDTO);

        ProductContentDocument productContentDocument = ObjectRandomizer.random(ProductContentDocument.class);
        Mockito.when(productContentsCouchDao.get(Mockito.anyString())).thenReturn(productContentDocument);

        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(null);

        try {
            productLanguageService.updateProductLanguages(ObjectRandomizer.randomLong(), updateProductLanguagesDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        List<ProductLanguageRecord> productLanguageRecords = new ArrayList<>();
        ProductLanguageRecord productLanguageRecord = new ProductLanguageRecord();
        productLanguageRecord.setCode("ca_ES");
        productLanguageRecord.setLanguageid(1);
        productLanguageRecord.setProductid(2);
        productLanguageRecord.setDefaultlanguage(Byte.valueOf("0"));
        productLanguageRecords.add(productLanguageRecord);

        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(productLanguageRecords);


        productLanguageService.updateProductLanguages(ObjectRandomizer.randomLong(), updateProductLanguagesDTO);
    }

}
