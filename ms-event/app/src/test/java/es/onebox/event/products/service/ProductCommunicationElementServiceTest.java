package es.onebox.event.products.service;

import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.utils.ObjectRandomizer;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.ProductCommunicationElementCouchDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductLanguageDao;
import es.onebox.event.products.dao.couch.ProductCommunicationElement;
import es.onebox.event.products.dao.couch.ProductCommunicationElementDetail;
import es.onebox.event.products.dao.couch.ProductCommunicationElementDocument;
import es.onebox.event.products.domain.ProductLanguageRecord;
import es.onebox.event.products.dto.CreateProductCommunicationElementImageDTO;
import es.onebox.event.products.dto.CreateProductCommunicationElementTextDTO;
import es.onebox.event.products.dto.CreateProductCommunicationElementsImagesDTO;
import es.onebox.event.products.dto.CreateProductCommunicationElementsTextsDTO;
import es.onebox.event.products.enums.ProductCommunicationElementTextsType;
import es.onebox.event.products.enums.ProductCommunicationElementsImagesType;
import es.onebox.event.products.lock.HazelcastLockRepository;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductCommunicationElementServiceTest {
    @Mock
    ProductLanguageDao productLanguageDao;
    @Mock
    ProductDao productDao;
    @Mock
    ProductCommunicationElementCouchDao productCommunicationElementCouchDao;
    @Mock
    S3BinaryRepository s3BinaryRepository;
    @Mock
    StaticDataContainer staticDataContainer;
    @Mock
    EntitiesRepository entitiesRepository;
    @Mock
    RefreshDataService refreshDataService;
    @Mock
    HazelcastLockRepository hazelcastLockRepository;
    @Mock
    WebhookService webhookService;
    @InjectMocks
    ProductCommunicationElementService productCommunicationElementService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductCommunicationElementsTexts() {
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        try {
            productCommunicationElementService.getProductCommunicationElementsTexts(ObjectRandomizer.randomLong());
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
            productCommunicationElementService.getProductCommunicationElementsTexts(ObjectRandomizer.randomLong());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(new ArrayList<>());

        try {
            productCommunicationElementService.getProductCommunicationElementsTexts(ObjectRandomizer.randomLong());
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

        ProductCommunicationElementDocument productCommunicationElementDocument = new ProductCommunicationElementDocument();
        productCommunicationElementDocument.setProductId(ObjectRandomizer.randomLong());
        productCommunicationElementDocument.setLanguageElements(new HashMap<>());
        ProductCommunicationElement productCommunicationElement = new ProductCommunicationElement();
        productCommunicationElement.setTexts(new ArrayList<>());
        ProductCommunicationElementDetail productCommunicationElementDetail = new ProductCommunicationElementDetail();
        productCommunicationElementDetail.setType("DESCRIPTION");
        productCommunicationElementDetail.setValue("dfgdfgdfg");
        productCommunicationElement.getTexts().add(productCommunicationElementDetail);
        productCommunicationElementDocument.getLanguageElements().put("ca_ES", productCommunicationElement);
        Mockito.when(productCommunicationElementCouchDao.get(Mockito.anyString())).thenReturn(productCommunicationElementDocument);

        productCommunicationElementService.getProductCommunicationElementsTexts(ObjectRandomizer.randomLong());
    }

    @Test
    void createProductCommunicationElementsTexts() {
        Mockito.doNothing().when(refreshDataService).refreshProduct(Mockito.anyLong());
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);

        CreateProductCommunicationElementsTextsDTO createProductCommunicationElementsTexts = new CreateProductCommunicationElementsTextsDTO();
        CreateProductCommunicationElementTextDTO createProductCommunicationElementText = new CreateProductCommunicationElementTextDTO();
        createProductCommunicationElementText.setType(ProductCommunicationElementTextsType.PRODUCT_NAME);
        createProductCommunicationElementText.setLanguage("ca-ES");
        createProductCommunicationElementText.setValue("text");
        createProductCommunicationElementsTexts.add(createProductCommunicationElementText);
        try {
            productCommunicationElementService.createProductCommunicationElementsTexts(ObjectRandomizer.randomLong(), createProductCommunicationElementsTexts);
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

        Mockito.doNothing().when(productCommunicationElementCouchDao).upsert(Mockito.anyString(), Mockito.any());

        ProductCommunicationElementDocument productCommunicationElementDocument = new ProductCommunicationElementDocument();
        productCommunicationElementDocument.setProductId(ObjectRandomizer.randomLong());
        productCommunicationElementDocument.setLanguageElements(new HashMap<>());
        ProductCommunicationElement productCommunicationElement = new ProductCommunicationElement();
        productCommunicationElement.setTexts(new ArrayList<>());
        ProductCommunicationElementDetail productCommunicationElementDetail = new ProductCommunicationElementDetail();
        productCommunicationElementDetail.setType("DESCRIPTION");
        productCommunicationElementDetail.setValue("dfgdfgdfg");
        productCommunicationElement.getTexts().add(productCommunicationElementDetail);
        productCommunicationElementDocument.getLanguageElements().put("ca_ES", productCommunicationElement);

        Mockito.when(productCommunicationElementCouchDao.get(Mockito.anyString())).thenReturn(productCommunicationElementDocument);

        productCommunicationElementService.createProductCommunicationElementsTexts(ObjectRandomizer.randomLong(), createProductCommunicationElementsTexts);
    }

    @Test
    void deleteProductCommunicationElementsImages() {
        Mockito.doNothing().when(refreshDataService).refreshProduct(Mockito.anyLong());
        Long productId = ObjectRandomizer.randomLong();
        String language = "ca_ES";

        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);

        try {
            productCommunicationElementService.deleteProductCommunicationElementsImages(productId, language, ProductCommunicationElementsImagesType.LANDSCAPE, 0L);
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
            productCommunicationElementService.deleteProductCommunicationElementsImages(productId, language, ProductCommunicationElementsImagesType.LANDSCAPE, 0L);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        try {
            productCommunicationElementService.deleteProductCommunicationElementsImages(productId, language, ProductCommunicationElementsImagesType.LANDSCAPE, 0L);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        List<ProductLanguageRecord> list = new ArrayList<>();
        ProductLanguageRecord productLanguageRecord = new ProductLanguageRecord();
        productLanguageRecord.setProductid(productId.intValue());
        productLanguageRecord.setLanguageid(1);
        productLanguageRecord.setDefaultlanguage(Byte.valueOf("0"));
        productLanguageRecord.setCode("en_US");
        list.add(productLanguageRecord);
        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(list);
        try {
            productCommunicationElementService.deleteProductCommunicationElementsImages(productId, language, ProductCommunicationElementsImagesType.LANDSCAPE, 0L);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_LANGUAGE_NOT_RELATED.getErrorCode(), e.getErrorCode());
        }

        list.get(0).setCode("ca_ES");
        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(list);

        Mockito.when(productCommunicationElementCouchDao.get(productId.toString())).thenReturn(null);
        try {
            productCommunicationElementService.deleteProductCommunicationElementsImages(productId, language, ProductCommunicationElementsImagesType.LANDSCAPE, 0L);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_COMMUNICATIONS_ELEMENTS_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        ProductCommunicationElementDocument productCommunicationElementDocument = new ProductCommunicationElementDocument();
        productCommunicationElementDocument.setProductId(productId);
        productCommunicationElementDocument.setLanguageElements(new HashMap<>());
        ProductCommunicationElement productCommunicationElement = new ProductCommunicationElement();
        ProductCommunicationElementDetail productCommunicationElementDetail = ObjectRandomizer.random(ProductCommunicationElementDetail.class);
        productCommunicationElementDetail.setPosition(0);
        productCommunicationElement.setTexts(new ArrayList<>());
        productCommunicationElement.setImages(new ArrayList<>());
        productCommunicationElement.getImages().add(productCommunicationElementDetail);
        productCommunicationElementDocument.getLanguageElements().put("en_US", productCommunicationElement);
        Mockito.when(productCommunicationElementCouchDao.get(productId.toString())).thenReturn(productCommunicationElementDocument);
        try {
            productCommunicationElementService.deleteProductCommunicationElementsImages(productId, language, ProductCommunicationElementsImagesType.LANDSCAPE, 0L);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_COMMUNICATIONS_ELEMENTS_LANGUAGE_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        productCommunicationElementDocument.getLanguageElements().put("ca_ES", productCommunicationElement);
        Mockito.when(productCommunicationElementCouchDao.get(productId.toString())).thenReturn(productCommunicationElementDocument);

        Mockito.doNothing().when(productCommunicationElementCouchDao).upsert(Mockito.anyString(), Mockito.any());

        Map<Long, String> langMap = new HashMap<>();
        langMap.put(1L, "ca-ES");
        Mockito.when(entitiesRepository.getAllIdAndCodeLanguages()).thenReturn(langMap);

        EntityDTO entityDTO = ObjectRandomizer.random(EntityDTO.class);
        Mockito.when(entitiesRepository.getEntity(Mockito.anyInt())).thenReturn(entityDTO);

        Mockito.doNothing().when(s3BinaryRepository).delete(Mockito.anyString());

        productCommunicationElementService.deleteProductCommunicationElementsImages(productId, language, ProductCommunicationElementsImagesType.LANDSCAPE, 0L);
    }

    @Test
    void getProductCommunicationElementsImages() {
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        try {
            productCommunicationElementService.getProductCommunicationElementsImages(ObjectRandomizer.randomLong());
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
            productCommunicationElementService.getProductCommunicationElementsImages(ObjectRandomizer.randomLong());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(new ArrayList<>());

        try {
            productCommunicationElementService.getProductCommunicationElementsImages(ObjectRandomizer.randomLong());
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

        ProductCommunicationElementDocument productCommunicationElementDocument = new ProductCommunicationElementDocument();
        productCommunicationElementDocument.setProductId(ObjectRandomizer.randomLong());
        productCommunicationElementDocument.setLanguageElements(new HashMap<>());
        ProductCommunicationElement productCommunicationElement = new ProductCommunicationElement();
        productCommunicationElement.setImages(new ArrayList<>());
        ProductCommunicationElementDetail productCommunicationElementDetail = new ProductCommunicationElementDetail();
        productCommunicationElementDetail.setType("LANDSCAPE");
        productCommunicationElementDetail.setValue("dfgdfgdfg");
        productCommunicationElementDetail.setAltText("Alternative text");
        productCommunicationElement.getImages().add(productCommunicationElementDetail);
        productCommunicationElementDocument.getLanguageElements().put("ca_ES", productCommunicationElement);
        Mockito.when(productCommunicationElementCouchDao.get(Mockito.anyString())).thenReturn(productCommunicationElementDocument);

        productCommunicationElementService.getProductCommunicationElementsImages(ObjectRandomizer.randomLong());
    }

    @Test
    void createProductCommunicationElementsImages() {
        Mockito.doNothing().when(refreshDataService).refreshProduct(Mockito.anyLong());
        Long productId = ObjectRandomizer.randomLong();

        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);

        CreateProductCommunicationElementsImagesDTO createProductCommunicationElementsImages = new CreateProductCommunicationElementsImagesDTO();
        CreateProductCommunicationElementImageDTO createProductCommunicationElementImage = new CreateProductCommunicationElementImageDTO();
        createProductCommunicationElementImage.setType(ProductCommunicationElementsImagesType.LANDSCAPE);
        createProductCommunicationElementImage.setLanguage("ca-ES");
        createProductCommunicationElementImage.setLanguageId(1L);
        createProductCommunicationElementImage.setValue("text");
        createProductCommunicationElementImage.setAltText("Alternative text");
        createProductCommunicationElementsImages.add(createProductCommunicationElementImage);
        try {
            productCommunicationElementService.createProductCommunicationElementsImages(productId, createProductCommunicationElementsImages);
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
            productCommunicationElementService.createProductCommunicationElementsImages(productId, createProductCommunicationElementsImages);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(new ArrayList<>());
        try {
            productCommunicationElementService.createProductCommunicationElementsImages(productId, createProductCommunicationElementsImages);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_LANGUAGES_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        List<ProductLanguageRecord> list = new ArrayList<>();
        ProductLanguageRecord productLanguageRecord = new ProductLanguageRecord();
        productLanguageRecord.setProductid(productId.intValue());
        productLanguageRecord.setLanguageid(1);
        productLanguageRecord.setDefaultlanguage(Byte.valueOf("0"));
        productLanguageRecord.setCode("en_US");
        list.add(productLanguageRecord);
        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(list);
        try {
            productCommunicationElementService.createProductCommunicationElementsImages(productId, createProductCommunicationElementsImages);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_LANGUAGE_NOT_RELATED.getErrorCode(), e.getErrorCode());
        }

        list.get(0).setCode("ca_ES");
        Mockito.when(productLanguageDao.findByProductId(Mockito.anyLong())).thenReturn(list);


        Mockito.doNothing().when(productCommunicationElementCouchDao).upsert(Mockito.anyString(), Mockito.any());

        ProductCommunicationElementDocument productCommunicationElementDocument = ObjectRandomizer.random(ProductCommunicationElementDocument.class);
        Mockito.when(productCommunicationElementCouchDao.get(Mockito.anyString())).thenReturn(productCommunicationElementDocument);

        EntityDTO entityDTO = ObjectRandomizer.random(EntityDTO.class);
        Mockito.when(entitiesRepository.getEntity(Mockito.anyInt())).thenReturn(entityDTO);

        Map<Long, String> langMap = new HashMap<>();
        langMap.put(1L, "ca_ES");
        Mockito.when(entitiesRepository.getAllIdAndCodeLanguages()).thenReturn(langMap);

        Mockito.doNothing().when(s3BinaryRepository).delete(Mockito.anyString());
        Mockito.doNothing().when(s3BinaryRepository).upload(Mockito.anyString(), Mockito.any());
        productCommunicationElementService.createProductCommunicationElementsImages(productId, createProductCommunicationElementsImages);
    }

}
