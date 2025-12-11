package es.onebox.mgmt.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class MasterdataCountryValue extends MasterdataValue implements Serializable {
    
    @Serial private static final long serialVersionUID = 1L;

    private String internationalPhonePrefix;

    public String getInternationalPhonePrefix() {
        return internationalPhonePrefix;
    }
    public void setInternationalPhonePrefix(String internationalPhonePrefix) {
        this.internationalPhonePrefix = internationalPhonePrefix;
    }

    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o, new String[0]);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this, new String[0]);
    }
}
