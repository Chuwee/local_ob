package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.CategoriesDTO;
import es.onebox.mgmt.events.enums.EventSessionPack;
import es.onebox.mgmt.events.enums.TaxModeDTO;
import es.onebox.mgmt.events.tours.dto.TourSettingsDTO;
import es.onebox.mgmt.sessions.dto.EventSubscriptionListDTO;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateEventSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -7651850142096176382L;

    @JsonProperty("categories")
    private CategoriesDTO categories;

    @JsonProperty("sales_goal")
    private SalesGoalDTO salesGoal;

    private BookingSettingsDTO bookings;

    private LanguagesDTO languages;

    @JsonProperty("festival")
    private Boolean isFestival;

    @JsonProperty("tour")
    private TourSettingsDTO tour;

    @JsonProperty("session_pack")
    private EventSessionPack sessionPack;

    @JsonProperty("allow_venue_reports")
    private Boolean allowVenueReports;

    @JsonProperty("use_producer_fiscal_data")
    private Boolean useProducerFiscalData;

    @JsonProperty("use_tiered_pricing")
    private Boolean useTieredPricing;

    @JsonProperty("subscription_list")
    private EventSubscriptionListDTO subscriptionList;

    @JsonProperty("invitation_use_ticket_template")
    private Boolean invitationUseTicketTemplate;

    private EventSettingsGroupsDTO groups;

    @JsonProperty("attendant_tickets")
    private EventAttendantTicketsDTO attendantTickets;

    @Valid
    @JsonProperty("interactive_venue")
    private SettingsInteractiveVenueDTO interactiveVenue;

    @JsonProperty("simplified_invoice_prefix")
    private Long invoicePrefixId;

    @Valid
    @JsonProperty("accommodations")
    private EventAccommodationsConfigDTO accommodationsConfig;

    @Valid
    @JsonProperty("whitelabel_settings")
    private EventWhitelabelSettingsDTO eventWhiteLabelSettings;
    @JsonProperty("event_external_config")
    private EventExternalConfigDTO eventExternalConfig;
    @Valid
    @JsonProperty("change_seat_settings")
    private EventChangeSeatSettingsDTO eventChangeSeatSettings;
    @JsonProperty("tax_mode")
    private TaxModeDTO taxMode;
    @JsonProperty("transfer_settings")
    private EventTransferTicketDTO eventTransferTicket;

    public CategoriesDTO getCategories() {
        return categories;
    }

    public void setCategories(CategoriesDTO categories) {
        this.categories = categories;
    }

    public SalesGoalDTO getSalesGoal() {
        return salesGoal;
    }

    public void setSalesGoal(SalesGoalDTO salesGoal) {
        this.salesGoal = salesGoal;
    }

    public BookingSettingsDTO getBookings() {
        return bookings;
    }

    public void setBookings(BookingSettingsDTO bookings) {
        this.bookings = bookings;
    }

    public LanguagesDTO getLanguages() {
        return languages;
    }

    public void setLanguages(LanguagesDTO languages) {
        this.languages = languages;
    }

    public TourSettingsDTO getTour() {
        return tour;
    }

    public void setTour(TourSettingsDTO tour) {
        this.tour = tour;
    }

    public EventSessionPack getSessionPack() {
        return sessionPack;
    }

    public void setSessionPack(EventSessionPack sessionPack) {
        this.sessionPack = sessionPack;
    }

    public Boolean getAllowVenueReports() {
        return allowVenueReports;
    }

    public void setAllowVenueReports(Boolean allowVenueReports) {
        this.allowVenueReports = allowVenueReports;
    }

    public Boolean getUseProducerFiscalData() {
        return useProducerFiscalData;
    }

    public void setUseProducerFiscalData(Boolean useProducerFiscalData) {
        this.useProducerFiscalData = useProducerFiscalData;
    }

    public Boolean getUseTieredPricing() {
        return useTieredPricing;
    }

    public void setUseTieredPricing(Boolean useTieredPricing) {
        this.useTieredPricing = useTieredPricing;
    }

    public Boolean getInvitationUseTicketTemplate() {
        return invitationUseTicketTemplate;
    }

    public void setInvitationUseTicketTemplate(Boolean invitationUseTicketTemplate) {
        this.invitationUseTicketTemplate = invitationUseTicketTemplate;
    }

    public Boolean getIsFestival() {
        return isFestival;
    }

    public void setIsFestival(Boolean isFestival) {
        this.isFestival = isFestival;
    }

    public EventSubscriptionListDTO getSubscriptionList() {
        return subscriptionList;
    }

    public void setSubscriptionList(EventSubscriptionListDTO subscriptionList) {
        this.subscriptionList = subscriptionList;
    }

    public EventSettingsGroupsDTO getGroups() {
        return groups;
    }

    public void setGroups(EventSettingsGroupsDTO groups) {
        this.groups = groups;
    }

    public EventAttendantTicketsDTO getAttendantTickets() {
        return attendantTickets;
    }

    public void setAttendantTickets(EventAttendantTicketsDTO attendantTickets) {
        this.attendantTickets = attendantTickets;
    }

    public SettingsInteractiveVenueDTO getInteractiveVenue() {
        return interactiveVenue;
    }

    public void setInteractiveVenue(SettingsInteractiveVenueDTO interactiveVenue) {
        this.interactiveVenue = interactiveVenue;
    }

    public Long getInvoicePrefixId() {
        return invoicePrefixId;
    }

    public void setInvoicePrefixId(Long invoicePrefixId) {
        this.invoicePrefixId = invoicePrefixId;
    }

    public EventAccommodationsConfigDTO getAccommodationsConfig() {
        return accommodationsConfig;
    }

    public void setAccommodationsConfig(EventAccommodationsConfigDTO accommodationsConfig) {
        this.accommodationsConfig = accommodationsConfig;
    }

    public EventWhitelabelSettingsDTO getEventWhiteLabelSettings() {
        return eventWhiteLabelSettings;
    }

    public void setEventWhiteLabelSettings(EventWhitelabelSettingsDTO eventWhiteLabelSettings) {
        this.eventWhiteLabelSettings = eventWhiteLabelSettings;
    }

    public EventExternalConfigDTO getEventExternalConfig() {
        return eventExternalConfig;
    }

    public void setEventExternalConfig(EventExternalConfigDTO eventExternalConfig) {
        this.eventExternalConfig = eventExternalConfig;
    }

    public EventChangeSeatSettingsDTO getEventChangeSeatSettings() {
        return eventChangeSeatSettings;
    }

    public void setEventChangeSeatSettings(EventChangeSeatSettingsDTO eventChangeSeatSettings) {
        this.eventChangeSeatSettings = eventChangeSeatSettings;
    }

    public TaxModeDTO getTaxMode() {
        return taxMode;
    }

    public void setTaxMode(TaxModeDTO taxMode) {
        this.taxMode = taxMode;
    }

    public EventTransferTicketDTO getEventTransferTicket() {
        return eventTransferTicket;
    }

    public void setEventTransferTicket(EventTransferTicketDTO eventTransferTicket) {
        this.eventTransferTicket = eventTransferTicket;
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
