package es.onebox.mgmt.channels.reviews;

import es.onebox.audit.core.Audit;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.channels.reviews.dto.ChannelReviewConfigResponseDTO;
import es.onebox.mgmt.channels.reviews.dto.ChannelReviewConfigUpdateBulkDTO;
import es.onebox.mgmt.channels.reviews.dto.ChannelReviewConfigUpdateDTO;
import es.onebox.mgmt.channels.reviews.dto.ChannelReviewDTO;
import es.onebox.mgmt.channels.reviews.dto.ChannelReviewUpdateDTO;
import es.onebox.mgmt.channels.reviews.dto.ReviewsConfigFilterDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.ms.channel.dto.reviews.ChannelReviewScope;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ChannelReviewsController.BASE_URI)
public class ChannelReviewsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/reviews";

    private static final String AUDIT_COLLECTION = "CHANNEL_REVIEWS";

    private final ChannelReviewsService channelReviewsService;

    public ChannelReviewsController(ChannelReviewsService channelReviewsService) {
        this.channelReviewsService = channelReviewsService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping()
    public ChannelReviewDTO getChannelReview(@PathVariable @Min(value = 1, message = "channelId must be above 0") Integer channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return channelReviewsService.getChannelReview(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelReview(@PathVariable @Min(value = 1, message = "channelId must be above 0") Integer channelId,
                                    @Valid @RequestBody ChannelReviewUpdateDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        channelReviewsService.updateChannelReview(channelId, request);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/config")
    public ChannelReviewConfigResponseDTO getChannelReviewsConfig(@PathVariable @Min(value = 1, message = "channelId must be above 0") Integer channelId,
                                                                  @BindUsingJackson ReviewsConfigFilterDTO filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return channelReviewsService.getChannelReviewsConfig(channelId, filter);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping("/config/{scope}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void upsertChannelReviewsConfigBulk(@PathVariable @Min(value = 1, message = "channelId must be above 0") Integer channelId,
                                          @PathVariable @Valid ChannelReviewScope scope,
                                    @Valid @RequestBody ChannelReviewConfigUpdateBulkDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        channelReviewsService.upsertChannelReviewsConfigBulk(channelId, scope, request);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping("/config/{scope}/{scopeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelReviewConfig(@PathVariable @Min(value = 1, message = "channelId must be above 0") Integer channelId,
                                          @PathVariable @Valid ChannelReviewScope scope,
                                          @PathVariable @Min(value = 1, message = "scopeId must be above 0") Integer scopeId,
                                          @Valid @RequestBody ChannelReviewConfigUpdateDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        channelReviewsService.updateChannelReviewConfig(channelId, scope, scopeId, request);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/config/{scope}/{scopeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChannelReviewConfig(@PathVariable @Min(value = 1, message = "channelId must be above 0") Integer channelId,
                                          @PathVariable @Valid ChannelReviewScope scope,
                                          @PathVariable @Min(value = 1, message = "scopeId must be above 0") Integer scopeId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        channelReviewsService.deleteChannelReviewConfig(channelId, scope, scopeId);
    }
}
