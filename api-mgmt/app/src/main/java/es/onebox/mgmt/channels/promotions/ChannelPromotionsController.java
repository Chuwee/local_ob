package es.onebox.mgmt.channels.promotions;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.channels.dto.ChannelPromotionDetailDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionsDTO;
import es.onebox.mgmt.channels.promotions.dto.ChannelPromotionsFilter;
import es.onebox.mgmt.channels.promotions.dto.CreateChannelPromotionDTO;
import es.onebox.mgmt.channels.promotions.dto.UpdateChannelPromotionDTO;
import es.onebox.mgmt.channels.promotions.service.ChannelPromotionsService;
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

import java.util.Optional;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@RestController
@Validated
@RequestMapping(value = ChannelPromotionsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ChannelPromotionsController {

    protected static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/promotions";
    private static final String AUDIT_COLLECTION = "CHANNEL_PROMOTIONS";

    private final ChannelPromotionsService channelPromotionsService;

    @Autowired
    public ChannelPromotionsController(ChannelPromotionsService channelPromotionsService) {
        this.channelPromotionsService = channelPromotionsService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping()
    public ChannelPromotionsDTO getChannelPromotions(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                     @BindUsingJackson @Valid ChannelPromotionsFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return channelPromotionsService.getChannelPromotions(channelId, Optional.ofNullable(filter).orElse(new ChannelPromotionsFilter()));
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/{promotionId}")
    public ChannelPromotionDetailDTO getChannelPromotion(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                         @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return channelPromotionsService.getChannelPromotion(channelId, promotionId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public IdDTO createChannelPromotion(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                        @Valid @RequestBody CreateChannelPromotionDTO createChannelPromotionDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return channelPromotionsService.createChannelPromotion(channelId, createChannelPromotionDTO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{promotionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelPromotion(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                       @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId,
                                       @RequestBody @Valid UpdateChannelPromotionDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        channelPromotionsService.updateChannelPromotion(channelId, promotionId, request);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @DeleteMapping(value = "/{promotionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChannelPromotion(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                       @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        channelPromotionsService.deleteChannelPromotion(channelId, promotionId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/{promotionId}/clone")
    public IdDTO clone(@PathVariable @Min(value = 1, message = "channel must be above 0") Long channelId,
                       @PathVariable @Min(value = 1, message = "promotionId must be above 0") Long promotionId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CLONE);
        return channelPromotionsService.cloneChannelPromotion(channelId, promotionId);
    }

}
