package es.onebox.fever.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.fever.dto.enums.PostBookingQuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;

public class UpdatePostBookingQuestionDTO {

    @NotNull(message = "Question id cannot be null")
    @NotBlank(message = "Question id cannot be blank")
    private String id;

    @NotNull(message = "Question name cannot be null")
    @NotBlank(message = "Question name cannot be blank")
    private String name;

    @Valid
    @NotNull(message = "Question label cannot be null")
    private TranslationDTO label;

    @Valid
    @NotNull(message = "Question message cannot be null")
    private TranslationDTO message;

    @NotNull(message = "Question type cannot be null")
    @JsonProperty("post_booking_question_type")
    private PostBookingQuestionType postBookingQuestionType;

    @Valid
    private Set<ChoiceDTO> choices;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TranslationDTO getLabel() {
        return label;
    }

    public void setLabel(TranslationDTO label) {
        this.label = label;
    }

    public TranslationDTO getMessage() {
        return message;
    }

    public void setMessage(TranslationDTO message) {
        this.message = message;
    }

    public PostBookingQuestionType getPostBookingQuestionType() {
        return postBookingQuestionType;
    }

    public void setPostBookingQuestionType(PostBookingQuestionType postBookingQuestionType) {
        this.postBookingQuestionType = postBookingQuestionType;
    }

    public Set<ChoiceDTO> getChoices() {
        return choices;
    }

    public void setChoices(Set<ChoiceDTO> choices) {
        this.choices = choices;
    }
}
