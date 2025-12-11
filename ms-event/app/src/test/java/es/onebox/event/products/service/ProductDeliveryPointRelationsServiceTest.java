package es.onebox.event.products.service;

import es.onebox.utils.ObjectRandomizer;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.priceengine.simulation.record.ProductRecord;
import es.onebox.event.products.dao.DeliveryPointDao;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductDeliveryPointRelationDao;
import es.onebox.event.products.domain.DeliveryPointRecord;
import es.onebox.event.products.domain.ProductDeliveryPointRelationRecord;
import es.onebox.event.products.dto.ProductDeliveryPointRelationDTO;
import es.onebox.event.products.dto.ProductDeliveryPointsRelationsDTO;
import es.onebox.event.products.dto.SearchProductDeliveryPointRelationFilterDTO;
import es.onebox.event.products.dto.UpsertProductDeliveryPointRelationDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelProductDeliveryPointRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

class ProductDeliveryPointRelationsServiceTest {
    @Mock
    ProductDeliveryPointRelationDao productDeliveryPointRelationDao;
    @Mock
    ProductDao productDao;
    @Mock
    DeliveryPointDao deliveryPointDao;
    @Mock
    EntitiesRepository entitiesRepository;
    @Mock
    RefreshDataService refreshDataService;

    @InjectMocks
    ProductDeliveryPointRelationService productDeliveryPointRelationService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProductDeliveryPointsRelations() {
        Long productId = ObjectRandomizer.randomLong();
        UpsertProductDeliveryPointRelationDTO upsertProductDeliveryPointRelationDTO = new UpsertProductDeliveryPointRelationDTO();
        upsertProductDeliveryPointRelationDTO.setDeliveryPointIds(List.of(1L));

        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);

        try {
            productDeliveryPointRelationService.upsertProductDeliveryPointRelation(productId, upsertProductDeliveryPointRelationDTO);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode());
        }

        ProductRecord productRecord = new ProductRecord();
        productRecord.setProductid(2);
        productRecord.setProducerName("dfsdf");
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(productRecord);

        Mockito.when(deliveryPointDao.findById(Mockito.anyInt())).thenReturn(null);

        try {
            productDeliveryPointRelationService.upsertProductDeliveryPointRelation(productId, upsertProductDeliveryPointRelationDTO);
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), MsEventErrorCode.PRODUCT_DELIVERY_POINT_NOT_FOUND.getErrorCode());
        }

        DeliveryPointRecord deliveryPointRecord = new DeliveryPointRecord();
        deliveryPointRecord.setDeliverypointid(2);
        deliveryPointRecord.setName("dfgdfg");
        Mockito.when(deliveryPointDao.getById(Mockito.anyInt())).thenReturn(deliveryPointRecord);

        Mockito.when(entitiesRepository.getEntity(anyInt())).thenReturn(null);

        CpanelProductDeliveryPointRecord cpanelProductDeliveryPointRecord = new CpanelProductDeliveryPointRecord();
        cpanelProductDeliveryPointRecord.setId(2);
        cpanelProductDeliveryPointRecord.setDeliverypointid(2);
        cpanelProductDeliveryPointRecord.setProductid(3);
        Mockito.when(productDeliveryPointRelationDao.insert(any())).thenReturn(cpanelProductDeliveryPointRecord);

        productDeliveryPointRelationService.upsertProductDeliveryPointRelation(productId, upsertProductDeliveryPointRelationDTO);
    }

    @Test
    void getProductDeliveryPointsRelations() {
        Long productId = ObjectRandomizer.randomLong();
        Mockito.when(productDeliveryPointRelationDao.findByRelationId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
        try {
            productDeliveryPointRelationService.getProductDeliveryPointRelation(productId, ObjectRandomizer.randomLong());
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_DELIVERY_POINT_RELATION_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        ProductDeliveryPointRelationRecord productDeliveryPointRelationRecord = new ProductDeliveryPointRelationRecord();
        productDeliveryPointRelationRecord.setId(23);
        productDeliveryPointRelationRecord.setDeliverypointid(12);
        productDeliveryPointRelationRecord.setProductDeliveryPointName("xcfxcv");
        productDeliveryPointRelationRecord.setProductid(1);
        productDeliveryPointRelationRecord.setProductName("xczxcv");

        Mockito.when(productDeliveryPointRelationDao.findByRelationId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(productDeliveryPointRelationRecord);

        ProductDeliveryPointRelationDTO productDeliveryPointRelationDTO = productDeliveryPointRelationService.getProductDeliveryPointRelation(productId, ObjectRandomizer.randomLong());
        assertNotNull(productDeliveryPointRelationDTO);
    }

    @Test
    void searchProductDeliveryPointsRelations() {

        Long productId = ObjectRandomizer.randomLong();
        Mockito.when(productDeliveryPointRelationDao.getProductDeliveryPointsRelations(Mockito.anyLong(), any())).thenReturn(null);

        SearchProductDeliveryPointRelationFilterDTO filter = ObjectRandomizer.random(SearchProductDeliveryPointRelationFilterDTO.class);
        ProductDeliveryPointsRelationsDTO productDeliveryPointsRelationsDTO = productDeliveryPointRelationService.searchProductDeliveryPoinRelations(productId, filter);
        assertNull(productDeliveryPointsRelationsDTO);


        ProductDeliveryPointRelationRecord productDeliveryPointRelationRecord = new ProductDeliveryPointRelationRecord();
        productDeliveryPointRelationRecord.setId(5);
        productDeliveryPointRelationRecord.setProductid(2);
        productDeliveryPointRelationRecord.setProductName("sdfsdf");
        productDeliveryPointRelationRecord.setDeliverypointid(3);
        productDeliveryPointRelationRecord.setProductDeliveryPointName("xfsf");
        Mockito.when(productDeliveryPointRelationDao.getProductDeliveryPointsRelations(Mockito.anyLong(), any())).thenReturn(List.of(productDeliveryPointRelationRecord));

        SearchProductDeliveryPointRelationFilterDTO searchProductDeliveryPointRelationFilterDTO = ObjectRandomizer.random(SearchProductDeliveryPointRelationFilterDTO.class);
        ProductDeliveryPointsRelationsDTO productDeliveryPointsRelationsDTO1 = productDeliveryPointRelationService.searchProductDeliveryPoinRelations(productId, searchProductDeliveryPointRelationFilterDTO);

        assertNotNull(productDeliveryPointsRelationsDTO1);
        assertNotNull(productDeliveryPointsRelationsDTO1.getData());
        assertEquals(1, productDeliveryPointsRelationsDTO1.getData().size());
    }

}
