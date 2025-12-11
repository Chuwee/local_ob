package es.onebox.mgmt.seasontickets.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.sessions.dto.SessionPreSaleLoyaltyProgramDTO;
import es.onebox.mgmt.sessions.dto.SessionPresalePeriodDTO;
import jakarta.validation.constraints.Min;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.util.List;

@Validated
public class UpdateSeasonTicketPresaleDTO implements Serializable {


    private static final long serialVersionUID = 1L;

    private Boolean active;
    private String name;
    @JsonProperty("presale_period")
    private SessionPresalePeriodDTO presalePeriod;
    private List<Long> channels;
    @JsonProperty("customer_types")
    private List<Long> customerTypes;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
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

    public List<Long> getChannels() {
        return channels;
    }

    public void setChannels(List<Long> channels) {
        this.channels = channels;
    }

    public List<Long> getCustomerTypes() {
        return customerTypes;
    }

    public void setCustomerTypes(List<Long> customerTypes) {
        this.customerTypes = customerTypes;
    }

    public SessionPreSaleLoyaltyProgramDTO getLoyaltyProgram() {
        return loyaltyProgram;
    }

    public void setLoyaltyProgram(SessionPreSaleLoyaltyProgramDTO loyaltyProgram) {
        this.loyaltyProgram = loyaltyProgram;
    }

    public Boolean getMemberTicketsLimitEnabled() {
        return memberTicketsLimitEnabled;
    }

    public void setMemberTicketsLimitEnabled(Boolean memberTicketsLimitEnabled) {
        this.memberTicketsLimitEnabled = memberTicketsLimitEnabled;
    }

    public Integer getMemberTicketsLimit() {
        return memberTicketsLimit;
    }

    public void setMemberTicketsLimit(Integer memberTicketsLimit) {
        this.memberTicketsLimit = memberTicketsLimit;
    }

    public Integer getGeneralTicketsLimit() {
        return generalTicketsLimit;
    }

    public void setGeneralTicketsLimit(Integer generalTicketsLimit) {
        this.generalTicketsLimit = generalTicketsLimit;
    }
}
