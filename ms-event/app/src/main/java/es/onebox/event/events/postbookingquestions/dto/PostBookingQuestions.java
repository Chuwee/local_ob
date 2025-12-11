package es.onebox.event.events.postbookingquestions.dto;

import es.onebox.event.events.postbookingquestions.domain.PostBookingQuestion;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PostBookingQuestions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    List<PostBookingQuestion> postBookingQuestions;

    public PostBookingQuestions() {}

    public PostBookingQuestions(PostBookingQuestions postBookingQuestions) {
        this.postBookingQuestions = postBookingQuestions.getPostBookingQuestions();
    }

    public List<PostBookingQuestion> getPostBookingQuestions() {
        return postBookingQuestions;
    }

    public void setPostBookingQuestions(List<PostBookingQuestion> postBookingQuestions) {
        this.postBookingQuestions = postBookingQuestions;
    }
}
