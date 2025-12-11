package es.onebox.event.events.postbookingquestions.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdateEventPostBookingQuestionsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private Boolean enabled;
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
