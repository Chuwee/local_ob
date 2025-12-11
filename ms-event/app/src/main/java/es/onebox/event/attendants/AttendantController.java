package es.onebox.event.attendants;

import es.onebox.event.attendants.dto.EventAttendantsConfigDTO;
import es.onebox.event.attendants.dto.ModifyEventAttendantsConfigDTO;
import es.onebox.event.attendants.dto.ModifySessionAttendantsConfigDTO;
import es.onebox.event.attendants.dto.SessionAttendantsConfigDTO;
import es.onebox.event.config.ApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.BASE_URL)
public class AttendantController {

    private final AttendantsConfigService attendantsConfigService;

    @Autowired
    public AttendantController(AttendantsConfigService attendantsConfigService) {
        this.attendantsConfigService = attendantsConfigService;
    }

    @GetMapping( "/events/{eventId}/sessions/{sessionId}/attendants")
    public SessionAttendantsConfigDTO getSessionAttendantsConfig(@PathVariable("sessionId") Long sessionId, @PathVariable("eventId") Long eventId) {
        return attendantsConfigService.getSessionAttendantsConfig(sessionId);
    }

    @PostMapping("/events/{eventId}/sessions/{sessionId}/attendants")
    public void createSessionAttendantsConfig(@PathVariable("sessionId") Long sessionId,
            @PathVariable("eventId") Long eventId, @RequestBody ModifySessionAttendantsConfigDTO attendantsDTO) {
        attendantsConfigService.createSessionAttendantsConfig(sessionId, eventId,
                SessionAttendantConfigConverter.toEntity(sessionId, attendantsDTO));
    }

    @PutMapping( "/events/{eventId}/sessions/{sessionId}/attendants")
    public void upsertSessionAttendantsConfig(@PathVariable("sessionId") Long sessionId,
            @PathVariable("eventId") Long eventId, @RequestBody ModifySessionAttendantsConfigDTO attendantsDTO) {
        attendantsConfigService.upsertSessionAttendantsConfig(sessionId, eventId,
                SessionAttendantConfigConverter.toEntity(sessionId, attendantsDTO));
    }

    @DeleteMapping("/events/{eventId}/sessions/{sessionId}/attendants")
    public void deleteSessionAttendantsConfig(@PathVariable("sessionId") Long sessionId,
            @PathVariable("eventId") Long eventId) {
        attendantsConfigService.deleteSessionAttendantsConfig(sessionId, eventId);
    }

    @GetMapping("/events/{eventId}/attendants")
    public EventAttendantsConfigDTO getEventAttendantsConfig(@PathVariable("eventId") Long eventId) {
        return attendantsConfigService.getEventsAttendantConfig(eventId);
    }

    @PostMapping( "/events/{eventId}/attendants")
    public void upsertEventAttendantsConfig(@PathVariable("eventId") Long eventId,
            @RequestBody ModifyEventAttendantsConfigDTO eventAttendantsDTO) {
        attendantsConfigService.upsertEventsAttendantConfig(eventId, EventAttendantConfigConverter.toEntity(eventId, eventAttendantsDTO));
    }

    @PostMapping( "/events/{eventId}/attendants/addChannel")
    public void addChannelToAttendantsConfig(@PathVariable("eventId") Long eventId, @RequestBody Long channelId) {
        attendantsConfigService.addChannelToAttendantsConfig(eventId, channelId);
    }
}
