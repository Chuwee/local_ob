package es.onebox.event.events.postbookingquestions.domain;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;
import es.onebox.event.events.postbookingquestions.enums.PostBookingQuestionType;

import java.util.Set;

@CouchDocument
public class PostBookingQuestion {

    @Id
    private String id;
    private String name;
    private Translation label;
    private Translation message;
    private PostBookingQuestionType type;
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

    public PostBookingQuestionType getType() {
        return type;
    }

    public void setType(PostBookingQuestionType type) {
        this.type = type;
    }

    public Set<Choice> getChoices() {
        return choices;
    }

    public void setChoices(Set<Choice> choices) {
        this.choices = choices;
    }
}
