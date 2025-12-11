package es.onebox.mgmt.entities.externalconfiguration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.DigitalTicketMode;
import es.onebox.mgmt.entities.externalconfiguration.enums.PartnerValidationType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ClubConfigOperativeDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 916023094093476869L;

    @JsonProperty("partner_validation_type")
    private PartnerValidationType partnerValidationType;

    @JsonProperty("payment_method")
    private Integer paymentMethod;

    @JsonProperty("generate_partner_ticket")
    private Boolean generatePartnerTicket;

    @JsonProperty("scheduled")
    private Boolean scheduled;

    @JsonProperty("fixed_delay_ms")
    private Integer fixedDelayMs;

    @JsonProperty("check_partner_grant")
    private Boolean checkPartnerGrant;

    @JsonProperty("partner_grant_capacities")
    private List<Integer> partnerGrantCapacities;

    @JsonProperty("check_partner_pin_regexp")
    private Boolean checkPartnerPinRegexp;

    @JsonProperty("partner_pin_regexp")
    private String partnerPinRegexp;
    @JsonProperty("send_id_number")
    private Boolean sendIdNumber;
    @JsonProperty("id_number_max_length")
    @Min(value = 5, message = "id_number_max_length must be above 5")
    @Max(value = 30, message = "id_number_max_length must be below 30")
    private Integer idNumberMaxLength;

    @JsonProperty(value = "digital_ticket_mode")
    private DigitalTicketMode digitalTicketMode;

    public PartnerValidationType getPartnerValidationType() {
        return partnerValidationType;
    }

    public void setPartnerValidationType(PartnerValidationType partnerValidationType) {
        this.partnerValidationType = partnerValidationType;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Boolean getGeneratePartnerTicket() {
        return generatePartnerTicket;
    }

    public void setGeneratePartnerTicket(Boolean generatePartnerTicket) {
        this.generatePartnerTicket = generatePartnerTicket;
    }

    public Boolean getScheduled() {
        return scheduled;
    }

    public void setScheduled(Boolean scheduled) {
        this.scheduled = scheduled;
    }

    public Integer getFixedDelayMs() {
        return fixedDelayMs;
    }

    public void setFixedDelayMs(Integer fixedDelayMs) {
        this.fixedDelayMs = fixedDelayMs;
    }

    public Boolean getCheckPartnerGrant() {
        return checkPartnerGrant;
    }

    public void setCheckPartnerGrant(Boolean checkPartnerGrant) {
        this.checkPartnerGrant = checkPartnerGrant;
    }

    public List<Integer> getPartnerGrantCapacities() {
        return partnerGrantCapacities;
    }

    public void setPartnerGrantCapacities(List<Integer> partnerGrantCapacities) {
        this.partnerGrantCapacities = partnerGrantCapacities;
    }

    public Boolean getCheckPartnerPinRegexp() {
        return checkPartnerPinRegexp;
    }

    public void setCheckPartnerPinRegexp(Boolean checkPartnerPinRegexp) {
        this.checkPartnerPinRegexp = checkPartnerPinRegexp;
    }

    public String getPartnerPinRegexp() {
        return partnerPinRegexp;
    }

    public void setPartnerPinRegexp(String partnerPinRegexp) {
        this.partnerPinRegexp = partnerPinRegexp;
    }

    public Boolean getSendIdNumber() {
        return sendIdNumber;
    }

    public void setSendIdNumber(Boolean sendIdNumber) {
        this.sendIdNumber = sendIdNumber;
    }

    public Integer getIdNumberMaxLength() {
        return idNumberMaxLength;
    }

    public void setIdNumberMaxLength(Integer idNumberMaxLength) {
        this.idNumberMaxLength = idNumberMaxLength;
    }

    public DigitalTicketMode getDigitalTicketMode() {
        return digitalTicketMode;
    }

    public void setDigitalTicketMode(DigitalTicketMode digitalTicketMode) {
        this.digitalTicketMode = digitalTicketMode;
    }
}
