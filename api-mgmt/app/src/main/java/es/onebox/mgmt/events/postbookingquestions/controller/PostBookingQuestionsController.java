package es.onebox.mgmt.events.postbookingquestions.controller;

import es.onebox.mgmt.config.ApiConfig;
import es.onebox.mgmt.events.postbookingquestions.service.PostBookingQuestionsService;
import es.onebox.mgmt.events.postbookingquestions.dto.EventChannelsPostBookingQuestionsDTO;
import es.onebox.mgmt.events.postbookingquestions.dto.PostBookingQuestionsDTO;
import es.onebox.mgmt.events.postbookingquestions.dto.PostBookingQuestionsFilter;
import es.onebox.mgmt.events.postbookingquestions.dto.UpdateEventPostBookingQuestionsDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static es.onebox.core.security.Roles.Codes.ROLE_ENT_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_EVN_MGR;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_ANS;
import static es.onebox.core.security.Roles.Codes.ROLE_OPR_MGR;

@RestController
@Validated
@RequestMapping(PostBookingQuestionsController.BASE_URI)
public class PostBookingQuestionsController {

    public static final String BASE_URI = ApiConfig.BASE_URL;
    private final PostBookingQuestionsService postBookingQuestionsService;

    @Autowired
    public PostBookingQuestionsController(PostBookingQuestionsService postBookingQuestionsService) {
        this.postBookingQuestionsService = postBookingQuestionsService;
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/post-booking-questions")
    public PostBookingQuestionsDTO getPostBookingQuestions(PostBookingQuestionsFilter filter) {

        return postBookingQuestionsService.getPostBookingQuestions(filter);
    }

    @Secured({ROLE_EVN_MGR, ROLE_ENT_ANS, ROLE_OPR_MGR, ROLE_OPR_ANS})
    @GetMapping("/events/{eventId}/post-booking-questions")
    public EventChannelsPostBookingQuestionsDTO getEventPostBookingQuestions(@PathVariable("eventId") Integer eventId) {

        return postBookingQuestionsService.getEventPostBookingQuestions(eventId);
    }

    @Secured({ROLE_EVN_MGR, ROLE_OPR_MGR})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping("/events/{eventId}/post-booking-questions")
    public void updateEventPostBookingQuestions(@PathVariable("eventId") Integer eventId, @Valid @RequestBody UpdateEventPostBookingQuestionsDTO request) {

        postBookingQuestionsService.updateEventPostBookingQuestions(eventId, request);
    }
}
