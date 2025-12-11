package es.onebox.event.datasources.ms.ticket.dto;

import java.util.List;

public class PassbookTemplate extends BasePassbookTemplate {

    private static final long serialVersionUID = 1L;
    
    private PassbookField header;
    private PassbookField primaryField;
    private List<PassbookField> secondaryFields;
    private List<PassbookField> auxiliaryFields;
    private List<PassbookField> backFields;
    private List<String> languages;

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

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }
}
