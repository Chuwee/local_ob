package es.onebox.mgmt.channels;

import es.onebox.audit.core.Audit;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.webmvc.annotation.BindUsingJackson;
import es.onebox.mgmt.channels.dto.BookingSettingsDTO;
import es.onebox.mgmt.channels.dto.ChannelCancellationServicesDTO;
import es.onebox.mgmt.channels.dto.ChannelDetailDTO;
import es.onebox.mgmt.channels.dto.ChannelEventsSaleRestrictionsDTO;
import es.onebox.mgmt.channels.dto.ChannelVouchersDTO;
import es.onebox.mgmt.channels.dto.ChannelWhitelabelSettingsDTO;
import es.onebox.mgmt.channels.dto.ChannelsFilter;
import es.onebox.mgmt.channels.dto.ChannelsResponseDTO;
import es.onebox.mgmt.channels.dto.MemberDatesFilterDTO;
import es.onebox.mgmt.channels.dto.UpdateChannelRequestDTO;
import es.onebox.mgmt.channels.dto.UpdateChannelVouchersRequestDTO;
import es.onebox.mgmt.channels.dto.UpdateDateFilterDTO;
import es.onebox.mgmt.channels.dto.UpdateMemberConfigChargesDTO;
import es.onebox.mgmt.channels.members.service.MembersConfigService;
import es.onebox.mgmt.common.auth.dto.AuthConfigDTO;
import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.config.AuditTag;
import es.onebox.mgmt.datasources.ms.entity.enums.MemberPeriodType;
import es.onebox.mgmt.events.dto.CreateChannelDTO;
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

import static es.onebox.core.security.Roles.Codes.ROLE_CNL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_COL_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ADMIN;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_ENT_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_SYS_MGR;
import static es.onebox.mgmt.config.AuditTag.AUDIT_SERVICE;

@Validated
@RestController
@RequestMapping(value = ChannelsController.BASE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
public class ChannelsController {

    public static final String BASE_URI = ApiConfig.BASE_URL + "/channels";

    private static final String AUDIT_COLLECTION = "CHANNELS";
    private static final String AUDIT_BOOKING_SETTINGS = "BOOKING_SETTINGS";
    private static final String AUDIT_CHANNEL_CANCELLATION_SERVICES = "CHANNEL_CANCELLATION_SERVICES";
    private static final String AUDIT_CHANNEL_VOUCHERS = "CHANNEL_VOUCHERS";
    private static final String AUDIT_CHANNEL_EVENT_SALE_RESTRICTIONS = "AUDIT_CHANNEL_EVENT_SALE_RESTRICTIONS";
    private static final String AUDIT_CHANNEL_EVENT_WHITELABEL_SETTINGS = "AUDIT_CHANNEL_EVENT_WHITELABEL_SETTINGS";
    private static final String AUDIT_SUBCOLLECTION_CUSTOMERS_AUTH = "CUSTOMERS_AUTH_CONFIG";

    private final ChannelsService service;
    private final MembersConfigService membersConfigService;

    @Autowired
    public ChannelsController(ChannelsService service,
                              MembersConfigService membersConfigService) {
        this.service = service;
        this.membersConfigService = membersConfigService;
    }

    @Secured({ROLE_SYS_ANS, ROLE_SYS_MGR, ROLE_CNL_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping()
    public ChannelsResponseDTO search(@BindUsingJackson @Valid ChannelsFilter filter) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_SEARCH);
        return service.getChannels(filter);
    }
    
    @Secured({ROLE_SYS_ANS, ROLE_SYS_MGR, ROLE_CNL_MGR, ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/{channelId}")
    public ChannelDetailDTO get(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_GET);
        return service.getChannel(channelId);
    }

    @Secured({ROLE_OPR_MGR})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public IdDTO create(@Valid @RequestBody CreateChannelDTO createChannel) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_CREATE);
        return service.createChannel(createChannel);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @DeleteMapping("/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_DELETE);
        service.deleteChannel(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping("/{channelId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @Valid @RequestBody UpdateChannelRequestDTO updateChannelRequestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateChannel(channelId, updateChannelRequestDTO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{channelId}/booking-settings")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBookingsSettings(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
            @RequestBody BookingSettingsDTO bookingSettings) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_BOOKING_SETTINGS, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateBookingsSettings(channelId, bookingSettings);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @GetMapping(value = "/{channelId}/booking-settings")
    public BookingSettingsDTO getBookingsSettings(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_BOOKING_SETTINGS, AuditTag.AUDIT_ACTION_GET);
        return service.getBookingsSettings(channelId);
    }

    @Secured({ROLE_OPR_MGR})
    @PutMapping(value = "/{channelId}/cancellation-services")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateCancellationServices(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                       @RequestBody ChannelCancellationServicesDTO cancellationServices) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_CHANNEL_CANCELLATION_SERVICES, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateCancellationServices(channelId, cancellationServices);
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/{channelId}/cancellation-services")
    public ChannelCancellationServicesDTO getCancellationServices(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_CHANNEL_CANCELLATION_SERVICES, AuditTag.AUDIT_ACTION_GET);
        return service.getCancellationServices(channelId);
    }

    @Secured({ROLE_OPR_MGR, ROLE_OPR_ANS, ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS})
    @GetMapping(value = "/{channelId}/vouchers")
    public ChannelVouchersDTO getChannelVouchersConfig(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_CHANNEL_VOUCHERS, AuditTag.AUDIT_ACTION_GET);
        return service.getChannelVouchersConfig(channelId);
    }

    @Secured({ROLE_OPR_MGR, ROLE_CNL_MGR, ROLE_ENT_MGR})
    @PutMapping(value = "/{channelId}/vouchers")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelVouchersConfig(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                       @Valid @RequestBody UpdateChannelVouchersRequestDTO updateChannelRequestDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_CHANNEL_VOUCHERS, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateChannelVouchersConfig(channelId, updateChannelRequestDTO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{channelId}/member-config-charges")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateMemberConfigCharges(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                          @Valid @RequestBody UpdateMemberConfigChargesDTO updateMemberConfigChargesDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        membersConfigService.updateMemberConfigCharges(channelId, updateMemberConfigChargesDTO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/{channelId}/event-sale-restrictions")
    public ChannelEventsSaleRestrictionsDTO getEventSaleRestrictions(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_CHANNEL_EVENT_SALE_RESTRICTIONS, AuditTag.AUDIT_ACTION_GET);
        return service.getEventSaleRestrictions(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_ENT_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{channelId}/event-sale-restrictions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateEventSaleRestrictions(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                            @RequestBody @Valid ChannelEventsSaleRestrictionsDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_CHANNEL_EVENT_SALE_RESTRICTIONS, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateEventSaleRestrictions(channelId, request);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping(value = "/{channelId}/whitelabel-settings")
    public ChannelWhitelabelSettingsDTO getChannelWhitelabelSettings(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_CHANNEL_EVENT_WHITELABEL_SETTINGS, AuditTag.AUDIT_ACTION_GET);
        return service.getChannelWhitelabelSettings(channelId);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{channelId}/whitelabel-settings")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateChannelWhitelabelSettings(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                            @RequestBody @Valid ChannelWhitelabelSettingsDTO request) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_CHANNEL_EVENT_WHITELABEL_SETTINGS, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateChannelWhitelabelSettings(channelId, request);
    }

    @Secured({ROLE_OPR_MGR, ROLE_ENT_ADMIN, ROLE_CNL_MGR, ROLE_COL_MGR})
    @GetMapping("/{channelId}/auth-config")
    public AuthConfigDTO getAuthConfig(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId
    ) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_CUSTOMERS_AUTH, AuditTag.AUDIT_ACTION_GET);
        return service.getAuthConfig(channelId);
    }

    @Secured({ROLE_OPR_MGR, ROLE_ENT_ADMIN, ROLE_CNL_MGR, ROLE_COL_MGR})
    @PutMapping("/{channelId}/auth-config")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateAuthConfig(
            @PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId, @RequestBody AuthConfigDTO authConfigDTO
    ) {
        Audit.addTags(AuditTag.AUDIT_SERVICE, AUDIT_COLLECTION, AUDIT_SUBCOLLECTION_CUSTOMERS_AUTH, AuditTag.AUDIT_ACTION_UPDATE);
        service.updateAuthConfig(channelId, authConfigDTO);
    }


    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @PutMapping(value = "/{channelId}/member-config/dates-filter/{type}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateDatesFilter(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                  @PathVariable MemberPeriodType type,
                                  @Valid @RequestBody UpdateDateFilterDTO dateFilterDTO) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        membersConfigService.updateDatesFilter(channelId, type, dateFilterDTO);
    }

    @Secured({ROLE_CNL_MGR, ROLE_OPR_MGR})
    @GetMapping(value = "/{channelId}/member-config/dates-filter/{type}")
    public MemberDatesFilterDTO getDatesFilter(@PathVariable @Min(value = 1, message = "channelId must be above 0") Long channelId,
                                               @PathVariable MemberPeriodType type) {
        Audit.addTags(AUDIT_SERVICE, AUDIT_COLLECTION, AuditTag.AUDIT_ACTION_UPDATE);
        return membersConfigService.getDatesFilter(channelId, type);
    }
}