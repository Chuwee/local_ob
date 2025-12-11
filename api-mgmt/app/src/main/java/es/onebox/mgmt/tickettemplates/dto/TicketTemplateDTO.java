package es.onebox.mgmt.tickettemplates.dto;

import es.onebox.mgmt.events.dto.LanguagesDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class TicketTemplateDTO extends BaseTicketTemplateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private LanguagesDTO languages;

    public LanguagesDTO getLanguages() {
        return languages;
    }

    public void setLanguages(LanguagesDTO languages) {
        this.languages = languages;
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
