package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.CommunicationPaymentBenefitElementType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class PaymentBenefitCommunicationElementDTO extends IdDTO {

    private CommunicationPaymentBenefitElementType type;
    private String language;
    private String backgroundColor;
    private String textColor;
    private String value;
    private Boolean visible;

    public CommunicationPaymentBenefitElementType getType() {
        return type;
    }

    public void setType(CommunicationPaymentBenefitElementType type) {
        this.type = type;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
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
