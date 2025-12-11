package es.onebox.mgmt.seasontickets.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.sessions.dto.SessionPreSaleChannelDTO;
import es.onebox.mgmt.sessions.dto.SessionPreSaleCustomerTypeDTO;
import es.onebox.mgmt.sessions.dto.SessionPreSaleLoyaltyProgramDTO;
import es.onebox.mgmt.sessions.dto.SessionPresalePeriodDTO;
import es.onebox.mgmt.sessions.enums.PresaleStatus;
import jakarta.validation.constraints.Min;

import java.io.Serializable;
import java.util.List;

public class SeasonTicketPresaleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Boolean active;
    private PresaleStatus status;
    private String name;
    @JsonProperty("presale_period")
    private SessionPresalePeriodDTO presalePeriod;
    private List<SessionPreSaleChannelDTO> channels;
    @JsonProperty("customer_types")
    private List<SessionPreSaleCustomerTypeDTO> customerTypes;
    @JsonProperty("loyalty_program")
    private SessionPreSaleLoyaltyProgramDTO loyaltyProgram;
    @JsonProperty("member_tickets_limit_enabled")
    private Boolean memberTicketsLimitEnabled;
    @Min(value = 0, message = "member tickets limit must be greater than or equal to 0")
    @JsonProperty("member_tickets_limit")
    private Integer memberTicketsLimit;
    @Min(value = 0, message = "general tickets limit must be greater than or equal to 0")
    @JsonProperty("general_tickets_limit")
    private Integer generalTicketsLimit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public PresaleStatus getStatus() {
        return status;
    }

    public void setStatus(PresaleStatus status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SessionPresalePeriodDTO getPresalePeriod() {
        return presalePeriod;
    }

    public void setPresalePeriod(SessionPresalePeriodDTO presalePeriod) {
        this.presalePeriod = presalePeriod;
    }

    public List<SessionPreSaleChannelDTO> getChannels() {
        return channels;
    }

    public void setChannels(List<SessionPreSaleChannelDTO> channels) {
        this.channels = channels;
    }

    public List<SessionPreSaleCustomerTypeDTO> getCustomerTypes() {
        return customerTypes;
    }

    public void setCustomerTypes(List<SessionPreSaleCustomerTypeDTO> customerTypes) { this.customerTypes = customerTypes; }

    public SessionPreSaleLoyaltyProgramDTO getLoyaltyProgram() {
        return loyaltyProgram;
    }

    public void setLoyaltyProgram(SessionPreSaleLoyaltyProgramDTO loyaltyProgram) { this.loyaltyProgram = loyaltyProgram; }

    public Boolean getMemberTicketsLimitEnabled() {
        return memberTicketsLimitEnabled;
    }

    public void setMemberTicketsLimitEnabled(Boolean memberTicketsLimitEnabled) {this.memberTicketsLimitEnabled = memberTicketsLimitEnabled; }

    public Integer getMemberTicketsLimit() {
        return memberTicketsLimit;
    }

    public void setMemberTicketsLimit(Integer memberTicketsLimit) {
        this.memberTicketsLimit = memberTicketsLimit;
    }

    public Integer getGeneralTicketsLimit() {return generalTicketsLimit; }

    public void setGeneralTicketsLimit(Integer generalTicketsLimit) {this.generalTicketsLimit = generalTicketsLimit; }
}
