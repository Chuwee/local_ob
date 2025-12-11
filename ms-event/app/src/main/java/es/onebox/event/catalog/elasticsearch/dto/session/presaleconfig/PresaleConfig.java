package es.onebox.event.catalog.elasticsearch.dto.session.presaleconfig;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PresaleConfig  implements Serializable {

    @Serial
    private static final long serialVersionUID = 6945315084572541194L;

    private Integer id;
    private String name;
    private Integer status;
    private Integer numInputs;
    private Integer validatorId;
    private Integer validatorType;
    private Integer memberTicketsLimit;
    private Integer generalTicketsLimit;
    private PresaleValidityPeriod validityPeriod;
    private List<Long> channelIds;
    private List<Long> customerTypes;
    private PresaleLoyaltyProgram loyaltyProgram;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getNumInputs() {
        return numInputs;
    }

    public void setNumInputs(Integer numInputs) {
        this.numInputs = numInputs;
    }

    public Integer getValidatorId() {
        return validatorId;
    }

    public void setValidatorId(Integer validatorId) {
        this.validatorId = validatorId;
    }

    public Integer getValidatorType() {
        return validatorType;
    }

    public void setValidatorType(Integer validatorType) {
        this.validatorType = validatorType;
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

    public PresaleValidityPeriod getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(PresaleValidityPeriod validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public List<Long> getCustomerTypes() {
        return customerTypes;
    }

    public void setCustomerTypes(List<Long> customerTypes) {
        this.customerTypes = customerTypes;
    }

    public PresaleLoyaltyProgram getLoyaltyProgram() {
        return loyaltyProgram;
    }

    public void setLoyaltyProgram(PresaleLoyaltyProgram loyaltyProgram) {
        this.loyaltyProgram = loyaltyProgram;
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
