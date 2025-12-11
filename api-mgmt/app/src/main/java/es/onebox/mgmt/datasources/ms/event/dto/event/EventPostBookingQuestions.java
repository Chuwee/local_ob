package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.mgmt.events.postbookingquestions.dto.PostBookingQuestionDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class EventPostBookingQuestions implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Boolean enabled;
    private List<PostBookingQuestion> postBookingQuestions;
    private PostBookingQuestionsChannels channels;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public List<PostBookingQuestion> getPostBookingQuestions() {
        return postBookingQuestions;
    }

    public void setPostBookingQuestions(List<PostBookingQuestion> postBookingQuestions) {
        this.postBookingQuestions = postBookingQuestions;
    }

    public PostBookingQuestionsChannels getChannels() {
        return channels;
    }

    public void setChannels(PostBookingQuestionsChannels channels) {
        this.channels = channels;
    }
}
