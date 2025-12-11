package es.onebox.mgmt.loyaltypoints.channels.controller;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.loyaltypoints.channels.dto.UpdateChannelLoyaltyPointsDTO;
import es.onebox.mgmt.loyaltypoints.channels.dto.ChannelLoyaltyPointsDTO;
import es.onebox.mgmt.loyaltypoints.channels.service.LoyaltyPointsService;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static es.onebox.core.security.Roles.Codes.*;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = LoyaltyPointsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class LoyaltyPointsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/loyalty-points";
    private static final String AUDIT_CHANNEL_LOYALTY_POINTS = "CHANNEL_LOYALTY_POINTS";

    private final LoyaltyPointsService loyaltyPointsService;

    @Autowired
    public LoyaltyPointsController(LoyaltyPointsService loyaltyPointsService) {
        this.loyaltyPointsService = loyaltyPointsService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLoyaltyPoints(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                    @Valid @NotNull @RequestBody UpdateChannelLoyaltyPointsDTO updateLoyaltyPointsDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_CHANNEL_LOYALTY_POINTS, AuditTag.AUDIT_ACTION_UPDATE);
        loyaltyPointsService.updateLoyaltyPoints(channelId, updateLoyaltyPointsDTO);
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping
    public ChannelLoyaltyPointsDTO getLoyaltyPoints(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_CHANNEL_LOYALTY_POINTS, AuditTag.AUDIT_ACTION_GET);
        return loyaltyPointsService.getLoyaltyPoints(channelId);
    }

}
