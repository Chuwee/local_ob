package es.onebox.common.datasources.ms.event.dto;

import es.onebox.common.datasources.ms.event.enums.TicketCommunicationElementTag;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class TicketCommunicationElementDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private TicketCommunicationElementTag tag;
    private String language;
    private String value;
    private String imageBinary;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TicketCommunicationElementTag getTag() {
        return tag;
    }

    public void setTag(TicketCommunicationElementTag tag) {
        this.tag = tag;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getImageBinary() {
        return imageBinary;
    }

    public void setImageBinary(String imageBinary) {
        this.imageBinary = imageBinary;
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
