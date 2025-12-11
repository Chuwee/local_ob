package es.onebox.event.events.postbookingquestions.dao;

import es.onebox.event.events.postbookingquestions.dao.record.EventPostBookingQuestionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoPostBookingQuestionRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.tables.CpanelEventoPostBookingQuestion.CPANEL_EVENTO_POST_BOOKING_QUESTION;
import static es.onebox.jooq.cpanel.tables.CpanelPostBookingQuestion.CPANEL_POST_BOOKING_QUESTION;

@Repository
public class EventPostBookingQuestionDao extends DaoImpl<CpanelEventoPostBookingQuestionRecord, Integer> {

    protected EventPostBookingQuestionDao() { super(CPANEL_EVENTO_POST_BOOKING_QUESTION ); }

    public List<EventPostBookingQuestionRecord> getEventPostBookingQuestions(Integer eventId) {

        return dsl.select(CPANEL_POST_BOOKING_QUESTION.IDPOSTBOOKINGQUESTION,
                        CPANEL_POST_BOOKING_QUESTION.IDEXTERNO)
                .from(CPANEL_EVENTO_POST_BOOKING_QUESTION)
                .join(CPANEL_POST_BOOKING_QUESTION)
                .on(CPANEL_EVENTO_POST_BOOKING_QUESTION.IDPOSTBOOKINGQUESTION.eq(CPANEL_POST_BOOKING_QUESTION.IDPOSTBOOKINGQUESTION))
                .where(CPANEL_EVENTO_POST_BOOKING_QUESTION.IDEVENTO.eq(eventId))
                .and(CPANEL_POST_BOOKING_QUESTION.ESTADO.eq(1))
                .fetchInto(EventPostBookingQuestionRecord.class);
    }

    public void deleteByEventId(Integer eventId) {

        dsl.delete(CPANEL_EVENTO_POST_BOOKING_QUESTION)
                .where(CPANEL_EVENTO_POST_BOOKING_QUESTION.IDEVENTO.eq(eventId))
                .execute();
    }

    public void deleteInactivePostBookingQuestions() {

        dsl.delete(CPANEL_EVENTO_POST_BOOKING_QUESTION)
            .using(CPANEL_POST_BOOKING_QUESTION)
            .where(CPANEL_EVENTO_POST_BOOKING_QUESTION.IDPOSTBOOKINGQUESTION
                    .eq(CPANEL_POST_BOOKING_QUESTION.IDPOSTBOOKINGQUESTION))
            .and(CPANEL_POST_BOOKING_QUESTION.ESTADO.eq(0))
            .execute();
    }
}
