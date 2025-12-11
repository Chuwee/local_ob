package es.onebox.mgmt.events.postbookingquestions.service;

import es.onebox.mgmt.datasources.ms.event.repository.PostBookingQuestionsRepository;
import es.onebox.mgmt.events.postbookingquestions.converter.PostBookingQuestionsConverter;
import es.onebox.mgmt.events.postbookingquestions.dto.EventChannelsPostBookingQuestionsDTO;
import es.onebox.mgmt.events.postbookingquestions.dto.PostBookingQuestionsDTO;
import es.onebox.mgmt.events.postbookingquestions.dto.PostBookingQuestionsFilter;
import es.onebox.mgmt.events.postbookingquestions.dto.UpdateEventPostBookingQuestionsDTO;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostBookingQuestionsService {

    private final PostBookingQuestionsRepository postBookingQuestionsRepository;
    private final ValidationService validationService;

    @Autowired
    public PostBookingQuestionsService(PostBookingQuestionsRepository postBookingQuestionsRepository, ValidationService validationService) {
        this.postBookingQuestionsRepository = postBookingQuestionsRepository;
        this.validationService = validationService;
    }

    public PostBookingQuestionsDTO getPostBookingQuestions(PostBookingQuestionsFilter filter) {

        return PostBookingQuestionsConverter.fromMs(postBookingQuestionsRepository.getPostBookingQuestions(filter));
    }

    public EventChannelsPostBookingQuestionsDTO getEventPostBookingQuestions(Integer eventId) {

        validationService.getAndCheckEvent(Long.valueOf(eventId));
        return PostBookingQuestionsConverter.fromMs(postBookingQuestionsRepository.getEventPostBookingQuestions(eventId));
    }

    public void updateEventPostBookingQuestions(Integer eventId, UpdateEventPostBookingQuestionsDTO request) {

        validationService.getAndCheckEvent(Long.valueOf(eventId));
        postBookingQuestionsRepository.updateEventPostBookingQuestions(eventId
                , PostBookingQuestionsConverter.toMsEventPostBookingQuestions(request));
    }
}
