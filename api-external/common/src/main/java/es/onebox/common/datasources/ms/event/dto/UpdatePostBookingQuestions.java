package es.onebox.common.datasources.ms.event.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdatePostBookingQuestions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    @Valid
    List<UpdatePostBookingQuestion> postBookingQuestions;

    public List<UpdatePostBookingQuestion> getPostBookingQuestions() {
        return postBookingQuestions;
    }

    public void setPostBookingQuestions(List<UpdatePostBookingQuestion> postBookingQuestions) {
        this.postBookingQuestions = postBookingQuestions;
    }
}
