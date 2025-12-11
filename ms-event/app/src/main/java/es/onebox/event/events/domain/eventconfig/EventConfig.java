package es.onebox.event.events.domain.eventconfig;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;
import es.onebox.event.common.domain.Restrictions;
import es.onebox.event.common.enums.InteractiveVenueType;
import es.onebox.event.events.enums.Provider;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

@CouchDocument
public class EventConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer eventId;
    private Restrictions restrictions;
    private String venue3dId;
    private Integer maxMembers;
    private Integer passbookId;
    private InteractiveVenueType interactiveVenueType;
    private boolean useSector3dView;
    private boolean useSeat3dView;
    private boolean useVenue3dView;
    private boolean use3dVenueModule;
    private boolean use3dVenueModuleV2;
    private String customSelectTemplate;
    private String passbookTemplateCode;
    private EventPassbookConfig eventPassbookConfig;
    private AccommodationsConfig accommodationsConfig;
    private Provider inventoryProvider;
    private TicketTemplateSettings ticketTemplateSettings;
    // DELETE ME: https://oneboxtds.atlassian.net/browse/OB-33685
    private Set<Long> upsellingPriceZones;
    private EventWhitelabelSettings whitelabelSettings;
    private EventExternalConfig eventExternalConfig;
    private EventChangeSeatConfig eventChangeSeatConfig;
    private EventTransferTicketConfig eventTransferTicketConfig;
    private Boolean phoneVerificationRequired;
    private Boolean attendantVerificationRequired;
    private PostBookingQuestionsConfig postBookingQuestionsConfig;

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public Restrictions getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(Restrictions restrictions) {
        this.restrictions = restrictions;
    }

    public String getVenue3dId() {
        return venue3dId;
    }

    public void setVenue3dId(String venue3dId) {
        this.venue3dId = venue3dId;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    public Integer getPassbookId() {
        return passbookId;
    }

    public void setPassbookId(Integer passbookId) {
        this.passbookId = passbookId;
    }

    public boolean isUseSector3dView() {
        return useSector3dView;
    }

    public void setUseSector3dView(boolean useSector3dView) {
        this.useSector3dView = useSector3dView;
    }

    public boolean isUseSeat3dView() {
        return useSeat3dView;
    }

    public void setUseSeat3dView(boolean useSeat3dView) {
        this.useSeat3dView = useSeat3dView;
    }

    public boolean isUseVenue3dView() {
        return useVenue3dView;
    }

    public void setUseVenue3dView(boolean useVenue3dView) {
        this.useVenue3dView = useVenue3dView;
    }

    public boolean isUse3dVenueModule() {
        return use3dVenueModule;
    }

    public void setUse3dVenueModule(boolean use3dVenueModule) {
        this.use3dVenueModule = use3dVenueModule;
    }

    public boolean isUse3dVenueModuleV2() {
        return use3dVenueModuleV2;
    }

    public void setUse3dVenueModuleV2(boolean use3dVenueModuleV2) {
        this.use3dVenueModuleV2 = use3dVenueModuleV2;
    }

    public InteractiveVenueType getInteractiveVenueType() {
        return interactiveVenueType;
    }

    public void setInteractiveVenueType(InteractiveVenueType interactiveVenueType) {
        this.interactiveVenueType = interactiveVenueType;
    }

    public String getCustomSelectTemplate() {
        return customSelectTemplate;
    }

    public void setCustomSelectTemplate(String customSelectTemplate) {
        this.customSelectTemplate = customSelectTemplate;
    }

    public String getPassbookTemplateCode() {
        return passbookTemplateCode;
    }

    public void setPassbookTemplateCode(String passbookTemplateCode) {
        this.passbookTemplateCode = passbookTemplateCode;
    }

    public EventPassbookConfig getEventPassbookConfig() {
        return eventPassbookConfig;
    }

    public void setEventPassbookConfig(EventPassbookConfig eventPassbookConfig) {
        this.eventPassbookConfig = eventPassbookConfig;
    }

    public AccommodationsConfig getAccommodationsConfig() {
        return accommodationsConfig;
    }

    public void setAccommodationsConfig(AccommodationsConfig accommodationsConfig) {
        this.accommodationsConfig = accommodationsConfig;
    }
    
    public Provider getInventoryProvider() {
        return inventoryProvider;
    }
    
    public void setInventoryProvider(Provider inventoryProvider) {
        this.inventoryProvider = inventoryProvider;
    }

    public TicketTemplateSettings getTicketTemplateSettings() {
        return ticketTemplateSettings;
    }

    public void setTicketTemplateSettings(TicketTemplateSettings ticketTemplateSettings) {
        this.ticketTemplateSettings = ticketTemplateSettings;
    }

    public Set<Long> getUpsellingPriceZones() {
        return upsellingPriceZones;
    }
    
    public void setUpsellingPriceZones(Set<Long> upsellingPriceZones) {
        this.upsellingPriceZones = upsellingPriceZones;
    }

    public EventWhitelabelSettings getWhitelabelSettings() {
        return whitelabelSettings;
    }

    public void setWhitelabelSettings(EventWhitelabelSettings whitelabelSettings) {
        this.whitelabelSettings = whitelabelSettings;
    }

    public EventExternalConfig getEventExternalConfig() {
        return eventExternalConfig;
    }

    public void setEventExternalConfig(EventExternalConfig eventExternalConfig) {
        this.eventExternalConfig = eventExternalConfig;
    }

    public EventChangeSeatConfig getEventChangeSeatConfig() {
        return eventChangeSeatConfig;
    }

    public void setEventChangeSeatConfig(EventChangeSeatConfig eventChangeSeatConfig) {
        this.eventChangeSeatConfig = eventChangeSeatConfig;
    }

    public EventTransferTicketConfig getEventTransferTicketConfig() {
        return eventTransferTicketConfig;
    }

    public void setEventTransferTicketConfig(EventTransferTicketConfig eventTransferTicketConfig) {
        this.eventTransferTicketConfig = eventTransferTicketConfig;
    }

    public Boolean getPhoneVerificationRequired() {
        return phoneVerificationRequired;
    }

    public void setPhoneVerificationRequired(Boolean phoneVerificationRequired) {
        this.phoneVerificationRequired = phoneVerificationRequired;
    }

    public Boolean getAttendantVerificationRequired() {
        return attendantVerificationRequired;
    }

    public void setAttendantVerificationRequired(Boolean attendantVerificationRequired) {
        this.attendantVerificationRequired = attendantVerificationRequired;
    }

    public PostBookingQuestionsConfig getPostBookingQuestionsConfig() {
        return postBookingQuestionsConfig;
    }

    public void setPostBookingQuestionsConfig(PostBookingQuestionsConfig postBookingQuestionsConfig) {
        this.postBookingQuestionsConfig = postBookingQuestionsConfig;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
