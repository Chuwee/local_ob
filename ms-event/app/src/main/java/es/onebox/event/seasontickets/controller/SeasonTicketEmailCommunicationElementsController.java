package es.onebox.event.seasontickets.controller;

import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.communicationelements.enums.EmailCommunicationElementTagType;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.EmailCommunicationElementDTO;
import es.onebox.event.events.request.EmailCommunicationElementFilter;
import es.onebox.event.seasontickets.service.SeasonTicketEmailCommunicationElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Validated
@RestController
@RequestMapping(ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/email-communication-elements")
public class SeasonTicketEmailCommunicationElementsController {

    private final SeasonTicketEmailCommunicationElementService seasonTicketEmailCommunicationElementService;
    private final RefreshDataService refreshDataService;

    @Autowired
    public SeasonTicketEmailCommunicationElementsController(SeasonTicketEmailCommunicationElementService seasonTicketEmailCommunicationElementService,
                                                            RefreshDataService refreshDataService) {
        this.seasonTicketEmailCommunicationElementService = seasonTicketEmailCommunicationElementService;
        this.refreshDataService = refreshDataService;
    }

    @RequestMapping(method = GET)
    public List<EmailCommunicationElementDTO> getCommunicationElements(@PathVariable Long seasonTicketId, @Valid EmailCommunicationElementFilter filter) {
        return seasonTicketEmailCommunicationElementService.findCommunicationElements(seasonTicketId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = POST)
    public void updateCommunicationElements(@PathVariable Long seasonTicketId, @Valid @RequestBody EmailCommunicationElementDTO[] elements) {
        seasonTicketEmailCommunicationElementService.updateSeasonTicketCommunicationElements(seasonTicketId, new HashSet<>(Arrays.asList(elements)));
        refreshDataService.refreshEvent(seasonTicketId, "updateCommunicationElements", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = DELETE, value = "/languages/{language}/types/{type}")
    public void deleteCommunicationElement(@PathVariable Long seasonTicketId, @PathVariable String language, @PathVariable EmailCommunicationElementTagType type) {
        seasonTicketEmailCommunicationElementService.deleteCommunicationElement(seasonTicketId, type, language);
        refreshDataService.refreshEvent(seasonTicketId, "deleteCommunicationElement", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

}
