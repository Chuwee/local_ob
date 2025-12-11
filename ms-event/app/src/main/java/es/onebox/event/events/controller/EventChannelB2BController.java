package es.onebox.event.events.controller;

import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.ChannelEventB2BQuotaAssignationsDTO;
import es.onebox.event.events.dto.UpdateChannelEventAssignationsDTO;
import es.onebox.event.events.service.EventChannelB2BService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/channels/{channelId}/B2B")
public class EventChannelB2BController {

    private final EventChannelB2BService eventChannelB2bService;
    private final RefreshDataService refreshDataService;

    @Autowired
    public EventChannelB2BController(EventChannelB2BService eventChannelB2bService, RefreshDataService refreshDataService) {
        this.eventChannelB2bService = eventChannelB2bService;
        this.refreshDataService = refreshDataService;
    }

    @GetMapping("/assignations")
    public ChannelEventB2BQuotaAssignationsDTO getEventChannelAssignation(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                                                          @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        return eventChannelB2bService.getChannelEventB2BAssignations(eventId, channelId);
    }

    @PutMapping("/assignations")
    public void updateEventChannelAssignation(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                              @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                              @RequestBody @NotNull UpdateChannelEventAssignationsDTO request) {
        eventChannelB2bService.updateEventChannelAssignation(eventId, channelId, request);
        refreshDataService.refreshEvent(eventId, "updateEventChannelAssignation");
    }


    @DeleteMapping("/assignations")
    public void deleteEventChannelAssignation(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId,
                                              @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        eventChannelB2bService.deleteEventChannelAssignation(eventId, channelId);
        refreshDataService.refreshEvent(eventId, "deleteEventChannelAssignation");
    }
}
