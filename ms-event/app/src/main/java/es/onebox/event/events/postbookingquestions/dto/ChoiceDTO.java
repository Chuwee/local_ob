package es.onebox.event.events.postbookingquestions.dto;

public class ChoiceDTO {

    private TranslationDTO label;
    private Integer position;

    private TranslationDTO additionalFreeTextChoiceQuestion;

    public TranslationDTO getLabel() {
        return label;
    }

    public void setLabel(TranslationDTO label) {
        this.label = label;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public TranslationDTO getAdditionalFreeTextChoiceQuestion() {
        return additionalFreeTextChoiceQuestion;
    }

    public void setAdditionalFreeTextChoiceQuestion(TranslationDTO additionalFreeTextChoiceQuestion) {
        this.additionalFreeTextChoiceQuestion = additionalFreeTextChoiceQuestion;
    }
}
