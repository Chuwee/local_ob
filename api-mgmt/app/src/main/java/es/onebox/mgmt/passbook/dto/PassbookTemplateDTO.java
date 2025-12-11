package es.onebox.mgmt.passbook.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.dto.LanguagesDTO;

import java.util.List;

public class PassbookTemplateDTO extends BasePassbookTemplateDTO {

    private PassbookFieldDTO header;
    @JsonProperty("primary_field")
    private PassbookFieldDTO primaryField;
    @JsonProperty("secondary_fields")
    private List<PassbookFieldDTO> secondaryFields;
    @JsonProperty("auxiliary_fields")
    private List<PassbookFieldDTO> auxiliaryFields;
    @JsonProperty("back_fields")
    private List<PassbookFieldDTO> backFields;
    private LanguagesDTO languages;

    public PassbookFieldDTO getHeader() {
        return header;
    }

    public void setHeader(PassbookFieldDTO header) {
        this.header = header;
    }

    public PassbookFieldDTO getPrimaryField() {
        return primaryField;
    }

    public void setPrimaryField(PassbookFieldDTO primaryField) {
        this.primaryField = primaryField;
    }

    public List<PassbookFieldDTO> getSecondaryFields() {
        return secondaryFields;
    }

    public void setSecondaryFields(List<PassbookFieldDTO> secondaryFields) {
        this.secondaryFields = secondaryFields;
    }

    public List<PassbookFieldDTO> getAuxiliaryFields() {
        return auxiliaryFields;
    }

    public void setAuxiliaryFields(List<PassbookFieldDTO> auxiliaryFields) {
        this.auxiliaryFields = auxiliaryFields;
    }

    public List<PassbookFieldDTO> getBackFields() {
        return backFields;
    }

    public void setBackFields(List<PassbookFieldDTO> backFields) {
        this.backFields = backFields;
    }

    public LanguagesDTO getLanguages() {
        return languages;
    }

    public void setLanguages(LanguagesDTO languages) {
        this.languages = languages;
    }
}
