package es.onebox.mgmt.events.postbookingquestions.converter;

import es.onebox.mgmt.datasources.ms.event.dto.event.EventPostBookingQuestions;
import es.onebox.mgmt.datasources.ms.event.dto.event.PostBookingQuestion;
import es.onebox.mgmt.datasources.ms.event.dto.event.PostBookingQuestions;
import es.onebox.mgmt.datasources.ms.event.dto.event.PostBookingQuestionsChannels;
import es.onebox.mgmt.datasources.ms.event.dto.event.Translation;
import es.onebox.mgmt.datasources.ms.event.dto.event.UpdateEventPostBookingQuestions;
import es.onebox.mgmt.events.postbookingquestions.dto.EventChannelsPostBookingQuestionsDTO;
import es.onebox.mgmt.events.postbookingquestions.dto.PostBookingQuestionDTO;
import es.onebox.mgmt.events.postbookingquestions.dto.PostBookingQuestionsChannelsDTO;
import es.onebox.mgmt.events.postbookingquestions.dto.PostBookingQuestionsDTO;
import es.onebox.mgmt.events.dto.TranlationDTO;
import es.onebox.mgmt.events.postbookingquestions.dto.UpdateEventPostBookingQuestionsDTO;
import es.onebox.mgmt.events.postbookingquestions.enums.EventChannelsPBQType;

import java.util.stream.Collectors;

public class PostBookingQuestionsConverter {

    private PostBookingQuestionsConverter () {}

    public static PostBookingQuestionsDTO fromMs(PostBookingQuestions postBookingQuestions) {

        PostBookingQuestionsDTO  postBookingQuestionsDTO = new PostBookingQuestionsDTO();
        postBookingQuestionsDTO.setMetadata(postBookingQuestions.getMetadata());
        postBookingQuestionsDTO.setData(postBookingQuestions.getData().stream().map(PostBookingQuestionsConverter::convertToDTO).collect(Collectors.toList()));

        return postBookingQuestionsDTO;
    }

    public static EventChannelsPostBookingQuestionsDTO fromMs(EventPostBookingQuestions eventPostBookingQuestions) {

        EventChannelsPostBookingQuestionsDTO  eventChannelsPostBookingQuestionsDTO = new EventChannelsPostBookingQuestionsDTO();
        eventChannelsPostBookingQuestionsDTO.setEnabled(eventPostBookingQuestions.getEnabled());
        eventChannelsPostBookingQuestionsDTO.setPostBookingQuestions(eventPostBookingQuestions.getPostBookingQuestions()
                .stream().map(PostBookingQuestionsConverter::convertToDTO).collect(Collectors.toList()));
        if (eventPostBookingQuestions.getChannels() != null) {
            eventChannelsPostBookingQuestionsDTO.setChannels(convertToDTO(eventPostBookingQuestions.getChannels()));
        }

        return eventChannelsPostBookingQuestionsDTO;
    }

    private static PostBookingQuestionDTO convertToDTO(PostBookingQuestion postBookingQuestion) {

        PostBookingQuestionDTO postBookingQuestionDTO = new PostBookingQuestionDTO();
        postBookingQuestionDTO.setId(postBookingQuestion.getId());
        postBookingQuestionDTO.setName(postBookingQuestion.getName());
        postBookingQuestionDTO.setLabel(convertToDTO(postBookingQuestion.getLabel()));

        return postBookingQuestionDTO;
    }

    private static TranlationDTO convertToDTO(Translation translation) {

        TranlationDTO tranlationDTO = new TranlationDTO();
        tranlationDTO.setDefaultValue(translation.getDefaultValue());
        if (translation.getTranslations() != null) {
            tranlationDTO.setTranslations(translation.getTranslations());
        }

        return tranlationDTO;
    }

    public static UpdateEventPostBookingQuestions toMsEventPostBookingQuestions(UpdateEventPostBookingQuestionsDTO request) {

        UpdateEventPostBookingQuestions updateEventPostBookingQuestions = new UpdateEventPostBookingQuestions();
        updateEventPostBookingQuestions.setEnabled(request.getEnabled());
        updateEventPostBookingQuestions.setPostBookingQuestions(request.getPostBookingQuestions());
        updateEventPostBookingQuestions.setChannels(convertToDTO(request.getChannels()));

        return updateEventPostBookingQuestions;
    }

    private static PostBookingQuestionsChannels convertToDTO(PostBookingQuestionsChannelsDTO postBookingQuestionsChannelsDTO) {

        PostBookingQuestionsChannels postBookingQuestionsChannels = new PostBookingQuestionsChannels();

        if (postBookingQuestionsChannelsDTO.getIds() != null) {
            postBookingQuestionsChannels.setIds(postBookingQuestionsChannelsDTO.getIds());
        }
        postBookingQuestionsChannels.setType(es.onebox.mgmt.datasources.ms.event.dto.event.EventChannelsPBQType
                .valueOf(postBookingQuestionsChannelsDTO.getType().name()));

        return postBookingQuestionsChannels;
    }

    private static PostBookingQuestionsChannelsDTO convertToDTO(PostBookingQuestionsChannels postBookingQuestionsChannels) {

        PostBookingQuestionsChannelsDTO postBookingQuestionsChannelsDTO = new PostBookingQuestionsChannelsDTO();
        postBookingQuestionsChannelsDTO.setIds(postBookingQuestionsChannels.getIds());
        postBookingQuestionsChannelsDTO.setType(EventChannelsPBQType.valueOf(postBookingQuestionsChannels.getType().name()));

        return postBookingQuestionsChannelsDTO;
    }
}
