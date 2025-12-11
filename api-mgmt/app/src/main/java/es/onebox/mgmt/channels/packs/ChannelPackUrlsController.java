package es.onebox.mgmt.channels.packs;

import es.onebox.mgmt.datasources.ms.channel.enums.PackSubtype;
import es.onebox.mgmt.datasources.ms.channel.enums.PackType;
import es.onebox.mgmt.packs.dto.ticketcontents.PackUrlDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.packs.service.PackUrlsService;
import jakarta.validation.constraints.Min;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(ChannelPackUrlsController.BASE_URI)
public class ChannelPackUrlsController {

    static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/packs/{packId}/funnel-urls";

    private static final String CHANNEL_ID_MUST_BE_ABOVE_0 = "Channel Id must be above 0";
    private static final String PACK_ID_MUST_BE_ABOVE_0 = "Pack Id must be above 0";

    private final PackUrlsService packUrlsService;

    public ChannelPackUrlsController(PackUrlsService packUrlsService) {
        this.packUrlsService = packUrlsService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public List<PackUrlDTO> getPackUrls(@PathVariable @Min(value = 1, message = CHANNEL_ID_MUST_BE_ABOVE_0) Long channelId,
                                        @PathVariable @Min(value = 1, message = PACK_ID_MUST_BE_ABOVE_0) Long packId) {
        return packUrlsService.getChannelPackUrls(channelId, packId, PackSubtype.CHANNEL);
    }

}
