package es.onebox.event.events.postbookingquestions.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdatePostBookingQuestionsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Valid
    List<UpdatePostBookingQuestionDTO> postBookingQuestions;

    public List<UpdatePostBookingQuestionDTO> getPostBookingQuestions() {
        return postBookingQuestions;
    }

    public void setPostBookingQuestions(List<UpdatePostBookingQuestionDTO> postBookingQuestions) {
        this.postBookingQuestions = postBookingQuestions;
    }
}
