package es.onebox.event.catalog.elasticsearch.dto.channelevent;

import java.io.Serial;
import java.io.Serializable;

public class PostBookingQuestionChoice implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private PostBookingQuestionTranslation label;
    private Integer value;
    private PostBookingQuestionTranslation additionalQuestion;

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public PostBookingQuestionTranslation getLabel() {
        return label;
    }

    public void setLabel(PostBookingQuestionTranslation label) {
        this.label = label;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public PostBookingQuestionTranslation getAdditionalQuestion() {
        return additionalQuestion;
    }

    public void setAdditionalQuestion(PostBookingQuestionTranslation additionalQuestion) {
        this.additionalQuestion = additionalQuestion;
    }
}
