package es.onebox.event.seasontickets.dao;

import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dao.record.RenewalRecord;
import es.onebox.jooq.cpanel.tables.CpanelEvento;
import es.onebox.jooq.cpanel.tables.CpanelExternalEvent;
import es.onebox.jooq.cpanel.tables.CpanelRenewal;
import es.onebox.jooq.cpanel.tables.records.CpanelRenewalRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.Tables.CPANEL_EVENTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_EXTERNAL_EVENT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_RENEWAL;

@Repository
public class RenewalDao extends DaoImpl<CpanelRenewalRecord, Integer> {

    private static final CpanelRenewal renewal = CPANEL_RENEWAL.as("renewal");
    private static final CpanelEvento evento = CPANEL_EVENTO.as("evento");
    private static final CpanelExternalEvent externalEvent = CPANEL_EXTERNAL_EVENT.as("externalEvent");

    private static final Field<String> JOIN_EVENT_NAME = evento.NOMBRE.as("eventName");
    private static final Field<String> JOIN_EXTERNAL_EVENT_NAME = externalEvent.NAME.as("externalEventName");

    private static final SelectFieldOrAsterisk[] JOIN_FIELDS = {
            JOIN_EVENT_NAME, JOIN_EXTERNAL_EVENT_NAME
    };

    protected RenewalDao() {
        super(CPANEL_RENEWAL);
    }

    public RenewalRecord getRenewalData(Long seasonTicketId) {
        SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(renewal.fields(), JOIN_FIELDS);

        SelectJoinStep<Record> query =
                dsl.select(fields)
                        .from(renewal)
                        .leftOuterJoin(evento).on(evento.IDEVENTO.eq(renewal.IDEVENTOORIGINAL))
                        .leftOuterJoin(externalEvent).on(externalEvent.INTERNALID.eq(renewal.IDEVENTOEXTERNOORIGINAL));

        Condition conditions = DSL.trueCondition();
        conditions = conditions.and(renewal.IDEVENTO.eq(seasonTicketId.intValue()));
        query.where(conditions);

        return query.fetchOne(this::buildRenewalData);
    }

    private RenewalRecord buildRenewalData(Record record) {
        RenewalRecord renewalRecord = record.into(RenewalRecord.class);
        renewalRecord.setOriginSeasonTicketName(record.getValue(JOIN_EVENT_NAME));
        renewalRecord.setOriginExternalEventName(record.getValue(JOIN_EXTERNAL_EVENT_NAME));
        renewalRecord.setIsExternalEvent(ConverterUtils.isByteAsATrue(record.getValue(renewal.ISEXTERNALEVENT)));
        return renewalRecord;
    }
}
