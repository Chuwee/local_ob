package es.onebox.mgmt.datasources.ms.channel.dto;

import es.onebox.core.serializer.dto.common.CodeNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Map;

public class ChannelPurchaseConfigLinkDestination extends CodeNameDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, String> i18nUrl;

    public ChannelPurchaseConfigLinkDestination(){}

    public ChannelPurchaseConfigLinkDestination(String code, String name, Map<String, String> i18nUrl){
        super(code, name);
        this.i18nUrl = i18nUrl;
    }

    public Map<String, String> getI18nUrl() {
        return i18nUrl;
    }

    public void setI18nUrl(Map<String, String> i18nUrl) {
        this.i18nUrl = i18nUrl;
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
