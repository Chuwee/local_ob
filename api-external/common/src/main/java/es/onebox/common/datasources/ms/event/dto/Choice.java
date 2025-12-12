package es.onebox.common.datasources.ms.event.dto;

public class Choice {

    private Translation label;
    private Integer position;
    private Translation additionalFreeTextChoiceQuestion;

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
