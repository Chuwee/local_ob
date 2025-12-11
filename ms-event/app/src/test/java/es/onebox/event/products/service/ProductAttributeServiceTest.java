package es.onebox.event.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.ProductAttributeDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductVariantDao;
import es.onebox.event.products.dto.CreateProductAttributeDTO;
import es.onebox.event.products.dto.ProductAttributesDTO;
import es.onebox.event.products.dto.UpdateProductAttributeDTO;
import es.onebox.event.products.helper.ProductHelper;
import es.onebox.event.products.helper.ProductLanguageHelper;
import es.onebox.jooq.cpanel.tables.records.CpanelProductAttributeRecord;
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

class ProductAttributeServiceTest {
    private static final Long PRODUCT_ID = 1L;
    private static final Long ATTRIBUTE_ID = 1L;
    private static final String ATTRIBUTE_NAME = "New attribute";
    private static final String ANOTHER_ATTRIBUTE_NAME = "Another attribute";
    private static final Long ANOTHER_ATTRIBUTE_ID = 2L;
    private static final String UPDATE_NAME = "New attribute name";

    @Mock
    ProductDao productDao;

    @Mock
    ProductAttributeDao productAttributeDao;

    @Mock
    ProductLanguageHelper productLanguageHelper;

    @Mock
    RefreshDataService refreshDataService;

    @Mock
    ProductVariantDao productVariantDao;

    @Mock
    ProductHelper productHelper;

    @InjectMocks
    ProductAttributeService productAttributeService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProductAttribute() {
        CreateProductAttributeDTO createProductAttributeDTO = new CreateProductAttributeDTO();
        createProductAttributeDTO.setName(ATTRIBUTE_NAME);

        Mockito.when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(null);

        //product not found
        try {
            productAttributeService.createProductAttribute(PRODUCT_ID, createProductAttributeDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        //More than one product attribute
        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setState(1);

        Mockito.when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(cpanelProductRecord);
        Mockito.when(productAttributeDao.getTotalAttributes(PRODUCT_ID)).thenReturn(2);

        try {
            productAttributeService.createProductAttribute(PRODUCT_ID, createProductAttributeDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.MAX_ATTRIBUTES_REACHED.getErrorCode(), e.getErrorCode());
        }

        //Attribute created
        Mockito.when(productAttributeDao.getTotalAttributes(PRODUCT_ID)).thenReturn(1);

        CpanelProductAttributeRecord cpanelProductAttributeRecord = new CpanelProductAttributeRecord();
        cpanelProductAttributeRecord.setName(ATTRIBUTE_NAME);
        cpanelProductAttributeRecord.setAttributeid(ATTRIBUTE_ID.intValue());

        Mockito.when(productAttributeDao.insert(any())).thenReturn(cpanelProductAttributeRecord);

        assertEquals(ATTRIBUTE_ID, productAttributeService.createProductAttribute(PRODUCT_ID, createProductAttributeDTO));
    }

    @Test
    void updateProductAttribute() {
        UpdateProductAttributeDTO updateProductAttributeDTO = new UpdateProductAttributeDTO();
        updateProductAttributeDTO.setName(UPDATE_NAME);

        Mockito.when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(null);

        //product not found
        try {
            productAttributeService.updateProductAttribute(PRODUCT_ID, ATTRIBUTE_ID, updateProductAttributeDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        //Attribute not found
        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setState(1);

        Mockito.when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(cpanelProductRecord);

        try {
            productAttributeService.updateProductAttribute(PRODUCT_ID, ATTRIBUTE_ID, updateProductAttributeDTO);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_ATTRIBUTE_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        //Attribute updated
        CpanelProductAttributeRecord cpanelProductAttributeRecord = new CpanelProductAttributeRecord();
        cpanelProductAttributeRecord.setAttributeid(ATTRIBUTE_ID.intValue());
        cpanelProductAttributeRecord.setProductid(PRODUCT_ID.intValue());

        Mockito.when(productAttributeDao.findById(ATTRIBUTE_ID.intValue())).thenReturn(cpanelProductAttributeRecord);

        productAttributeService.updateProductAttribute(PRODUCT_ID, ATTRIBUTE_ID, updateProductAttributeDTO);
    }

    @Test
    void getProductAttributes() {
        //product not found
        Mockito.when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(null);

        try {
            productAttributeService.getProductAttributes(PRODUCT_ID);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        //Product attributes returned successfully
        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setState(1);

        Mockito.when(productDao.findById(PRODUCT_ID.intValue())).thenReturn(cpanelProductRecord);
        Mockito.when(productAttributeDao.findByProductId(PRODUCT_ID)).thenReturn(null);

        List<CpanelProductAttributeRecord> productAttributeRecordList = getProductAttributeRecords();

        Mockito.when(productAttributeDao.findByProductId(PRODUCT_ID)).thenReturn(productAttributeRecordList);

        ProductAttributesDTO productAttributesDTO = productAttributeService.getProductAttributes(PRODUCT_ID);

        assertEquals(2, productAttributesDTO.size());
    }

    private static List<CpanelProductAttributeRecord> getProductAttributeRecords() {
        CpanelProductAttributeRecord cpanelProductAttributeRecord1 = new CpanelProductAttributeRecord();
        cpanelProductAttributeRecord1.setProductid(PRODUCT_ID.intValue());
        cpanelProductAttributeRecord1.setName(ATTRIBUTE_NAME);
        cpanelProductAttributeRecord1.setAttributeid(ATTRIBUTE_ID.intValue());
        CpanelProductAttributeRecord cpanelProductAttributeRecord2 = new CpanelProductAttributeRecord();
        cpanelProductAttributeRecord2.setProductid(PRODUCT_ID.intValue());
        cpanelProductAttributeRecord2.setName(ANOTHER_ATTRIBUTE_NAME);
        cpanelProductAttributeRecord2.setAttributeid(ANOTHER_ATTRIBUTE_ID.intValue());
        List<CpanelProductAttributeRecord> productAttributeRecordList = new ArrayList<>();

        productAttributeRecordList.add(cpanelProductAttributeRecord1);
        productAttributeRecordList.add(cpanelProductAttributeRecord2);
        return productAttributeRecordList;
    }
}