package es.onebox.event.catalog.elasticsearch.dto.seasonticket;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;

public class RenewalConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 6577710990968962166L;

    private Boolean enabled;
    private Boolean automatic;
    private Boolean automaticMandatory;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private String type;
    private Long bankAccountId;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getBankAccountId() {
        return bankAccountId;
    }

    public void setBankAccountId(Long bankAccountId) {
        this.bankAccountId = bankAccountId;
    }
}
