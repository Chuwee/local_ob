package es.onebox.event.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.catalog.dto.ChangeSeatAllowedSessions;
import es.onebox.event.catalog.dto.ChangeSeatChangeType;
import es.onebox.event.catalog.dto.ChangeSeatTickets;
import es.onebox.event.events.dao.EventConfigCouchDao;
import es.onebox.event.events.domain.eventconfig.EventConfig;
import es.onebox.event.events.dto.ChangeSeatNewTicketSelectionDTO;
import es.onebox.event.events.dto.ChangeSeatPriceDTO;
import es.onebox.event.events.dto.EventChangeSeatDTO;
import es.onebox.event.events.dto.EventChangeSeatExpiryDTO;
import es.onebox.event.events.dto.ReallocationChannelDTO;
import es.onebox.event.events.service.EventConfigService;
import es.onebox.event.exception.MsEventErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class EventConfigServiceTest {

    @Mock
    private EventConfigCouchDao eventConfigCouchDao;

    @InjectMocks
    private EventConfigService eventConfigService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateUpdateShouldThrowInvalidSeatConfigException(){
        Long eventId = 1L;
        Boolean allowChangeSeat = true;
        EventConfig eventConfig = new EventConfig();
        EventChangeSeatDTO changeSeat = new EventChangeSeatDTO();
        changeSeat.setNewTicketSelection(new ChangeSeatNewTicketSelectionDTO());
        changeSeat.getNewTicketSelection().setAllowedSessions(ChangeSeatAllowedSessions.SAME);
        changeSeat.getNewTicketSelection().setSameDateOnly(true);
        changeSeat.setEventChangeSeatExpiry(new EventChangeSeatExpiryDTO());
        changeSeat.setReallocationChannel(buildReallocationChannel());
        changeSeat.setChangeType(ChangeSeatChangeType.ALL);
        changeSeat.getNewTicketSelection().setPrice(new ChangeSeatPriceDTO());
        changeSeat.getNewTicketSelection().setTickets(ChangeSeatTickets.GREATER_OR_EQUAL);

        when(eventConfigCouchDao.getOrInitEventConfig(eventId)).thenReturn(eventConfig);
        OneboxRestException exception = assertThrows(OneboxRestException.class, () ->
                eventConfigService.updateEventChangeSeatsConfig(eventId, allowChangeSeat, changeSeat));
        assertEquals(MsEventErrorCode.INVALID_SESSION_DATE_CHANGE_SEAT_CONFIG.getMessage(), exception.getMessage());

        changeSeat.getNewTicketSelection().setSameDateOnly(false);
        when(eventConfigCouchDao.getOrInitEventConfig(eventId)).thenReturn(eventConfig);
        exception = assertThrows(OneboxRestException.class, () ->
            eventConfigService.updateEventChangeSeatsConfig(eventId, allowChangeSeat, changeSeat));
        assertEquals(MsEventErrorCode.INVALID_SESSION_DATE_CHANGE_SEAT_CONFIG.getMessage(), exception.getMessage());
    }

    private ReallocationChannelDTO buildReallocationChannel() {
        ReallocationChannelDTO reallocationChannel = new ReallocationChannelDTO();
        reallocationChannel.setId(1L);
        reallocationChannel.setApplyToAllChannelTypes(true);

        return reallocationChannel;
    }
}
