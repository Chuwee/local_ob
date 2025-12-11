package es.onebox.mgmt.datasources.ms.channel.dto.contents;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class ChannelAuditedTextBlock implements Serializable {

    private static final long serialVersionUID = 1L;

    private ZonedDateTime changedDate;
    private Long userId;
    private String subject;
    private String value;
    private Boolean useFreeText;
    private String language;


    public ZonedDateTime getChangedDate() {
        return changedDate;
    }

    public void setChangedDate(ZonedDateTime changedDate) {
        this.changedDate = changedDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Boolean getUseFreeText() {
        return useFreeText;
    }

    public void setUseFreeText(Boolean useFreeText) {
        this.useFreeText = useFreeText;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
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
