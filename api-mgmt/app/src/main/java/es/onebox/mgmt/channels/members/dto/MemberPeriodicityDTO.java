package es.onebox.mgmt.channels.members.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.dto.TranslationsDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class MemberPeriodicityDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4626670635370261621L;

    @JsonProperty("id")
    private Long periodicityId;

    @JsonProperty("name")
    private String periodicity;

    private Map<String, TranslationsDTO> translations;

    public MemberPeriodicityDTO() {
    }

    public MemberPeriodicityDTO(Long periodicityId, String periodicity) {
        this.periodicityId = periodicityId;
        this.periodicity = periodicity;
    }

    public Long getPeriodicityId() {
        return periodicityId;
    }

    public void setPeriodicityId(Long periodicityId) {
        this.periodicityId = periodicityId;
    }

    public String getPeriodicity() {
        return periodicity;
    }

    public void setPeriodicity(String periodicity) {
        this.periodicity = periodicity;
    }

    public Map<String, TranslationsDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, TranslationsDTO> translations) {
        this.translations = translations;
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
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
