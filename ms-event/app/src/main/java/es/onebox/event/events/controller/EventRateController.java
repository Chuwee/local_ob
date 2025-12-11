package es.onebox.event.events.controller;

import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.event.common.CommonIdResponse;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.CreateEventRateDTO;
import es.onebox.event.events.dto.EventRateRestrictionsDTO;
import es.onebox.event.events.dto.EventRatesDTO;
import es.onebox.event.events.dto.RateDTO;
import es.onebox.event.events.dto.RateRestrictedDTO;
import es.onebox.event.events.dto.UpdateEventRateDTO;
import es.onebox.event.events.dto.UpdateRateRestrictionsDTO;
import es.onebox.event.events.helper.EventRateHelper;
import es.onebox.event.events.request.RatesFilter;
import es.onebox.event.events.service.EventRateService;
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
import java.util.List;

@RestController
@RequestMapping(EventRateController.BASE_URI)
public class EventRateController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}";
    private static final String RATES = "/rates";
    private static final String RATE = RATES + "/{rateId}";
    private static final String RATE_RESTRICTIONS = RATE + "/restrictions";
    private static final String RATE_EXTERNAL_TYPES = RATES + "/external-types";
    private static final String RATES_RESTRICTIONS = RATES + "/restrictions";

    private final EventRateService eventRateService;
    private final RefreshDataService refreshDataService;
    private final WebhookService webhookService;
    private final EventRateHelper eventRateHelper;

    @Autowired
    public EventRateController(EventRateService eventRateService, RefreshDataService refreshDataService, WebhookService webhookService, EventRateHelper eventRateHelper) {
        this.eventRateService = eventRateService;
        this.refreshDataService = refreshDataService;
        this.webhookService = webhookService;
        this.eventRateHelper = eventRateHelper;
    }

    @GetMapping(value = RATES)
    public EventRatesDTO getEventRates(RatesFilter filter, @PathVariable(value = "eventId") Integer eventId) {
        return eventRateService.findEventRatesByEventId(eventId, filter);
    }

    @GetMapping(value = RATE)
    public RateDTO getEventRates(@PathVariable(value = "eventId") Integer eventId,
                                 @PathVariable(value = "rateId") Integer rateId) {
        return eventRateService.findRate(eventId, rateId);
    }

    @PostMapping(value = RATES)
    public CommonIdResponse createEventRate(@RequestBody CreateEventRateDTO eventRateDTO,
                                            @PathVariable(value = "eventId") Integer eventId) {

        return eventRateService.createEventRate(eventId, eventRateDTO);
    }

    @PutMapping(value = RATES)
    public void updateEventRates(@RequestBody UpdateEventRateDTO[] rates,
                                 @PathVariable(value = "eventId") Integer eventId) {

        eventRateService.updateEventRates(eventId, Arrays.asList(rates));
        refreshDataService.refreshEvent(eventId.longValue(), "updateEventRates");
        webhookService.sendEventNotification(eventId.longValue(),
                NotificationSubtype.EVENT_RATE_DETAIL);
    }

    @PutMapping(value = RATE)
    public void updateEventRate(@RequestBody UpdateEventRateDTO rateDTO,
                                @PathVariable(value = "eventId") Integer eventId,
                                @PathVariable(value = "rateId") Integer rateId) {

        eventRateService.updateEventRate(eventId, rateId, rateDTO);
        refreshDataService.refreshEvent(eventId.longValue(), "updateEventRate");
        webhookService.sendEventNotification(eventId.longValue(),
                NotificationSubtype.EVENT_RATE_DETAIL);
    }

    @DeleteMapping(value = RATE)
    public void deleteEventRate(@PathVariable(value = "eventId") Integer eventId,
                                @PathVariable(value = "rateId") Integer rateId) {
        eventRateService.deleteEventRate(eventId, rateId);
        refreshDataService.refreshEvent(eventId.longValue(), "deleteEventRate");
        webhookService.sendWebhookGenericEvent(
                eventId.longValue(), null, rateId.longValue(), null, NotificationSubtype.EVENT_RATE_DELETED
        );
    }

    @GetMapping(value = RATE_RESTRICTIONS)
    public EventRateRestrictionsDTO getEventRateRestrictions(@PathVariable(value = "eventId") Integer eventId,
                                                             @PathVariable(value = "rateId") Integer rateId) {
        return eventRateService.getEventRateRestrictions(eventId, rateId);
    }

    @GetMapping(value = RATE_EXTERNAL_TYPES)
    public List<IdNameCodeDTO> getRateExternalTypes(@PathVariable(value = "eventId") Long eventId) {
        return eventRateService.getRateExternalTypes(eventId);
    }

    @PostMapping(value = RATE_RESTRICTIONS)
    public void upsertEventRateRestrictions(@PathVariable(value = "eventId") Integer eventId,
                                            @PathVariable(value = "rateId") Integer rateId,
                                            @RequestBody UpdateRateRestrictionsDTO restrictionsDTO) {
        eventRateService.updateEventRateRestrictions(eventId, rateId, restrictionsDTO);
        refreshDataService.refreshEvent(eventId.longValue(), "upsertEventRateRestrictions");
    }

    @DeleteMapping(value = RATE_RESTRICTIONS)
    public void deleteEventRateRestrictions(@PathVariable(value = "eventId") Integer eventId,
                                            @PathVariable(value = "rateId") Integer rateId) {
        eventRateService.deleteEventRateRestrictions(eventId, rateId);
        refreshDataService.refreshEvent(eventId.longValue(), "deleteEventRateRestrictions");
    }

    @GetMapping(value = RATES_RESTRICTIONS)
    public List<RateRestrictedDTO> getRestrictedRates(@PathVariable(value = "eventId") Integer eventId) {
        return eventRateService.getRestrictedRates(eventId);
    }

    @PostMapping(value = "/rates/external-migration")
    public void migrateExternalRates(@PathVariable(value = "eventId") Integer eventId) {
        eventRateHelper.refreshExternalRates(eventId, null, null);
    }

}
