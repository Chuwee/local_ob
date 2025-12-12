package es.onebox.common.datasources.ms.event.dto;

import es.onebox.common.datasources.ms.event.enums.PostBookingQuestionType;

import java.util.Set;

public class UpdatePostBookingQuestion {

    private String id;
    private String name;
    private Translation label;
    private Translation message;
    private PostBookingQuestionType postBookingQuestionType;
    private Set<Choice> choices;

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

    public Translation getLabel() {
        return label;
    }

    public void setLabel(Translation label) {
        this.label = label;
    }

    public Translation getMessage() {
        return message;
    }

    public void setMessage(Translation message) {
        this.message = message;
    }

    public PostBookingQuestionType getPostBookingQuestionType() {
        return postBookingQuestionType;
    }

    public void setPostBookingQuestionType(PostBookingQuestionType postBookingQuestionType) {
        this.postBookingQuestionType = postBookingQuestionType;
    }

    public Set<Choice> getChoices() {
        return choices;
    }

    public void setChoices(Set<Choice> choices) {
        this.choices = choices;
    }
}
