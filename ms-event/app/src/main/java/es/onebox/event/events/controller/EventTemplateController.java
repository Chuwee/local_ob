package es.onebox.event.events.controller;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.EventTemplatePriceDTO;
import es.onebox.event.events.dto.EventTemplateRestrictionDTO;
import es.onebox.event.events.dto.UpdateEventTemplatePriceDTO;
import es.onebox.event.events.service.EventTemplateService;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.sessions.dto.UpdateSaleRestrictionDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(EventTemplateController.VENUE_TEMPLATES)
public class EventTemplateController {

    public static final String VENUE_TEMPLATES = ApiConfig.BASE_URL + "/events/{eventId}/venue-templates/";
    private static final String VENUE_TEMPLATE = "/{templateId}";
    private static final String VENUE_TEMPLATE_PRICE = VENUE_TEMPLATE + "/prices";
    private static final String VENUE_TEMPLATE_PRICE_TYPE_RESTRICTION = VENUE_TEMPLATE + "/price-types/{priceTypeId}/restrictions";
    private static final String VENUE_TEMPLATE_RESTRICTED_PRICE_TYPES = VENUE_TEMPLATE + "/restricted-price-types";
    private static final String VENUE_TEMPLATE_RESET_PRICE_CURRENCY = "reset-price-currency";
    private static final String EVENT_ID_MUST_BE_ABOVE_0 = "Event Id must be above 0";
    private static final String TEMPLATE_ID_MUST_BE_ABOVE_0 = "Template Id must be above 0";
    private static final String PRICE_TYPE_ID_MUST_BE_ABOVE_0 = "Price type Id must be above 0";

    private final EventTemplateService eventTemplateService;
    private final RefreshDataService refreshDataService;
    private final WebhookService webhookService;

    private static final Logger LOG = LoggerFactory.getLogger(EventTemplateController.class);

    @Autowired
    public EventTemplateController(EventTemplateService eventTemplateService,
                                   RefreshDataService refreshDataService, WebhookService webhookService) {
        this.eventTemplateService = eventTemplateService;
        this.refreshDataService = refreshDataService;
        this.webhookService = webhookService;
    }

    @GetMapping(VENUE_TEMPLATE_PRICE)
    public List<EventTemplatePriceDTO> getVenueTemplatePrices(@PathVariable("eventId") Long eventId,
                                                              @PathVariable("templateId") Long templateId,
                                                              @RequestParam(value = "sessionId", required = false) List<Long> sessionIdList,
                                                              @RequestParam(value = "rateGroupId", required = false) List<Integer> groupRateList,
                                                              @RequestParam(value = "rateGroupProductId", required = false) List<Integer> groupRateProductList) {

        if (eventId == null || eventId <= 0) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_ID_MANDATORY);
        }
        if (templateId == null || templateId <= 0) {
            throw new OneboxRestException(MsEventErrorCode.TEMPLATE_ID_MANDATORY);
        }
        return eventTemplateService.getPrices(eventId, templateId, sessionIdList, groupRateList, groupRateProductList);
    }

    @PutMapping(VENUE_TEMPLATE_PRICE)
    public void updateVenueTemplatePrices(
            @PathVariable("eventId") @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable("templateId") @Min(value = 1, message = TEMPLATE_ID_MUST_BE_ABOVE_0) Long templateId,
            @RequestBody UpdateEventTemplatePriceDTO[] prices) {
        LOG.info("[UPDATEVENUETEMPLATEPRICES] eventId {} templateId {}", eventId, templateId);
        if (eventId == null || eventId <= 0) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_ID_MANDATORY);
        }
        if (templateId == null || templateId <= 0) {
            throw new OneboxRestException(MsEventErrorCode.TEMPLATE_ID_MANDATORY);
        }
        eventTemplateService.updatePrices(eventId, templateId, Arrays.asList(prices));

        refreshDataService.refreshEvent(eventId, "updateVenueTemplatePrices");
        webhookService.sendWebhookGenericEvent(
            eventId, templateId, null, null, NotificationSubtype.EVENT_PRICE_TYPE
        );
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(VENUE_TEMPLATE_PRICE_TYPE_RESTRICTION)
    public EventTemplateRestrictionDTO getVenueTemplatePriceTypeRestrictions(
            @PathVariable("eventId") @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable("templateId") @Min(value = 1, message = TEMPLATE_ID_MUST_BE_ABOVE_0) Long templateId,
            @PathVariable("priceTypeId") @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId) {
        return eventTemplateService.getVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(VENUE_TEMPLATE_PRICE_TYPE_RESTRICTION)
    public void createVenueTemplatePriceTypeRestrictions(
            @PathVariable("eventId") @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable("templateId") @Min(value = 1, message = TEMPLATE_ID_MUST_BE_ABOVE_0) Long templateId,
            @PathVariable("priceTypeId") @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId,
            @RequestBody @Valid UpdateSaleRestrictionDTO updateSaleRestrictionDTO) {
        eventTemplateService.createVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId, updateSaleRestrictionDTO);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(VENUE_TEMPLATE_PRICE_TYPE_RESTRICTION)
    public void deleteVenueTemplatePriceTypeRestrictions(
            @PathVariable("eventId") @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId,
            @PathVariable("templateId") @Min(value = 1, message = TEMPLATE_ID_MUST_BE_ABOVE_0) Long templateId,
            @PathVariable("priceTypeId") @Min(value = 1, message = PRICE_TYPE_ID_MUST_BE_ABOVE_0) Long priceTypeId) {
        eventTemplateService.deleteVenueTemplatePriceTypeRestrictions(eventId, templateId, priceTypeId);
    }

    @GetMapping(VENUE_TEMPLATE_RESTRICTED_PRICE_TYPES)
    public List<IdNameDTO> getVenueTemplateRestrictedPricesTypes(@PathVariable("eventId") Long eventId,
                                                  @PathVariable("templateId") Long templateId) {
        if (eventId == null || eventId <= 0) {
            throw new OneboxRestException(MsEventErrorCode.EVENT_ID_MANDATORY);
        }
        if (templateId == null || templateId <= 0) {
            throw new OneboxRestException(MsEventErrorCode.TEMPLATE_ID_MANDATORY);
        }
        return eventTemplateService.getRestrictedPriceTypes(eventId, templateId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(VENUE_TEMPLATE_RESET_PRICE_CURRENCY)
    public void resetEventVenueTemplatesPricesCurrency(
            @PathVariable("eventId") @Min(value = 1, message = EVENT_ID_MUST_BE_ABOVE_0) Long eventId){
        eventTemplateService.resetEventVenueTemplatesPricesCurrency(eventId);
    }

}
