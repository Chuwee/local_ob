package es.onebox.event.catalog.elasticsearch.dto.channelevent;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

public class PostBookingQuestion implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private PostBookingQuestionTranslation label;
    private PostBookingQuestionTranslation message;
    private PostBookingQuestionType type;
    private Set<PostBookingQuestionChoice> choices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public PostBookingQuestionTranslation getLabel() {
        return label;
    }

    public void setLabel(PostBookingQuestionTranslation label) {
        this.label = label;
    }

    public PostBookingQuestionTranslation getMessage() {
        return message;
    }

    public void setMessage(PostBookingQuestionTranslation message) {
        this.message = message;
    }

    public PostBookingQuestionType getType() {
        return type;
    }

    public void setType(PostBookingQuestionType type) {
        this.type = type;
    }

    public Set<PostBookingQuestionChoice> getChoices() {
        return choices;
    }

    public void setChoices(Set<PostBookingQuestionChoice> choices) {
        this.choices = choices;
    }
}