package es.onebox.mgmt.channels.gateways.dto;

import es.onebox.mgmt.validation.annotation.LanguageIETF;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Map;

public class ChannelGatewayDetailTranslationsDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @LanguageIETF
    private Map<String, String> name;
    @LanguageIETF
    private Map<String, String> subtitle;

    public Map<String, String> getName() {
        return name;
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public Map<String, String> getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(Map<String, String> subtitle) {
        this.subtitle = subtitle;
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
