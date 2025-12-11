package es.onebox.event.products.service;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.utils.ObjectRandomizer;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.dao.ProductDao;
import es.onebox.event.products.dto.ProductDeliveryDTO;
import es.onebox.event.products.enums.ProductDeliveryTimeUnitType;
import es.onebox.event.products.enums.ProductDeliveryType;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;

class ProductDeliveryServiceTest {
    @Mock
    ProductDao productDao;
    @Mock
    RefreshDataService refreshDataService;

    @InjectMocks
    ProductDeliveryService productDeliveryService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductChannels() {
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        try {
            productDeliveryService.getProductDelivery(ObjectRandomizer.randomLong());
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
        cpanelProductRecord.setDeliverytype(1);
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(cpanelProductRecord);

        ProductDeliveryDTO productDelivery = productDeliveryService.getProductDelivery(ObjectRandomizer.randomLong());
        assertNull(productDelivery);

        cpanelProductRecord.setDeliverytype(3);
        cpanelProductRecord.setDeliverystarttimeunit(1);
        cpanelProductRecord.setDeliverystarttimevalue(2);
        cpanelProductRecord.setDeliveryendtimeunit(1);
        cpanelProductRecord.setDeliveryendtimevalue(2);
        cpanelProductRecord.setDeliverydatefrom(Timestamp.valueOf("2018-10-01 10:00:00"));
        cpanelProductRecord.setDeliverydateto(Timestamp.valueOf("2019-10-01 10:00:00"));
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(cpanelProductRecord);
        productDelivery = productDeliveryService.getProductDelivery(ObjectRandomizer.randomLong());

        Assertions.assertEquals(productDelivery.getDeliveryDateFrom(), CommonUtils.timestampToZonedDateTime(cpanelProductRecord.getDeliverydatefrom()));
        Assertions.assertEquals(productDelivery.getDeliveryDateTo(), CommonUtils.timestampToZonedDateTime(cpanelProductRecord.getDeliverydateto()));
    }

    @Test
    void updateProductLanguages() {
        Mockito.doNothing().when(refreshDataService).refreshProduct(Mockito.anyLong());
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(null);
        try {
            productDeliveryService.updateProductDelivery(ObjectRandomizer.randomLong(), ObjectRandomizer.random(ProductDeliveryDTO.class));
        } catch (OneboxRestException e) {
            assertEquals(MsEventErrorCode.PRODUCT_NOT_FOUND.getErrorCode(), e.getErrorCode());
        }

        CpanelProductRecord cpanelProductRecord = new CpanelProductRecord();
        cpanelProductRecord.setProductid(1);
        cpanelProductRecord.setEntityid(2);
        cpanelProductRecord.setState(2);
        cpanelProductRecord.setName("dfsdf");
        cpanelProductRecord.setProducerid(3);
        cpanelProductRecord.setTaxid(4);
        Mockito.when(productDao.findById(Mockito.anyInt())).thenReturn(cpanelProductRecord);

        ProductDeliveryDTO productDelivery = new ProductDeliveryDTO();
        productDelivery.setDeliveryType(ProductDeliveryType.SESSION);
        productDelivery.setStartTimeUnit(ProductDeliveryTimeUnitType.HOURS);
        productDelivery.setStartTimeValue(2L);
        productDelivery.setEndTimeUnit(ProductDeliveryTimeUnitType.HOURS);
        productDelivery.setEndTimeValue(3L);

        CpanelProductRecord cpanelProductRecordUpdated = new CpanelProductRecord();

        cpanelProductRecordUpdated.setDeliverytype(productDelivery.getDeliveryType().getId());
        cpanelProductRecordUpdated.setDeliverystarttimeunit(productDelivery.getStartTimeUnit().getId());
        cpanelProductRecordUpdated.setDeliveryendtimeunit(productDelivery.getEndTimeUnit().getId());
        cpanelProductRecordUpdated.setDeliverystarttimevalue(productDelivery.getStartTimeValue().intValue());
        cpanelProductRecordUpdated.setDeliveryendtimevalue(productDelivery.getEndTimeValue().intValue());
        cpanelProductRecordUpdated.setDeliverydatefrom(CommonUtils.zonedDateTimeToTimestamp(productDelivery.getDeliveryDateFrom()));
        cpanelProductRecordUpdated.setDeliverydateto(CommonUtils.zonedDateTimeToTimestamp(productDelivery.getDeliveryDateTo()));

        Mockito.when(productDao.update(cpanelProductRecord)).thenReturn(cpanelProductRecordUpdated);

        ProductDeliveryDTO productDeliveryDTO = productDeliveryService.updateProductDelivery(ObjectRandomizer.randomLong(), productDelivery);

        assertEquals(productDeliveryDTO.getDeliveryType().getId(), cpanelProductRecordUpdated.getDeliverytype().intValue());
        assertEquals(productDeliveryDTO.getStartTimeValue(), cpanelProductRecordUpdated.getDeliverystarttimevalue().longValue());
        assertEquals(productDeliveryDTO.getEndTimeValue(), cpanelProductRecordUpdated.getDeliveryendtimevalue().longValue());
        assertEquals(productDeliveryDTO.getStartTimeUnit().getId(), cpanelProductRecordUpdated.getDeliverystarttimeunit());
        assertEquals(productDeliveryDTO.getEndTimeUnit().getId(), cpanelProductRecordUpdated.getDeliveryendtimeunit());
        assertEquals(productDeliveryDTO.getDeliveryDateFrom(), CommonUtils.timestampToZonedDateTime(cpanelProductRecordUpdated.getDeliverydatefrom()));
        assertEquals(productDeliveryDTO.getDeliveryDateTo(), CommonUtils.timestampToZonedDateTime(cpanelProductRecordUpdated.getDeliverydateto()));
    }

}
