package es.onebox.event.catalog.controller;

import es.onebox.event.catalog.dto.ChannelCatalogEventDetailDTO;
import es.onebox.event.catalog.dto.ChannelCatalogEventSessionsResponse;
import es.onebox.event.catalog.dto.ChannelCatalogEventsResponse;
import es.onebox.event.catalog.dto.ChannelCatalogProductsResponse;
import es.onebox.event.catalog.dto.ChannelCatalogSessionDetailDTO;
import es.onebox.event.catalog.dto.ChannelCatalogSessionsResponse;
import es.onebox.event.catalog.dto.filter.ChannelCatalogEventSessionsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogEventsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogProductsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogSessionsFilter;
import es.onebox.event.catalog.dto.filter.ChannelCatalogTypeFilter;
import es.onebox.event.catalog.dto.packs.ChannelPacks;
import es.onebox.event.catalog.dto.price.CatalogVenueConfigPricesSimulationDTO;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPack;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPackFilter;
import es.onebox.event.catalog.elasticsearch.indexer.ChannelPackIndexer;
import es.onebox.event.catalog.service.ChannelCatalogPackService;
import es.onebox.event.catalog.service.ChannelCatalogService;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.catalog.dto.packs.CatalogPackPricesSimulationDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/channel-catalog/{channelId}")
public class ChannelCatalogController {

    private final ChannelCatalogService channelCatalogService;
    private final ChannelCatalogPackService channelCatalogPackService;
    private final ChannelPackIndexer channelPackIndexer;

    @Autowired
    public ChannelCatalogController(ChannelCatalogService channelCatalogService, ChannelCatalogPackService channelCatalogPackService, ChannelPackIndexer channelPackIndexer) {
        this.channelCatalogService = channelCatalogService;
        this.channelCatalogPackService = channelCatalogPackService;
        this.channelPackIndexer = channelPackIndexer;
    }

    @GetMapping(value = "/events")
    public ChannelCatalogEventsResponse searchChannelEvents(@PathVariable Long channelId,
                                                            @Valid ChannelCatalogEventsFilter filter) {
        return channelCatalogService.searchEvents(channelId, filter);
    }

    @GetMapping(value = "/events/{eventId}")
    public ChannelCatalogEventDetailDTO getChannelEvent(@PathVariable Long channelId,
                                                        @PathVariable Long eventId,
                                                        ChannelCatalogTypeFilter filter) {
        return channelCatalogService.getEventDetail(channelId, eventId, filter);
    }

    @GetMapping(value = "/events/{eventId}/sessions")
    public ChannelCatalogEventSessionsResponse searchChannelSessions(
            @PathVariable final Long channelId,
            @PathVariable final Long eventId,
            ChannelCatalogEventSessionsFilter filter) {
        return this.channelCatalogService.searchSessions(channelId, eventId, filter);
    }

    @GetMapping(value = "/events/{eventId}/sessions/{sessionId}")
    public ChannelCatalogSessionDetailDTO getChannelSession(
            @PathVariable final Long channelId,
            @PathVariable final Long eventId,
            @PathVariable final Long sessionId,
            ChannelCatalogTypeFilter filter) {
        return this.channelCatalogService.getSessionDetail(channelId, eventId, sessionId, filter);
    }

    @GetMapping(value = "/sessions/{sessionId}")
    public ChannelCatalogSessionDetailDTO getChannelSession (
            @PathVariable Long channelId,
            @PathVariable Long sessionId,
            ChannelCatalogTypeFilter filter){
        return channelCatalogService.getSessionDetail(channelId, null, sessionId, filter);
    }

    @GetMapping(value = "/sessions")
    public ChannelCatalogSessionsResponse searchChannelSessions(@PathVariable final Long channelId, ChannelCatalogSessionsFilter filter) {
        return this.channelCatalogService.searchSessions(channelId, filter);
    }

    @GetMapping(value = "/sessions/{sessionId}/simulation")
    @ResponseStatus(HttpStatus.OK)
    public CatalogVenueConfigPricesSimulationDTO pricesSimulation(@PathVariable Long channelId, @PathVariable Long sessionId, ChannelCatalogTypeFilter filter) {
        return channelCatalogService.getPriceSimulationByChannelAndSession(channelId, sessionId, filter);
    }

    @GetMapping("/packs")
    public ChannelPacks searchChannelPacks(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                           ChannelPackFilter filter) {
        return channelCatalogPackService.searchPacks(channelId, filter);
    }

    @GetMapping("/packs/{packId}")
    public ChannelPack getChannelPack(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                      @PathVariable @Min(value = 1, message = "packId must be above 0") Long packId) {
        return channelCatalogPackService.getPack(channelId, packId);
    }

    @GetMapping("/packs/{packId}/prices-simulation")
    public CatalogPackPricesSimulationDTO priceSimulation(@PathVariable Long channelId, @PathVariable Long packId) {
        return channelCatalogPackService.getPriceSimulation(channelId, packId);
    }

    @PutMapping("/packs/{packId}")
    public void updateChannelPack(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                  @PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
                                  @RequestParam(required = false, defaultValue = "true") Boolean mustUpdateEvent,
                                  @RequestParam(required = false, defaultValue = "true") Boolean isFullUpsert) {
        channelPackIndexer.indexChannelPacks(channelId, packId, mustUpdateEvent, EventIndexationType.FULL, isFullUpsert);
    }

    // WARNING - This is a first approach to a channel product catalog and it is still in development. Avoid its usage until it is finished
    @GetMapping(value = "/products")
    public ChannelCatalogProductsResponse searchChannelProducts(@PathVariable Long channelId,
                                                                @Valid ChannelCatalogProductsFilter filter) {
        return channelCatalogService.searchProducts(channelId, filter);
    }
}
