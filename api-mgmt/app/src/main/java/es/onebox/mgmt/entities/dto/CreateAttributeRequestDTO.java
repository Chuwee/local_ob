package es.onebox.mgmt.entities.dto;

public class CreateAttributeRequestDTO {

    private String name;
    private AttributeTextsDTO texts;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AttributeTextsDTO getTexts() {
        return texts;
    }

    public void setTexts(AttributeTextsDTO texts) {
        this.texts = texts;
    }
}
