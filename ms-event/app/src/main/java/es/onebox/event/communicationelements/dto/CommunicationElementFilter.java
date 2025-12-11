package es.onebox.event.communicationelements.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Set;

public abstract class CommunicationElementFilter<E extends Enum<E>> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Set<E> tags;
    private Integer languageId;
    private String language;


    public Set<E> getTags() {
        return tags;
    }

    public void setTags(Set<E> tags) {
        this.tags = tags;
    }

    public Integer getLanguageId() {
        return languageId;
    }

    public void setLanguageId(Integer languageId) {
        this.languageId = languageId;
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
