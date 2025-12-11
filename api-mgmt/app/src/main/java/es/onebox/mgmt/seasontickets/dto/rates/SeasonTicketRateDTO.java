package es.onebox.mgmt.seasontickets.dto.rates;

import es.onebox.mgmt.events.dto.RateDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class SeasonTicketRateDTO extends RateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private Integer position;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SeasonTicketRateDTO rateDTO = (SeasonTicketRateDTO) o;
        return Objects.equals(getId(), rateDTO.getId()) &&
                Objects.equals(getName(), rateDTO.getName()) &&
                Objects.equals(getIsDefault(), rateDTO.getIsDefault()) &&
                Objects.equals(getRestrictiveAccess(), rateDTO.getRestrictiveAccess()) &&
                Objects.equals(getTexts(), rateDTO.getTexts()) &&
                Objects.equals(enabled, rateDTO.getEnabled());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getIsDefault(), getRestrictiveAccess(), getTexts(), getEnabled());
    }

    @Override
    public String toString() {
        return "RateDTO{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", isDefault=" + getIsDefault() +
                ", restrictiveAccess=" + getRestrictiveAccess() +
                ", enabled="+ getEnabled() +
                ", texts=" + getTexts() +
                '}';
    }
}
