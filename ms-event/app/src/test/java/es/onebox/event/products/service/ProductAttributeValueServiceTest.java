package es.onebox.event.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.ProductAttributeDao;
import es.onebox.event.products.dao.ProductAttributeValueDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductVariantDao;
import es.onebox.event.products.dto.CreateProductAttributeValueDTO;
import es.onebox.event.products.dto.ProductAttributeValuesDTO;
import es.onebox.event.products.dto.SearchProductAttributeValueFilterDTO;
import es.onebox.event.products.dto.UpdateProductAttributeValueDTO;
import es.onebox.event.products.helper.ProductHelper;
import es.onebox.event.products.helper.ProductLanguageHelper;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeValueRecord;
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
import static org.mockito.ArgumentMatchers.any;

class ProductAttributeValueServiceTest {
    private static final Long PRODUCT_ID = 1L;
    private static final Long ATTRIBUTE_ID = 1L;
    private static final Long VALUE_ID = 1L;
    private static final Integer ATTRIBUTE_VALUE_POSITION = 0;
    private static final String ATTRIBUTE_VALUE_NAME = "New attribute value";
    private static final String ANOTHER_ATTRIBUTE_VALUE_NAME = "Another attribute value";
    private static final Long ANOTHER_ATTRIBUTE_VALUE_ID = 2L;
    private static final String UPDATE_VALUE_NAME = "New attribute value name";

    @Mock
    ProductDao productDao;

    @Mock
    ProductLanguageHelper productLanguageHelper;

    @Mock
    ProductAttributeDao productAttributeDao;

    @Mock
    ProductVariantDao productVariantDao;

    @Mock
    RefreshDataService refreshDataService;

    @Mock
    ProductAttributeValueDao productAttributeValueDao;

    @Mock
    ProductHelper productHelper;

    @InjectMocks
    ProductAttributeValueService productAttributeValueService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProductAttributeValue() {
        CreateProductAttributeValueDTO createProductAttributeValueDTO = new CreateProductAttributeValueDTO();
        createProductAttributeValueDTO.setName(ATTRIBUTE_VALUE_NAME);
        createProductAttributeValueDTO.setPosition(ATTRIBUTE_VALUE_POSITION);

        Mockito.when(productAttributeValueDao.findById(VALUE_ID.intValue())).thenReturn(null);

        //product not found
        try {
            productAttributeValueService.createProductAttributeValue(PRODUCT_ID, ATTRIBUTE_ID, createProductAttributeValueDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setState(1);

        Mockito.when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(cpanelProductRecord);

        //attribute not found
        try {
            productAttributeValueService.createProductAttributeValue(PRODUCT_ID, ATTRIBUTE_ID, createProductAttributeValueDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_ATTRIBUTE_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelProductAttributeRecord cpanelProductAttributeRecord = new CpanelProductAttributeRecord();
        cpanelProductAttributeRecord.setAttributeid(1);

        Mockito.when(productAttributeDao.findById(ATTRIBUTE_ID.intValue())).thenReturn(new CpanelProductAttributeRecord());
        Mockito.when(productVariantDao.getCountProductVariantsByProductId(PRODUCT_ID)).thenReturn(26L);

        //Max attribute values reached
        try {
            productAttributeValueService.createProductAttributeValue(PRODUCT_ID, ATTRIBUTE_ID, createProductAttributeValueDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.MAX_ATTRIBUTE_VALUES_BY_PRODUCT_REACHED.getErrorCode(), e.getErrorCode());
        }

        //Attribute value created
        Mockito.when(productVariantDao.getCountProductVariantsByProductId(PRODUCT_ID)).thenReturn(10L);
        Mockito.when(productAttributeValueDao.getTotalAttributeValuesByProduct(PRODUCT_ID)).thenReturn(1L);

        CpanelProductAttributeValueRecord cpanelProductAttributeValueRecord = new CpanelProductAttributeValueRecord();
        cpanelProductAttributeValueRecord.setProductattributeid(ATTRIBUTE_ID.intValue());
        cpanelProductAttributeValueRecord.setValueid(VALUE_ID.intValue());
        cpanelProductAttributeValueRecord.setName(ATTRIBUTE_VALUE_NAME);
        cpanelProductAttributeValueRecord.setPosition(1);
        Mockito.when(productAttributeValueDao.insert(any())).thenReturn(cpanelProductAttributeValueRecord);

        assertEquals(VALUE_ID, productAttributeValueService.createProductAttributeValue(PRODUCT_ID, ATTRIBUTE_ID, createProductAttributeValueDTO));
    }

    @Test
    void updateProductAttributeValue() {
        UpdateProductAttributeValueDTO updateProductAttributeValueDTO = new UpdateProductAttributeValueDTO();
        updateProductAttributeValueDTO.setName(UPDATE_VALUE_NAME);
        updateProductAttributeValueDTO.setPosition(2);

        Mockito.when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(null);

        //product not found
        try {
            productAttributeValueService.updateProductAttributeValue(PRODUCT_ID, ATTRIBUTE_ID, VALUE_ID, updateProductAttributeValueDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        //Attribute not found
        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setState(1);

        Mockito.when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(cpanelProductRecord);

        //attribute not found
        try {
            productAttributeValueService.updateProductAttributeValue(PRODUCT_ID, ATTRIBUTE_ID, VALUE_ID, updateProductAttributeValueDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_ATTRIBUTE_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelProductAttributeRecord cpanelProductAttributeRecord = new CpanelProductAttributeRecord();
        cpanelProductAttributeRecord.setAttributeid(1);

        try {
            productAttributeValueService.updateProductAttributeValue(PRODUCT_ID, ATTRIBUTE_ID, VALUE_ID, updateProductAttributeValueDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_ATTRIBUTE_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        Mockito.when(productAttributeDao.findById(ATTRIBUTE_ID.intValue())).thenReturn(new CpanelProductAttributeRecord());

        //Product attribute value not found
        try {
            productAttributeValueService.updateProductAttributeValue(PRODUCT_ID, ATTRIBUTE_ID, VALUE_ID, updateProductAttributeValueDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_ATTRIBUTE_VALUE_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        Mockito.when(productAttributeValueDao.findById(VALUE_ID.intValue())).thenReturn(new CpanelProductAttributeValueRecord());

        //Attribute updated
        CpanelProductAttributeValueRecord cpanelProductAttributeValueRecord = new CpanelProductAttributeValueRecord();
        cpanelProductAttributeValueRecord.setProductattributeid(ATTRIBUTE_ID.intValue());
        cpanelProductAttributeValueRecord.setValueid(VALUE_ID.intValue());
        cpanelProductAttributeValueRecord.setName("New name");
        cpanelProductAttributeValueRecord.setPosition(2);

        Mockito.when(productAttributeValueDao.findById(VALUE_ID.intValue())).thenReturn(cpanelProductAttributeValueRecord);
        Mockito.when(productVariantDao.searchProductVariants(PRODUCT_ID, null)).thenReturn(new ArrayList<>());

        productAttributeValueService.updateProductAttributeValue(PRODUCT_ID, ATTRIBUTE_ID, VALUE_ID, updateProductAttributeValueDTO);
    }

    @Test
    void getProductAttributes() {
        SearchProductAttributeValueFilterDTO searchProductAttributeValueFilterDTO = new SearchProductAttributeValueFilterDTO();
        //product not found
        Mockito.when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(null);

        try {
            productAttributeValueService.getProductAttributeValues(PRODUCT_ID, ATTRIBUTE_ID, searchProductAttributeValueFilterDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        //Product attributes returned successfully
        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setState(1);

        CpanelProductAttributeRecord cpanelProductAttributeRecord = new CpanelProductAttributeRecord();
        cpanelProductRecord.setProductid(PRODUCT_ID.intValue());

        Mockito.when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(cpanelProductRecord);
        Mockito.when(productAttributeDao.findById(ATTRIBUTE_ID.intValue())).thenReturn(cpanelProductAttributeRecord);
        Mockito.when(productAttributeValueDao.getProductAttributeValues(ATTRIBUTE_ID, searchProductAttributeValueFilterDTO)).thenReturn(null);

        List<CpanelProductAttributeValueRecord> productAttributeValueRecordList = getProductAttributeValueRecords();

        Mockito.when(productAttributeValueDao.getProductAttributeValues(ATTRIBUTE_ID, searchProductAttributeValueFilterDTO)).thenReturn(productAttributeValueRecordList);

        ProductAttributeValuesDTO productAttributeValuesDTO = productAttributeValueService.getProductAttributeValues(PRODUCT_ID, ATTRIBUTE_ID, searchProductAttributeValueFilterDTO);

        assertEquals(2, productAttributeValuesDTO.getData().size());
    }

    private static List<CpanelProductAttributeValueRecord> getProductAttributeValueRecords() {
        CpanelProductAttributeValueRecord cpanelProductAttributeValueRecord1 = new CpanelProductAttributeValueRecord();
        cpanelProductAttributeValueRecord1.setProductattributeid(ATTRIBUTE_ID.intValue());
        cpanelProductAttributeValueRecord1.setName(ATTRIBUTE_VALUE_NAME);
        cpanelProductAttributeValueRecord1.setPosition(0);
        cpanelProductAttributeValueRecord1.setValueid(VALUE_ID.intValue());
        CpanelProductAttributeValueRecord cpanelProductAttributeValueRecord2 = new CpanelProductAttributeValueRecord();
        cpanelProductAttributeValueRecord2.setProductattributeid(ATTRIBUTE_ID.intValue());
        cpanelProductAttributeValueRecord2.setPosition(1);
        cpanelProductAttributeValueRecord2.setName(ANOTHER_ATTRIBUTE_VALUE_NAME);
        cpanelProductAttributeValueRecord2.setValueid(ANOTHER_ATTRIBUTE_VALUE_ID.intValue());
        List<CpanelProductAttributeValueRecord> productAttributeValueRecordList = new ArrayList<>();

        productAttributeValueRecordList.add(cpanelProductAttributeValueRecord1);
        productAttributeValueRecordList.add(cpanelProductAttributeValueRecord2);

        return productAttributeValueRecordList;
    }
}
