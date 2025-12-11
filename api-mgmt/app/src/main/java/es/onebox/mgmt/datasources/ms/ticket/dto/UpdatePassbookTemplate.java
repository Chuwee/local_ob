package es.onebox.mgmt.datasources.ms.ticket.dto;

import java.io.Serializable;
import java.util.List;

public class UpdatePassbookTemplate implements Serializable {

    private String name;
    private String description;
    private List<String> languages;
    private String defaultLanguage;
    private PassbookDesign passbookDesign;
    private Boolean defaultPassbook;
    private Boolean obfuscateBarcode;
    private PassbookField header;
    private PassbookField primaryField;
    private List<PassbookField> secondaryFields;
    private List<PassbookField> auxiliaryFields;
    private List<PassbookField> backFields;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public PassbookDesign getPassbookDesign() {
        return passbookDesign;
    }

    public void setPassbookDesign(PassbookDesign passbookDesign) {
        this.passbookDesign = passbookDesign;
    }

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

    public PassbookField getHeader() {
        return header;
    }

    public void setHeader(PassbookField header) {
        this.header = header;
    }

    public PassbookField getPrimaryField() {
        return primaryField;
    }

    public void setPrimaryField(PassbookField primaryField) {
        this.primaryField = primaryField;
    }

    public List<PassbookField> getSecondaryFields() {
        return secondaryFields;
    }

    public void setSecondaryFields(List<PassbookField> secondaryFields) {
        this.secondaryFields = secondaryFields;
    }

    public List<PassbookField> getAuxiliaryFields() {
        return auxiliaryFields;
    }

    public void setAuxiliaryFields(List<PassbookField> auxiliaryFields) {
        this.auxiliaryFields = auxiliaryFields;
    }

    public List<PassbookField> getBackFields() {
        return backFields;
    }

    public void setBackFields(List<PassbookField> backFields) {
        this.backFields = backFields;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public void setDefaultLanguage(String defaultLanguage) {
        this.defaultLanguage = defaultLanguage;
    }
}
