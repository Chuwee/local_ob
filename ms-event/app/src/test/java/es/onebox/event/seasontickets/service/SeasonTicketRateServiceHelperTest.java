package es.onebox.event.seasontickets.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.seasontickets.dao.SeasonTicketSessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SeasonTicketRateServiceHelperTest {

    @InjectMocks
    private SeasonTicketRateServiceHelper seasonTicketRateServiceHelper;

    @Mock
    private SeasonTicketSessionDao seasonTicketSessionDao;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getSessionIdFromSeasonTicketIdNullTest() {
        Integer seasonTicketId = 1;

        when(seasonTicketSessionDao.searchSessionInfoByEventId(any()))
                .thenReturn(null);

        Assertions.assertThrows(OneboxRestException.class, () ->
                seasonTicketRateServiceHelper.getSessionIdFromSeasonTicketId(seasonTicketId));
    }

    @Test
    public void getSessionIdFromSeasonTicketIdNoNullTest() {
        Integer seasonTicketId = 1;
        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setIdsesion(100);

        when(seasonTicketSessionDao.searchSessionInfoByEventId(any()))
                .thenReturn(Collections.singletonList(sessionRecord));

        Integer result = seasonTicketRateServiceHelper.getSessionIdFromSeasonTicketId(seasonTicketId);
        assertEquals(100, result);
    }
}
