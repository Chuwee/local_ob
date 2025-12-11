package es.onebox.event.catalog.dto.presales;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.sessions.dao.enums.PresaleStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.Map;

public class ChannelSessionPresaleDTO extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = 6963894766694744745L;

    private Integer numInputs;
    private PresaleStatus status;
    private Integer validatorId;
    private PresaleValidatorType validatorType;
    private Integer memberTicketsLimit;
    private Integer generalTicketsLimit;
    private PresaleValidityPeriodDTO validityPeriod;
    private PresaleConstraintsDTO constraints;
    private Map<String, String> additionalInfo;

    public Integer getNumInputs() {
        return numInputs;
    }

    public void setNumInputs(Integer numInputs) {
        this.numInputs = numInputs;
    }

    public PresaleStatus getStatus() {
        return status;
    }

    public void setStatus(PresaleStatus status) {
        this.status = status;
    }

    public Integer getValidatorId() {
        return validatorId;
    }

    public void setValidatorId(Integer validatorId) {
        this.validatorId = validatorId;
    }

    public PresaleValidatorType getValidatorType() {
        return validatorType;
    }

    public void setValidatorType(PresaleValidatorType validatorType) {
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

    public PresaleValidityPeriodDTO getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(PresaleValidityPeriodDTO validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public PresaleConstraintsDTO getConstraints() {
        return constraints;
    }

    public void setConstraints(PresaleConstraintsDTO constraints) {
        this.constraints = constraints;
    }

    public Map<String, String> getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(Map<String, String> additionalInfo) {
        this.additionalInfo = additionalInfo;
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
