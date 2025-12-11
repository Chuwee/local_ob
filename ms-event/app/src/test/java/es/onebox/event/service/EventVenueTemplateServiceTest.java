package es.onebox.event.service;

import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.prices.DefaultPriceBuilder;
import es.onebox.event.events.prices.PriceBuilderFactory;
import es.onebox.event.events.prices.enums.PriceBuilderType;
import es.onebox.utils.ObjectRandomizer;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.dto.EventTemplatePriceDTO;
import es.onebox.event.events.enums.PriceType;
import es.onebox.event.events.prices.EventPriceRecord;
import es.onebox.event.events.prices.EventPricesDao;
import es.onebox.event.events.service.EventTemplateService;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

public class EventVenueTemplateServiceTest {

    @Mock
    private EventPricesDao eventPricesDao;

    @Mock
    private EventDao eventDao;

    @Mock
    private EventConfigCouchDao eventConfigCouchDao;

    @Mock
    private PriceBuilderFactory priceBuilderFactory;

    @InjectMocks
    private EventTemplateService service;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getPricesTest_ok() {
        Long eventId = 1L;
        List<EventPriceRecord> prices = ObjectRandomizer.randomListOf(EventPriceRecord.class, 5);
        prices.forEach(p -> {
            p.setEventId(eventId.intValue());
            p.setPriceType(PriceType.INDIVIDUAL);
        });

        CpanelConfigRecintoRecord template = new CpanelConfigRecintoRecord();
        template.setTipoplantilla(1);
        Mockito.when(eventDao.getEventVenueTemplate(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(template);
        Mockito.when(eventPricesDao.getVenueTemplatePrices(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(prices);
        Mockito.when(eventConfigCouchDao.get(ArgumentMatchers.anyString())).thenReturn(new EventConfig());
        Mockito.when(priceBuilderFactory.getPriceBuilder(PriceBuilderType.DEFAULT)).thenReturn(new DefaultPriceBuilder(eventPricesDao));

        List<EventTemplatePriceDTO> dtos = service.getPrices(eventId, 1L);

        Assertions.assertNotNull(dtos);
        Assertions.assertEquals(prices.size(), dtos.size());
        Assertions.assertEquals(prices.get(0).getPriceZoneId().intValue(), dtos.get(0).getPriceTypeId().intValue());
    }

    @Test
    public void getPricesTest_invalidEvent() {
        Long eventId = -1L;
        List<EventPriceRecord> prices = ObjectRandomizer.randomListOf(EventPriceRecord.class, 5);
        prices.forEach(p -> p.setEventId(eventId.intValue()));

        Assertions.assertThrows(OneboxRestException.class, () ->
                service.getPrices(eventId, 1L));
    }

    @Test
    public void getPricesTest_invalidTemplate() {
        Long eventId = 1L;
        List<EventPriceRecord> prices = ObjectRandomizer.randomListOf(EventPriceRecord.class, 5);
        prices.forEach(p -> p.setEventId(eventId.intValue()));

        Assertions.assertThrows(OneboxRestException.class, () ->
                service.getPrices(eventId, -1L));
    }

    @Test
    public void getPricesTest_templateDoesNotBelongToEvent() {
        Long eventId = 1L;
        List<EventPriceRecord> prices = ObjectRandomizer.randomListOf(EventPriceRecord.class, 5);
        prices.forEach(p -> p.setEventId(200));

        Mockito.when(eventDao.getEventVenueTemplate(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(null);

        Assertions.assertThrows(OneboxRestException.class, () ->
                service.getPrices(eventId, 1L));
    }


}
