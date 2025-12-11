package es.onebox.event.seasontickets.controller;

import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.communicationelements.enums.PassbookCommunicationElementTagType;
import es.onebox.event.communicationelements.enums.TicketCommunicationElementTagType;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.EventCommunicationElementDTO;
import es.onebox.event.events.dto.PassbookCommunicationElementDTO;
import es.onebox.event.events.enums.TicketCommunicationElementCategory;
import es.onebox.event.events.request.EventCommunicationElementFilter;
import es.onebox.event.events.request.PassbookCommunicationElementFilter;
import es.onebox.event.seasontickets.service.SeasonTicketCommunicationElementService;
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
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = ApiConfig.BASE_URL + "/season-tickets/{seasonTicketId}/communication-elements")
public class SeasonTicketCommunicationElementsController {

    private final SeasonTicketCommunicationElementService seasonTicketCommunicationElementService;
    private final RefreshDataService refreshDataService;

    @Autowired
    public SeasonTicketCommunicationElementsController(SeasonTicketCommunicationElementService seasonTicketCommunicationElementService,
                                                       RefreshDataService refreshDataService) {
        this.seasonTicketCommunicationElementService = seasonTicketCommunicationElementService;
        this.refreshDataService = refreshDataService;
    }

    @RequestMapping(method = GET)
    public List<EventCommunicationElementDTO> getSeasonTicketCommunicationElements(@PathVariable Long seasonTicketId, @Valid EventCommunicationElementFilter filter) {
        return seasonTicketCommunicationElementService.findCommunicationElements(seasonTicketId, filter);
    }

    @RequestMapping(method = POST)
    public void updateSeasonTicketCommunicationElements(@PathVariable Long seasonTicketId, @Valid @RequestBody EventCommunicationElementDTO[] elements) {

        seasonTicketCommunicationElementService.updateCommunicationElements(seasonTicketId, Arrays.asList(elements));
        refreshDataService.refreshEvent(seasonTicketId, "updateSeasonTicketCommunicationElements", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/PASSBOOK")
    public List<PassbookCommunicationElementDTO> getEventPassbookCommunicationElements(@PathVariable Long seasonTicketId,
                                                                                       PassbookCommunicationElementFilter filter) {
        return seasonTicketCommunicationElementService.getPassbookCommunicationElements(seasonTicketId, filter);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = POST, value = "/PASSBOOK")
    public void updatePassbookCommunicationElements(@PathVariable Long seasonTicketId, @Valid @RequestBody PassbookCommunicationElementDTO[] elements) {
        seasonTicketCommunicationElementService.updatePassbookCommunicationElements(seasonTicketId, new HashSet<>(Arrays.asList(elements)));
        refreshDataService.refreshEvent(seasonTicketId, "updatePassbookCommunicationElements", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = DELETE, value = "/{type}/languages/{language}/types/{tag}")
    public void deleteCommunicationElement(@PathVariable Long seasonTicketId, @PathVariable String language, @PathVariable TicketCommunicationElementCategory type, @PathVariable TicketCommunicationElementTagType tag) {
        seasonTicketCommunicationElementService.deleteCommunicationElement(seasonTicketId, tag, language, type);
        refreshDataService.refreshEvent(seasonTicketId, "deleteCommunicationElement", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(method = DELETE, value = "/PASSBOOK/languages/{language}/types/{tag}")
    public void deletePassbookCommunicationElement(@PathVariable Long seasonTicketId, @PathVariable String language, @PathVariable PassbookCommunicationElementTagType tag) {
        seasonTicketCommunicationElementService.deletePassbookCommunicationElement(seasonTicketId, tag, language);
        refreshDataService.refreshEvent(seasonTicketId, "deletePassbookCommunicationElement", EventIndexationType.PARTIAL_COM_ELEMENTS);
    }

}
