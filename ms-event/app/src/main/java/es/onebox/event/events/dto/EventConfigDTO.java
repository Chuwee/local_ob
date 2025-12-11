package es.onebox.event.events.dto;

import es.onebox.event.common.enums.InteractiveVenueType;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.events.postbookingquestions.dto.PostBookingQuestionsConfigDTO;
import es.onebox.event.sessions.dto.RestrictionsDTO;
import es.onebox.event.sessions.dto.TicketTemplateSettingsDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class EventConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer eventId;
    private Boolean useSector3dView;
    private Boolean useSeat3dView;
    private Boolean use3dVenueModule;
    private Boolean use3dVenueModuleV2;
    private Boolean useVenue3dView;
    private InteractiveVenueType interactiveVenueType;
    private String venue3dId;
    private String customSelectTemplate;
    private Integer maxMembers;
    private Set<Long> upsellingPriceZones;
    private Provider inventoryProvider;
    private TicketTemplateSettingsDTO ticketTemplateSettings;
    private RestrictionsDTO restrictions;
    private AccommodationsConfigDTO accommodationsConfig;
    private EventExternalConfigDTO eventExternalConfig;
    private Boolean phoneVerificationRequired;
    private PostBookingQuestionsConfigDTO postBookingQuestionsConfig;
    private Boolean allowTransferTicket;
    private EventTransferTicketDTO transfer;

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Boolean isUseSector3dView() {
        return useSector3dView;
    }

    public void setUseSector3dView(Boolean useSector3dView) {
        this.useSector3dView = useSector3dView;
    }

    public Boolean isUseSeat3dView() {
        return useSeat3dView;
    }

    public void setUseSeat3dView(Boolean useSeat3dView) {
        this.useSeat3dView = useSeat3dView;
    }

    public Boolean isUse3dVenueModule() {
        return use3dVenueModule;
    }

    public void setUse3dVenueModule(Boolean use3dVenueModule) {
        this.use3dVenueModule = use3dVenueModule;
    }

    public Boolean isUse3dVenueModuleV2() {
        return use3dVenueModuleV2;
    }

    public void setUse3dVenueModuleV2(Boolean use3dVenueModuleV2) {
        this.use3dVenueModuleV2 = use3dVenueModuleV2;
    }

    public Boolean getUseVenue3dView() {
        return useVenue3dView;
    }

    public void setUseVenue3dView(Boolean useVenue3dView) {
        this.useVenue3dView = useVenue3dView;
    }

    public InteractiveVenueType getInteractiveVenueType() {
        return interactiveVenueType;
    }

    public void setInteractiveVenueType(InteractiveVenueType interactiveVenueType) {
        this.interactiveVenueType = interactiveVenueType;
    }

    public String getVenue3dId() {
        return venue3dId;
    }

    public void setVenue3dId(String venue3dId) {
        this.venue3dId = venue3dId;
    }

    public String getCustomSelectTemplate() {
        return customSelectTemplate;
    }

    public void setCustomSelectTemplate(String customSelectTemplate) {
        this.customSelectTemplate = customSelectTemplate;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    public Set<Long> getUpsellingPriceZones() {
        return upsellingPriceZones;
    }

    public void setUpsellingPriceZones(Set<Long> upsellingPriceZones) {
        this.upsellingPriceZones = upsellingPriceZones;
    }

    public Provider getInventoryProvider() {
        return inventoryProvider;
    }

    public void setInventoryProvider(Provider inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
    }

    public TicketTemplateSettingsDTO getTicketTemplateSettings() {
        return ticketTemplateSettings;
    }

    public void setTicketTemplateSettings(TicketTemplateSettingsDTO ticketTemplateSettings) {
        this.ticketTemplateSettings = ticketTemplateSettings;
    }

    public RestrictionsDTO getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(RestrictionsDTO restrictions) {
        this.restrictions = restrictions;
    }

    public AccommodationsConfigDTO getAccommodationsConfig() {
        return accommodationsConfig;
    }

    public void setAccommodationsConfig(AccommodationsConfigDTO accommodationsConfig) {
        this.accommodationsConfig = accommodationsConfig;
    }

    public EventExternalConfigDTO getEventExternalConfig() {
        return eventExternalConfig;
    }

    public void setEventExternalConfig(EventExternalConfigDTO eventExternalConfig) {
        this.eventExternalConfig = eventExternalConfig;
    }

    public Boolean getPhoneVerificationRequired() {
        return phoneVerificationRequired;
    }

    public void setPhoneVerificationRequired(Boolean phoneVerificationRequired) {
        this.phoneVerificationRequired = phoneVerificationRequired;
    }

    public PostBookingQuestionsConfigDTO getPostBookingQuestionsConfig() {
        return postBookingQuestionsConfig;
    }

    public void setPostBookingQuestionsConfig(PostBookingQuestionsConfigDTO postBookingQuestionsConfig) {
        this.postBookingQuestionsConfig = postBookingQuestionsConfig;
    }

    public EventTransferTicketDTO getTransfer() {
        return transfer;
    }

    public void setTransfer(EventTransferTicketDTO transfer) {
        this.transfer = transfer;
    }

    public Boolean getAllowTransferTicket() {
        return allowTransferTicket;
    }

    public void setAllowTransferTicket(Boolean allowTransferTicket) {
        this.allowTransferTicket = allowTransferTicket;
    }
}
