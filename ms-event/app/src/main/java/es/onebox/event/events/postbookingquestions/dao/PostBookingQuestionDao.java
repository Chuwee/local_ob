package es.onebox.event.events.postbookingquestions.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelPostBookingQuestionRecord;

import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.tables.CpanelPostBookingQuestion.CPANEL_POST_BOOKING_QUESTION;

@Repository
public class PostBookingQuestionDao extends DaoImpl<CpanelPostBookingQuestionRecord, Integer> {

    protected PostBookingQuestionDao() {
        super(CPANEL_POST_BOOKING_QUESTION );
    }

    public List<CpanelPostBookingQuestionRecord> getActivePostBookingQuestions() {

        return dsl
                .select()
                .from(CPANEL_POST_BOOKING_QUESTION)
                .where(CPANEL_POST_BOOKING_QUESTION.ESTADO.eq(1))
                .fetchInto(CpanelPostBookingQuestionRecord.class);
    }

    public void changePostBookingQuestionsStatus() {

        dsl.update(CPANEL_POST_BOOKING_QUESTION)
                .set(CPANEL_POST_BOOKING_QUESTION.ESTADO, 0)
                .execute();
    }

    public void upsert(CpanelPostBookingQuestionRecord record) {

        dsl.insertInto(CPANEL_POST_BOOKING_QUESTION, CPANEL_POST_BOOKING_QUESTION.IDEXTERNO, CPANEL_POST_BOOKING_QUESTION.ESTADO)
                .values(record.getIdexterno(), record.getEstado())
                .onDuplicateKeyUpdate()
                .set(CPANEL_POST_BOOKING_QUESTION.ESTADO, record.getEstado())
                .execute();
    }
}
