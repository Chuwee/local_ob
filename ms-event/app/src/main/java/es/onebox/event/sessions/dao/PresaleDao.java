package es.onebox.event.sessions.dao;

import es.onebox.event.sessions.dao.enums.PresaleStatus;
import es.onebox.event.sessions.dao.record.PresaleRecord;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelPreventa;
import es.onebox.jooq.cpanel.tables.CpanelPreventaCanal;
import es.onebox.jooq.cpanel.tables.CpanelPreventaCustomType;
import es.onebox.jooq.cpanel.tables.CpanelPreventaLoyaltyProgram;
import es.onebox.jooq.cpanel.tables.records.CpanelPreventaRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
public class PresaleDao extends DaoImpl<CpanelPreventaRecord, Integer> {

    private static final CpanelPreventa presaleTable = Tables.CPANEL_PREVENTA;
    private static final CpanelPreventaCanal presaleChannelTable = Tables.CPANEL_PREVENTA_CANAL;
    private static final CpanelPreventaCustomType presaleCustomTypeTable = Tables.CPANEL_PREVENTA_CUSTOM_TYPE;
    private static final CpanelPreventaLoyaltyProgram presaleLoyaltyProgramTable = Tables.CPANEL_PREVENTA_LOYALTY_PROGRAM;
    public static final String CHANNELS_COLUMN = "channels";
    public static final String CUSTOM_TYPES_COLUMN = "customTypes";
    public static final String POINTS_COLUMN = "points";

    protected PresaleDao() {
        super(presaleTable);
    }

    public Map<Long, List<PresaleRecord>> getPresalesBySessionIds(List<Long> sessionIds) {
        List<Field> fields = new ArrayList<>(Arrays.asList(presaleTable.fields()));
        fields.add(DSL.field("group_concat(distinct {0}, ',')", SQLDataType.VARCHAR, presaleChannelTable.IDCANAL).as(CHANNELS_COLUMN));
        fields.add(DSL.field("group_concat(distinct {0}, ',')", SQLDataType.VARCHAR, presaleCustomTypeTable.CUSTOMTYPEID).as(CUSTOM_TYPES_COLUMN));
        fields.add(presaleLoyaltyProgramTable.POINTS.as(POINTS_COLUMN));
        return dsl.select(fields)
                .from(presaleTable)
                .leftJoin(presaleChannelTable).on(presaleTable.IDPREVENTA.eq(presaleChannelTable.IDPREVENTA))
                .leftJoin(presaleCustomTypeTable).on(presaleTable.IDPREVENTA.eq(presaleCustomTypeTable.PRESALESID))
                .leftJoin(presaleLoyaltyProgramTable).on(presaleTable.IDPREVENTA.eq(presaleLoyaltyProgramTable.IDPREVENTA))
                .where(presaleTable.IDSESION.in(sessionIds).and(presaleTable.ESTADO.eq(PresaleStatus.ENABLED.getId())))
                .groupBy(presaleTable.IDPREVENTA)
                .fetchGroups(r -> r.get(presaleTable.IDSESION).longValue(), this::buildPresaleRecord);
    }

    public List<CpanelPreventaRecord> findSessionPresalesBySessionId(Long sessionId) {
        return dsl.select(presaleTable.fields())
                .from(presaleTable)
                .where(presaleTable.IDSESION.eq(sessionId.intValue()))
                .and(presaleTable.ESTADO.ne(PresaleStatus.DELETED.getId()))
                .fetch().into(CpanelPreventaRecord.class);
    }

    public void deleteByPresaleId(Integer presaleId) {
        dsl.update(presaleTable)
                .set(presaleTable.ESTADO, 0)
                .where(presaleTable.IDPREVENTA.eq(presaleId))
                .execute();
    }
    private PresaleRecord buildPresaleRecord(Record presaleConfigRecord) {
        PresaleRecord result = new PresaleRecord();
        result.setEstado(presaleConfigRecord.get(presaleTable.ESTADO));
        result.setNombre(presaleConfigRecord.get(presaleTable.NOMBRE));
        result.setIdsesion(presaleConfigRecord.get(presaleTable.IDSESION));
        result.setIdpreventa(presaleConfigRecord.get(presaleTable.IDPREVENTA));
        result.setIdvalidador(presaleConfigRecord.get(presaleTable.IDVALIDADOR));
        result.setTipovalidador(presaleConfigRecord.get(presaleTable.TIPOVALIDADOR));
        result.setFechafinpreventa(presaleConfigRecord.get(presaleTable.FECHAFINPREVENTA));
        result.setFechainiciopreventa(presaleConfigRecord.get(presaleTable.FECHAINICIOPREVENTA));
        result.setTiporangovalidacion(presaleConfigRecord.get(presaleTable.TIPORANGOVALIDACION));
        if (presaleConfigRecord.get(presaleTable.LIMITEACOMPANANTES) != null) {
            result.setLimiteacompanantes(presaleConfigRecord.get(presaleTable.LIMITEACOMPANANTES));
        }
        if (presaleConfigRecord.get(presaleTable.LIMITETICKETSSOCIO) != null) {
            result.setLimiteticketssocio(presaleConfigRecord.get(presaleTable.LIMITETICKETSSOCIO));
        }
        if (presaleConfigRecord.get(DSL.field(CHANNELS_COLUMN)) != null ) {
            String channels = presaleConfigRecord.get(DSL.field(CHANNELS_COLUMN)).toString();
            result.setChannelIds(Arrays.stream(channels.split(",")).filter(StringUtils::isNotBlank).map(Long::valueOf).toList());
        }
        if (presaleConfigRecord.get(DSL.field(CUSTOM_TYPES_COLUMN)) != null ) {
            String customTypes = presaleConfigRecord.get(DSL.field(CUSTOM_TYPES_COLUMN)).toString();
            result.setCustomerTypeIds(Arrays.stream(customTypes.split(",")).filter(StringUtils::isNotBlank).map(Long::valueOf).toList());
        }
        if (presaleConfigRecord.get(DSL.field(POINTS_COLUMN)) != null ) {
            Long points = presaleConfigRecord.get(DSL.field(POINTS_COLUMN), Long.class);
            result.setPoints(points);
        }
        return result;
    }
}
