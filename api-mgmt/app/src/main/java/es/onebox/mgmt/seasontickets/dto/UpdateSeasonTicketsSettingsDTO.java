package es.onebox.mgmt.seasontickets.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.dto.BookingSettingsDTO;
import es.onebox.mgmt.common.CategoriesDTO;
import es.onebox.mgmt.events.dto.LanguagesDTO;
import es.onebox.mgmt.events.dto.SalesGoalDTO;
import es.onebox.mgmt.events.dto.SettingsInteractiveVenueDTO;
import es.onebox.mgmt.events.tours.dto.TourSettingsDTO;
import es.onebox.mgmt.sessions.dto.PresalesRedirectionPolicyDTO;
import es.onebox.mgmt.sessions.dto.SeasonTicketSubscriptionListDTO;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateSeasonTicketsSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private String zoneId;
    private LanguagesDTO languages;
    @Valid
    private UpdateSeasonTicketOperativeDTO operative;
    private BookingSettingsDTO bookings;
    @JsonProperty("subscription_list")
    private SeasonTicketSubscriptionListDTO subscriptionList;
    @JsonProperty("use_producer_fiscal_data")
    private Boolean useProducerFiscalData;
    @JsonProperty("invitation_use_ticket_template")
    private Boolean invitationUseTicketTemplate;
    @JsonProperty("sales_goal")
    private SalesGoalDTO salesGoal;
    private TourSettingsDTO tour;
    private CategoriesDTO categories;
    @Valid
    @JsonProperty("interactive_venue")
    private SettingsInteractiveVenueDTO interactiveVenue;
    @JsonProperty("presales_redirection_policy")
    private PresalesRedirectionPolicyDTO presalesRedirectionPolicy;
    @JsonProperty("simplified_invoice_prefix")
    private Long simplifiedInvoicePrefix;

    public LanguagesDTO getLanguages() {
        return languages;
    }

    public void setLanguages(LanguagesDTO languages) {
        this.languages = languages;
    }

    public UpdateSeasonTicketOperativeDTO getOperative() {
        return operative;
    }

    public void setOperative(UpdateSeasonTicketOperativeDTO operative) {
        this.operative = operative;
    }

    public BookingSettingsDTO getBookings() {
        return bookings;
    }

    public void setBookings(BookingSettingsDTO bookings) {
        this.bookings = bookings;
    }

    public SeasonTicketSubscriptionListDTO getSubscriptionList() {
        return subscriptionList;
    }

    public void setSubscriptionList(SeasonTicketSubscriptionListDTO subscriptionList) {
        this.subscriptionList = subscriptionList;
    }

    public SalesGoalDTO getSalesGoal() {
        return salesGoal;
    }

    public void setSalesGoal(SalesGoalDTO salesGoal) {
        this.salesGoal = salesGoal;
    }

    public TourSettingsDTO getTour() {
        return tour;
    }

    public void setTour(TourSettingsDTO tour) {
        this.tour = tour;
    }

    public CategoriesDTO getCategories() {
        return categories;
    }

    public void setCategories(CategoriesDTO categories) {
        this.categories = categories;
    }

    public Boolean getUseProducerFiscalData() {
        return useProducerFiscalData;
    }

    public void setUseProducerFiscalData(Boolean useProducerFiscalData) {
        this.useProducerFiscalData = useProducerFiscalData;
    }

    public Boolean getInvitationUseTicketTemplate() {
        return invitationUseTicketTemplate;
    }

    public void setInvitationUseTicketTemplate(Boolean invitationUseTicketTemplate) {
        this.invitationUseTicketTemplate = invitationUseTicketTemplate;
    }

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public SettingsInteractiveVenueDTO getInteractiveVenue() {
        return interactiveVenue;
    }

    public void setInteractiveVenue(SettingsInteractiveVenueDTO interactiveVenue) {
        this.interactiveVenue = interactiveVenue;
    }

    public PresalesRedirectionPolicyDTO getPresalesRedirectionPolicy() {return presalesRedirectionPolicy;}

    public void setPresalesRedirectionPolicy(PresalesRedirectionPolicyDTO presalesRedirectionPolicy) {
        this.presalesRedirectionPolicy = presalesRedirectionPolicy;
    }

    public Long getSimplifiedInvoicePrefix() {
        return simplifiedInvoicePrefix;
    }

    public void setSimplifiedInvoicePrefix(Long simplifiedInvoicePrefix) {
        this.simplifiedInvoicePrefix = simplifiedInvoicePrefix;
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
