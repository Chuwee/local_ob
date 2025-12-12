package es.onebox.fever.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public class ChoiceDTO {

    @Valid
    @NotNull(message = "Choice label cannot be null")
    private TranslationDTO label;
    private Integer position;

    @Valid
    @JsonProperty("additional_free_text_choice_question")
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
