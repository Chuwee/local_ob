package es.onebox.mgmt.events.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.utils.dto.DateConvertible;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.time.ZoneId;
import java.util.Map;

public class EventDTO extends BaseEventDTO implements DateConvertible {

    @Serial
    private static final long serialVersionUID = -6959897765333279098L;

    private EventSettingsDTO settings;

    @JsonIgnore
    private String operatorTZ;

    @JsonProperty("additional_config")
    private AdditionalConfigDTO additionalConfig;

    @JsonProperty("has_sales")
    private Boolean hasSales;

    @JsonProperty("has_sales_request")
    private Boolean hasSalesRequest;

    @JsonProperty("external_data")
    private Map<String, Object> externalData;

    public EventSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(EventSettingsDTO settings) {
        this.settings = settings;
    }

    public String getOperatorTZ() {
        return operatorTZ;
    }

    public void setOperatorTZ(String operatorTZ) {
        this.operatorTZ = operatorTZ;
    }

    public AdditionalConfigDTO getAdditionalConfig() {
        return additionalConfig;
    }

    public void setAdditionalConfig(AdditionalConfigDTO additionalConfig) {
        this.additionalConfig = additionalConfig;
    }

    public Boolean getHasSales() {
        return hasSales;
    }

    public void setHasSales(Boolean hasSales) {
        this.hasSales = hasSales;
    }

    public Boolean getHasSalesRequest() {
        return hasSalesRequest;
    }

    public void setHasSalesRequest(Boolean hasSalesRequest) {
        this.hasSalesRequest = hasSalesRequest;
    }

    public Map<String, Object> getExternalData() {
        return externalData;
    }

    public void setExternalData(Map<String, Object> externalData) {
        this.externalData = externalData;
    }

    @Override
    public void convertDates() {
        super.convertDates();
        if (operatorTZ != null && settings != null && settings.getBookings() != null &&
                settings.getBookings().getBookingExpiration() != null && settings.getBookings().getBookingExpiration().getDate() != null) {
            this.settings.getBookings().getBookingExpiration().setDate(
                    settings.getBookings().getBookingExpiration().getDate().withZoneSameInstant(ZoneId.of(operatorTZ)));
        }
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
