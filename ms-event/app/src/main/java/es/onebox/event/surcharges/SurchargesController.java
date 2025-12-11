package es.onebox.event.surcharges;

import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.surcharges.dto.SurchargeListDTO;
import es.onebox.event.surcharges.dto.SurchargeTypeDTO;
import es.onebox.event.surcharges.dto.SurchargesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(SurchargesController.BASE_URI)
public class SurchargesController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/events/{eventId}/surcharges";

    private final SurchargesService surchargesService;
    private final RefreshDataService refreshDataService;
    private final WebhookService webhookService;

    @Autowired
    public SurchargesController(SurchargesService surchargesService, RefreshDataService refreshDataService, WebhookService webhookService) {
        this.surchargesService = surchargesService;
        this.refreshDataService = refreshDataService;
        this.webhookService = webhookService;
    }


    @GetMapping()
    public List<SurchargesDTO> getEventSurcharge(@PathVariable Long eventId,
                                                 @RequestParam(value = "type", required = false) List<SurchargeTypeDTO> types) {
        return surchargesService.getRanges(eventId, types);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setEventSurcharge(@PathVariable Long eventId, @RequestBody SurchargeListDTO surchargeListDTO) {
        surchargesService.setSurcharges(eventId, surchargeListDTO);
        refreshDataService.refreshEvent(eventId, "setEventSurcharge");
        webhookService.sendEventNotification(eventId, NotificationSubtype.EVENT_SURCHARGES);
    }

    @DeleteMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEventSurcharge(@PathVariable Long eventId) {
        surchargesService.deleteSurchargesAndRanges(eventId);
        refreshDataService.refreshEvent(eventId, "deleteEventSurcharge");
        webhookService.sendEventNotification(eventId);
    }
}
