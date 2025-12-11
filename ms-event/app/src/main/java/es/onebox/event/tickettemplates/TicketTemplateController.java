package es.onebox.event.tickettemplates;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.tickettemplates.dto.CloneTicketTemplateDTO;
import es.onebox.event.tickettemplates.dto.CommunicationElementDTO;
import es.onebox.event.tickettemplates.dto.TicketCommunicationElementFilter;
import es.onebox.event.tickettemplates.dto.TicketTemplateLiteralDTO;
import es.onebox.event.tickettemplates.dto.TicketLiteralElementFilter;
import es.onebox.event.tickettemplates.dto.TicketTemplateDTO;
import es.onebox.event.tickettemplates.dto.TicketTemplateDesignDTO;
import es.onebox.event.tickettemplates.dto.TicketTemplatesDTO;
import es.onebox.event.tickettemplates.dto.TicketTemplatesFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/ticket-templates")
public class TicketTemplateController {

    private final TicketTemplateService ticketTemplateService;

    @Autowired
    public TicketTemplateController(TicketTemplateService ticketTemplateService) {
        this.ticketTemplateService = ticketTemplateService;
    }

    @RequestMapping(method = GET, value = "/{ticketTemplateId}")
    public TicketTemplateDTO getTicketTemplate(@PathVariable(value = "ticketTemplateId") Long ticketTemplateId) {
        return ticketTemplateService.getTicketTemplate(ticketTemplateId);
    }

    @GetMapping
    public TicketTemplatesDTO getTicketTemplates(TicketTemplatesFilter filter) {
        return ticketTemplateService.findTicketTemplates(filter);
    }

    @RequestMapping(method = POST)
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO createTicketTemplate(@RequestBody TicketTemplateDTO templateDTO) {

        Integer ticketTemplateId = ticketTemplateService.createTicketTemplate(templateDTO);

        return new IdDTO(ticketTemplateId.longValue());
    }

    @RequestMapping(method = POST, value = "/{ticketTemplateId}/clone")
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO cloneTicketTemplate(@PathVariable(value = "ticketTemplateId") Long ticketTemplateId,
                                     @Valid @RequestBody CloneTicketTemplateDTO body) {
        return new IdDTO(ticketTemplateService.cloneTicketTemplate(ticketTemplateId, body));
    }

    @RequestMapping(method = PUT, value = "/{ticketTemplateId}")
    public void updateTicketTemplate(@PathVariable(value = "ticketTemplateId") Long ticketTemplateId,
                                     @RequestBody TicketTemplateDTO templateDTO) {

        ticketTemplateService.updateTicketTemplate(ticketTemplateId, templateDTO);
    }

    @RequestMapping(method = DELETE, value = "/{ticketTemplateId}")
    public void deleteTicketTemplate(@PathVariable(value = "ticketTemplateId") Long ticketTemplateId) {

        ticketTemplateService.deleteTicketTemplate(ticketTemplateId);
    }

    @RequestMapping(method = GET, value = "/{ticketTemplateId}/communication-elements")
    public List<CommunicationElementDTO> getTicketTemplatesCommunicationElements(
            @PathVariable Long ticketTemplateId, @Valid TicketCommunicationElementFilter filter) {
        return ticketTemplateService.findCommunicationElements(ticketTemplateId, filter);
    }

    @RequestMapping(method = POST, value = "/{ticketTemplateId}/communication-elements")
    public void updateTicketTemplatesCommunicationElements(@PathVariable Long ticketTemplateId,
                                                           @Valid @RequestBody CommunicationElementDTO[] elements) {
        ticketTemplateService.upsertCommunicationElements(ticketTemplateId, Arrays.asList(elements));
    }

    @RequestMapping(method = GET, value = "/{ticketTemplateId}/literals")
    public List<TicketTemplateLiteralDTO> getTicketTemplatesLiterals(
            @PathVariable Long ticketTemplateId, TicketLiteralElementFilter filter) {
        return ticketTemplateService.findLiterals(ticketTemplateId, filter);
    }

    @RequestMapping(method = POST, value = "/{ticketTemplateId}/literals")
    public void updateTicketTemplatesLiterals(@PathVariable Long ticketTemplateId,
                                              @Valid @RequestBody TicketTemplateLiteralDTO[] elements) {
        ticketTemplateService.updateLiterals(ticketTemplateId, Arrays.asList(elements));
    }

    @RequestMapping(method = GET, value = "/designs")
    public List<TicketTemplateDesignDTO> getTicketTemplateDesigns() {
        return ticketTemplateService.findTicketTemplateDesigns();
    }

}
