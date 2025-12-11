package es.onebox.event.events.controller;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.event.common.CommonIdResponse;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.dto.CreateSaleGroupTierDTO;
import es.onebox.event.events.dto.TierCommElemFilterDTO;
import es.onebox.event.events.dto.TierCommunicationElementDTO;
import es.onebox.event.events.dto.TierCreationRequestDTO;
import es.onebox.event.events.dto.TierDTO;
import es.onebox.event.events.dto.TierExtendedDTO;
import es.onebox.event.events.dto.TierPriceTypeAvailabilityDTO;
import es.onebox.event.events.dto.TierUpdateRequestDTO;
import es.onebox.event.events.dto.TiersDTO;
import es.onebox.event.events.dto.UpdateSaleGroupTierDTO;
import es.onebox.event.events.request.TiersFilter;
import es.onebox.event.events.service.EventTierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.io.Serializable;
import java.util.List;

@RestController
@RequestMapping(ApiConfig.BASE_URL + "/events/{eventId}/tiers")
public class EventTierController {


    private final EventTierService eventTierService;

    @Autowired
    public EventTierController(EventTierService eventTierService){
        this.eventTierService = eventTierService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonIdResponse createEventTier(@PathVariable(value = "eventId") Long eventId,
                                            @RequestBody TierCreationRequestDTO tierDTO) {
        return eventTierService.createEventTier(eventId, tierDTO);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public TiersDTO getEventTiers(@PathVariable(value = "eventId") Long eventId,
                                  @Valid TiersFilter tiersFilter) {
        return eventTierService.getEventTiers(eventId, tiersFilter);
    }

    @GetMapping(value = "/{tierId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TierExtendedDTO getEventTier(@PathVariable(value = "eventId") Long eventId,
                                        @PathVariable(value = "tierId") Long tierId) {
        return eventTierService.getEventTier(eventId, tierId);
    }


    @PutMapping(value = "/{tierId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public TierDTO updateEventTier(@PathVariable(value = "eventId") Long eventId,
                                   @PathVariable(value = "tierId") Long tierId,
                                   @RequestBody TierUpdateRequestDTO tierDTO) {
        return eventTierService.updateEventTier(eventId, tierId, tierDTO);
    }

    @DeleteMapping(value = "/{tierId}")
    public void deleteEventTier(@PathVariable(value = "eventId") Long eventId,
                                @PathVariable(value = "tierId") Long tierId) {
        eventTierService.deleteEventTier(eventId, tierId);
    }

    @DeleteMapping(value = "/{tierId}/limit")
    public void deleteEventTierLimit(@PathVariable(value = "eventId") Long eventId,
                                     @PathVariable(value = "tierId") Long tierId) {
        eventTierService.deleteEventTierLimit(eventId, tierId);
    }


    @PostMapping(value = "/{tierId}/saleGroups")
    public void createEventTierSaleGroup(@PathVariable(value = "eventId") Long eventId,
                                         @PathVariable(value = "tierId") Long tierId,
                                         @RequestBody CreateSaleGroupTierDTO createSaleGroupTier) {
        if (createSaleGroupTier == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "Create data is mandatory", null);
        }
        eventTierService.createEventTierSaleGroup(eventId, tierId, createSaleGroupTier.getSaleGroupId(), createSaleGroupTier.getLimit());
    }

    @PutMapping(value = "/{tierId}/saleGroups/{saleGroupId}")
    public void updateEventTierSaleGroup(@PathVariable(value = "eventId") Long eventId,
                                         @PathVariable(value = "tierId") Long tierId,
                                         @PathVariable(value = "saleGroupId") Long saleGroupId,
                                         @RequestBody UpdateSaleGroupTierDTO updateSaleGroupTierDTO) {
        if (updateSaleGroupTierDTO == null) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "Create data is mandatory", null);
        }
        eventTierService.updateEventTierSaleGroup(eventId, tierId, saleGroupId, updateSaleGroupTierDTO.getLimit());
    }

    @DeleteMapping(value = "/{tierId}/saleGroups/{saleGroupId}")
    public void deleteEventTierSaleGroup(@PathVariable(value = "eventId") Long eventId,
                                         @PathVariable(value = "tierId") Long tierId,
                                         @PathVariable(value = "saleGroupId") Long saleGroupId) {
        eventTierService.deleteEventTierSaleGroup(eventId, tierId, saleGroupId);
    }


    @GetMapping(value = "/tiersAvailability")
    public List<TierPriceTypeAvailabilityDTO> getTierSaleGroupsAvailabilities(@PathVariable(value = "eventId") Long eventId) {
        return eventTierService.getTierSaleGroupsAvailabilities(eventId);
    }

    @DeleteMapping(value = "/deleteForSalesGroup")
    public void deleteEventTierForSalesGroup(@PathVariable(value = "eventId") Long eventId,
                                             @RequestBody IdDTO salesGroupIdWrapper) {
        eventTierService.deleteAllTierSaleGroup(eventId, salesGroupIdWrapper);
    }

    @DeleteMapping(value = "/deleteForPriceType")
    public void deleteTiersForPriceType(@PathVariable(value = "eventId") Long eventId,
                                             @RequestBody IdDTO priceTypeIdWrapper) {
        eventTierService.deleteTiersForPriceType(eventId, priceTypeIdWrapper);
    }

    @PutMapping(value = "/{tierId}/decrementLimit")
    public void decrementEventTierLimit(@PathVariable(value = "eventId") Long eventId,
                                         @PathVariable(value = "tierId") Long tierId) {
        eventTierService.decrementEventTierLimit(eventId, tierId);
    }

    @PutMapping(value = "/{tierId}/incrementLimit")
    public void incrementEventTierLimit(@PathVariable(value = "eventId") Long eventId,
                                        @PathVariable(value = "tierId") Long tierId) {
        eventTierService.incrementEventTierLimit(eventId, tierId);
    }

    @PutMapping(value = "/{tierId}/saleGroups/{saleGroupId}/decrementLimit")
    public void decrementEventTierSaleGroupLimit(@PathVariable(value = "eventId") Long eventId,
                                         @PathVariable(value = "tierId") Long tierId,
                                         @PathVariable(value = "saleGroupId") Long saleGroupId) {
        eventTierService.decrementEventTierSaleGroupLimit(eventId, tierId, saleGroupId);
    }

    @PutMapping(value = "/{tierId}/saleGroups/{saleGroupId}/incrementLimit")
    public void incrementEventTierSaleGroupLimit(@PathVariable(value = "eventId") Long eventId,
                                                 @PathVariable(value = "tierId") Long tierId,
                                                 @PathVariable(value = "saleGroupId") Long saleGroupId) {
        eventTierService.incrementEventTierSaleGroupLimit(eventId, tierId, saleGroupId);
    }

    @PostMapping(value = "/{tierId}/communication-elements")
    public ResponseEntity<Serializable> updateCommElements(@PathVariable(value = "eventId") Long eventId,
                                                           @PathVariable(value = "tierId") Long tierId,
                                                           @RequestBody TierCommunicationElementDTO[] communicationElements) {

        eventTierService.updateCommElements(eventId, tierId, communicationElements);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/{tierId}/communication-elements")
    public List<TierCommunicationElementDTO> getCommElements(@PathVariable(value = "eventId") Long eventId,
                                                             @PathVariable(value = "tierId") Long tierId,
                                                             @BindUsingJackson @Valid TierCommElemFilterDTO filter) {

        return eventTierService.getCommElements(eventId, tierId, filter);
    }


}
