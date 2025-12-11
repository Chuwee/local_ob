package es.onebox.event.products.service;

import es.onebox.event.products.enums.SelectionType;
import es.onebox.utils.ObjectRandomizer;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductEventDao;
import es.onebox.event.products.dao.ProductSessionDeliveryPointDao;
import es.onebox.event.products.domain.ProductEventRecord;
import es.onebox.event.products.domain.ProductSessionDeliveryPointRecord;
import es.onebox.event.products.dto.UpdateProductSessionDeliveryPointDTO;
import es.onebox.event.products.dto.UpdateProductSessionDeliveryPointDetailDTO;
import es.onebox.event.products.dto.UpdateProductSessionDeliveryPointsDTO;
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

class ProductSessionDeliveryPointServiceTest {
    @Mock
    EventDao eventDao;
    @Mock
    ProductEventDao productEventDao;
    @Mock
    ProductDao productDao;
    @Mock
    ProductSessionDeliveryPointDao productSessionDeliveryPointDao;
    @Mock
    RefreshDataService refreshDataService;

    @InjectMocks
    ProductSessionDeliveryPointService productSessionDeliveryPointService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductSessionDeliveryPoints() {

        Long productId = ObjectRandomizer.randomLong();
        Long eventId = ObjectRandomizer.randomLong();
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        try {
            productSessionDeliveryPointService.getProductSessionDeliveryPoints(productId, eventId, null);
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
            productSessionDeliveryPointService.getProductSessionDeliveryPoints(productId, eventId, null);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.EVENT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelEventoRecord eventoRecord = new CpanelEventoRecord();
        eventoRecord.setIdevento(eventId.intValue());
        eventoRecord.setNombre("esrfsdf");
        Mockito.when(eventDao.getById(Mockito.anyInt())).thenReturn(eventoRecord);

        Mockito.when(productEventDao.findByProductIdAndEventId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
        try {
            productSessionDeliveryPointService.getProductSessionDeliveryPoints(productId, eventId, null);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_EVENT_NOT_RELATED.getErrorCode(), e.getErrorCode());
        }

        ProductEventRecord productEventRecord = new ProductEventRecord();
        productEventRecord.setSessionsselectiontype(SelectionType.ALL.getId());
        productEventRecord.setProductid(productId.intValue());
        productEventRecord.setEventid(eventId.intValue());
        Mockito.when(productEventDao.findByProductIdAndEventId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(productEventRecord);

        List<ProductSessionDeliveryPointRecord> productSessionDeliveryPointRecords = new ArrayList<>();
        ProductSessionDeliveryPointRecord productSessionDeliveryPointRecord = new ProductSessionDeliveryPointRecord();
        productSessionDeliveryPointRecord.setProducteventid(13);
        productSessionDeliveryPointRecord.setSessionid(12);
        productSessionDeliveryPointRecord.setDeliverypointid(3);
        productSessionDeliveryPointRecord.setDefaultdeliverypoint((byte) 0);
        productSessionDeliveryPointRecord.setSessionName("dsfsdf");
        productSessionDeliveryPointRecord.setProductName("sdfsdf");
        productSessionDeliveryPointRecord.setProductDeliveryPointName("sdfsd");
        productSessionDeliveryPointRecords.add(productSessionDeliveryPointRecord);
        Mockito.when(productSessionDeliveryPointDao.findByProductSession(Mockito.anyLong(), Mockito.anyLong())).thenReturn(productSessionDeliveryPointRecords);
        productSessionDeliveryPointService.getProductSessionDeliveryPoints(productId, eventId, null);
    }

    @Test
    void updateProductSessionDeliveryPoints() {
        Long productId = ObjectRandomizer.randomLong();
        Long eventId = ObjectRandomizer.randomLong();

        Mockito.doNothing().when(refreshDataService).refreshProduct(Mockito.anyLong());
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        try {
            productSessionDeliveryPointService.updateProductSessionDeliveryPoint(productId, eventId, ObjectRandomizer.random(UpdateProductSessionDeliveryPointsDTO.class));
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
            productSessionDeliveryPointService.updateProductSessionDeliveryPoint(productId, eventId, ObjectRandomizer.random(UpdateProductSessionDeliveryPointsDTO.class));
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.EVENT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelEventoRecord eventoRecord = new CpanelEventoRecord();
        eventoRecord.setIdevento(eventId.intValue());
        eventoRecord.setNombre("esrfsdf");
        Mockito.when(eventDao.getById(Mockito.anyInt())).thenReturn(eventoRecord);

        Mockito.when(productEventDao.findByProductIdAndEventId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
        try {
            productSessionDeliveryPointService.updateProductSessionDeliveryPoint(productId, eventId, ObjectRandomizer.random(UpdateProductSessionDeliveryPointsDTO.class));
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_EVENT_NOT_RELATED.getErrorCode(), e.getErrorCode());
        }

        ProductEventRecord productEventRecord = new ProductEventRecord();
        productEventRecord.setSessionsselectiontype(SelectionType.RESTRICTED.getId());
        productEventRecord.setProductid(productId.intValue());
        productEventRecord.setEventid(eventId.intValue());
        Mockito.when(productEventDao.findByProductIdAndEventId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(productEventRecord);

        Mockito.when(productSessionDeliveryPointDao.findByProductSession(Mockito.anyLong(), Mockito.anyLong())).thenReturn(null);
        Mockito.when(productSessionDeliveryPointDao.insert(Mockito.any())).thenReturn(null);

        UpdateProductSessionDeliveryPointsDTO updateProductSessionDeliveryPointsDTO = new UpdateProductSessionDeliveryPointsDTO();
        UpdateProductSessionDeliveryPointDTO updateProductSessionDeliveryPointDTO = new UpdateProductSessionDeliveryPointDTO();
        List<UpdateProductSessionDeliveryPointDetailDTO> updateProductSessionDeliveryPointDetailDTOS = new ArrayList<>();
        UpdateProductSessionDeliveryPointDetailDTO updateProductSessionDeliveryPointDetailDTO = ObjectRandomizer.random(UpdateProductSessionDeliveryPointDetailDTO.class);
        updateProductSessionDeliveryPointDetailDTO.setDefault(true);
        updateProductSessionDeliveryPointDetailDTOS.add(updateProductSessionDeliveryPointDetailDTO);
        updateProductSessionDeliveryPointDTO.setId(12L);
        updateProductSessionDeliveryPointDTO.setDeliveryPoints(updateProductSessionDeliveryPointDetailDTOS);
        updateProductSessionDeliveryPointsDTO.add(updateProductSessionDeliveryPointDTO);
        productSessionDeliveryPointService.updateProductSessionDeliveryPoint(productId, eventId, updateProductSessionDeliveryPointsDTO);
    }

}
