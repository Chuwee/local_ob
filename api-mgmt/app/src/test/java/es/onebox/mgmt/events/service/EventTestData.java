package es.onebox.mgmt.events.service;

import es.onebox.mgmt.common.interactivevenue.dto.InteractiveVenueType;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.EntityInteractiveVenue;
import es.onebox.mgmt.datasources.ms.entity.dto.Operator;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventVenueViewConfig;
import es.onebox.mgmt.events.dto.SettingsInteractiveVenueDTO;
import es.onebox.mgmt.events.dto.UpdateEventRequestDTO;
import es.onebox.mgmt.events.dto.UpdateEventSettingsDTO;

import java.util.List;

public class EventTestData {

    public static final Long EVENT_ID = 1L;
    public static final Long ENTITY_ID = 1L;

    // should not throw an exception
    public static UpdateEventRequestDTO getUpdateEventSettingsDTONotException(){
        UpdateEventRequestDTO updateEventRequestDTO = new UpdateEventRequestDTO();
        UpdateEventSettingsDTO updateEventSettingsDTO = new UpdateEventSettingsDTO();
        SettingsInteractiveVenueDTO settingsInteractiveVenueDTO = new SettingsInteractiveVenueDTO();
        settingsInteractiveVenueDTO.setInteractiveVenueType(InteractiveVenueType.VENUE_3D_PACIFA);
        settingsInteractiveVenueDTO.setAllowInteractiveVenue(true);
        updateEventSettingsDTO.setInteractiveVenue(settingsInteractiveVenueDTO);
        updateEventRequestDTO.setSettings(updateEventSettingsDTO);
        return updateEventRequestDTO;
    }

    // should throw an exception
    public static UpdateEventRequestDTO getUpdateEventSettingsDTOException(){
        UpdateEventRequestDTO updateEventRequestDTO = new UpdateEventRequestDTO();
        UpdateEventSettingsDTO updateEventSettingsDTO = new UpdateEventSettingsDTO();
        SettingsInteractiveVenueDTO settingsInteractiveVenueDTO = new SettingsInteractiveVenueDTO();
        // without venueType should throw an exception
        settingsInteractiveVenueDTO.setAllowInteractiveVenue(true);
        updateEventSettingsDTO.setInteractiveVenue(settingsInteractiveVenueDTO);
        updateEventRequestDTO.setSettings(updateEventSettingsDTO);
        return updateEventRequestDTO;
    }

    public static Event getEvent(){
        Event event = new Event();
        EventVenueViewConfig eventVenueViewConfig = new EventVenueViewConfig();
        eventVenueViewConfig.setUseVenue3dView(true);
        eventVenueViewConfig.setInteractiveVenueType(InteractiveVenueType.VENUE_3D_PACIFA);
        event.setEventVenueViewConfig(eventVenueViewConfig);
        event.setId(EVENT_ID);
        event.setEntityId(ENTITY_ID);
        return event;
    }

    public static Entity getEntity(){
        Entity entity = new Entity();
        entity.setId(ENTITY_ID);
        EntityInteractiveVenue entityInteractiveVenue = new EntityInteractiveVenue();
        entityInteractiveVenue.setEnabled(true);
        entityInteractiveVenue.setAllowedVenues(List.of
                (es.onebox.mgmt.datasources.common.enums.InteractiveVenueType.VENUE_3D_PACIFA));
        entity.setInteractiveVenue(entityInteractiveVenue);
        return entity;
    }

    public static Operator getOperator() {
        return new Operator();
    }
}
