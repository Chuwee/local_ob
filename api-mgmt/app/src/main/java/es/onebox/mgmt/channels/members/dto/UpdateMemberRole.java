package es.onebox.mgmt.channels.members.dto;

import es.onebox.mgmt.channels.dto.TranslationsDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

public class UpdateMemberRole implements Serializable {

    @Serial
    private static final long serialVersionUID = 1062146952819989865L;
    private Long id;

    private Map<String, TranslationsDTO> translations;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Map<String, TranslationsDTO> getTranslations() {
        return translations;
    }

    public void setTranslations(Map<String, TranslationsDTO> translations) {
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
