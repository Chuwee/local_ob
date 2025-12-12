package es.onebox.common.datasources.webhook.dto.fever;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeverAddressDTO {
    @JsonProperty("country_code")
    private String countryCode;
    @JsonProperty("city_criteria_id")
    private Integer cityCriteriaId;
    private String address;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Integer getCityCriteriaId() {
        return cityCriteriaId;
    }

    public void setCityCriteriaId(Integer cityCriteriaId) {
        this.cityCriteriaId = cityCriteriaId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
