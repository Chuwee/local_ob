package es.onebox.event.products.service;

import es.onebox.utils.ObjectRandomizer;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductEventDao;
import es.onebox.event.products.dao.ProductEventDeliveryPointDao;
import es.onebox.event.products.domain.ProductEventDeliveryPointRecord;
import es.onebox.event.products.domain.ProductEventRecord;
import es.onebox.event.products.dto.UpdateProductEventDeliveryPointDTO;
import es.onebox.event.products.dto.UpdateProductEventDeliveryPointsDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
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

class ProductEventDeliveryPointServiceTest {
    @Mock
    EventDao eventDao;
    @Mock
    ProductEventDao productEventDao;
    @Mock
    ProductDao productDao;
    @Mock
    ProductEventDeliveryPointDao productEventDeliveryPointDao;
    @Mock
    RefreshDataService refreshDataService;

    @InjectMocks
    ProductEventDeliveryPointService productEventDeliveryPointService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductEventDeliveryPoints() {

        Long productId = ObjectRandomizer.randomLong();
        Long eventId = ObjectRandomizer.randomLong();
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        try {
            productEventDeliveryPointService.getProductEventDeliveryPoints(productId, eventId);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setProductid(productId.intValue());
        cpanelProductRecord.setEntityid(2);
        cpanelProductRecord.setName("dfsdf");
        cpanelProductRecord.setProducerid(3);
        cpanelProductRecord.setState(1);
        cpanelProductRecord.setTaxid(4);
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(cpanelProductRecord);

        Mockito.when(eventDao.getById(Mockito.anyInt())).thenReturn(null);
        try {
            productEventDeliveryPointService.getProductEventDeliveryPoints(productId, eventId);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.EVENT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelEventoRecord evento = new CpanelEventoRecord();
        evento.setIdevento(eventId.intValue());
        evento.setNombre("esrfsdf");
        Mockito.when(eventDao.getById(Mockito.anyInt())).thenReturn(evento);

        Mockito.when(productEventDao.findByProductId(Mockito.anyInt(), Mockito.anyBoolean())).thenReturn(null);
        try {
            productEventDeliveryPointService.getProductEventDeliveryPoints(productId, eventId);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_EVENT_NOT_RELATED.getErrorCode(), e.getErrorCode());
        }

        ProductEventRecord productEventRecord = new ProductEventRecord();
        productEventRecord.setProductid(productId.intValue());
        productEventRecord.setEventid(eventId.intValue());
        Mockito.when(productEventDao.findByProductIdAndEventId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(productEventRecord);

        List<ProductEventDeliveryPointRecord> productEventDeliveryPointRecords = new ArrayList<>();
        ProductEventDeliveryPointRecord productEventDeliveryPointRecord = new ProductEventDeliveryPointRecord();
        productEventDeliveryPointRecord.setProducteventid(15);
        productEventDeliveryPointRecord.setDeliverypointid(3);
        productEventDeliveryPointRecord.setProductId(productId.intValue());
        productEventDeliveryPointRecord.setEventId(eventId.intValue());
        productEventDeliveryPointRecord.setDefaultdeliverypoint((byte) 0);
        productEventDeliveryPointRecord.setEventName("dsfsdf");
        productEventDeliveryPointRecord.setProductName("sdfsdf");
        productEventDeliveryPointRecord.setProductDeliveryPointName("sdfsd");
        productEventDeliveryPointRecords.add(productEventDeliveryPointRecord);
        Mockito.when(productEventDeliveryPointDao.findByProductEvent(Mockito.anyLong(), Mockito.anyLong())).thenReturn(productEventDeliveryPointRecords);
        productEventDeliveryPointService.getProductEventDeliveryPoints(productId, eventId);
    }

    @Test
    void updateProductEventDeliveryPoints() {
        Long productId = ObjectRandomizer.randomLong();
        Long eventId = ObjectRandomizer.randomLong();

        Mockito.doNothing().when(refreshDataService).refreshProduct(Mockito.anyLong());
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        try {
            productEventDeliveryPointService.updateProductEventDeliveryPoint(productId, eventId, ObjectRandomizer.random(UpdateProductEventDeliveryPointsDTO.class));
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setProductid(productId.intValue());
        cpanelProductRecord.setEntityid(2);
        cpanelProductRecord.setName("dfsdf");
        cpanelProductRecord.setProducerid(3);
        cpanelProductRecord.setState(1);
        cpanelProductRecord.setTaxid(4);
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(cpanelProductRecord);

        Mockito.when(eventDao.getById(Mockito.anyInt())).thenReturn(null);

        try {
            productEventDeliveryPointService.updateProductEventDeliveryPoint(productId, eventId, ObjectRandomizer.random(UpdateProductEventDeliveryPointsDTO.class));
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.EVENT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelEventoRecord evento = new CpanelEventoRecord();
        evento.setIdevento(eventId.intValue());
        evento.setNombre("esrfsdf");
        Mockito.when(eventDao.getById(Mockito.anyInt())).thenReturn(evento);

        Mockito.when(productEventDao.findByProductId(Mockito.anyInt(), Mockito.anyBoolean())).thenReturn(null);
        try {
            productEventDeliveryPointService.updateProductEventDeliveryPoint(productId, eventId, ObjectRandomizer.random(UpdateProductEventDeliveryPointsDTO.class));
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_EVENT_NOT_RELATED.getErrorCode(), e.getErrorCode());
        }

        ProductEventRecord productEventRecord = new ProductEventRecord();
        productEventRecord.setProductid(productId.intValue());
        productEventRecord.setEventid(eventId.intValue());
        Mockito.when(productEventDao.findByProductIdAndEventId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(productEventRecord);

        Mockito.when(productEventDeliveryPointDao.findByProductEvent(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
        Mockito.when(productEventDeliveryPointDao.insert(Mockito.any())).thenReturn(null);

        UpdateProductEventDeliveryPointsDTO updateProductEventDeliveryPointsDTO = new UpdateProductEventDeliveryPointsDTO();
        UpdateProductEventDeliveryPointDTO updateProductEventDeliveryPointDTO = new UpdateProductEventDeliveryPointDTO();
        updateProductEventDeliveryPointDTO.setDeliveryPointId(1L);
        updateProductEventDeliveryPointDTO.setDefault(true);
        updateProductEventDeliveryPointsDTO.add(updateProductEventDeliveryPointDTO);
        productEventDeliveryPointService.updateProductEventDeliveryPoint(productId, eventId, updateProductEventDeliveryPointsDTO);
    }

}
