package es.onebox.mgmt.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class Periodicity implements Serializable {

    @Serial
    private static final long serialVersionUID = -6891752431732744677L;
    private Long id;
    private Map<String, Translations> translations;

    public Periodicity() {
    }

    public Periodicity(Long id, Map<String, Translations> translations) {
        this.id = id;
        this.translations = translations;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, Translations> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, Translations> translations) {
        this.translations = translations;
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
