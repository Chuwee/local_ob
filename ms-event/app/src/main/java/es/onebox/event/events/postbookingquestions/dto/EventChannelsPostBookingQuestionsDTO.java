package es.onebox.event.events.postbookingquestions.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class EventChannelsPostBookingQuestionsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private List<PostBookingQuestionDTO> postBookingQuestions;
    private PostBookingQuestionsChannelsDTO channels;

    public EventChannelsPostBookingQuestionsDTO(boolean enabled, List<PostBookingQuestionDTO> postBookingQuestions
            , PostBookingQuestionsChannelsDTO channels) {
        this.enabled = enabled;
        this.postBookingQuestions = postBookingQuestions;
        this.channels = channels;
    }

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
