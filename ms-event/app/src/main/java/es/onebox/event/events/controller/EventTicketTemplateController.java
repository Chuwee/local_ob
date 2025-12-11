package es.onebox.event.events.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.TicketTemplateDTO;
import es.onebox.event.events.service.EventTicketTemplateService;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/ticket-templates")
public class EventTicketTemplateController {
    
    private final EventTicketTemplateService eventTicketTemplateService;
    
    @Autowired
    public EventTicketTemplateController(final EventTicketTemplateService eventTicketTemplateService) {
        this.eventTicketTemplateService = eventTicketTemplateService;
    }

    @RequestMapping(method = GET)
    public List<TicketTemplateDTO> getEventTemplates(@PathVariable(value = "eventId") Integer eventId) {
        return eventTicketTemplateService.findTicketTemplatesByEventId(eventId);
    }
}
