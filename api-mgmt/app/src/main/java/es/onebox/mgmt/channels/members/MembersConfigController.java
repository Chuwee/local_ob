package es.onebox.mgmt.channels.members;

import es.onebox.audit.core.Audit;
import es.onebox.mgmt.channels.dto.EmissionReasonsDTO;
import es.onebox.mgmt.channels.dto.MemberCapacitiesListDTO;
import es.onebox.mgmt.channels.dto.MemberCapacitiesRequestDTO;
import es.onebox.mgmt.channels.dto.MemberConfigsDTO;
import es.onebox.mgmt.channels.dto.MembershipPaymentInfoDTO;
import es.onebox.mgmt.channels.dto.PaymentModesDTO;
import es.onebox.mgmt.channels.dto.SubscriptionModeDTO;
import es.onebox.mgmt.channels.dto.TranslationsDTO;
import es.onebox.mgmt.channels.dto.TranslationsRequestDTO;
import es.onebox.mgmt.channels.dto.UpdateMemberConfigsDTO;
import es.onebox.mgmt.channels.members.service.MembersConfigService;
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
import java.util.Map;

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = MembersConfigController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class MembersConfigController {
    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels/{channelId}/member-config";
    public static final String CAPACITIES_ID = "/{id}";
    public static final String CAPACITIES = "/capacities";
    public static final String ROLES = "/roles";
    public static final String PERIODICITIES = "/periodicities";
    public static final String MEMBERSHIP = "/membership";
    public static final String SUBSCRIPTION_MODES = "/subscription-modes";
    public static final String SUBSCRIPTION_MODES_SID = "/{sId}";
    public static final String SUBSCRIPTION_MODES_SID_COMMUNICATIONS = "/{sId}/communications";
    public static final String EMISSION_REASONS = "/emission-reasons";
    public static final String PAYMENT_MODES = "/payment-modes";
    private static final String AUDIT_COLLECTION = "MEMBER_CONFIG";

    private final MembersConfigService membersConfigService;

    @Autowired
    public MembersConfigController(MembersConfigService membersConfigService) {
        this.membersConfigService = membersConfigService;
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ANS})
    @GetMapping()
    public MemberConfigsDTO getMemberConfig(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return membersConfigService.getMemberConfig(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping()
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMemberConfig(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                   @Valid @RequestBody UpdateMemberConfigsDTO updateMemberConfigsDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        membersConfigService.updateMemberConfig(channelId, updateMemberConfigsDTO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS})
    @GetMapping(CAPACITIES)
    public MemberCapacitiesListDTO getMemberConfigCapacities
            (@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return membersConfigService.getMemberConfigCapacities(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @PutMapping(CAPACITIES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMemberConfigCapacities(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                             @Valid @RequestBody MemberCapacitiesRequestDTO memberCapacitiesRequestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        membersConfigService.updateMemberConfigCapacities(channelId, memberCapacitiesRequestDTO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @DeleteMapping(value = CAPACITIES + CAPACITIES_ID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMemberConfigCapacity(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                           @PathVariable Long id) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        membersConfigService.deleteMemberConfigCapacities(channelId, id);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS})
    @GetMapping(SUBSCRIPTION_MODES)
    public List<SubscriptionModeDTO> getSubscriptionModes(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return membersConfigService.getSubscriptionModes(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @PostMapping(SUBSCRIPTION_MODES)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void createOrUpdateSubscriptionModes(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                @Valid @RequestBody SubscriptionModeDTO subscriptionModes) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        membersConfigService.createSubscriptionModes(channelId, subscriptionModes);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @PutMapping(value = SUBSCRIPTION_MODES + SUBSCRIPTION_MODES_SID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSubscriptionMode(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                       @PathVariable String sId, @Valid @RequestBody SubscriptionModeDTO subscriptionMode) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        membersConfigService.updateSubscriptionMode(channelId, sId, subscriptionMode);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @DeleteMapping(value = SUBSCRIPTION_MODES + SUBSCRIPTION_MODES_SID)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubscriptionMode(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                       @PathVariable String sId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        membersConfigService.deleteSubscriptionMode(channelId, sId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS})
    @GetMapping(value = SUBSCRIPTION_MODES + SUBSCRIPTION_MODES_SID)
    public SubscriptionModeDTO getSubscriptionMode(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                   @PathVariable String sId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return membersConfigService.getSubscriptionMode(channelId, sId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS})
    @GetMapping(value = SUBSCRIPTION_MODES + SUBSCRIPTION_MODES_SID_COMMUNICATIONS)
    public Map<String, TranslationsDTO> getSubscriptionModeCommunications
            (@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
             @PathVariable String sId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return membersConfigService.getSubscriptionModeCommunications(channelId, sId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @PutMapping(value = SUBSCRIPTION_MODES + SUBSCRIPTION_MODES_SID_COMMUNICATIONS)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateSubscriptionModeCommunications(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                                     @PathVariable String sId, @Valid @RequestBody TranslationsRequestDTO translationsMap) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        membersConfigService.updateSubscriptionModeCommunications(channelId, sId, translationsMap);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS})
    @GetMapping(value = PERIODICITIES + "/{periodicityId}/communications")
    @ResponseStatus(HttpStatus.OK)
    public TranslationsRequestDTO get(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                      @PathVariable @Min(value = 1, message = "periodicityId must be above 0") Long periodicityId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        return this.membersConfigService.getMemberPeriodicityCommunication(channelId, periodicityId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @PutMapping(value = PERIODICITIES + "/{periodicityId}/communications")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                       @PathVariable @Min(value = 1, message = "periodicityId must be above 0") Long periodicityId,
                       @Valid @RequestBody TranslationsRequestDTO translationsRequestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.membersConfigService.updateMemberPeriodicityCommunication(channelId, periodicityId, translationsRequestDTO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS})
    @GetMapping(value = ROLES + "/{roleId}/communications")
    @ResponseStatus(HttpStatus.OK)
    public TranslationsRequestDTO getRoles(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                           @PathVariable @Min(value = 1, message = "roleId must be above 0") Long roleId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        return this.membersConfigService.getMemberRoleCommunication(channelId, roleId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @PutMapping(value = ROLES + "/{roleId}/communications")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateRoles(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                            @PathVariable @Min(value = 1, message = "roleId must be above 0") Long roleId,
                            @Valid @RequestBody TranslationsRequestDTO translationsRequestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        this.membersConfigService.updateMemberRoleCommunication(channelId, roleId, translationsRequestDTO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_ENT_MGR})
    @PutMapping(MEMBERSHIP)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setMembershipPaymentInfo(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                         @RequestBody MembershipPaymentInfoDTO membershipPaymentInfoDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        membersConfigService.setMembershipPaymentInfo(channelId, membershipPaymentInfoDTO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ANS})
    @GetMapping(value = EMISSION_REASONS)
    public EmissionReasonsDTO getEmissionReasons(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return membersConfigService.getEmissionReasons(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_ENT_ANS})
    @GetMapping(value = PAYMENT_MODES)
    public PaymentModesDTO getPaymentModes(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return membersConfigService.getPaymentModes(channelId);
    }
}
