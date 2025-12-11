package es.onebox.event.catalog.elasticsearch.dto.channelevent;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import es.onebox.event.catalog.elasticsearch.enums.CommElementType;

import java.io.Serializable;

public class ChannelEventCommunicationElement implements Serializable {

    private static final long serialVersionUID = 1L;

    private CommElementType type;
    private String languageCode;
    private String value;
    private String linkUrl;
    private Integer languageId;
    private Integer itemId;
    private Integer position;
    private String altText;

    public CommElementType getType() {
        return type;
    }

    public void setType(CommElementType type) {
        this.type = type;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getPosition() {return position;}

    public void setPosition(Integer position) {this.position = position;}

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
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
