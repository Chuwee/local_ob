package es.onebox.event.products.service;

import es.onebox.utils.ObjectRandomizer;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dao.ProductEventDao;
import es.onebox.event.products.dao.ProductSessionDao;
import es.onebox.event.products.domain.ProductEventRecord;
import es.onebox.event.products.dto.UpdateProductEventDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelProductEventRecord;
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

class ProductEventServiceTest {
    @Mock
    ProductEventDao productEventDao;
    @Mock
    ProductDao productDao;
    @Mock
    EventDao eventDao;
    @Mock
    ProductSessionDao productSessionDao;
    @Mock
    RefreshDataService refreshDataService;
    @Mock
    WebhookService webhookService;
    @InjectMocks
    ProductEventService productEventService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductEvents() {
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        try {
            productEventService.getProductEvents(ObjectRandomizer.randomLong(), null);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setProductid(1);
        cpanelProductRecord.setEntityid(2);
        cpanelProductRecord.setName("new Product");
        cpanelProductRecord.setProducerid(3);
        cpanelProductRecord.setState(1);
        cpanelProductRecord.setTaxid(4);
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(cpanelProductRecord);

        Mockito.when(productEventDao.findByProductId(Mockito.anyInt(), Mockito.anyBoolean())).thenReturn(null);
        try {
            productEventService.getProductEvents(ObjectRandomizer.randomLong(), null);
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_CHANNELS_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        List<ProductEventRecord> cpanelProductEventRecords = new ArrayList<>();
        ProductEventRecord productEventRecord = new ProductEventRecord();
        productEventRecord.setEventName("new Event");
        productEventRecord.setProductName("new Product");
        productEventRecord.setProductid(2);
        productEventRecord.setEventid(3);
        productEventRecord.setStatus(1);
        productEventRecord.setSessionsselectiontype(0);
        cpanelProductEventRecords.add(productEventRecord);
        Mockito.when(productEventDao.findByProductId(Mockito.anyInt(), Mockito.anyBoolean()))
                .thenReturn(cpanelProductEventRecords);
        productEventService.getProductEvents(ObjectRandomizer.randomLong(), null);
    }

    @Test
    void updateProductEvents() {
        ProductEventRecord productEventRecord1 = new ProductEventRecord();
        productEventRecord1.setEventid(1);
        productEventRecord1.setProductid(2);
        productEventRecord1.setStatus(1);
        productEventRecord1.setProducteventid(12);
        productEventRecord1.setSessionsselectiontype(0);
        Mockito.when(productEventDao.findByProductIdAndEventId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(productEventRecord1);
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        try {
            productEventService.updateProductEvent(ObjectRandomizer.randomLong(), productEventRecord1.getEventid().longValue(),
                    ObjectRandomizer.random(UpdateProductEventDTO.class));
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setProductid(1);
        cpanelProductRecord.setEntityid(2);
        cpanelProductRecord.setName("new Product");
        cpanelProductRecord.setProducerid(3);
        cpanelProductRecord.setState(1);
        cpanelProductRecord.setTaxid(4);
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(cpanelProductRecord);

        CpanelProductEventRecord cpanelProductEventRecord = new CpanelProductEventRecord();
        cpanelProductEventRecord.setEventid(1);
        cpanelProductEventRecord.setProductid(2);
        Mockito.when(productEventDao.insert(Mockito.any())).thenReturn(cpanelProductEventRecord);

        Mockito.when(productEventDao.findByProductId(Mockito.anyInt(), Mockito.anyBoolean())).thenReturn(null);

        UpdateProductEventDTO updateProductEventDTO = new UpdateProductEventDTO();

        List<ProductEventRecord> productEventRecords = new ArrayList<>();
        ProductEventRecord productEventRecord = new ProductEventRecord();
        productEventRecord.setProductName("new Product");
        productEventRecord.setEventName("new Event");
        productEventRecord.setStatus(1);
        productEventRecord.setProductid(2);
        productEventRecord.setEventid(3);
        productEventRecord.setSessionsselectiontype(1);
        productEventRecords.add(productEventRecord);

        Mockito.when(productEventDao.findByProductId(Mockito.anyInt(), Mockito.anyBoolean())).thenReturn(productEventRecords);

        productEventService.updateProductEvent(ObjectRandomizer.randomLong(), productEventRecord.getEventid().longValue(),
                updateProductEventDTO);
    }
}
