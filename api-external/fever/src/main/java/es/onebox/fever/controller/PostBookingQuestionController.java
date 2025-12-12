package es.onebox.fever.controller;

import es.onebox.common.config.ApiConfig;
import es.onebox.common.security.Role;
import es.onebox.fever.dto.UpdatePostBookingQuestionsDTO;
import es.onebox.fever.service.PostBookingQuestionService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping(value = ApiConfig.FeverApiConfig.BASE_URL)
public class PostBookingQuestionController {

    private final PostBookingQuestionService postBookingQuestionService;

    public PostBookingQuestionController(PostBookingQuestionService postBookingQuestionService) {

        this.postBookingQuestionService = postBookingQuestionService;
    }

    @Secured(Role.CHANNEL_INTEGRATION)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/post-booking-questions")
    public void updatePostBookingQuestions(@Valid @RequestBody UpdatePostBookingQuestionsDTO request) {

        postBookingQuestionService.updatePostBookingQuestions(request);
    }
}
