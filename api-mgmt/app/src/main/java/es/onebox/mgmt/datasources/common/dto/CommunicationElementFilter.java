package es.onebox.mgmt.datasources.common.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Set;

public class CommunicationElementFilter<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Set<T> tags;
    private Integer languageId;
    private String language;

    public Set<T> getTags() {
        return tags;
    }

    public void setTags(Set<T> tags) {
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
