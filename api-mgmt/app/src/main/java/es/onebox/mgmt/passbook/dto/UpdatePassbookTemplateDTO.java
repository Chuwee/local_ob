package es.onebox.mgmt.passbook.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.events.dto.LanguagesDTO;

import java.io.Serializable;
import java.util.List;

public class UpdatePassbookTemplateDTO implements Serializable {

    private String name;
    private String description;
    private LanguagesDTO languages;
    @JsonProperty("passbook_design")
    private PassbookDesignDTO passbookDesign;
    @JsonProperty("default_passbook")
    private Boolean defaultPassbook;
    @JsonProperty("obfuscated_barcode")
    private Boolean obfuscateBarcode;
    private PassbookFieldDTO header;
    @JsonProperty("primary_field")
    private PassbookFieldDTO primaryField;
    @JsonProperty("secondary_fields")
    private List<PassbookFieldDTO> secondaryFields;
    @JsonProperty("auxiliary_fields")
    private List<PassbookFieldDTO> auxiliaryFields;
    @JsonProperty("back_fields")
    private List<PassbookFieldDTO> backFields;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isDefaultPassbook() {
        return defaultPassbook;
    }

    public void setDefaultPassbook(Boolean defaultPassbook) {
        this.defaultPassbook = defaultPassbook;
    }

    public Boolean isObfuscateBarcode() {
        return obfuscateBarcode;
    }

    public void setObfuscateBarcode(Boolean obfuscateBarcode) {
        this.obfuscateBarcode = obfuscateBarcode;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PassbookDesignDTO getPassbookDesign() {
        return passbookDesign;
    }

    public void setPassbookDesign(PassbookDesignDTO passbookDesign) {
        this.passbookDesign = passbookDesign;
    }

    public LanguagesDTO getLanguages() {
        return languages;
    }

    public void setLanguages(LanguagesDTO languages) {
        this.languages = languages;
    }
}
