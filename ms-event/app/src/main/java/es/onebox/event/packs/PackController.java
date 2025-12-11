package es.onebox.event.packs;

import es.onebox.event.catalog.elasticsearch.context.EventIndexationType;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.enums.TicketCommunicationElementCategory;
import es.onebox.event.catalog.amqp.catalogpacksupdate.CatalogPacksUpdateProducer;
import es.onebox.event.packs.dto.CreatePackItemsDTO;
import es.onebox.event.packs.dto.PackCommunicationElementDTO;
import es.onebox.event.packs.dto.PackCommunicationElementFilter;
import es.onebox.event.packs.dto.PackCreateRequest;
import es.onebox.event.packs.dto.PackDTO;
import es.onebox.event.packs.dto.PackDetailDTO;
import es.onebox.event.packs.dto.PackItemDTO;
import es.onebox.event.packs.dto.PackItemPriceTypeRequest;
import es.onebox.event.packs.dto.PackItemPriceTypesResponseDTO;
import es.onebox.event.packs.dto.UpdatePackItemSubitemsRequestDTO;
import es.onebox.event.packs.dto.PackItemSubsetsFilter;
import es.onebox.event.packs.dto.PackItemSubsetsResponseDTO;
import es.onebox.event.packs.dto.PackTicketContentsDTO;
import es.onebox.event.packs.dto.PackUpdateRequest;
import es.onebox.event.packs.dto.PacksFilterRequest;
import es.onebox.event.packs.dto.PacksResponse;
import es.onebox.event.packs.dto.UpdatePackItemDTO;
import es.onebox.event.packs.enums.PackTicketContentTagType;
import es.onebox.event.packs.service.PackContentsService;
import es.onebox.event.packs.service.PackService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

import static es.onebox.event.catalog.amqp.catalogpacksupdate.CatalogPacksUpdateProcessor.PACK_COMM_ELEMENTS_UPDATE_HEADER;

@Validated
@RestController
@RequestMapping(PackController.BASE_URI)
public class PackController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/packs";

    private static final String ORIGIN = "ms-event packs";

    private final PackService packsService;
    private final PackContentsService packContentsService;
    private final CatalogPacksUpdateProducer catalogPacksUpdateProducer;

    @Autowired
    public PackController(PackService packsService, PackContentsService packCommunicationElementService,
                          CatalogPacksUpdateProducer catalogPacksUpdateProducer) {
        this.packsService = packsService;
        this.packContentsService = packCommunicationElementService;
        this.catalogPacksUpdateProducer = catalogPacksUpdateProducer;
    }

    @GetMapping
    public PacksResponse searchPacks(@Valid PacksFilterRequest filter) {
        return packsService.searchPacks(filter);
    }

    @GetMapping("/{packId}")
    public PackDetailDTO getPackById(@PathVariable @Min(value = 1, message = "packId must be above 0") Long packId) {
        return packsService.getPackById(packId);
    }

    @PostMapping
    public PackDTO createPack(@RequestBody @Valid PackCreateRequest request) {
        PackDTO packDTO = packsService.createPack(request);
        packsService.createPackCommElements(packDTO.getId());
        return packDTO;
    }

    @PutMapping("/{packId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updatePack(@PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
                           @RequestBody PackUpdateRequest request) {
        packsService.updatePack(packId, request);
        catalogPacksUpdateProducer.sendMessage(packId, ORIGIN + " updatePack", EventIndexationType.FULL);
    }

    @DeleteMapping("/{packId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deletePack(@PathVariable @Min(value = 1, message = "packId must be above 0") Long packId) {
        packsService.deletePack(packId);
        catalogPacksUpdateProducer.sendMessage(packId, ORIGIN + " deletePack");
    }

    @GetMapping("/{packId}/items")
    public List<PackItemDTO> getPackItems(@PathVariable @Min(value = 1, message = "packId must be above 0") Long packId) {
        return packsService.getPackItems(packId);
    }

    @PostMapping("/{packId}/items")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void createPackItems(@PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
                                @Valid @RequestBody CreatePackItemsDTO request) {
        packsService.createPackItems(packId, request);
        catalogPacksUpdateProducer.sendMessage(packId, ORIGIN + " createPackItems", EventIndexationType.FULL);
    }

    @PutMapping("/{packId}/items/{packItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePackItem(@PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
                               @PathVariable @Min(value = 1, message = "itemId must be above 0") Long packItemId,
                               @Valid @RequestBody UpdatePackItemDTO request) {
        packsService.updatePackItem(packId, packItemId, request);
        catalogPacksUpdateProducer.sendMessage(packId, ORIGIN + " updatePackItem");
    }


    @DeleteMapping("/{packId}/items/{packItemId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deletePackItem(@PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
                               @PathVariable @Min(value = 1, message = "itemId must be above 0") Long packItemId) {
        packsService.deletePackItem(packId, packItemId);
        catalogPacksUpdateProducer.sendMessage(packId, ORIGIN + " deletePackItem", EventIndexationType.FULL);
    }

    @GetMapping("/{packId}/items/{packItemId}/price-types")
    public PackItemPriceTypesResponseDTO getPackItemPriceTypes(
            @PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
            @PathVariable @Min(value = 1, message = "packItemId must be above 0") Long packItemId) {
        return packsService.getPackItemPriceTypes(packId, packItemId);
    }

    @PutMapping("/{packId}/items/{packItemId}/price-types")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePackItemPriceTypes(
            @PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
            @PathVariable @Min(value = 1, message = "packItemId must be above 0") Long packItemId,
            @Valid @RequestBody PackItemPriceTypeRequest request) {
        packsService.updatePackItemPriceTypes(packId, packItemId, request);
        catalogPacksUpdateProducer.sendMessage(packId, ORIGIN + " updatePackItemPriceTypes");
    }

    @GetMapping("/{packId}/items/{packItemId}/subitems")
    public PackItemSubsetsResponseDTO getPackItemSubitems(
            @PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
            @PathVariable @Min(value = 1, message = "packItemId must be above 0") Long packItemId,
            PackItemSubsetsFilter packItemSubsetsFilter) {
        return packsService.getPackItemSubsets(packId, packItemId, packItemSubsetsFilter);
    }

    @PutMapping("/{packId}/items/{packItemId}/subitems")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePackItemSubitems(
            @PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
            @PathVariable @Min(value = 1, message = "packItemId must be above 0") Long packItemId,
            @Valid @RequestBody UpdatePackItemSubitemsRequestDTO request) {
        packsService.updatePackItemSubsets(packId, packItemId, request);
        catalogPacksUpdateProducer.sendMessage(packId, ORIGIN + " updatePackItemSubitems", EventIndexationType.FULL);
    }

    @GetMapping("/{packId}/communication-elements")
    public List<PackCommunicationElementDTO> getPackCommunicationElements(
            @PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
            @Valid PackCommunicationElementFilter filter) {
        return packContentsService.getPackCommunicationElements(packId, filter);
    }

    @PostMapping("/{packId}/communication-elements")
    public void updatePackCommunicationElements(
            @PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
            @Valid @RequestBody PackCommunicationElementDTO[] elements) {
        packContentsService.updatePackCommunicationElements(packId, Arrays.asList(elements));
        catalogPacksUpdateProducer.sendMessage(packId, ORIGIN + " updatePackCommunicationElements", PACK_COMM_ELEMENTS_UPDATE_HEADER);
    }

    @GetMapping("/{packId}/ticket-contents/{category}")
    public PackTicketContentsDTO getPackTicketContent(@PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
                                                      @PathVariable TicketCommunicationElementCategory category,
                                                      @RequestParam(value = "language", required = false) String language,
                                                      @RequestParam(value = "type", required = false) PackTicketContentTagType type) {
        return packContentsService.getPackTicketContent(packId, category, language, type);
    }

    @PutMapping("/{packId}/ticket-contents/{category}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void updatePackTicketContent(@PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
                                        @PathVariable TicketCommunicationElementCategory category,
                                        @Valid @RequestBody PackTicketContentsDTO body) {
        packContentsService.updatePackTicketContent(packId, category, body);
    }

    @DeleteMapping("/{packId}/ticket-contents/{category}/languages/{languageCode}/types/{imageType}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteTicketPDFContent(@PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
                                       @PathVariable TicketCommunicationElementCategory category,
                                       @PathVariable String languageCode,
                                       @PathVariable PackTicketContentTagType imageType) {
        packContentsService.deletePackTicketContent(packId, category, languageCode, imageType);
    }
}
