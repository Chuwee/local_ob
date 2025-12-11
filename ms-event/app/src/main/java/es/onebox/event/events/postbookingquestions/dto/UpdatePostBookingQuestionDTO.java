package es.onebox.event.events.postbookingquestions.dto;

import es.onebox.event.events.postbookingquestions.enums.PostBookingQuestionType;

import java.util.Set;

public class UpdatePostBookingQuestionDTO {

    private String id;
    private String name;
    private TranslationDTO label;
    private TranslationDTO message;
    private PostBookingQuestionType postBookingQuestionType;
    private Set<ChoiceDTO> choices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TranslationDTO getLabel() {
        return label;
    }

    public void setLabel(TranslationDTO label) {
        this.label = label;
    }

    public TranslationDTO getMessage() {
        return message;
    }

    public void setMessage(TranslationDTO message) {
        this.message = message;
    }

    public PostBookingQuestionType getPostBookingQuestionType() {
        return postBookingQuestionType;
    }

    public void setPostBookingQuestionType(PostBookingQuestionType postBookingQuestionType) {
        this.postBookingQuestionType = postBookingQuestionType;
    }

    public Set<ChoiceDTO> getChoices() {
        return choices;
    }

    public void setChoices(Set<ChoiceDTO> choices) {
        this.choices = choices;
    }
}
