package es.onebox.mgmt.channels.forms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.agreements.AgreementDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ChannelAgreementDTO extends AgreementDTO {

    private static final long serialVersionUID = 1L;

    private Integer position;
    @JsonProperty("external_key")
    private String externalKey;


    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getExternalKey() {
        return externalKey;
    }

    public void setExternalKey(String externalKey) {
        this.externalKey = externalKey;
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
