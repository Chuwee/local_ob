package es.onebox.mgmt.channels.contents.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class ChannelAuditedTextBlockDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("creation_date")
    private ZonedDateTime changedDate;
    private IdNameDTO author;
    private String subject;
    private String value;
    private String language;

    public ZonedDateTime getChangedDate() {
        return changedDate;
    }

    public void setChangedDate(ZonedDateTime changedDate) {
        this.changedDate = changedDate;
    }

    public IdNameDTO getAuthor() {
        return author;
    }

    public void setAuthor(IdNameDTO author) {
        this.author = author;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
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
