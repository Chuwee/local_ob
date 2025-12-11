package es.onebox.event.events.postbookingquestions.domain;

public class Choice {

    private String id;
    private Translation label;
    private Integer position;
    private Translation additionalFreeTextChoiceQuestion;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public Translation getLabel() {
        return label;
    }

    public void setLabel(Translation label) {
        this.label = label;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Translation getAdditionalFreeTextChoiceQuestion() {
        return additionalFreeTextChoiceQuestion;
    }

    public void setAdditionalFreeTextChoiceQuestion(Translation additionalFreeTextChoiceQuestion) {
        this.additionalFreeTextChoiceQuestion = additionalFreeTextChoiceQuestion;
    }
}
