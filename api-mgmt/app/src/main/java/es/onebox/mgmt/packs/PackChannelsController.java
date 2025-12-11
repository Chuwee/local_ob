package es.onebox.mgmt.packs;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.ms.channel.enums.PackSubtype;
import es.onebox.mgmt.packs.dto.channels.CreatePackChannelDTO;
import es.onebox.mgmt.packs.dto.channels.PackChannelDetailDTO;
import es.onebox.mgmt.packs.dto.channels.PackChannelsDTO;
import es.onebox.mgmt.packs.dto.channels.UpdatePackChannelDTO;
import es.onebox.mgmt.packs.dto.ticketcontents.PackUrlDTO;
import es.onebox.mgmt.packs.service.PackUrlsService;
import es.onebox.mgmt.packs.service.PacksService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
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

@RestController
@Validated
@RequestMapping(PackChannelsController.BASE_URI)
public class PackChannelsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/packs/{packId}/channels";

    private static final String AUDIT_PACK_COLLECTION = "PACKS_CHANNELS";

    private static final String CHANNEL_ID_MUST_BE_ABOVE_0 = "Channel Id must be above 0";
    private static final String PACK_ID_MUST_BE_ABOVE_0 = "Pack Id must be above 0";

    private final PacksService packsService;
    private final PackUrlsService packUrlsService;

    public PackChannelsController(PacksService packsService, PackUrlsService packUrlsService) {
        this.packsService = packsService;
        this.packUrlsService = packUrlsService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public PackChannelsDTO getPackChannels(@PathVariable @Min(value = 1, message = "pack must be above 0") Long packId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_PACK_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return packsService.getPackChannels(packId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{channelId}")
    public PackChannelDetailDTO getPack(@PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
                                        @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_PACK_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return packsService.getPackChannel(packId, channelId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPack(@PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
                           @Valid @RequestBody CreatePackChannelDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_PACK_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        packsService.createPackChannels(packId, request);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePackChannel(@PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
                                  @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_PACK_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        packsService.deletePackChannel(packId, channelId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PostMapping(value = "/{channelId}/request-approval")
    @ResponseStatus(HttpStatus.CREATED)
    public void requestChannelApproval(@PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
                                       @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_PACK_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        packsService.requestChannelApproval(packId, channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/{channelId}/funnel-urls")
    public List<PackUrlDTO> getPackUrls(@PathVariable @Min(value = 1, message = PACK_ID_MUST_BE_ABOVE_0) Long packId,
                                        @PathVariable @Min(value = 1, message = CHANNEL_ID_MUST_BE_ABOVE_0) Long channelId) {
        return packUrlsService.getChannelPackUrls(channelId, packId, PackSubtype.PROMOTER);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @PutMapping("/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updatePackChannel(@PathVariable @Min(value = 1, message = "packId must be above 0") Long packId,
                                  @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                  @Valid @RequestBody UpdatePackChannelDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_PACK_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        packsService.updatePackChannel(packId, channelId, request);
    }
}
