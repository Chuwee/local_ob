package es.onebox.fifaqatar.adapter.dto.response.ticketdetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.fifaqatar.adapter.dto.response.TicketResponse;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.List;

public class TicketDetailResponse extends TicketResponse {

    @Serial
    private static final long serialVersionUID = -6109314376061588371L;


    @JsonProperty("ticket_type") // make me enum [session_ticket, shop_ticket, game_ticket, season_pass]
    private String ticketType;
    @JsonProperty("session_id")
    private Integer sessionId;
    @JsonProperty("instructions")
    private String instructions;
    @JsonProperty("codes")
    private List<TicketCode> codes;
    @JsonProperty("ticket_price")
    private BigDecimal ticketPrice;
    @JsonProperty("discount_applied")
    private BigDecimal discountApplied;
    @JsonProperty("surcharge_applied")
    private BigDecimal surchargeApplied;
    @JsonProperty("validation_method")
    private String validationMethod; //TODO make em enum [in_app, qr_code]
    @JsonProperty("plane_name")
    private String placeName;
    @JsonProperty("place")
    private TicketPlace place;
    @JsonProperty("places")
    private List<TicketPlace> places;
    private String label;
    @JsonProperty("is_transferable")
    private Boolean transferable;
    @JsonProperty("is_transferred")
    private Boolean transferred;
    @JsonProperty("number_of_available_tickets_to_transfer")
    private Integer ticketsToTransfer;
    @JsonProperty("simplified_bill_url")
    private String simplifiedBillUrl;
    private List<TicketAttachment> attachments;
    @JsonProperty("is_exchangeable")
    private Boolean exchangeable;
    @JsonProperty("is_reschedulable")
    private Boolean reschedulable;
    @JsonProperty("add_on_items")
    private List<TicketAddonItems> addonItems;
    @JsonProperty("extra_info")
    private String extraInfo;
    @JsonProperty("seating_summary")
    private TicketSeatingSummary seatingSummary;
    @JsonProperty("release_condition")
    private TicketReleaseCondition releaseConditions;
    @JsonProperty("booking_questions")
    private TicketBookingQuestions bookingQuestions;
    @JsonProperty("web_extension")
    private TicketWebExtension webExtension;
    @JsonProperty("management")
    private TicketManagement management;
    @JsonProperty("secondary_market")
    private TicketManagement secMktManagement;


    public String getTicketType() {
        return ticketType;
    }

    public void setTicketType(String ticketType) {
        this.ticketType = ticketType;
    }

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<TicketCode> getCodes() {
        return codes;
    }

    public void setCodes(List<TicketCode> codes) {
        this.codes = codes;
    }

    public BigDecimal getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(BigDecimal ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public BigDecimal getDiscountApplied() {
        return discountApplied;
    }

    public void setDiscountApplied(BigDecimal discountApplied) {
        this.discountApplied = discountApplied;
    }

    public BigDecimal getSurchargeApplied() {
        return surchargeApplied;
    }

    public void setSurchargeApplied(BigDecimal surchargeApplied) {
        this.surchargeApplied = surchargeApplied;
    }

    public String getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(String validationMethod) {
        this.validationMethod = validationMethod;
    }

    @Override
    public String getPlaceName() {
        return placeName;
    }

    @Override
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public TicketPlace getPlace() {
        return place;
    }

    public void setPlace(TicketPlace place) {
        this.place = place;
    }

    public List<TicketPlace> getPlaces() {
        return places;
    }

    public void setPlaces(List<TicketPlace> places) {
        this.places = places;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Boolean getTransferable() {
        return transferable;
    }

    public void setTransferable(Boolean transferable) {
        this.transferable = transferable;
    }

    public Boolean getTransferred() {
        return transferred;
    }

    public void setTransferred(Boolean transferred) {
        this.transferred = transferred;
    }

    public Integer getTicketsToTransfer() {
        return ticketsToTransfer;
    }

    public void setTicketsToTransfer(Integer ticketsToTransfer) {
        this.ticketsToTransfer = ticketsToTransfer;
    }

    public String getSimplifiedBillUrl() {
        return simplifiedBillUrl;
    }

    public void setSimplifiedBillUrl(String simplifiedBillUrl) {
        this.simplifiedBillUrl = simplifiedBillUrl;
    }

    public List<TicketAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<TicketAttachment> attachments) {
        this.attachments = attachments;
    }

    public Boolean getExchangeable() {
        return exchangeable;
    }

    public void setExchangeable(Boolean exchangeable) {
        this.exchangeable = exchangeable;
    }

    public Boolean getReschedulable() {
        return reschedulable;
    }

    public void setReschedulable(Boolean reschedulable) {
        this.reschedulable = reschedulable;
    }

    public List<TicketAddonItems> getAddonItems() {
        return addonItems;
    }

    public void setAddonItems(List<TicketAddonItems> addonItems) {
        this.addonItems = addonItems;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public TicketSeatingSummary getSeatingSummary() {
        return seatingSummary;
    }

    public void setSeatingSummary(TicketSeatingSummary seatingSummary) {
        this.seatingSummary = seatingSummary;
    }

    public TicketReleaseCondition getReleaseConditions() {
        return releaseConditions;
    }

    public void setReleaseConditions(TicketReleaseCondition releaseConditions) {
        this.releaseConditions = releaseConditions;
    }

    public TicketBookingQuestions getBookingQuestions() {
        return bookingQuestions;
    }

    public void setBookingQuestions(TicketBookingQuestions bookingQuestions) {
        this.bookingQuestions = bookingQuestions;
    }

    public TicketWebExtension getWebExtension() {
        return webExtension;
    }

    public void setWebExtension(TicketWebExtension webExtension) {
        this.webExtension = webExtension;
    }

    public TicketManagement getManagement() {
        return management;
    }

    public void setManagement(TicketManagement management) {
        this.management = management;
    }

    public TicketManagement getSecMktManagement() {
        return secMktManagement;
    }

    public void setSecMktManagement(TicketManagement secMktManagement) {
        this.secMktManagement = secMktManagement;
    }
}
