package es.onebox.event.catalog.elasticsearch.dto.channelevent;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelEventPostBookingQuestions implements Serializable {

    @Serial
    private static final long serialVersionUID = 6255307028263398075L;

    private List<PostBookingQuestion> questions;

    public List<PostBookingQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<PostBookingQuestion> questions) {
        this.questions = questions;
    }
}
