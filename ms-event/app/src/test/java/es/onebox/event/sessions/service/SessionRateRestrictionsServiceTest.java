package es.onebox.event.sessions.service;

import es.onebox.event.common.domain.RateRestrictions;
import es.onebox.event.common.domain.RatesRestrictions;
import es.onebox.event.common.domain.Restrictions;
import es.onebox.event.common.services.CommonRatesService;
import es.onebox.event.events.dao.RateDao;
import es.onebox.event.events.dto.RateRestrictedDTO;
import es.onebox.event.events.dto.UpdateRateRestrictionsDTO;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.event.sessions.utils.RateRestrictionsValidator;
import es.onebox.event.venues.dao.PriceTypeConfigDao;
import es.onebox.jooq.cpanel.tables.records.CpanelTarifaRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SessionRateRestrictionsServiceTest {

    @InjectMocks
    private SessionRateRestrictionsService sessionRateRestrictionsService;

    @Mock
    private PriceTypeConfigDao priceTypeConfigDao;

    @Mock
    private SessionConfigCouchDao sessionConfigCouchDao;

    @Mock
    private SessionValidationHelper sessionValidationHelper;

    @Mock
    private RateDao rateDao;

    @Mock
    private CommonRatesService commonRatesService;

    @Mock
    private RateRestrictionsValidator rateRestrictionsValidator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(sessionRateRestrictionsService, "commonRatesService", commonRatesService);
        ReflectionTestUtils.setField(sessionRateRestrictionsService, "rateRestrictionsValidator", rateRestrictionsValidator);
    }

    @Test
    public void upsertSessionRateRestrictionsTest() {
        Long eventId = 1L;
        Long sessionId = 1L;
        Integer rateId = 1;
        UpdateRateRestrictionsDTO updateRateRestrictionsDTO = new UpdateRateRestrictionsDTO();

        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setEntityId(1);
        when(sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId)).thenReturn(sessionRecord);

        SessionConfig sessionConfig = new SessionConfig();
        when(sessionConfigCouchDao.getOrInitSessionConfig(sessionId)).thenReturn(sessionConfig);

        sessionRateRestrictionsService.upsertSessionRateRestrictions(eventId, sessionId, rateId, updateRateRestrictionsDTO);

        verify(sessionConfigCouchDao, times(1)).upsert(any(), any());
    }

    @Test
    public void deleteSessionRateRestrictionsTest() {
        Long sessionId = 1L;
        Long eventId = 1L;
        Integer rateId = 1;

        SessionConfig sessionConfig = new SessionConfig();
        sessionConfig.setRestrictions(new Restrictions());
        RatesRestrictions ratesRestrictions = new RatesRestrictions();
        ratesRestrictions.put(rateId, new RateRestrictions());
        sessionConfig.getRestrictions().setRates(ratesRestrictions);
        when(sessionConfigCouchDao.getOrInitSessionConfig(sessionId)).thenReturn(sessionConfig);

        sessionRateRestrictionsService.deleteSessionRateRestrictions(eventId, sessionId, rateId);

        verify(sessionConfigCouchDao, times(1)).upsert(any(), any());
    }

    @Test
    public void getRestrictedRatesTest() {
        Long eventId = 1L;
        Long sessionId = 1L;

        SessionRecord sessionRecord = new SessionRecord();
        when(sessionValidationHelper.getSessionAndValidateWithEvent(eventId, sessionId)).thenReturn(sessionRecord);

        List<CpanelTarifaRecord> eventRates = Collections.emptyList();
        when(rateDao.getEventRates(eventId.intValue())).thenReturn(eventRates);

        SessionConfig sessionConfig = new SessionConfig();
        sessionConfig.setRestrictions(new Restrictions());
        sessionConfig.getRestrictions().setRates(new RatesRestrictions());
        when(sessionConfigCouchDao.getOrInitSessionConfig(sessionId)).thenReturn(sessionConfig);

        List<RateRestrictedDTO> restrictedRates = sessionRateRestrictionsService.getRestrictedRates(eventId, sessionId);

        assertNotNull(restrictedRates);
    }
}