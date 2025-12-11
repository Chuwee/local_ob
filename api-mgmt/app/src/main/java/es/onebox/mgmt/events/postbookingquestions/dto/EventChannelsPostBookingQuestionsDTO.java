package es.onebox.mgmt.events.postbookingquestions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class EventChannelsPostBookingQuestionsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    @JsonProperty("questions")
    private List<PostBookingQuestionDTO> postBookingQuestions;
    private PostBookingQuestionsChannelsDTO channels;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<PostBookingQuestionDTO> getPostBookingQuestions() {
        return postBookingQuestions;
    }

    public void setPostBookingQuestions(List<PostBookingQuestionDTO> postBookingQuestions) {
        this.postBookingQuestions = postBookingQuestions;
    }

    public PostBookingQuestionsChannelsDTO getChannels() {
        return channels;
    }

    public void setChannels(PostBookingQuestionsChannelsDTO channels) {
        this.channels = channels;
    }
}
