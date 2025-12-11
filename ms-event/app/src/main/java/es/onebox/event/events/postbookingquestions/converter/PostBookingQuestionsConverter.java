package es.onebox.event.events.postbookingquestions.converter;

import es.onebox.core.serializer.dto.response.MetadataBuilder;
import es.onebox.event.events.postbookingquestions.domain.Choice;
import es.onebox.event.events.postbookingquestions.domain.Translation;
import es.onebox.event.events.postbookingquestions.dto.ChoiceDTO;
import es.onebox.event.events.postbookingquestions.dto.TranslationDTO;
import es.onebox.event.events.postbookingquestions.dto.UpdatePostBookingQuestionDTO;
import es.onebox.event.events.postbookingquestions.domain.PostBookingQuestion;
import es.onebox.event.events.postbookingquestions.dto.PostBookingQuestionDTO;
import es.onebox.event.events.postbookingquestions.dto.PostBookingQuestionsDTO;
import es.onebox.event.events.postbookingquestions.dto.UpdateEventPostBookingQuestionsDTO;
import es.onebox.event.events.postbookingquestions.request.PostBookingQuestionsFilter;
import es.onebox.event.tags.utils.LanguageUtils;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoCanalPostBookingQuestionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoPostBookingQuestionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPostBookingQuestionRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class PostBookingQuestionsConverter {

    private PostBookingQuestionsConverter() {}

    public static PostBookingQuestionsDTO convert(List<PostBookingQuestionDTO> postBookingQuestionDTOS, PostBookingQuestionsFilter filter, long total) {

        PostBookingQuestionsDTO postBookingQuestionsDTO = new PostBookingQuestionsDTO();
        postBookingQuestionsDTO.setMetadata(MetadataBuilder.build(filter, total));
        postBookingQuestionsDTO.setData(postBookingQuestionDTOS);

        return postBookingQuestionsDTO;
    }

    public static Map<String, Integer> convert(List<CpanelPostBookingQuestionRecord> postBookingQuestionRecords) {

        Map<String, Integer> postBookingQuestions = new HashMap<>();

        for (CpanelPostBookingQuestionRecord postBookingQuestionRecord : postBookingQuestionRecords) {
            postBookingQuestions.put(postBookingQuestionRecord.getIdexterno(), postBookingQuestionRecord.getIdpostbookingquestion());
        }

        return postBookingQuestions;
    }

    public static PostBookingQuestionDTO convertToPBQuestionDTO(PostBookingQuestion postBookingQuestion, Integer internalId) {

        PostBookingQuestionDTO postBookingQuestionDTO = new PostBookingQuestionDTO();

        postBookingQuestionDTO.setId(internalId);
        postBookingQuestionDTO.setName(postBookingQuestion.getName());
        if (postBookingQuestion.getLabel() != null) {
            postBookingQuestionDTO.setLabel(convertToTranslationDTO(postBookingQuestion.getLabel()));
        }

        return postBookingQuestionDTO;
    }

    public static Set<CpanelEventoPostBookingQuestionRecord> toEventPostBookingQuestionRecord(Integer eventId, UpdateEventPostBookingQuestionsDTO request) {

        Set<CpanelEventoPostBookingQuestionRecord> records = new HashSet<>();

        for (Integer postBookingQuestionId: request.getPostBookingQuestions()) {
            CpanelEventoPostBookingQuestionRecord record = new CpanelEventoPostBookingQuestionRecord();
            record.setIdevento(eventId);
            record.setIdpostbookingquestion(postBookingQuestionId);
            records.add(record);
        }

        return records;
    }

    public static Set<CpanelEventoCanalPostBookingQuestionRecord> toEventChannelPostBookingQuestionRecord(Integer eventId, UpdateEventPostBookingQuestionsDTO request) {

        Set<CpanelEventoCanalPostBookingQuestionRecord> records = new HashSet<>();

        for (Integer channelId: request.getChannels().getIds()) {
            CpanelEventoCanalPostBookingQuestionRecord record = new CpanelEventoCanalPostBookingQuestionRecord();
            record.setIdevento(eventId);
            record.setIdcanal(channelId);
            records.add(record);
        }

        return records;
    }

    public static List<CpanelPostBookingQuestionRecord> toPostBookingQuestionRecord(List<UpdatePostBookingQuestionDTO> postBookingQuestions) {

        List<CpanelPostBookingQuestionRecord> postBookingQuestionRecords = new ArrayList<>();

        for (UpdatePostBookingQuestionDTO postBookingQuestion: postBookingQuestions) {
            CpanelPostBookingQuestionRecord record = new CpanelPostBookingQuestionRecord();
            record.setIdexterno(postBookingQuestion.getId());
            record.setEstado(1);

            postBookingQuestionRecords.add(record);
        }

        return postBookingQuestionRecords;
    }

    public static List<PostBookingQuestion> convertToList(List<UpdatePostBookingQuestionDTO> request) {

        return new ArrayList<>(request.stream().map(PostBookingQuestionsConverter::convertToPBQuestion).toList());
    }

    private static PostBookingQuestion convertToPBQuestion(UpdatePostBookingQuestionDTO request) {

        PostBookingQuestion postBookingQuestion = new PostBookingQuestion();
        postBookingQuestion.setId(request.getId());
        postBookingQuestion.setName(request.getName());

        if (request.getLabel() != null) {
            postBookingQuestion.setLabel(convertToTranslation(request.getLabel()));
        }
        if (request.getMessage() != null) {
            postBookingQuestion.setMessage(convertToTranslation(request.getMessage()));
        }
        postBookingQuestion.setType(request.getPostBookingQuestionType());
        if (request.getChoices() != null) {
            postBookingQuestion.setChoices(request.getChoices().stream()
                    .map(PostBookingQuestionsConverter::convertToChoice).collect(Collectors.toSet()));
        }

        return postBookingQuestion;
    }

    private static Translation convertToTranslation(TranslationDTO translationDTO) {

        Translation translation = new Translation();
        translation.setDefaultValue(translationDTO.getDefaultValue());
        if (translationDTO.getTranslations() != null) {
            Map<String, String> map = new HashMap<>();
            for (Map.Entry<String, String> e : translationDTO.getTranslations().entrySet()) {
                map.put(LanguageUtils.toLocaleCode(e.getKey()), e.getValue());
            }
            translation.setTranslations(map);
        }

        return translation;
    }

    private static TranslationDTO convertToTranslationDTO(Translation translation) {

        TranslationDTO translationDTO = new TranslationDTO();
        translationDTO.setDefaultValue(translation.getDefaultValue());
        if (translation.getTranslations() != null) {
            Map<String, String> map = new HashMap<>();
            for (Map.Entry<String, String> e : translation.getTranslations().entrySet()) {
                map.put(LanguageUtils.toLocaleCode(e.getKey()), e.getValue());
            }
            translationDTO.setTranslations(map);
        }

        return translationDTO;
    }

    private static Choice convertToChoice(ChoiceDTO choiceDTO) {

        Choice choice = new Choice();
        choice.setId(UUID.randomUUID().toString());
        if (choiceDTO.getLabel() != null) {
            choice.setLabel(convertToTranslation(choiceDTO.getLabel()));
        }
        choice.setPosition(choiceDTO.getPosition());
        if (choiceDTO.getAdditionalFreeTextChoiceQuestion() != null) {
            choice.setAdditionalFreeTextChoiceQuestion(convertToTranslation(choiceDTO.getAdditionalFreeTextChoiceQuestion()));
        }

        return choice;
    }
}
