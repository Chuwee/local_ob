package es.onebox.common.datasources.accesscontrol.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class ACTicketDTO implements Serializable {

    private static final long serialVersionUID = -7865658569337198206L;

    private TicketType type;
    @JsonProperty("ticket_type")
    private TicketItemType ticketItemType;
    private String barcode;
    @JsonProperty("order_code")
    private String orderCode;
    @JsonProperty("session_id")
    private Long sessionId;
    private TicketSessionDTO session;
    @JsonProperty("related_session")
    private IdNameDTO relatedSession;
    @JsonProperty("admission_date")
    private TicketSessionDateDTO admissionDate;
    private IdNameDTO event;
    private IdNameDTO venue;
    private ACSeatDTO seat;
    private TicketValidationStatus status;
    private List<TicketValidationsDTO> validations;
    private List<TicketSaleDTO> sales;
    @JsonProperty("price_type")
    private PriceDTO price;
    private ACRateDTO rate;
    @JsonProperty("update_date")
    private ZonedDateTime updatedDate;
    private String provider;
    private Map<String, Object> attendant;

    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
    }

    public TicketItemType getTicketItemType() {
        return ticketItemType;
    }

    public void setTicketItemType(TicketItemType ticketItemType) {
        this.ticketItemType = ticketItemType;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public TicketSessionDTO getSession() {
        return session;
    }

    public void setSession(TicketSessionDTO session) {
        this.session = session;
    }

    public IdNameDTO getRelatedSession() {
        return relatedSession;
    }

    public void setRelatedSession(IdNameDTO relatedSession) {
        this.relatedSession = relatedSession;
    }

    public TicketSessionDateDTO getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(TicketSessionDateDTO admissionDate) {
        this.admissionDate = admissionDate;
    }

    public IdNameDTO getEvent() {
        return event;
    }

    public void setEvent(IdNameDTO event) {
        this.event = event;
    }

    public IdNameDTO getVenue() {
        return venue;
    }

    public void setVenue(IdNameDTO venue) {
        this.venue = venue;
    }

    public ACSeatDTO getSeat() {
        return seat;
    }

    public void setSeat(ACSeatDTO seat) {
        this.seat = seat;
    }

    public TicketValidationStatus getStatus() {
        return status;
    }

    public void setStatus(TicketValidationStatus status) {
        this.status = status;
    }

    public List<TicketValidationsDTO> getValidations() {
        return validations;
    }

    public void setValidations(List<TicketValidationsDTO> validations) {
        this.validations = validations;
    }

    public List<TicketSaleDTO> getSales() {
        return sales;
    }

    public void setSales(List<TicketSaleDTO> sales) {
        this.sales = sales;
    }

    public PriceDTO getPrice() {
        return price;
    }

    public void setPrice(PriceDTO price) {
        this.price = price;
    }

    public ACRateDTO getRate() {
        return rate;
    }

    public void setRate(ACRateDTO rate) {
        this.rate = rate;
    }

    public ZonedDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(ZonedDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public Map<String, Object> getAttendant() {
        return attendant;
    }

    public void setAttendant(Map<String, Object> attendant) {
        this.attendant = attendant;
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
