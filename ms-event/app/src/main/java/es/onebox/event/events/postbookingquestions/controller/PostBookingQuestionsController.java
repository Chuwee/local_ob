package es.onebox.event.events.postbookingquestions.controller;

import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.config.ApiConfig;
import es.onebox.event.events.postbookingquestions.dto.EventChannelsPostBookingQuestionsDTO;
import es.onebox.event.events.postbookingquestions.dto.PostBookingQuestionsDTO;
import es.onebox.event.events.postbookingquestions.dto.UpdateEventPostBookingQuestionsDTO;
import es.onebox.event.events.postbookingquestions.dto.UpdatePostBookingQuestionsDTO;
import es.onebox.event.events.postbookingquestions.request.PostBookingQuestionsFilter;
import es.onebox.event.events.postbookingquestions.service.PostBookingQuestionsService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping(PostBookingQuestionsController.BASE_URI)
public class PostBookingQuestionsController {

    public static final String BASE_URI = ApiConfig.BASE_URL;
    private final PostBookingQuestionsService postBookingQuestionsService;
    private final RefreshDataService refreshDataService;


    @Autowired
    public PostBookingQuestionsController(PostBookingQuestionsService postBookingQuestionsService, RefreshDataService refreshDataService) {
        this.postBookingQuestionsService = postBookingQuestionsService;
        this.refreshDataService = refreshDataService;
    }

    @GetMapping("/post-booking-questions")
    public PostBookingQuestionsDTO getPostBookingQuestions(PostBookingQuestionsFilter filter) {

        return postBookingQuestionsService.getPostBookingQuestions(filter);
    }

    @PutMapping("/post-booking-questions")
    public void updatePostBookingQuestions(@Valid @RequestBody UpdatePostBookingQuestionsDTO request) {

        postBookingQuestionsService.updatePostBookingQuestions(request);
    }

    @GetMapping("/events/{eventId}/post-booking-questions")
    public EventChannelsPostBookingQuestionsDTO getEventPostBookingQuestions(@PathVariable("eventId") Integer eventId) {

        return postBookingQuestionsService.getEventPostBookingQuestions(eventId);
    }

    @PutMapping("/events/{eventId}/post-booking-questions")
    public void updateEventPostBookingQuestions(@PathVariable("eventId") Integer eventId, @Valid @RequestBody UpdateEventPostBookingQuestionsDTO request) {

        postBookingQuestionsService.updateEventPostBookingQuestions(eventId, request);
        refreshDataService.refreshEvent(eventId.longValue(), "updatePostBookingQuestions");
    }
}
