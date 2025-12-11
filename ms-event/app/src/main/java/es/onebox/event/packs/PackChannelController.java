package es.onebox.event.packs;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.catalog.amqp.catalogpacksupdate.CatalogPacksUpdateProducer;
import es.onebox.event.packs.dto.CreatePackChannelDTO;
import es.onebox.event.packs.dto.PackChannelDetailDTO;
import es.onebox.event.packs.dto.PackChannelSearchFilter;
import es.onebox.event.packs.dto.PackChannelsDTO;
import es.onebox.event.packs.dto.RequestSalesPackChannelDTO;
import es.onebox.event.packs.dto.UpdatePackChannelDTO;
import es.onebox.event.packs.service.PackChannelService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;

@RestController
@RequestMapping(PackChannelController.BASE_URI)
public class PackChannelController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/packs/{packId}/channels";

    private static final String ORIGIN = "ms-event packChannels";

    private final PackChannelService packChannelService;
    private final CatalogPacksUpdateProducer catalogPacksUpdateProducer;

    @Autowired
    public PackChannelController(PackChannelService packChannelService, CatalogPacksUpdateProducer catalogPacksUpdateProducer) {
        this.packChannelService = packChannelService;
        this.catalogPacksUpdateProducer = catalogPacksUpdateProducer;
    }

    @GetMapping()
    public PackChannelsDTO getPackChannels(@PathVariable Long packId,
                                           @Valid PackChannelSearchFilter packChannelSearchFilter) {
        return packChannelService.getPackChannels(packId, packChannelSearchFilter);
    }

    @GetMapping(value = "/{channelId}")
    public PackChannelDetailDTO getPackChannel(@PathVariable Long packId, @PathVariable Long channelId) {
        return packChannelService.getPackChannel(packId, channelId);
    }

    @PostMapping()
    public ResponseEntity<Serializable> createPackChannels(@PathVariable Long packId,
                                                          @RequestBody CreatePackChannelDTO request) {
        packChannelService.createPackChannels(packId, request.getChannelIds());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping(value = "/{channelId}")
    public ResponseEntity<Serializable> updatePackChannel(@PathVariable Long packId, @PathVariable Long channelId,
                                                          @RequestBody UpdatePackChannelDTO updatePackChannel) {
        packChannelService.updatePackChannel(packId, channelId, updatePackChannel);
        catalogPacksUpdateProducer.sendMessage(packId, ORIGIN + " updatePackChannel", EventIndexationType.FULL);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/{channelId}")
    public void deletePackChannel(@PathVariable Long packId, @PathVariable Long channelId) {
        packChannelService.deletePackChannel(packId, channelId);
        catalogPacksUpdateProducer.sendMessage(packId, ORIGIN + " deletePackChannel", EventIndexationType.FULL);
    }

    @PostMapping(value = "/{channelId}/request-approval")
    public ResponseEntity<Serializable> requestChannelApproval(@PathVariable Long packId, @PathVariable Long channelId,
                                                               @RequestBody RequestSalesPackChannelDTO requestSalesPackChannelDTO) {
        if (requestSalesPackChannelDTO == null || requestSalesPackChannelDTO.getUserId() == null
                || requestSalesPackChannelDTO.getUserId() <= 0) {
            throw new OneboxRestException(CoreErrorCode.BAD_PARAMETER, "userId is mandatory", null);
        }
        packChannelService.requestChannelApproval(packId, channelId, requestSalesPackChannelDTO.getUserId());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    /* TODO pack-channel features
    @GetMapping(value = "/{channelId}/price-simulation")
    public List<VenueConfigPricesSimulationDTO> getPackChannelPricesSimulation(@PathVariable Long packId,
                                                                               @PathVariable Long channelId) {
        return priceSimulationService.getPriceSimulationIdEventAndChannelId(packId, channelId);
    }


    @GetMapping(value = "/{channelId}/communication-elements")
    public List<EventCommunicationElementDTO> getChannelEventCommunicationElements(@PathVariable Long packId,
                                                                                   @PathVariable Long channelId,
                                                                                   @Valid PackChannelCommunicationElementFilter filter) {
        //channelEventCommunicationElementsService.findCommunicationElements(packId, channelId, filter);
        return List.of();
    }

    @PostMapping(value = "/{channelId}/communication-elements")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelEventCommunicationElements(@PathVariable Long packId, @PathVariable Long channelId,
                                                        @Valid @RequestBody EventCommunicationElementDTO[] elements) {
        //channelEventCommunicationElementsService.updateChannelEventCommunicationElements(packId, channelId, Arrays.asList(elements));
        //packsUpdateProducer.sendMessage(packId, ORIGIN + ".updatePackChannelCommElements");
    }

     */

}
