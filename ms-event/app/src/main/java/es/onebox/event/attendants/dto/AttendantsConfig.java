package es.onebox.event.attendants.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AttendantsConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean autofill;
    private Boolean allowEditAutofill;
    private List<Long> editAutofillDisallowedSectors;

    public Boolean getAutofill() {
        return autofill;
    }

    public void setAutofill(Boolean autofill) {
        this.autofill = autofill;
    }

    public Boolean getAllowEditAutofill() {
        return allowEditAutofill;
    }

    public void setAllowEditAutofill(Boolean allowEditAutofill) {
        this.allowEditAutofill = allowEditAutofill;
    }

    public List<Long> getEditAutofillDisallowedSectors() {
        return editAutofillDisallowedSectors;
    }

    public void setEditAutofillDisallowedSectors(
        List<Long> editAutofillDisallowedSectors) {
        this.editAutofillDisallowedSectors = editAutofillDisallowedSectors;
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
