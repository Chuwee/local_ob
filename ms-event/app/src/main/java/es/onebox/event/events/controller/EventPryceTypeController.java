package es.onebox.event.events.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.onebox.event.common.request.PriceTypeFilter;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.PriceTypesDTO;
import es.onebox.event.events.dto.UpdatePriceTypesDTO;
import es.onebox.event.events.service.EventPriceTypeService;

@RestController
@Validated
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/price-types")
public class EventPryceTypeController {

    private final EventPriceTypeService eventPriceTypeService;
    
    protected EventPryceTypeController(EventPriceTypeService eventPriceTypeService) {
        this.eventPriceTypeService = eventPriceTypeService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public PriceTypesDTO getEventPriceTypes(@PathVariable @Min(value = 1, message = "eventId must be above 0")  Long eventId, PriceTypeFilter filter) {
        return eventPriceTypeService.getEventPriceTypes(eventId, filter);
    }
    
    // DELETE ME: https://oneboxtds.atlassian.net/browse/OB-33685
    @PutMapping("/bulk")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable @Min(value = 1, message = "eventId must be above 0") Long eventId, @Valid @RequestBody UpdatePriceTypesDTO body) {
        eventPriceTypeService.upsert(eventId, body);
    }
}
