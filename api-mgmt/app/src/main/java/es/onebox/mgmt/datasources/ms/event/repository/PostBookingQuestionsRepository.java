package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventPostBookingQuestions;
import es.onebox.mgmt.datasources.ms.event.dto.event.PostBookingQuestions;
import es.onebox.mgmt.datasources.ms.event.dto.event.UpdateEventPostBookingQuestions;
import es.onebox.mgmt.events.postbookingquestions.dto.PostBookingQuestionsFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class PostBookingQuestionsRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public PostBookingQuestionsRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public PostBookingQuestions getPostBookingQuestions(PostBookingQuestionsFilter filter) {
        return msEventDatasource.getPostBookingQuestions(filter);
    }

    public EventPostBookingQuestions getEventPostBookingQuestions(Integer eventId) {
        return msEventDatasource.getEventPostBookingQuestions(eventId);
    }

    public void updateEventPostBookingQuestions(Integer eventId, UpdateEventPostBookingQuestions request) {
        msEventDatasource.updateEventPostBookingQuestions(eventId, request);
    }
}
