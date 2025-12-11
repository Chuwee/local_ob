package es.onebox.mgmt.venues.dto.elementsinfo;

import org.hibernate.validator.constraints.Length;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class VenueTemplateElementRestrictionLanguageDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Length(max = 200, message = "description max size is 200")
    private String description;
    private List<VenueTemplateElementRestrictionCheckDTO> agreement;

    public VenueTemplateElementRestrictionLanguageDTO() {
    }

    public VenueTemplateElementRestrictionLanguageDTO
            (String description, List<VenueTemplateElementRestrictionCheckDTO> agreement) {
        this.description = description;
        this.agreement = agreement;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<VenueTemplateElementRestrictionCheckDTO> getAgreement() {
        return agreement;
    }

    public void setAgreement(List<VenueTemplateElementRestrictionCheckDTO> agreement) {
        this.agreement = agreement;
    }
}
