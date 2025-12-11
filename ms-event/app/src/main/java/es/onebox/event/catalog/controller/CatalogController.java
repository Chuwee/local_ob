package es.onebox.event.catalog.controller;

import es.onebox.event.catalog.dto.ChannelEventDTO;
import es.onebox.event.catalog.dto.filter.EventCatalogFilter;
import es.onebox.event.catalog.dto.filter.ProductCatalogFilter;
import es.onebox.event.catalog.dto.product.ProductCatalogDTO;
import es.onebox.event.catalog.dto.product.ProductCatalogsDTO;
import es.onebox.event.catalog.dto.product.ProductStockSearchDTO;
import es.onebox.event.catalog.dto.product.ProductVariantStock;
import es.onebox.event.catalog.dto.venue.container.VenueDescriptor;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.seasonticket.SeasonTicket;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.service.CatalogService;
import es.onebox.event.catalog.service.Event2ESService;
import es.onebox.event.catalog.service.EventCatalogSearchService;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.products.amqp.productupdater.ProductCatalogUpdater;
import es.onebox.event.promotions.dto.EventPromotion;
import es.onebox.event.promotions.service.EventPromotionsService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(ApiConfig.BASE_URL)
public class CatalogController {

    private final EventCatalogSearchService eventCatalogSearchService;
    private final EventPromotionsService eventPromotionsService;
    private final Event2ESService event2ESService;
    private final CatalogService catalogService;
    private final ProductCatalogUpdater productCatalogUpdater;

    @Autowired
    public CatalogController(EventCatalogSearchService eventCatalogSearchService, EventPromotionsService eventPromotionsService, Event2ESService event2ESService, CatalogService catalogService, ProductCatalogUpdater productCatalogUpdater) {
        this.eventCatalogSearchService = eventCatalogSearchService;
        this.eventPromotionsService = eventPromotionsService;
        this.event2ESService = event2ESService;
        this.catalogService = catalogService;
        this.productCatalogUpdater = productCatalogUpdater;
    }

    @GetMapping(value = "/catalog/events/{eventId}/channels/{channelId}")
    public ChannelEventDTO getChannelEvent(@PathVariable Long eventId, @PathVariable Long channelId) {
        return eventCatalogSearchService.getEventCatalog(eventId, channelId);
    }

    @GetMapping(value = "/catalog/channels/{channelId}")
    public List<ChannelEventDTO> searchChannelEvents(@PathVariable Integer channelId, EventCatalogFilter eventCatalogFilter) {
        return eventCatalogSearchService.searchEventsCatalog(channelId, eventCatalogFilter);
    }

    @GetMapping(value = "/catalog/events/{eventId}")
    public Event getEvent(@PathVariable Integer eventId) {
        return this.catalogService.getEvent(eventId);
    }

    @GetMapping(value = "/catalog/sessions/{sessionId}")
    public Session getSession(@PathVariable Integer sessionId) {
        return this.catalogService.getSession(sessionId);
    }

    @GetMapping(value = "/catalog/sessions")
    public List<Session> getSessions(@RequestParam Set<Integer> sessionIds) {
        return this.catalogService.getSessions(sessionIds);
    }

    @GetMapping(value = "/catalog/season-tickets/{seasonTicketId}")
    public SeasonTicket getSeasonTicket(@PathVariable Integer seasonTicketId) {
        return this.catalogService.getSeasonTicket(seasonTicketId);
    }

    @GetMapping(value = "/catalog/sessions/{sessionId}/venue-descriptors")
    public VenueDescriptor getSession(@PathVariable Long sessionId) {
        return this.catalogService.getSessionVenueDescriptor(sessionId);
    }

    @GetMapping(value = "/catalog/venue-descriptors/{venueTemplateId}")
    public VenueDescriptor getTemplateVenueDescriptor(@PathVariable Long venueTemplateId) {
        return this.catalogService.getVenueDescriptor(venueTemplateId);
    }

    @GetMapping(value = "/catalog/event-promotions/{eventPromotionId}")
    public EventPromotion getEventPromotion(@PathVariable Long eventPromotionId) {
        return eventPromotionsService.getEventPromotionById(eventPromotionId);
    }

    @GetMapping(value = "/catalog/event-promotions")
    public List<EventPromotion> searchEventPromotions(@RequestParam(required = false) List<Long> eventIds, @RequestParam(required = false) List<Long> promotionIds) {
        return eventPromotionsService.searchEventPromotions(eventIds, promotionIds);
    }

    @PutMapping(value = "/catalog/events/{eventId}")
    public void refreshEvent(@PathVariable Long eventId,
                             @RequestParam(required = false) Boolean occupation,
                             @RequestParam(required = false) EventIndexationType type) {
        if (Boolean.TRUE.equals(occupation)) {
            event2ESService.updateOccupation(eventId, null);
        } else {
            event2ESService.updateCatalog(eventId, null, type);
        }
    }

    @PutMapping(value = "/catalog/events/{eventId}/sessions/{sessionId}")
    public void refreshSession(@PathVariable Long eventId, @PathVariable Long sessionId,
                               @RequestParam(required = false) Boolean occupation,
                               @RequestParam(required = false) EventIndexationType type) {
        if (Boolean.TRUE.equals(occupation)) {
            event2ESService.updateOccupation(eventId, sessionId);
        } else {
            event2ESService.updateCatalog(eventId, sessionId, type);
        }
    }

    @GetMapping(value = "/catalog/channels/{channelId}/products")
    public ProductCatalogsDTO getSessionProducts(@PathVariable Long channelId, @Valid ProductCatalogFilter productCatalogFilter) {
        return catalogService.findSessionProducts(channelId, productCatalogFilter);
    }

    @GetMapping(value = "/catalog/products/{productId}")
    public ProductCatalogDTO getCatalogProduct(@PathVariable Long productId) {
        return catalogService.findCatalogProduct(productId);
    }

    @GetMapping(value = "/catalog/products/{productId}/variants/{variantId}/stock")
    public Long getProductVariantStock(@PathVariable Long productId, @PathVariable Long variantId) {
        return catalogService.getProductVariantStock(productId, variantId);
    }

    @PutMapping(value = "/catalog/products/{productId}/variants/{variantId}/stock")
    public void updateProductVariantStock(@PathVariable Long productId, @PathVariable Long variantId, @Valid @RequestBody ProductVariantStock productVariantStock) {
        catalogService.updateProductVariantStock(productId, variantId, productVariantStock);
    }

    @GetMapping(value = "/catalog/products/{productId}/variants/{variantId}/sessions/{sessionId}/stock")
    public Long getProductVariantSessionStock(@PathVariable Long productId, @PathVariable Long variantId, @PathVariable Long sessionId) {
        return catalogService.getProductVariantSessionStock(productId, variantId, sessionId);
    }

    @PutMapping(value = "/catalog/products/{productId}/variants/{variantId}/sessions/{sessionId}/stock")
    public void updateProductVariantStock(@PathVariable Long productId, @PathVariable Long variantId,
                                          @Valid @RequestBody ProductVariantStock productVariantStock,
                                          @PathVariable Long sessionId) {
        catalogService.updateProductVariantSessionStock(productId, variantId, productVariantStock, sessionId);
    }

    @PostMapping(value = "/catalog/products/stock")
    public Map<Long ,Map<Long, Long>> getProductVariantSessionsStockSearch(@RequestBody ProductStockSearchDTO request) {
        return catalogService.getProductVariantStockSessions(request);
    }

    @PutMapping(value = "/catalog/products/{productId}/update")
    @ResponseStatus(HttpStatus.OK)
    public void updateProduct(@Min(value = 1, message = "productId must be above 0") @PathVariable Long productId) {
        productCatalogUpdater.updateCatalog(productId);
    }
}
