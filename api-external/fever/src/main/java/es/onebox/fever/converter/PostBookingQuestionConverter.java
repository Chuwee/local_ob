package es.onebox.fever.converter;

import es.onebox.common.datasources.ms.event.dto.Choice;
import es.onebox.common.datasources.ms.event.enums.PostBookingQuestionType;
import es.onebox.common.datasources.ms.event.dto.Translation;
import es.onebox.common.datasources.ms.event.dto.UpdatePostBookingQuestion;
import es.onebox.common.datasources.ms.event.dto.UpdatePostBookingQuestions;
import es.onebox.fever.dto.ChoiceDTO;
import es.onebox.fever.dto.TranslationDTO;
import es.onebox.fever.dto.UpdatePostBookingQuestionDTO;
import es.onebox.fever.dto.UpdatePostBookingQuestionsDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PostBookingQuestionConverter {

    private PostBookingQuestionConverter() {}

    public static UpdatePostBookingQuestions toMsUpdatePostBookingQuestions(UpdatePostBookingQuestionsDTO request) {

        UpdatePostBookingQuestions updatePostBookingQuestions = new UpdatePostBookingQuestions();
        List<UpdatePostBookingQuestion> updatePostBookingQuestionList = new ArrayList<>(request.getPostBookingQuestions()
                .stream().map(PostBookingQuestionConverter::convertToUpdatePostBookingQuestion).toList());

        updatePostBookingQuestions.setPostBookingQuestions(updatePostBookingQuestionList);

        return updatePostBookingQuestions;
    }

    private static UpdatePostBookingQuestion convertToUpdatePostBookingQuestion(UpdatePostBookingQuestionDTO request) {

        UpdatePostBookingQuestion updatePostBookingQuestion = new UpdatePostBookingQuestion();
        updatePostBookingQuestion.setId(request.getId());
        updatePostBookingQuestion.setName(request.getName());

        if (request.getLabel() != null) {
            updatePostBookingQuestion.setLabel(convertToTranslation(request.getLabel()));
        }
        if (request.getMessage() != null) {
            updatePostBookingQuestion.setMessage(convertToTranslation(request.getMessage()));
        }
        updatePostBookingQuestion.setPostBookingQuestionType(PostBookingQuestionType.valueOf(request.getPostBookingQuestionType().name()));
        if (request.getChoices() != null) {
            updatePostBookingQuestion.setChoices(request.getChoices().stream()
                    .map(PostBookingQuestionConverter::convertToChoice).collect(Collectors.toSet()));
        }

        return updatePostBookingQuestion;
    }

    private static Translation convertToTranslation(TranslationDTO translationDTO) {

        Translation translation = new Translation();
        translation.setDefaultValue(translationDTO.getDefaultValue());
        if (translationDTO.getTranslations() != null) {
            Map<String, String> map = new HashMap<>();
            for (Map.Entry<String, String> e : translationDTO.getTranslations().entrySet()) {
                map.put(e.getKey().replace('_', '-'), e.getValue());
            }
            translation.setTranslations(map);
        }

        return translation;
    }

    private static Choice convertToChoice(ChoiceDTO choiceDTO) {

        Choice choice = new Choice();
        if (choiceDTO.getLabel() != null && choiceDTO.getLabel().getTranslations() != null) {
            choice.setLabel(convertToTranslation(choiceDTO.getLabel()));
        }
        choice.setPosition(choiceDTO.getPosition());
        if (choiceDTO.getAdditionalFreeTextChoiceQuestion() != null) {
            choice.setAdditionalFreeTextChoiceQuestion(convertToTranslation(choiceDTO.getAdditionalFreeTextChoiceQuestion()));
        }

        return choice;
    }
}
