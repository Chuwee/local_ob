package es.onebox.mgmt.seasontickets.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.utils.dto.DateConvertible;
import es.onebox.mgmt.seasontickets.enums.RenewalType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

public class SeasonTicketRenewalDTO implements Serializable, DateConvertible {

    public static final String UTC = "UTC";

    @Serial
    private static final long serialVersionUID = 3066552941959104040L;

    @JsonIgnore
    private String zoneId;
    private Boolean enable;
    @JsonProperty("start_date")
    private ZonedDateTime startDate;
    @JsonProperty("end_date")
    private ZonedDateTime endDate;
    @JsonProperty("in_process")
    private Boolean inProcess;
    private Boolean automatic;
    @JsonProperty("automatic_mandatory")
    private Boolean automaticMandatory;
    @JsonProperty("renewal_type")
    private RenewalType renewalType;
    @JsonProperty("renewal_type_config")
    Map<String, Object> renewalTypeConfig;
    @JsonProperty("bank_account_id")
    private Long bankAccountId;
    @JsonProperty("group_by_reference")
    private Boolean groupByReference;

    public String getZoneId() {
        return zoneId;
    }

    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public Boolean getInProcess() {
        return inProcess;
    }

    public void setInProcess(Boolean inProcess) {
        this.inProcess = inProcess;
    }

    public Boolean getAutomatic() {
        return automatic;
    }

    public void setAutomatic(Boolean automatic) {
        this.automatic = automatic;
    }

    public Boolean getAutomaticMandatory() {
        return automaticMandatory;
    }

    public void setAutomaticMandatory(Boolean automaticMandatory) {
        this.automaticMandatory = automaticMandatory;
    }

    public RenewalType getRenewalType() {
        return renewalType;
    }

    public void setRenewalType(RenewalType renewalType) {
        this.renewalType = renewalType;
    }

    public Map<String, Object> getRenewalTypeConfig() {
        return renewalTypeConfig;
    }

    public void setRenewalTypeConfig(Map<String, Object> renewalTypeConfig) {
        this.renewalTypeConfig = renewalTypeConfig;
    }

    public Long getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }

    public Boolean getGroupByReference() {
        return groupByReference;
    }

    public void setGroupByReference(Boolean groupByReference) {
        this.groupByReference = groupByReference;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public void convertDates() {
        if (StringUtils.isEmpty(zoneId)) {
            zoneId = UTC;
        }
        if (startDate != null) {
            startDate = startDate.withZoneSameInstant(ZoneId.of(zoneId));
        }
        if (endDate != null) {
            endDate = endDate.withZoneSameInstant(ZoneId.of(zoneId));
        }
    }
}