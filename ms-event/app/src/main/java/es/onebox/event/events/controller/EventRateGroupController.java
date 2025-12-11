package es.onebox.event.events.controller;

import es.onebox.event.common.CommonIdResponse;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.CreateRateGroupRequestDTO;
import es.onebox.event.events.dto.RateGroupDTO;
import es.onebox.event.events.dto.RatesGroupDTO;
import es.onebox.event.events.dto.UpdateRateGroupRequestDTO;
import es.onebox.event.events.request.RatesFilter;
import es.onebox.event.events.service.EventRateGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/rates-group")
public class EventRateGroupController {

    private final EventRateGroupService eventRateGroupService;
    private final RefreshDataService refreshDataService;

    @Autowired
    public EventRateGroupController(EventRateGroupService eventRateGroupService, RefreshDataService refreshDataService) {
        this.eventRateGroupService = eventRateGroupService;
        this.refreshDataService = refreshDataService;
    }

    @GetMapping
    public RatesGroupDTO getEventRatesGroup(RatesFilter filter, @PathVariable(value = "eventId") Integer eventId) {
        return eventRateGroupService.findRatesGroupByEventId(eventId, filter);
    }

    @GetMapping("/{rateGroupId}")
    public RateGroupDTO getEventRatesGroup(@PathVariable(value = "eventId") Integer eventId,
                                      @PathVariable(value = "rateGroupId") Integer rateGroupId) {
        return eventRateGroupService.findRate(eventId, rateGroupId);
    }

    @PostMapping
    public CommonIdResponse createEventRateGroup(@RequestBody CreateRateGroupRequestDTO rateDTO,
                                            @PathVariable(value = "eventId") Integer eventId) {

        return eventRateGroupService.createEventRateGroup(eventId, rateDTO);
    }

    @PutMapping
    public void updateEventRatesGroup(@RequestBody UpdateRateGroupRequestDTO[] ratesGroup,
                                      @PathVariable(value = "eventId") Integer eventId) {

        eventRateGroupService.updateEventRatesGroup(eventId, Arrays.asList(ratesGroup));
        refreshDataService.refreshEvent(eventId.longValue(), "updateEventRatesGroup");
    }

    @PutMapping(value = "/{rateGroupId}")
    public void updateEventRate(@RequestBody UpdateRateGroupRequestDTO rateGroupDTO,
                                @PathVariable(value = "eventId") Integer eventId,
                                @PathVariable(value = "rateGroupId") Integer rateGroupId) {
        rateGroupDTO.setId(rateGroupId.longValue());
        eventRateGroupService.updateEventRatesGroup(eventId, Collections.singletonList(rateGroupDTO));
        refreshDataService.refreshEvent(eventId.longValue(), "updateEventRate");
    }

    @DeleteMapping(value = "/{rateGroupId}")
    public void deleteEventRate(@PathVariable(value = "eventId") Integer eventId,
                                @PathVariable(value = "rateGroupId") Integer rateGroupId) {
        eventRateGroupService.deleteEventRateGroup(eventId, rateGroupId);
        refreshDataService.refreshEvent(eventId.longValue(), "deleteEventRate");
    }

}
