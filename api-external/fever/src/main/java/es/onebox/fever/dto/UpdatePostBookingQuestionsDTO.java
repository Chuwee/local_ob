package es.onebox.fever.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("post_booking_questions")
    List<UpdatePostBookingQuestionDTO> postBookingQuestions;

    public List<UpdatePostBookingQuestionDTO> getPostBookingQuestions() {
        return postBookingQuestions;
    }

    public void setPostBookingQuestions(List<UpdatePostBookingQuestionDTO> postBookingQuestions) {
        this.postBookingQuestions = postBookingQuestions;
    }
}
