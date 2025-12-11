package es.onebox.event.attributes;

import es.onebox.event.datasources.ms.entity.MsEntityDatasource;
import es.onebox.event.events.dao.EventDao;
import es.onebox.event.sessions.SessionValidationHelper;
import es.onebox.jooq.cpanel.tables.records.CpanelAtributosEventoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelAtributosSesionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

public class SessionAttributeServiceTest {

    @InjectMocks
    private SessionAttributeService service;

    @Mock
    private SessionAttributeDao sessionAttributeDao;
    @Mock
    private EventDao eventDao;
    @Mock
    private MsEntityDatasource msEntityDatasource;
    @Mock
    private SessionValidationHelper sessionValidationHelper;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getSessionAttributes() {
        Long eventId = 10L;
        Long sessionId = 15L;

        List<CpanelAtributosSesionRecord> records = new ArrayList<>();
        records.add(new CpanelAtributosSesionRecord(1, 10, 1, 1, null));
        records.add(new CpanelAtributosSesionRecord(2, 10, 1, 2, null));
        records.add(new CpanelAtributosSesionRecord(3, 10, 2, null, "Hello"));
        records.add(new CpanelAtributosSesionRecord(4, 10, 3, null, "600"));

        when(eventDao.getById(eventId.intValue())).thenReturn(new CpanelEventoRecord());
        when(sessionAttributeDao.getSessionAttributes(sessionId.intValue())).thenReturn(records);

        List<AttributeDTO> result =  service.getSessionAttributes(eventId, sessionId);

        result.sort(Comparator.comparing(AttributeDTO::getId));

        assertEquals(3, result.size(), "All results are returned");

        assertEquals(result.get(0).getSelected().size(), 2);
        assertNull(result.get(0).getValue());

        assertEquals(result.get(1).getValue(), "Hello");
        assertTrue(CollectionUtils.isEmpty(result.get(1).getSelected()));

        assertEquals(result.get(2).getValue(), "600");
        assertTrue(CollectionUtils.isEmpty(result.get(2).getSelected()));
    }

}
