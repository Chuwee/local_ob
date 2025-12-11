package es.onebox.event.externalevents.controller;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.externalevents.controller.dto.ExternalEventDTO;
import es.onebox.event.externalevents.controller.dto.ExternalEventRateDTO;
import es.onebox.event.externalevents.controller.dto.ExternalEventTypeDTO;
import es.onebox.event.externalevents.service.ExternalEventsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@Validated
@RequestMapping(ApiConfig.BASE_URL + "/external-events")
public class ExternalEventsController {

    private final ExternalEventsService externalEventsService;

    @Autowired
    public ExternalEventsController(ExternalEventsService externalEventsService) {
        this.externalEventsService = externalEventsService;
    }

    @RequestMapping(method = POST)
    public void upsertExternalEvents(@Valid @RequestBody @NotNull ExternalEventDTO[] externalEvents) {
        externalEventsService.upsertExternalEvents(externalEvents);
    }

    @RequestMapping(method = GET)
    public List<ExternalEventDTO> getExternalEvents(@RequestParam(required = false) List<Integer> entityId,
                                                    @RequestParam(required = false) ExternalEventTypeDTO eventType) {
        return externalEventsService.getExternalEvents(entityId, eventType);
    }

    @RequestMapping(method = GET, value = "/{internalId}")
    public ExternalEventDTO getExternalEvent(@PathVariable Long internalId) {
        return externalEventsService.getExternalEvent(internalId);
    }

    @RequestMapping(method = GET, value = "/{internalId}/rates")
    public List<IdNameDTO> getExternalEventRates(@PathVariable Long internalId) {
        return externalEventsService.getRatesForExternalEvent(internalId);
    }

    @RequestMapping(method = POST, value = "/rates")
    public void upsertExternalEventRates(@Valid @RequestBody @NotNull ExternalEventRateDTO[] externalEventRates) {
        externalEventsService.upsertExternalEventRates(externalEventRates);
    }
}
