package es.onebox.mgmt.events.postbookingquestions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateEventPostBookingQuestionsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "enabled is mandatory")
    private Boolean enabled;
    @JsonProperty("questions")
    @NotNull
    private List<Integer> postBookingQuestions;
    @NotNull
    private PostBookingQuestionsChannelsDTO channels;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<Integer> getPostBookingQuestions() {
        return postBookingQuestions;
    }

    public void setPostBookingQuestions(List<Integer> postBookingQuestions) {
        this.postBookingQuestions = postBookingQuestions;
    }

    public PostBookingQuestionsChannelsDTO getChannels() {
        return channels;
    }

    public void setChannels(PostBookingQuestionsChannelsDTO channels) {
        this.channels = channels;
    }
}
