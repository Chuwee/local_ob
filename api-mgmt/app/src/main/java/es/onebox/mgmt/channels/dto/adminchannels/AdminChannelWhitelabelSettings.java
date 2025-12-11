package es.onebox.mgmt.channels.dto.adminchannels;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.channels.enums.WhitelabelType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class AdminChannelWhitelabelSettings implements Serializable {

    @Serial
    private static final long serialVersionUID = -4890540387315930804L;

    @JsonProperty("v4_enabled")
    private Boolean v4Enabled;
    @JsonProperty("v4_config_enabled")
    private Boolean v4ConfigEnabled;
    @JsonProperty("v2_receipt_template_enabled")
    private Boolean v2ReceiptTemplateEnabled;
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

    public Boolean getV2ReceiptTemplateEnabled() {
        return v2ReceiptTemplateEnabled;
    }

    public void setV2ReceiptTemplateEnabled(Boolean v2ReceiptTemplateEnabled) {
        this.v2ReceiptTemplateEnabled = v2ReceiptTemplateEnabled;
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
