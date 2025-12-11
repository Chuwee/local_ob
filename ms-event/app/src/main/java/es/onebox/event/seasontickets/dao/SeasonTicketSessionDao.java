package es.onebox.event.seasontickets.dao;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.seasontickets.dao.record.SessionCapacityGenerationStatusRecord;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelConfigRecinto;
import es.onebox.jooq.cpanel.tables.CpanelEntidadRecintoConfig;
import es.onebox.jooq.cpanel.tables.CpanelRecinto;
import es.onebox.jooq.cpanel.tables.CpanelSesion;
import es.onebox.jooq.cpanel.tables.CpanelTimeZoneGroup;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelTimeZoneGroupRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Record;
import org.jooq.Record1;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.TableField;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_SESION;

@Repository
public class SeasonTicketSessionDao extends DaoImpl<CpanelSesionRecord, Integer> {

    private static final CpanelSesion sesion = CPANEL_SESION.as("sesion");
    private static final CpanelEntidadRecintoConfig entidadRecintoConfig = Tables.CPANEL_ENTIDAD_RECINTO_CONFIG.as("entidadRecintoConfig");
    private static final CpanelConfigRecinto configRecinto = Tables.CPANEL_CONFIG_RECINTO.as("configRecinto");
    private static final CpanelRecinto recinto = Tables.CPANEL_RECINTO.as("recinto");
    private static final CpanelTimeZoneGroup recintoTZ = Tables.CPANEL_TIME_ZONE_GROUP.as("recintoTZ");

    private static final TableField<CpanelTimeZoneGroupRecord, String> JOIN_VENUE_TZ_OLSON = recintoTZ.OLSONID;
    private static final TableField<CpanelTimeZoneGroupRecord, String> JOIN_VENUE_TZ_NAME = recintoTZ.DISPLAYNAME;
    private static final TableField<CpanelTimeZoneGroupRecord, Integer> JOIN_VENUE_TZ_OFFSET = recintoTZ.RAWOFFSETMINS;


    private static final SelectFieldOrAsterisk[] JOIN_FIELDS = {
            JOIN_VENUE_TZ_OLSON,
            JOIN_VENUE_TZ_NAME,
            JOIN_VENUE_TZ_OFFSET,
    };

    protected SeasonTicketSessionDao() {
        super(CPANEL_SESION);
    }

    public List<SessionRecord> searchSessionInfoByEventId(Long eventId) {
        return searchSessionInfoByEventIds(Collections.singletonList(eventId));
    }

    public List<SessionRecord> getSessionStatus(Long seasonTicketId) {

        Integer seasonTicketIdInt = Math.toIntExact(seasonTicketId);
        List<SessionRecord> sessionRecords = dsl.select(sesion.ESTADO, sesion.ISPREVIEW)
                .from(sesion)
                .where(sesion.IDEVENTO.eq(seasonTicketIdInt))
                .fetch(record -> buildSessionRecord(record, record.size()));
        if(sessionRecords.isEmpty()) {
            return new ArrayList<>();
        }
        return sessionRecords;

    }

    public List<SessionRecord> searchSessionInfoByEventIds(List<Long> eventIds) {

        List<Integer> eventIdsList = eventIds.stream().map(Math::toIntExact).collect(Collectors.toList());
        List<SessionRecord> sessionRecords = dsl.select(ArrayUtils.addAll(sesion.fields(), JOIN_FIELDS))
                .from(sesion)
                .innerJoin(entidadRecintoConfig).on(entidadRecintoConfig.IDRELACIONENTRECINTO.eq(sesion.IDRELACIONENTIDADRECINTO))
                .innerJoin(configRecinto).on(configRecinto.IDCONFIGURACION.eq(entidadRecintoConfig.IDCONFIGURACION))
                .innerJoin(recinto).on(recinto.IDRECINTO.eq(configRecinto.IDRECINTO))
                .innerJoin(recintoTZ).on(recintoTZ.ZONEID.eq(recinto.TIMEZONE))
                .where(sesion.IDEVENTO.in(eventIdsList))
                .fetch(record -> buildSessionRecord(record, record.size()));
        if (sessionRecords.isEmpty()) {
            return new ArrayList<>();
        }
        return sessionRecords;
    }

    private static SessionRecord buildSessionRecord(Record record, int fields) {
        SessionRecord session = record.into(sesion.fields()).into(SessionRecord.class);

        //Add join fields only if has been added to base session fields
        if (fields > sesion.fields().length) {
            session.setVenueTZ(record.getValue(JOIN_VENUE_TZ_OLSON));
            session.setVenueTZName(record.getValue(JOIN_VENUE_TZ_NAME));
            session.setVenueTZOffset(record.getValue(JOIN_VENUE_TZ_OFFSET));
            session.setSessionId(record.getValue(sesion.IDSESION));
        }

        return session;
    }

    public void disableBookingByEvent(Long eventId) {
        dsl.update(sesion).
                set(sesion.RESERVASACTIVAS, (byte) 0).
                where(sesion.IDEVENTO.eq(eventId.intValue())).execute();
    }

    public SessionCapacityGenerationStatusRecord getCapacityGenerationStatusBySeasonTicketId(Integer seasonTicketId) {
        Record1<Integer> result = dsl.select(CPANEL_SESION.ESTADOGENERACIONAFORO)
                .from(CPANEL_SESION)
                .where(CPANEL_SESION.IDEVENTO.eq(seasonTicketId))
                .fetchOne();
        return CommonUtils.ifNotNull(result, () -> result.into(SessionCapacityGenerationStatusRecord.class));
    }
}
