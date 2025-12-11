package es.onebox.event.events.postbookingquestions.dao;

import es.onebox.event.events.postbookingquestions.dao.record.EventPostBookingQuestionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoCanalPostBookingQuestionRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.tables.CpanelEventoCanalPostBookingQuestion.CPANEL_EVENTO_CANAL_POST_BOOKING_QUESTION;
import static es.onebox.jooq.cpanel.tables.CpanelEventoPostBookingQuestion.CPANEL_EVENTO_POST_BOOKING_QUESTION;
import static es.onebox.jooq.cpanel.tables.CpanelPostBookingQuestion.CPANEL_POST_BOOKING_QUESTION;

@Repository
public class EventChannelPostBookingQuestionDao extends DaoImpl<CpanelEventoCanalPostBookingQuestionRecord, Integer> {

    protected EventChannelPostBookingQuestionDao() {
        super(CPANEL_EVENTO_CANAL_POST_BOOKING_QUESTION);
    }

    public List<Integer> getEventChannelsPostBookingQuestionsChannels(Integer eventId) {

        return dsl
                .select(CPANEL_EVENTO_CANAL_POST_BOOKING_QUESTION.IDCANAL)
                .from(CPANEL_EVENTO_CANAL_POST_BOOKING_QUESTION)
                .where(CPANEL_EVENTO_CANAL_POST_BOOKING_QUESTION.IDEVENTO.eq(eventId))
                .fetchInto(Integer.class);
    }

    public List<EventPostBookingQuestionRecord> getEventChannelsPostBookingQuestions(Integer eventId, Integer channelId) {

        return dsl.select(CPANEL_POST_BOOKING_QUESTION.IDPOSTBOOKINGQUESTION,
                        CPANEL_POST_BOOKING_QUESTION.IDEXTERNO)
                .from(CPANEL_EVENTO_CANAL_POST_BOOKING_QUESTION)
                .join(CPANEL_EVENTO_POST_BOOKING_QUESTION)
                .on(CPANEL_EVENTO_CANAL_POST_BOOKING_QUESTION.IDEVENTO.eq(CPANEL_EVENTO_POST_BOOKING_QUESTION.IDEVENTO))
                .join(CPANEL_POST_BOOKING_QUESTION)
                .on(CPANEL_EVENTO_POST_BOOKING_QUESTION.IDPOSTBOOKINGQUESTION.eq(CPANEL_POST_BOOKING_QUESTION.IDPOSTBOOKINGQUESTION))
                .where(CPANEL_EVENTO_CANAL_POST_BOOKING_QUESTION.IDEVENTO.eq(eventId))
                .and(CPANEL_EVENTO_CANAL_POST_BOOKING_QUESTION.IDCANAL.eq(channelId))
                .and(CPANEL_POST_BOOKING_QUESTION.ESTADO.eq(1))
                .fetchInto(EventPostBookingQuestionRecord.class);
    }

    public void deleteByEventId(Integer eventId) {

        dsl.delete(CPANEL_EVENTO_CANAL_POST_BOOKING_QUESTION)
                .where(CPANEL_EVENTO_CANAL_POST_BOOKING_QUESTION.IDEVENTO.eq(eventId))
                .execute();
    }

    public void deleteByChannelId(Integer channelId) {

        dsl.delete(CPANEL_EVENTO_CANAL_POST_BOOKING_QUESTION)
                .where(CPANEL_EVENTO_CANAL_POST_BOOKING_QUESTION.IDCANAL.eq(channelId))
                .execute();
    }
}
