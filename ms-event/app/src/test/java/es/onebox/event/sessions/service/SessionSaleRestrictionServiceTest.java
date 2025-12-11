package es.onebox.event.sessions.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.common.domain.PriceZoneRestriction;
import es.onebox.event.common.domain.PriceZonesRestrictions;
import es.onebox.event.common.domain.Restrictions;
import es.onebox.event.exception.MsEventSessionErrorCode;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.dto.UpdateSaleRestrictionDTO;
import es.onebox.event.venues.dao.PriceTypeConfigDao;
import es.onebox.jooq.cpanel.tables.records.CpanelZonaPreciosConfigRecord;

public class SessionSaleRestrictionServiceTest {

    @Mock
    private PriceTypeConfigDao priceTypeConfigDao;
    @Mock
    private SessionConfigCouchDao sessionConfigCouchDao;
    @Mock
    private SessionValidationHelper sessionValidationHelper;

    @InjectMocks
    private SessionSaleRestrictionsService service;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void upsertSaleConstraints() {
        final Long sessionId = 100L;
        final Long priceTypeId = 1L;
        final Long eventId = 101L;
        List<Long> existingPriceZones = Arrays.asList(2L, 3L);
        UpdateSaleRestrictionDTO update = new UpdateSaleRestrictionDTO();
        update.setRequiredPriceTypeIds(existingPriceZones);

        stubSession(eventId, sessionId, false);
        stubPriceTypes(4, 5, 6);
        try {
            service.upsertSaleRestrictions(eventId, sessionId, priceTypeId, update);
            fail("Should fail on not found price type");
        } catch (OneboxRestException e) {
            assertEquals(MsEventSessionErrorCode.PRICE_TYPE_NOT_IN_SESSION.getErrorCode(),e.getErrorCode(),"Fails on not found price type");
        }

        stubPriceTypes(1, 4, 5, 6);
        try {
            service.upsertSaleRestrictions(eventId, sessionId, priceTypeId, update);
            fail("Should fail on not found price type");
        } catch (OneboxRestException e) {
            assertEquals(MsEventSessionErrorCode.PRICE_TYPE_NOT_IN_SESSION.getErrorCode(),e.getErrorCode(),"Fails on not found price type");
        }

        stubPriceTypes(1, 2, 3, 4, 5, 6);

        try {
            service.upsertSaleRestrictions(eventId, sessionId, priceTypeId, update);
            fail("Should fail on empty required ticket number and locked ticket number");
        } catch (OneboxRestException e) {
            assertEquals(MsEventSessionErrorCode.TICKET_NUMBER_EXCLUSION_INPUT.getErrorCode(),e.getErrorCode(),"Fails on empty required ticket number and locked ticket number");
        }

        update.setLockedTicketsNumber(10);
        update.setRequiredTicketsNumber(5);
        try {
            service.upsertSaleRestrictions(eventId, sessionId, priceTypeId, update);
            fail("Should fail on both informed ticket number and locked ticket number");
        } catch (OneboxRestException e) {
            assertEquals(MsEventSessionErrorCode.TICKET_NUMBER_EXCLUSION_INPUT.getErrorCode(),e.getErrorCode(),"Fails on both informed ticket number and locked ticket number");
        }

        update.setRequiredTicketsNumber(null);
        stubSessionConfig(sessionId, null, null, null);
        service.upsertSaleRestrictions(eventId, sessionId, priceTypeId, update);
    }

    @Test
    public void getSaleRestriction() {
        final Long sessionId = 100L;
        final Long priceTypeId = 1L;
        final Long eventId = 101L;

        SessionRecord session = new SessionRecord();
        session.setIdevento(eventId.intValue());
        session.setVenueTemplateId(2);
        session.setIdsesion(2);
        when(sessionValidationHelper.getSessionAndValidateWithEvent(anyLong(), anyLong())).thenReturn(session);
        // session without restrictions
        SessionConfig sessionConfig = new SessionConfig();
        sessionConfig.setSessionId(sessionId.intValue());
        when(sessionConfigCouchDao.getOrInitSessionConfig(anyLong())).thenReturn(sessionConfig);
        try {
            service.getSaleRestriction(eventId, sessionId, priceTypeId);
        } catch (OneboxRestException e) {
            assertEquals(MsEventSessionErrorCode.PRICE_TYPE_RESTRICTION_NOT_FOUND.toString(), e.getErrorCode(),
                    "Fails on a deleted session");
        }

        // session without any price type restrictions
        Restrictions restrictions = new Restrictions();
        sessionConfig.setRestrictions(restrictions);
        when(sessionConfigCouchDao.getOrInitSessionConfig(anyLong())).thenReturn(sessionConfig);
        try {
            service.getSaleRestriction(eventId, sessionId, priceTypeId);
        } catch (OneboxRestException e) {
            assertEquals(MsEventSessionErrorCode.PRICE_TYPE_RESTRICTION_NOT_FOUND.toString(), e.getErrorCode(),
                    "Fails on a deleted session");
        }

        // session without the locked price type restriction
        PriceZonesRestrictions priceZonesRestrictions = new PriceZonesRestrictions();
        PriceZoneRestriction priceZoneRestriction2 = new PriceZoneRestriction();
        priceZoneRestriction2.setMaxItemsMultiplier(Double.valueOf("0.50"));
        priceZoneRestriction2.setRequiredPriceZones(Arrays.asList(8, 9));
        priceZonesRestrictions.put(2, priceZoneRestriction2);
        restrictions.setPriceZones(priceZonesRestrictions);
        sessionConfig.setRestrictions(restrictions);
        when(sessionConfigCouchDao.getOrInitSessionConfig(anyLong())).thenReturn(sessionConfig);
        try {
            service.getSaleRestriction(eventId, sessionId, priceTypeId);
        } catch (OneboxRestException e) {
            assertEquals(MsEventSessionErrorCode.PRICE_TYPE_RESTRICTION_NOT_FOUND.getErrorCode(), e.getErrorCode(),
                    "Fails on not found price type restriction");
        }

        // required price types not found
        PriceZoneRestriction priceZoneRestriction1 = new PriceZoneRestriction();
        priceZoneRestriction1.setMaxItemsMultiplier(Double.valueOf("0.33"));
        priceZoneRestriction1.setRequiredPriceZones(Arrays.asList(6, 7));
        priceZonesRestrictions.put(1, priceZoneRestriction1);
        restrictions.setPriceZones(priceZonesRestrictions);
        when(sessionConfigCouchDao.getOrInitSessionConfig(anyLong())).thenReturn(sessionConfig);
        CpanelZonaPreciosConfigRecord cpanelZonaPreciosConfigRecord6 = new CpanelZonaPreciosConfigRecord();
        cpanelZonaPreciosConfigRecord6.setIdzona(6);
        cpanelZonaPreciosConfigRecord6.setIdconfiguracion(6);
        cpanelZonaPreciosConfigRecord6.setDescripcion("price type 6");
        CpanelZonaPreciosConfigRecord cpanelZonaPreciosConfigRecord1 = new CpanelZonaPreciosConfigRecord();
        cpanelZonaPreciosConfigRecord1.setIdzona(1);
        cpanelZonaPreciosConfigRecord1.setIdconfiguracion(1);
        cpanelZonaPreciosConfigRecord1.setDescripcion("price type 1");
        when(priceTypeConfigDao.findByVenueTemplateId(anyInt())).thenReturn(Arrays.asList(cpanelZonaPreciosConfigRecord1, cpanelZonaPreciosConfigRecord6));

        try {
            service.getSaleRestriction(eventId, sessionId, priceTypeId);
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        // required price types not found
        when(sessionConfigCouchDao.getOrInitSessionConfig(anyLong())).thenReturn(sessionConfig);
        CpanelZonaPreciosConfigRecord cpanelZonaPreciosConfigRecord7 = new CpanelZonaPreciosConfigRecord();
        cpanelZonaPreciosConfigRecord7.setIdzona(7);
        cpanelZonaPreciosConfigRecord7.setIdconfiguracion(7);
        cpanelZonaPreciosConfigRecord7.setDescripcion("price type 7");
        when(priceTypeConfigDao.findByVenueTemplateId(anyInt())).thenReturn(Arrays.asList(cpanelZonaPreciosConfigRecord1, cpanelZonaPreciosConfigRecord6, cpanelZonaPreciosConfigRecord7));

        OneboxRestException ore = null;
        try {
            service.getSaleRestriction(eventId, sessionId, priceTypeId);
        } catch (OneboxRestException e) {
            ore = e;
        }
        assertNull(ore);
    }

    @Test
    public void getAllSessionRestriction() {
        final Long sessionId = 100L;
        final Long eventId = 101L;

        SessionRecord session = new SessionRecord();
        session.setIdevento(eventId.intValue());
        session.setVenueTemplateId(2);
        session.setIdsesion(2);
        when(sessionValidationHelper.getSessionAndValidateWithEvent(anyLong(), anyLong())).thenReturn(session);

        // session without restrictions
        SessionConfig sessionConfig = new SessionConfig();
        sessionConfig.setSessionId(sessionId.intValue());
        when(sessionConfigCouchDao.getOrInitSessionConfig(anyLong())).thenReturn(sessionConfig);
        try {
            service.getRestrictedPriceTypes(eventId, sessionId);
        } catch (OneboxRestException e) {
            assertEquals("400ME001", e.getErrorCode(),
                    "Fails on a deleted session");
        }

        // session without any price type restrictions
        Restrictions restrictions = new Restrictions();
        sessionConfig.setRestrictions(restrictions);
        when(sessionConfigCouchDao.getOrInitSessionConfig(anyLong())).thenReturn(sessionConfig);
        try {
            service.getRestrictedPriceTypes(eventId, sessionId);
        } catch (OneboxRestException e) {
            assertEquals("400ME001", e.getErrorCode(),
                    "Fails on a deleted session");
        }

        // session without the locked price type restriction
        PriceZonesRestrictions priceZonesRestrictions = new PriceZonesRestrictions();
        PriceZoneRestriction priceZoneRestriction2 = new PriceZoneRestriction();
        priceZoneRestriction2.setMaxItemsMultiplier(Double.valueOf("0.50"));
        priceZoneRestriction2.setRequiredPriceZones(Arrays.asList(8, 9));
        priceZonesRestrictions.put(2, priceZoneRestriction2);
        restrictions.setPriceZones(priceZonesRestrictions);
        sessionConfig.setRestrictions(restrictions);
        when(sessionConfigCouchDao.getOrInitSessionConfig(anyLong())).thenReturn(sessionConfig);
        try {
            service.getRestrictedPriceTypes(eventId, sessionId);
        } catch (OneboxRestException e) {
            assertEquals(MsEventSessionErrorCode.PRICE_TYPE_RESTRICTION_NOT_FOUND.toString(), e.getErrorCode(),
                    "Fails on a deleted session");
        }

        // required price types not found
        PriceZoneRestriction priceZoneRestriction1 = new PriceZoneRestriction();
        priceZoneRestriction1.setMaxItemsMultiplier(Double.valueOf("0.33"));
        priceZoneRestriction1.setRequiredPriceZones(Arrays.asList(6, 7));
        priceZonesRestrictions.put(1, priceZoneRestriction1);
        restrictions.setPriceZones(priceZonesRestrictions);
        when(sessionConfigCouchDao.getOrInitSessionConfig(anyLong())).thenReturn(sessionConfig);
        CpanelZonaPreciosConfigRecord cpanelZonaPreciosConfigRecord6 = new CpanelZonaPreciosConfigRecord();
        cpanelZonaPreciosConfigRecord6.setIdzona(6);
        cpanelZonaPreciosConfigRecord6.setIdconfiguracion(6);
        cpanelZonaPreciosConfigRecord6.setDescripcion("price type 6");
        CpanelZonaPreciosConfigRecord cpanelZonaPreciosConfigRecord1 = new CpanelZonaPreciosConfigRecord();
        cpanelZonaPreciosConfigRecord1.setIdzona(1);
        cpanelZonaPreciosConfigRecord1.setIdconfiguracion(1);
        cpanelZonaPreciosConfigRecord1.setDescripcion("price type 1");
        when(priceTypeConfigDao.findByVenueTemplateId(anyInt())).thenReturn(Arrays.asList(cpanelZonaPreciosConfigRecord1, cpanelZonaPreciosConfigRecord6));

        try {
            service.getRestrictedPriceTypes(eventId, sessionId);
        } catch (NullPointerException e) {
            assertTrue(true);
        }

        // required price types not found
        when(sessionConfigCouchDao.getOrInitSessionConfig(anyLong())).thenReturn(sessionConfig);
        CpanelZonaPreciosConfigRecord cpanelZonaPreciosConfigRecord7 = new CpanelZonaPreciosConfigRecord();
        cpanelZonaPreciosConfigRecord7.setIdzona(7);
        cpanelZonaPreciosConfigRecord7.setIdconfiguracion(7);
        cpanelZonaPreciosConfigRecord7.setDescripcion("price type 7");
        when(priceTypeConfigDao.findByVenueTemplateId(anyInt())).thenReturn(Arrays.asList(cpanelZonaPreciosConfigRecord1, cpanelZonaPreciosConfigRecord6, cpanelZonaPreciosConfigRecord7));

        OneboxRestException ore = null;
        try {
            service.getRestrictedPriceTypes(eventId, sessionId);
        } catch (OneboxRestException e) {
            ore = e;
        }
        assertNull(ore);
    }

    @Test
    public void deleteSaleRestriction() {
        final Long sessionId = 100L;
        final Long priceTypeId = 1L;
        final Long eventId = 101L;

        stubSessionConfig(sessionId, 2L, Arrays.asList(1, 2, 3), null);
        stubSession(eventId, sessionId, false);
        stubPriceTypes(4, 5, 6);
        try {
            service.deleteSaleRestrictions(eventId, sessionId, priceTypeId);
            fail("Should fail on price type not in session");
        } catch (OneboxRestException e) {
            assertEquals(MsEventSessionErrorCode.PRICE_TYPE_NOT_IN_SESSION.getErrorCode(),e.getErrorCode(),"Fails on price type not in session");
        }
        stubPriceTypes(1, 2, 3, 4, 5, 6);
        try {
            service.deleteSaleRestrictions(eventId, sessionId, priceTypeId);
            fail("Should fail on not found price type restriction");
        } catch (OneboxRestException e) {
            assertEquals(MsEventSessionErrorCode.PRICE_TYPE_RESTRICTION_NOT_FOUND.getErrorCode(),e.getErrorCode(),"Fails on not found price type restriction");
        }

        stubSessionConfig(sessionId, priceTypeId, Arrays.asList(1, 2, 3), 0.5);
        service.deleteSaleRestrictions(eventId, sessionId, priceTypeId);
    }


    private void stubSession(Long eventId, Long sessionId, boolean isGraphical) {
        SessionRecord s = new SessionRecord();
        s.setIdevento(eventId.intValue());
        s.setIdsesion(sessionId.intValue());
        s.setVenueTemplateGraphic(Byte.parseByte(isGraphical ? "1" : "0"));
        s.setVenueTemplateId(9876);
        when(sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId)).thenReturn(s);
    }

    private void stubSessionConfig(Long sessionId, Long priceTypeId, List<Integer> existingPriceZones,
                                   Double multiplier) {
        SessionConfig sessionConfig = new SessionConfig();
        sessionConfig.setSessionId(sessionId.intValue());
        sessionConfig.setRestrictions(new Restrictions());
        sessionConfig.getRestrictions().setPriceZones(new PriceZonesRestrictions());
        if (priceTypeId != null) {
            PriceZoneRestriction pzr = new PriceZoneRestriction();
            pzr.setRequiredPriceZones(existingPriceZones);
            pzr.setMaxItemsMultiplier(multiplier);
            sessionConfig.getRestrictions().getPriceZones().put(priceTypeId.intValue(), pzr);
        }
        when(sessionConfigCouchDao.getOrInitSessionConfig(sessionId)).thenReturn(sessionConfig);
    }

    private void stubPriceTypes(Integer... priceTypes) {
        List<CpanelZonaPreciosConfigRecord> result = new ArrayList<>();
        for (Integer id : priceTypes) {
            CpanelZonaPreciosConfigRecord pt = new CpanelZonaPreciosConfigRecord();
            pt.setIdzona(id);
            result.add(pt);
        }
        when(priceTypeConfigDao.findByVenueTemplateId(anyInt())).thenReturn(result);
    }
}
