package es.onebox.mgmt.datasources.ms.channel.dto;

import es.onebox.mgmt.channels.enums.WhitelabelType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class AdminChannel extends Channel {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean v4Enabled;
    private Boolean v4ConfigEnabled;
    private Integer idReceiptTemplate;
    private WhitelabelType whitelabelType;

    public Boolean getV4Enabled() {
        return v4Enabled;
    }

    public void setV4Enabled(Boolean v4Enabled) {
        this.v4Enabled = v4Enabled;
    }

    public Boolean getV4ConfigEnabled() {
        return v4ConfigEnabled;
    }

    public void setV4ConfigEnabled(Boolean v4ConfigEnabled) {
        this.v4ConfigEnabled = v4ConfigEnabled;
    }

    public Integer getIdReceiptTemplate() {
        return idReceiptTemplate;
    }

    public void setIdReceiptTemplate(Integer idReceiptTemplate) {
        this.idReceiptTemplate = idReceiptTemplate;
    }

    public WhitelabelType getWhitelabelType() {
        return whitelabelType;
    }

    public void setWhitelabelType(WhitelabelType whitelabelType) {
        this.whitelabelType = whitelabelType;
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
