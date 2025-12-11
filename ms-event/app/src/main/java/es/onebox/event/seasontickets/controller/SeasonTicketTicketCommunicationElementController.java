package es.onebox.event.seasontickets.controller;

import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.communicationelements.enums.PassbookCommunicationElementTagType;
import es.onebox.event.communicationelements.enums.TicketCommunicationElementTagType;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.PassbookCommunicationElementDTO;
import es.onebox.event.events.dto.TicketCommunicationElementDTO;
import es.onebox.event.events.enums.TicketCommunicationElementCategory;
import es.onebox.event.events.request.PassbookCommunicationElementFilter;
import es.onebox.event.events.request.TicketCommunicationElementFilter;
import es.onebox.event.seasontickets.service.SeasonTicketTicketCommunicationElementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/ticket-communication-elements")
public class SeasonTicketTicketCommunicationElementController {

    private final SeasonTicketTicketCommunicationElementService seasonTicketTicketCommunicationElementService;
    private final RefreshDataService refreshDataService;

    @Autowired
    public SeasonTicketTicketCommunicationElementController(
            SeasonTicketTicketCommunicationElementService seasonTicketTicketCommunicationElementService,
            RefreshDataService refreshDataService) {
        this.seasonTicketTicketCommunicationElementService = seasonTicketTicketCommunicationElementService;
        this.refreshDataService = refreshDataService;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{type}")
    public List<TicketCommunicationElementDTO> getEventTicketCommunicationElements(@PathVariable Long seasonTicketId,
                                                                                   @PathVariable TicketCommunicationElementCategory type, TicketCommunicationElementFilter filter) {
        return this.seasonTicketTicketCommunicationElementService.getSeasonTicketCommunicationElements(seasonTicketId, filter, type);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/PASSBOOK")
    public List<PassbookCommunicationElementDTO> getEventPassbookCommunicationElements(@PathVariable Long seasonTicketId,
                                                                                       PassbookCommunicationElementFilter filter) {
        return this.seasonTicketTicketCommunicationElementService.getSeasonTicketPassbookCommunicationElements(seasonTicketId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = POST, value = "/{type}")
    public void updateCommunicationElements(@PathVariable Long seasonTicketId, @PathVariable TicketCommunicationElementCategory type, @Valid @RequestBody TicketCommunicationElementDTO[] elements) {
        seasonTicketTicketCommunicationElementService.updateSeasonTicketCommunicationElements(seasonTicketId, new HashSet<>(Arrays.asList(elements)), type);
        refreshDataService.refreshEvent(seasonTicketId, "updateCommunicationElements", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = POST, value = "/PASSBOOK")
    public void updatePassbookCommunicationElements(@PathVariable Long seasonTicketId, @Valid @RequestBody PassbookCommunicationElementDTO[] elements) {
        seasonTicketTicketCommunicationElementService.updateEventPassbookCommunicationElements(seasonTicketId, new HashSet<>(Arrays.asList((elements))));
        refreshDataService.refreshEvent(seasonTicketId, "updatePassbookCommunicationElements", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = DELETE, value = "/{type}/languages/{language}/types/{tag}")
    public void deleteCommunicationElement(@PathVariable Long seasonTicketId, @PathVariable String language, @PathVariable TicketCommunicationElementCategory type, @PathVariable TicketCommunicationElementTagType tag) {
        seasonTicketTicketCommunicationElementService.deleteEventCommunicationElement(seasonTicketId, tag, language, type);
        refreshDataService.refreshEvent(seasonTicketId, "deleteCommunicationElement", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = DELETE, value = "/PASSBOOK/languages/{language}/types/{tag}")
    public void deletePassbookCommunicationElement(@PathVariable Long seasonTicketId, @PathVariable String language, @PathVariable PassbookCommunicationElementTagType tag) {
        seasonTicketTicketCommunicationElementService.deleteEventPassbookCommunicationElement(seasonTicketId, tag, language);
        refreshDataService.refreshEvent(seasonTicketId, "deletePassbookCommunicationElement", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }
}
