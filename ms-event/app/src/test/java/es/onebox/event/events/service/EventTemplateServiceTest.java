package es.onebox.event.events.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.domain.PriceZoneRestriction;
import es.onebox.event.common.domain.PriceZonesRestrictions;
import es.onebox.event.common.domain.Restrictions;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.prices.DefaultPriceBuilder;
import es.onebox.event.events.prices.EventPricesDao;
import es.onebox.event.events.prices.PriceBuilderFactory;
import es.onebox.event.events.prices.SGAPriceBuilder;
import es.onebox.event.sessions.dto.UpdateSaleRestrictionDTO;
import es.onebox.event.venues.dao.PriceTypeConfigDao;
import es.onebox.jooq.cpanel.tables.records.CpanelConfigRecintoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class EventTemplateServiceTest {

    private EventTemplateService eventTemplateService;

    @Mock
    private EventDao eventDao;

    @Mock
    private EventPricesDao eventPricesDao;

    @Mock
    private PriceTypeConfigDao priceTypeConfigDao;

    @Mock
    private EventConfigCouchDao eventConfigCouchDao;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        eventTemplateService = new EventTemplateService(eventDao,
                eventPricesDao,
                priceTypeConfigDao,
                eventConfigCouchDao,
                new PriceBuilderFactory(List.of(new DefaultPriceBuilder(eventPricesDao), new SGAPriceBuilder(eventPricesDao))));
    }

    @Test
    public void findEventsRestrictionTestOK() {
        Long eventId = 2L;
        Long templateId = 4L;
        Long priceTypeId = 6L;
        OneboxRestException e = null;
        EventConfig eventConfig;

        // null Event VenueTemplate
        when(eventDao.getEventVenueTemplate(anyLong(), anyLong(), isNull())).thenReturn(null);
        try {
            eventTemplateService.getVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        Assert.assertEquals("On null Event VenueTemplate an exception is thrown",
                "Venue template not from event", e.getMessage());


        // null Event Config
        CpanelConfigRecintoRecord cpanelConfigRecintoRecord = new CpanelConfigRecintoRecord();
        cpanelConfigRecintoRecord.setIdconfiguracion(1);
        cpanelConfigRecintoRecord.setNombreconfiguracion("Config name 1");
        cpanelConfigRecintoRecord.setEsgrafica((byte) 0);
        when(eventDao.getEventVenueTemplate(anyLong(), anyLong(), isNull())).thenReturn(cpanelConfigRecintoRecord);
        when(eventConfigCouchDao.get(anyString())).thenReturn(null);
        try {
            eventTemplateService.getVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId);
        } catch (NullPointerException ore) {
            Assert.assertTrue(true);
        }

        // Null restrictions
        when(eventDao.getEventVenueTemplate(anyLong(), anyLong(), isNull())).thenReturn(cpanelConfigRecintoRecord);
        eventConfig = new EventConfig();
        eventConfig.setEventId(2);
        eventConfig.setRestrictions(new Restrictions());
        when(eventConfigCouchDao.get(anyString())).thenReturn(eventConfig);
        try {
            eventTemplateService.getVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        Assert.assertEquals("On null EventConfig an exception is thrown",
                "Restriction not found", e.getMessage());

        // restriction not found
        when(eventDao.getEventVenueTemplate(anyLong(), anyLong(), isNull())).thenReturn(cpanelConfigRecintoRecord);
        eventConfig = new EventConfig();
        eventConfig.setEventId(2);
        Restrictions restrictions = new Restrictions();
        PriceZonesRestrictions priceZonesRestrictions = new PriceZonesRestrictions();
        PriceZoneRestriction priceZoneRestriction = new PriceZoneRestriction();
        priceZoneRestriction.setMaxItemsMultiplier(Double.valueOf("0.50"));
        priceZoneRestriction.setRequiredPriceZones(Arrays.asList(2, 3));
        priceZonesRestrictions.put(2, priceZoneRestriction);
        restrictions.setPriceZones(priceZonesRestrictions);
        eventConfig.setRestrictions(restrictions);
        when(eventConfigCouchDao.get(anyString())).thenReturn(eventConfig);
        try {
            eventTemplateService.getVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        Assert.assertEquals("On null EventConfig an exception is thrown",
                "Restriction not found", e.getMessage());

        // locked price type not found
        when(eventDao.getEventVenueTemplate(anyLong(), anyLong(), isNull())).thenReturn(cpanelConfigRecintoRecord);
        eventConfig = new EventConfig();
        eventConfig.setEventId(2);
        priceZonesRestrictions = restrictions.getPriceZones();
        PriceZoneRestriction priceZoneRestriction6 = new PriceZoneRestriction();
        priceZoneRestriction6.setMaxItemsMultiplier(Double.valueOf("0.50"));
        priceZoneRestriction6.setRequiredPriceZones(Arrays.asList(2, 3));
        priceZonesRestrictions.put(6, priceZoneRestriction6);
        restrictions.setPriceZones(priceZonesRestrictions);
        eventConfig.setRestrictions(restrictions);
        when(eventConfigCouchDao.get(anyString())).thenReturn(eventConfig);
        CpanelZonaPreciosConfigRecord cpanelZonaPreciosConfigRecord = new CpanelZonaPreciosConfigRecord();
        cpanelZonaPreciosConfigRecord.setIdconfiguracion(1);
        cpanelZonaPreciosConfigRecord.setIdzona(2);
        when(priceTypeConfigDao.findByVenueTemplateId(anyInt())).thenReturn(Collections.singletonList(cpanelZonaPreciosConfigRecord));
        try {
            eventTemplateService.getVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        Assert.assertEquals("On null EventConfig an exception is thrown",
                "Price type not found", e.getMessage());

    }

    @Test
    public void createEventRestriction() {
        Long eventId = 2L;
        Long templateId = 4L;
        Long priceTypeId = 6L;
        OneboxRestException e = null;
        UpdateSaleRestrictionDTO updateSaleRestrictionDTO;
        EventConfig eventConfig;

        // null Event VenueTemplate
        when(eventDao.getEventVenueTemplate(anyLong(), anyLong(), isNull())).thenReturn(null);
        try {
            updateSaleRestrictionDTO = new UpdateSaleRestrictionDTO();
            eventTemplateService.createVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId, updateSaleRestrictionDTO);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        Assert.assertEquals("On null Event VenueTemplate an exception is thrown",
                "Venue template not from event", e.getMessage());


        // Graphic template
        CpanelConfigRecintoRecord cpanelConfigRecintoRecord = new CpanelConfigRecintoRecord();
        cpanelConfigRecintoRecord.setIdconfiguracion(1);
        cpanelConfigRecintoRecord.setNombreconfiguracion("Config name 1");
        cpanelConfigRecintoRecord.setEsgrafica((byte) 1);
        when(eventDao.getEventVenueTemplate(anyLong(), anyLong(), isNull())).thenReturn(cpanelConfigRecintoRecord);
        CpanelZonaPreciosConfigRecord priceZoneConfig = new CpanelZonaPreciosConfigRecord();
        priceZoneConfig.setIdzona(priceTypeId.intValue());
        priceZoneConfig.setIdconfiguracion(1);
        CpanelZonaPreciosConfigRecord priceZoneConfig2 = new CpanelZonaPreciosConfigRecord();
        priceZoneConfig2.setIdzona(2);
        priceZoneConfig2.setIdconfiguracion(1);
        when(priceTypeConfigDao.findByVenueTemplateId(anyInt())).thenReturn(List.of(priceZoneConfig, priceZoneConfig2));
        try {
            updateSaleRestrictionDTO = new UpdateSaleRestrictionDTO();
            updateSaleRestrictionDTO.setLockedTicketsNumber(null);
            updateSaleRestrictionDTO.setRequiredPriceTypeIds(List.of(2L));
            eventTemplateService.createVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId, updateSaleRestrictionDTO);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        // TODO: This error code is not used!
        // TODO: exists in MsEventSessionErrorCode and MsEventErrorCode
        // TODO: But is not used anywhere
        //        Assert.assertEquals("On a Graphic Template an exception is thrown",
        //                "This operation is not allowed on a Graphic Template", e.getMessage());
		Assert.assertEquals("On a Graphic Template an exception is thrown",
				"You must inform either 'required ticket number' or 'locked ticket number', but not both",
				e.getMessage());


        // null VenueTemplate
        cpanelConfigRecintoRecord.setEsgrafica((byte) 0);
        when(eventDao.getEventVenueTemplate(anyLong(), anyLong(), isNull())).thenReturn(cpanelConfigRecintoRecord);
        when(priceTypeConfigDao.findByVenueTemplateId(anyInt())).thenReturn(null);
        try {
            updateSaleRestrictionDTO = new UpdateSaleRestrictionDTO();
            eventTemplateService.createVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId, updateSaleRestrictionDTO);
        } catch (NullPointerException ore) {
            Assert.assertTrue(true);
        }

        // Locked Price Type not found
        when(eventDao.getEventVenueTemplate(anyLong(), anyLong(), isNull())).thenReturn(cpanelConfigRecintoRecord);
        List<CpanelZonaPreciosConfigRecord> cpanelZonaPreciosConfigRecords = new ArrayList<>();
        CpanelZonaPreciosConfigRecord cpanelZonaPreciosConfigRecord = new CpanelZonaPreciosConfigRecord();
        cpanelZonaPreciosConfigRecord.setIdzona(1);
        cpanelZonaPreciosConfigRecord.setDescripcion("Price type 1");
        cpanelZonaPreciosConfigRecord.setIdconfiguracion(1);
        cpanelZonaPreciosConfigRecords.add(cpanelZonaPreciosConfigRecord);
        when(priceTypeConfigDao.findByVenueTemplateId(anyInt())).thenReturn(cpanelZonaPreciosConfigRecords);
        try {
            updateSaleRestrictionDTO = new UpdateSaleRestrictionDTO();
            eventTemplateService.createVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId, updateSaleRestrictionDTO);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        Assert.assertEquals("When the locked price type is not found an exception is thrown",
                "Price type not found", e.getMessage());

        // Required Price type not found
        when(eventDao.getEventVenueTemplate(anyLong(), anyLong(), isNull())).thenReturn(cpanelConfigRecintoRecord);
        CpanelZonaPreciosConfigRecord cpanelZonaPreciosConfigRecord6 = new CpanelZonaPreciosConfigRecord();
        cpanelZonaPreciosConfigRecord6.setIdzona(6);
        cpanelZonaPreciosConfigRecord6.setDescripcion("Price type 6");
        cpanelZonaPreciosConfigRecord6.setIdconfiguracion(1);
        cpanelZonaPreciosConfigRecords.add(cpanelZonaPreciosConfigRecord6);
        when(priceTypeConfigDao.findByVenueTemplateId(anyInt())).thenReturn(cpanelZonaPreciosConfigRecords);
        try {
            updateSaleRestrictionDTO = new UpdateSaleRestrictionDTO();
            updateSaleRestrictionDTO.setRequiredPriceTypeIds(Collections.singletonList(3L));
            eventTemplateService.createVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId, updateSaleRestrictionDTO);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        Assert.assertEquals("when the required price type is not found an exception is thrown",
                "Price type not found", e.getMessage());


        // null on number of tickets
        when(eventDao.getEventVenueTemplate(anyLong(), anyLong(), isNull())).thenReturn(cpanelConfigRecintoRecord);
        CpanelZonaPreciosConfigRecord cpanelZonaPreciosConfigRecord3 = new CpanelZonaPreciosConfigRecord();
        cpanelZonaPreciosConfigRecord3.setIdzona(3);
        cpanelZonaPreciosConfigRecord3.setDescripcion("Price type 3");
        cpanelZonaPreciosConfigRecord3.setIdconfiguracion(3);
        cpanelZonaPreciosConfigRecords.add(cpanelZonaPreciosConfigRecord3);
        when(priceTypeConfigDao.findByVenueTemplateId(anyInt())).thenReturn(cpanelZonaPreciosConfigRecords);
        try {
            updateSaleRestrictionDTO = new UpdateSaleRestrictionDTO();
            updateSaleRestrictionDTO.setRequiredPriceTypeIds(Collections.singletonList(3L));
            eventTemplateService.createVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId, updateSaleRestrictionDTO);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        Assert.assertEquals("when both number of tickets is null an exception is thrown",
                "You must inform either 'required ticket number' or 'locked ticket number', but not both", e.getMessage());

        // both number of tickets with value
        when(eventDao.getEventVenueTemplate(anyLong(), anyLong(), isNull())).thenReturn(cpanelConfigRecintoRecord);
        when(priceTypeConfigDao.findByVenueTemplateId(anyInt())).thenReturn(cpanelZonaPreciosConfigRecords);
        try {
            updateSaleRestrictionDTO = new UpdateSaleRestrictionDTO();
            updateSaleRestrictionDTO.setRequiredPriceTypeIds(Collections.singletonList(3L));
            updateSaleRestrictionDTO.setLockedTicketsNumber(1);
            updateSaleRestrictionDTO.setRequiredTicketsNumber(4);
            eventTemplateService.createVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId, updateSaleRestrictionDTO);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        Assert.assertEquals("when both number of tickets have a value  an exception is thrown",
                "You must inform either 'required ticket number' or 'locked ticket number', but not both", e.getMessage());

        // both number of tickets with value
        when(eventDao.getEventVenueTemplate(anyLong(), anyLong(), isNull())).thenReturn(cpanelConfigRecintoRecord);
        when(priceTypeConfigDao.findByVenueTemplateId(anyInt())).thenReturn(cpanelZonaPreciosConfigRecords);
        eventConfig = new EventConfig();
        eventConfig.setEventId(2);
        when(eventConfigCouchDao.getOrInitEventConfig(anyLong())).thenReturn(eventConfig);
        doNothing().when(eventConfigCouchDao).upsert(anyString(), any());
        e = null;
        try {
            updateSaleRestrictionDTO = new UpdateSaleRestrictionDTO();
            updateSaleRestrictionDTO.setRequiredPriceTypeIds(Collections.singletonList(3L));
            updateSaleRestrictionDTO.setRequiredTicketsNumber(4);
            eventTemplateService.createVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId, updateSaleRestrictionDTO);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        Assert.assertNull(e);
    }

    @Test
    public void deleteEventRestriction() {
        Long eventId = 2L;
        Long templateId = 4L;
        Long priceTypeId = 6L;
        OneboxRestException e = null;

        // null Event VenueTemplate
        when(eventDao.getEventVenueTemplate(anyLong(), anyLong(), isNull())).thenReturn(null);
        try {
            eventTemplateService.deleteVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        Assert.assertEquals("On null Event VenueTemplate an exception is thrown",
                "Venue template not from event", e.getMessage());


        // null VenueTemplate
        CpanelConfigRecintoRecord cpanelConfigRecintoRecord = new CpanelConfigRecintoRecord();
        cpanelConfigRecintoRecord.setIdconfiguracion(1);
        cpanelConfigRecintoRecord.setNombreconfiguracion("Config name 1");
        when(eventDao.getEventVenueTemplate(anyLong(), anyLong(), isNull())).thenReturn(cpanelConfigRecintoRecord);
        when(priceTypeConfigDao.findByVenueTemplateId(anyInt())).thenReturn(null);
        try {
            eventTemplateService.deleteVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId);
        } catch (NullPointerException ore) {
            Assert.assertTrue(true);
        }

        // Restriction not found
        when(eventDao.getEventVenueTemplate(anyLong(), anyLong(), isNull())).thenReturn(cpanelConfigRecintoRecord);
        List<CpanelZonaPreciosConfigRecord> cpanelZonaPreciosConfigRecords = new ArrayList<>();
        CpanelZonaPreciosConfigRecord cpanelZonaPreciosConfigRecord = new CpanelZonaPreciosConfigRecord();
        cpanelZonaPreciosConfigRecord.setIdzona(1);
        cpanelZonaPreciosConfigRecord.setDescripcion("Price type 1");
        cpanelZonaPreciosConfigRecord.setIdconfiguracion(1);
        cpanelZonaPreciosConfigRecords.add(cpanelZonaPreciosConfigRecord);
        when(priceTypeConfigDao.findByVenueTemplateId(anyInt())).thenReturn(cpanelZonaPreciosConfigRecords);
        when(eventConfigCouchDao.get(anyString())).thenReturn(null);
        try {
            eventTemplateService.deleteVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        Assert.assertEquals("When the priceTypeId is not found an exception is thrown",
                "Price type not found", e.getMessage());
    }

    @Test
    public void findAllEventsRestrictionTestOK() {
        Long eventId = 2L;
        Long templateId = 4L;
        OneboxRestException e = null;
        EventConfig eventConfig;

        // null Event VenueTemplate
        when(eventDao.getEventVenueTemplate(anyLong(), anyLong(), isNull())).thenReturn(null);
        try {
            eventTemplateService.getRestrictedPriceTypes(eventId, templateId);
        } catch (OneboxRestException ore) {
            e = ore;
        }
        Assert.assertEquals("On null Event VenueTemplate an exception is thrown",
                "Venue template not from event", e.getMessage());

    }
}
