package es.onebox.mgmt.countries;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class CountriesRequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;

    @JsonProperty("system_country")
    private Boolean systemCountry;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getSystemCountry() {
        return systemCountry;
    }

    public void setSystemCountry(Boolean systemCountry) {
        this.systemCountry = systemCountry;
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
