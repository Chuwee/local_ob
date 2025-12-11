package es.onebox.mgmt.channels.packs;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.packs.dto.BasePackItemDTO;
import es.onebox.mgmt.packs.dto.CreatePackDTO;
import es.onebox.mgmt.packs.dto.CreatePackItemsDTO;
import es.onebox.mgmt.packs.dto.PackDTO;
import es.onebox.mgmt.packs.dto.PackDetailDTO;
import es.onebox.mgmt.packs.dto.UpdatePackDTO;
import es.onebox.mgmt.packs.dto.UpdatePackItemDTO;
import es.onebox.mgmt.channels.packs.service.ChannelPacksService;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ChannelPacksController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ChannelPacksController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/packs";

    private static final String AUDIT_PACK_COLLECTION = "CHANNEL_PACKS";
    private static final String AUDIT_PACK_ITEM_COLLECTION = "CHANNEL_PACK_ITEMS";

    private final ChannelPacksService packsService;

    @Autowired
    public ChannelPacksController(ChannelPacksService packsService) {
        this.packsService = packsService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public List<PackDTO> getPacks(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_PACK_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return packsService.getPacks(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{packId}")
    public PackDetailDTO getPack(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                 @PathVariable @Min(value = 1, message = "packId must be above 0") Long packId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_PACK_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return packsService.getPack(channelId, packId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PackDTO createPack(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                              @Valid @RequestBody CreatePackDTO createPackDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_PACK_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return packsService.createPack(channelId, createPackDTO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping("/{packId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePack(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                           @PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
                           @Valid @RequestBody UpdatePackDTO updatePackDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_PACK_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        packsService.updatePack(channelId, packId, updatePackDTO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{packId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePack(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                           @PathVariable @Min(value = 1, message = "packId must be above 0") Long packId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_PACK_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        packsService.deletePack(channelId, packId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{packId}/items")
    public List<BasePackItemDTO> getPackItems(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                              @PathVariable @Min(value = 1, message = "packId must be above 0") Long packId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_PACK_ITEM_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return packsService.getPackItems(channelId, packId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PostMapping("/{packId}/items")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createPackItems(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                @PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
                                @Valid @RequestBody CreatePackItemsDTO createPackItemDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_PACK_ITEM_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        packsService.createPackItems(channelId, packId, createPackItemDTO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping("/{packId}/items/{packItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateItemPack(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                               @PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
                               @PathVariable @Min(value = 1, message = "itemId must be above 0") Long packItemId,
                               @Valid @RequestBody UpdatePackItemDTO updatePackItemDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_PACK_ITEM_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        packsService.updatePackItem(channelId, packId, packItemId, updatePackItemDTO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{packId}/items/{packItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteItemPack(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                               @PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
                               @PathVariable @Min(value = 1, message = "itemId must be above 0") Long packItemId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_PACK_ITEM_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        packsService.deletePackItem(channelId, packId, packItemId);
    }

}
