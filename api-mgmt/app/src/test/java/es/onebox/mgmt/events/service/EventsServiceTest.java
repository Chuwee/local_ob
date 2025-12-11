package es.onebox.mgmt.events.service;

// Exceptions
import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.common.interactivevenue.dto.InteractiveVenueType;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;

// DTOs
import es.onebox.mgmt.events.dto.UpdateEventRequestDTO;

// Mocks
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.order.repository.OrdersRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.events.EventsService;
import es.onebox.mgmt.validation.ValidationService;

// Mockito imports
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

// JUNIT imports
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.mockito.MockitoAnnotations;

import java.util.List;

class EventsServiceTest {

    @Mock
    private ValidationService validationService;

    @Mock
    private OrdersRepository ordersRepository;

    @Mock
    private EntitiesRepository entitiesRepository;

    @Mock
    private EventsRepository eventsRepository;

    @InjectMocks
    private EventsService eventsService;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testValidateUpdateInteractiveVenueException(){

        Long eventId = EventTestData.EVENT_ID;

        UpdateEventRequestDTO updateEventSettingsDTOException =
                EventTestData.getUpdateEventSettingsDTOException();

        Event event = EventTestData.getEvent();

        when(validationService.getAndCheckEvent(eventId)).thenReturn(event);
        when(ordersRepository.eventHasOrders(eventId)).thenReturn(false);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () -> {
            eventsService.updateEvent(eventId, updateEventSettingsDTOException);
        });

        assertEquals(ApiMgmtErrorCode.INVALID_EVENT_INTERACTIVE_VENUE_CONFIG.getMessage(), exception.getMessage());
        assertEquals(ApiMgmtErrorCode.INVALID_EVENT_INTERACTIVE_VENUE_CONFIG.getErrorCode(), exception.getErrorCode());

        verify(validationService).getAndCheckEvent(anyLong());
        verify(ordersRepository).eventHasOrders(anyLong());

    }

    @Test
    void testValidateUpdateInteractiveVenueNotException(){

        Long eventId = EventTestData.EVENT_ID;
        Long entityId = EventTestData.ENTITY_ID;

        UpdateEventRequestDTO updateEventSettingsDTONotException =
                EventTestData.getUpdateEventSettingsDTONotException();

        Event event = EventTestData.getEvent();
        Entity entity = EventTestData.getEntity();
        Operator operator = EventTestData.getOperator();

        when(validationService.getAndCheckEvent(eventId)).thenReturn(event);
        when(ordersRepository.eventHasOrders(eventId)).thenReturn(false);
        when(entitiesRepository.getEntity(eventId)).thenReturn(entity);
        when(entitiesRepository.getCachedOperator(entityId)).thenReturn(operator);

        assertDoesNotThrow(() -> eventsService.updateEvent(eventId, updateEventSettingsDTONotException));

        verify(validationService).getAndCheckEvent(anyLong());
        verify(ordersRepository).eventHasOrders(anyLong());
        verify(eventsRepository).updateEvent(any());

    }

    @Test
    void testValidateUpdateInteractiveVenue(){

        Long eventId = EventTestData.EVENT_ID;
        Long entityId = EventTestData.ENTITY_ID;
        Entity entity = EventTestData.getEntity();
        Operator operator = EventTestData.getOperator();
        Event event = EventTestData.getEvent();

        UpdateEventRequestDTO updateEventSettingsDTO =
                EventTestData.getUpdateEventSettingsDTOException();

        when(validationService.getAndCheckEvent(eventId)).thenReturn(event);
        when(ordersRepository.eventHasOrders(eventId)).thenReturn(false);

        OneboxRestException exception = assertThrows(OneboxRestException.class, () -> {
            eventsService.updateEvent(eventId, updateEventSettingsDTO);
        });

        assertEquals(ApiMgmtErrorCode.INVALID_EVENT_INTERACTIVE_VENUE_CONFIG.getMessage(), exception.getMessage());
        assertEquals(ApiMgmtErrorCode.INVALID_EVENT_INTERACTIVE_VENUE_CONFIG.getErrorCode(), exception.getErrorCode());

        verify(validationService).getAndCheckEvent(anyLong());
        verify(ordersRepository).eventHasOrders(anyLong());

        updateEventSettingsDTO.getSettings().getInteractiveVenue().setAllowInteractiveVenue(true);
        updateEventSettingsDTO.getSettings().getInteractiveVenue().setInteractiveVenueType(InteractiveVenueType.VENUE_3D_PACIFA);
        entity.setInteractiveVenue(null);

        when(validationService.getAndCheckEvent(eventId)).thenReturn(event);
        when(ordersRepository.eventHasOrders(eventId)).thenReturn(false);
        when(entitiesRepository.getEntity(eventId)).thenReturn(entity);

        exception = assertThrows(OneboxRestException.class, () -> {
            eventsService.updateEvent(eventId, updateEventSettingsDTO);
        });

        assertEquals(ApiMgmtErrorCode.FORBIDDEN_EVENT_INTERACTIVE_VENUE_UPDATE.getMessage(), exception.getMessage());
        assertEquals(ApiMgmtErrorCode.FORBIDDEN_EVENT_INTERACTIVE_VENUE_UPDATE.getErrorCode(), exception.getErrorCode());

        verify(validationService, times(2)).getAndCheckEvent(anyLong());
        verify(ordersRepository, times(2)).eventHasOrders(anyLong());

        updateEventSettingsDTO.getSettings().getInteractiveVenue().setAllowInteractiveVenue(true);
        updateEventSettingsDTO.getSettings().getInteractiveVenue()
                .setInteractiveVenueType(InteractiveVenueType.VENUE_3D_MMC_V2);
        entity.setInteractiveVenue(EventTestData.getEntity().getInteractiveVenue());
        entity.getInteractiveVenue().setEnabled(true);
        entity.getInteractiveVenue().setAllowedVenues(List.of(
                es.onebox.mgmt.datasources.common.enums.InteractiveVenueType.VENUE_3D_PACIFA));

        when(validationService.getAndCheckEvent(eventId)).thenReturn(event);
        when(ordersRepository.eventHasOrders(eventId)).thenReturn(false);
        when(entitiesRepository.getEntity(eventId)).thenReturn(entity);

        exception = assertThrows(OneboxRestException.class, () -> {
            eventsService.updateEvent(eventId, updateEventSettingsDTO);
        });

        assertEquals(ApiMgmtErrorCode.INTERACTIVE_VENUE_TYPE_NOT_FROM_ENTITY.getErrorCode(), exception.getErrorCode());

        verify(validationService, times(3)).getAndCheckEvent(anyLong());
        verify(ordersRepository, times(3)).eventHasOrders(anyLong());

        updateEventSettingsDTO.getSettings().getInteractiveVenue()
                .setInteractiveVenueType(InteractiveVenueType.VENUE_3D_PACIFA);

        when(validationService.getAndCheckEvent(eventId)).thenReturn(event);
        when(ordersRepository.eventHasOrders(eventId)).thenReturn(false);
        when(entitiesRepository.getEntity(eventId)).thenReturn(entity);
        when(entitiesRepository.getCachedOperator(entityId)).thenReturn(operator);

        assertDoesNotThrow(() -> eventsService.updateEvent(eventId, updateEventSettingsDTO));

        verify(validationService, times(4)).getAndCheckEvent(anyLong());
        verify(ordersRepository, times(4)).eventHasOrders(anyLong());
        verify(eventsRepository).updateEvent(any());

    }

}
