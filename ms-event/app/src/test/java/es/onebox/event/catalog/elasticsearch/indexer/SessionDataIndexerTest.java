package es.onebox.event.catalog.elasticsearch.indexer;

import es.onebox.cache.repository.CacheRepository;
import es.onebox.event.catalog.dao.CatalogSessionCouchDao;
import es.onebox.event.catalog.dao.record.SessionForCatalogRecord;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationContext;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.dao.SessionElasticDao;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.events.dao.CountrySubdivisionDao;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.loyaltypoints.sessions.dao.SessionLoyaltyPointsConfigCouchDao;
import es.onebox.event.sessions.dao.SeasonSessionDao;
import es.onebox.event.sessions.dto.SessionStatus;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SessionDataIndexerTest {

    private static final Integer EVENT_ID = 877;
    private static final Integer SESSION_ID = 123;

    @Mock
    private SessionElasticDao sessionElasticDao;
    @Mock
    private CatalogSessionCouchDao catalogSessionCouchDao;
    @Mock
    private SeasonSessionDao seasonSessionDao;
    @Mock
    private CountrySubdivisionDao countrySubdivisionDao;
    @Mock
    private SessionLoyaltyPointsConfigCouchDao sessionLoyaltyPointsConfigCouchDao;
    @Mock
    private EntitiesRepository entitiesRepository;
    @Mock
    private CacheRepository localCacheRepository;
    @Mock
    private StaticDataContainer staticDataContainer;

    private SessionDataIndexer sessionDataIndexer;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.sessionDataIndexer = new SessionDataIndexer(
                sessionElasticDao,
                catalogSessionCouchDao,
                seasonSessionDao,
                countrySubdivisionDao,
                sessionLoyaltyPointsConfigCouchDao,
                entitiesRepository,
                localCacheRepository,
                staticDataContainer,
                null
        );
    }

    @Test
    void testIndexSessions_whenPartialBasic_shouldUpdateBasicInfo() {
        EventIndexationContext context = buildContext(EventIndexationType.PARTIAL_BASIC);

        SessionData oldValue = buildSessionData();
        when(catalogSessionCouchDao.get(anyString())).thenReturn(oldValue.getSession());

        Assertions.assertEquals(oldValue.getSession().getSessionName(), "before");
        Assertions.assertEquals(oldValue.getSession().getSessionStatus(), SessionStatus.PLANNED.getId().byteValue());

        sessionDataIndexer.indexSessions(context);

        verify(sessionElasticDao, times(1)).bulkUpsert(anyBoolean(), anyString(),
                argThat((ArgumentMatcher<SessionData>) argument ->
                {
                    Session newValue = argument.getSession();
                    return newValue != null &&
                            newValue.getSessionName().equals("after") &&
                            newValue.getSessionStatus().equals(SessionStatus.READY.getId().byteValue());
                }));
    }

    @Test
    void testIndexSessions_whenPartialComElements_shouldUpdateComElementsInfo() {
        EventIndexationContext context = buildContext(EventIndexationType.PARTIAL_COM_ELEMENTS);
        CpanelElementosComEventoRecord elem1 = new CpanelElementosComEventoRecord();
        elem1.setIdelemento(1);
        elem1.setIdioma(1);
        elem1.setValor("test1");
        CpanelElementosComEventoRecord elem2 = new CpanelElementosComEventoRecord();
        elem2.setIdelemento(1);
        elem2.setIdioma(1);
        elem2.setValor("test2");
        context.setComElementsBySession(Map.of(SESSION_ID, List.of(elem1, elem2)));

        SessionData oldValue = buildSessionData();
        when(catalogSessionCouchDao.get(anyString())).thenReturn(oldValue.getSession());

        sessionDataIndexer.indexSessions(context);

        verify(sessionElasticDao, times(1)).bulkUpsert(anyBoolean(), anyString(),
                argThat((ArgumentMatcher<SessionData>) argument ->
                {
                    Session newValue = argument.getSession();
                    return newValue != null &&
                            newValue.getCommunicationElements().size() == 2 &&
                            newValue.getCommunicationElements().get(0).getValue().equals("test1") &&
                            newValue.getCommunicationElements().get(1).getValue().equals("test2");
                }));
    }

    private SessionData buildSessionData() {
        SessionData sessionData = new SessionData();
        Session session = new Session();
        session.setSessionId(SESSION_ID.longValue());
        session.setSessionName("before");
        session.setSessionStatus(SessionStatus.PLANNED.getId().byteValue());
        session.setEventId(EVENT_ID.longValue());
        session.setEntityId(1L);
        sessionData.setSession(session);
        return sessionData;
    }

    private EventIndexationContext buildContext(EventIndexationType type) {
        CpanelEventoRecord eventRecord = buildEventRecord();
        EventIndexationContext context = new EventIndexationContext(eventRecord, type);
        EntityDTO entity = new EntityDTO(1);
        entity.setOperator(new EntityDTO(1));
        context.setEntity(entity);
        context.setSessions(buildSessions());
        context.setSessionConfigs(new HashMap<>());
        context.setVenueTemplatesBySession(new HashMap<>());
        context.setVenueTemplateInfos(new HashMap<>());
        context.setVenueDescriptor(new HashMap<>());
        return context;
    }

    private List<SessionForCatalogRecord> buildSessions() {
        List<SessionForCatalogRecord> sessions = new ArrayList<>();
        SessionForCatalogRecord session = new SessionForCatalogRecord();
        session.setIdsesion(SESSION_ID);
        session.setIdevento(EVENT_ID);
        session.setNombre("after");
        session.setEstado(SessionStatus.READY.getId());
        session.setIdpromotor(1);
        sessions.add(session);
        return sessions;
    }

    private CpanelEventoRecord buildEventRecord() {
        CpanelEventoRecord record = new CpanelEventoRecord();
        record.setIdevento(EVENT_ID);
        record.setIdentidad(1);
        record.setTipoevento(EventType.NORMAL.getId());
        record.setNombre("Test Event");
        record.setEstado(2);
        record.setIdpromotor(1);
        return record;
    }
} 
