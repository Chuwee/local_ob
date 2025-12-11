package es.onebox.event.events.dao;

import es.onebox.event.events.dao.record.LimiteCupoRecord;
import es.onebox.event.events.dao.record.TierRecord;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelTierRecord;
import es.onebox.jooq.dao.DaoImpl;
import es.onebox.jooq.exception.EntityNotFoundException;
import org.jooq.Condition;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_CONFIG_RECINTO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_CUPOS_CONFIG;
import static es.onebox.jooq.cpanel.Tables.CPANEL_TIER_CUPO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_TIME_ZONE_GROUP;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ZONA_PRECIOS_CONFIG;
import static es.onebox.jooq.cpanel.tables.CpanelTier.CPANEL_TIER;

@Repository
public class TierDao extends DaoImpl<CpanelTierRecord, Integer> {


    protected TierDao() {
        super(Tables.CPANEL_TIER);
    }

    public Long countByZoneAndName(int zoneId, String name) {
        return dsl.selectCount()
                .from(CPANEL_TIER)
                .where(CPANEL_TIER.IDZONA.eq(zoneId))
                .and(CPANEL_TIER.NOMBRE.like(name))
                .fetchOne(0, Long.class);
    }

    public TierRecord getTier(int tierId) {
        try {
            ResultSet rs = dsl.select(CPANEL_TIER.IDTIER, CPANEL_TIER.IDZONA, CPANEL_TIER.NOMBRE, CPANEL_TIER.FECHA_INICIO,
                            CPANEL_TIER.PRECIO, CPANEL_TIER.VENTA, CPANEL_TIER.LIMITE, CPANEL_TIER.CONDICION,
                            CPANEL_ZONA_PRECIOS_CONFIG.DESCRIPCION, CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION,
                            CPANEL_TIER.TIMEZONE, CPANEL_TIME_ZONE_GROUP.OLSONID, CPANEL_TIME_ZONE_GROUP.RAWOFFSETMINS,
                            CPANEL_TIME_ZONE_GROUP.DISPLAYNAME, CPANEL_TIER_CUPO.IDCUPO, CPANEL_TIER_CUPO.LIMITE.as("limite_cupo"),
                            CPANEL_CUPOS_CONFIG.DESCRIPCION.as("desc_cupo"))
                    .from(CPANEL_TIER)
                    .join(CPANEL_ZONA_PRECIOS_CONFIG).on(CPANEL_TIER.IDZONA.eq(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA))
                    .join(CPANEL_TIME_ZONE_GROUP).on(CPANEL_TIER.TIMEZONE.eq(CPANEL_TIME_ZONE_GROUP.ZONEID))
                    .leftJoin(CPANEL_TIER_CUPO).on(CPANEL_TIER.IDTIER.eq(CPANEL_TIER_CUPO.IDTIER))
                    .leftJoin(CPANEL_CUPOS_CONFIG).on(CPANEL_TIER_CUPO.IDCUPO.eq(CPANEL_CUPOS_CONFIG.IDCUPO))
                    .where(CPANEL_TIER.IDTIER.eq(tierId))
                    .fetchResultSet();
            TierRecord result = null;
            while (rs.next()) {
                if (result == null) {
                    result = new TierRecord();
                    result.setIdtier(rs.getInt(CPANEL_TIER.IDTIER.getName()));
                    result.setIdzona(rs.getInt(CPANEL_TIER.IDZONA.getName()));
                    result.setNombre(rs.getString(CPANEL_TIER.NOMBRE.getName()));
                    result.setFechaInicio(rs.getTimestamp(CPANEL_TIER.FECHA_INICIO.getName()));
                    result.setPrecio(rs.getDouble(CPANEL_TIER.PRECIO.getName()));
                    result.setVenta(rs.getByte(CPANEL_TIER.VENTA.getName()));
                    result.setLimite(rs.getInt(CPANEL_TIER.LIMITE.getName()));
                    if (rs.wasNull()) {
                        result.setLimite(null);
                    }
                    result.setCondicion(rs.getInt(CPANEL_TIER.CONDICION.getName()));
                    result.setPriceTypeName(rs.getString(CPANEL_ZONA_PRECIOS_CONFIG.DESCRIPCION.getName()));
                    result.setVenueTemplateId(rs.getInt(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.getName()));
                    result.setTimezone(rs.getInt(CPANEL_TIER.TIMEZONE.getName()));
                    result.setTimeZoneOlsonId(rs.getString(CPANEL_TIME_ZONE_GROUP.OLSONID.getName()));
                    result.setTimeZoneOffset(rs.getInt(CPANEL_TIME_ZONE_GROUP.RAWOFFSETMINS.getName()));
                    result.setTimeZoneName(rs.getString(CPANEL_TIME_ZONE_GROUP.DISPLAYNAME.getName()));
                }
                Integer idCupo = rs.getInt(CPANEL_TIER_CUPO.IDCUPO.getName());
                if (!rs.wasNull()) {
                    if (result.getLimitesCupo() == null) {
                        result.setLimitesCupo(new ArrayList<>());
                    }
                    LimiteCupoRecord limiteCupo = new LimiteCupoRecord();
                    limiteCupo.setIdcupo(idCupo);
                    limiteCupo.setLimite(rs.getInt("limite_cupo"));
                    limiteCupo.setDescripcion("desc_cupo");
                    result.getLimitesCupo().add(limiteCupo);
                }
            }
            return result;
        } catch (EntityNotFoundException | SQLException e) {
            return null;
        }
    }


    public CpanelTierRecord findByZoneAndStartDate(int zoneId, Timestamp startDate) {
            return dsl.select(CPANEL_TIER.fields())
                    .from(CPANEL_TIER)
                    .where(CPANEL_TIER.IDZONA.eq(zoneId))
                    .and(CPANEL_TIER.FECHA_INICIO.eq(startDate))
                    .fetchOneInto(CpanelTierRecord.class);
    }

    public Long findTierZoneId(Integer tierId) {
            return dsl.select(CPANEL_TIER.IDZONA)
                    .from(CPANEL_TIER)
                    .where(CPANEL_TIER.IDTIER.eq(tierId))
                    .fetchOneInto(Long.class);
    }

    public List<TierRecord> findByEventId(int eventId, Integer venueTemplateId, Integer limit, Integer offset) {
        SelectConditionStep query = dsl.select(CPANEL_TIER.IDTIER, CPANEL_TIER.IDZONA, CPANEL_TIER.NOMBRE,
                        CPANEL_TIER.FECHA_INICIO, CPANEL_TIER.PRECIO, CPANEL_TIER.VENTA, CPANEL_TIER.LIMITE, CPANEL_TIER.CONDICION,
                        CPANEL_ZONA_PRECIOS_CONFIG.DESCRIPCION, CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION,
                        CPANEL_TIER.TIMEZONE, CPANEL_TIME_ZONE_GROUP.OLSONID, CPANEL_TIME_ZONE_GROUP.RAWOFFSETMINS,
                        CPANEL_TIME_ZONE_GROUP.DISPLAYNAME)
                .from(CPANEL_TIER)
                .join(CPANEL_ZONA_PRECIOS_CONFIG).on(CPANEL_TIER.IDZONA.eq(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA))
                .join(CPANEL_CONFIG_RECINTO).on(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(CPANEL_CONFIG_RECINTO.IDCONFIGURACION))
                .join(CPANEL_TIME_ZONE_GROUP).on(CPANEL_TIER.TIMEZONE.eq(CPANEL_TIME_ZONE_GROUP.ZONEID))
                .where(builderWhereClause(eventId, venueTemplateId));
        if (limit != null) {
            query.limit(limit);
        }
        if (offset != null) {
            query.offset(offset);
        }
        return query.fetch((RecordMapper<Record, TierRecord>) this::mapToTierRecord);
    }

    public List<CpanelTierRecord> getByEventId(int eventId) {
        SelectConditionStep query = dsl.select(CPANEL_TIER.fields())
                .from(CPANEL_TIER)
                .join(CPANEL_ZONA_PRECIOS_CONFIG).on(CPANEL_TIER.IDZONA.eq(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA))
                .join(CPANEL_CONFIG_RECINTO).on(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(CPANEL_CONFIG_RECINTO.IDCONFIGURACION))
                .where(builderWhereClause(eventId, null));
        return query.fetchInto(CpanelTierRecord.class);
    }

    public Long countByEventId(int eventId, Integer venueTemplateId) {
        return dsl.selectCount()
                .from(CPANEL_TIER)
                .join(CPANEL_ZONA_PRECIOS_CONFIG).on(CPANEL_TIER.IDZONA.eq(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA))
                .join(CPANEL_CONFIG_RECINTO).on(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(CPANEL_CONFIG_RECINTO.IDCONFIGURACION))
                .where(builderWhereClause(eventId, venueTemplateId))
                .fetchOne(0, Long.class);
    }

    public void delete(int tierId) {
        dsl.deleteFrom(CPANEL_TIER).where(CPANEL_TIER.IDTIER.eq(tierId)).execute();
    }

    private Condition builderWhereClause(int eventId, Integer venueTemplateId) {
        Condition conditions = DSL.trueCondition();
        conditions = conditions.and(CPANEL_CONFIG_RECINTO.IDEVENTO.eq(eventId));
        if (venueTemplateId != null) {
            conditions = conditions.and(CPANEL_CONFIG_RECINTO.IDCONFIGURACION.eq(venueTemplateId));
        }
        return conditions;
    }

    public List<TierRecord> findByVenueTemplate(int venueTemplateId) {
        return dsl.select(CPANEL_TIER.IDTIER, CPANEL_TIER.IDZONA, CPANEL_TIER.NOMBRE, CPANEL_TIER.FECHA_INICIO,
                        CPANEL_TIER.PRECIO, CPANEL_TIER.VENTA, CPANEL_TIER.CONDICION, CPANEL_ZONA_PRECIOS_CONFIG.DESCRIPCION,
                        CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION, CPANEL_TIER.LIMITE, CPANEL_TIER.TIMEZONE,
                        CPANEL_TIME_ZONE_GROUP.OLSONID, CPANEL_TIME_ZONE_GROUP.RAWOFFSETMINS, CPANEL_TIME_ZONE_GROUP.DISPLAYNAME)
                .from(CPANEL_TIER)
                .join(CPANEL_ZONA_PRECIOS_CONFIG).on(CPANEL_TIER.IDZONA.eq(CPANEL_ZONA_PRECIOS_CONFIG.IDZONA))
                .join(CPANEL_TIME_ZONE_GROUP).on(CPANEL_TIER.TIMEZONE.eq(CPANEL_TIME_ZONE_GROUP.ZONEID))
                .where(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION.eq(venueTemplateId))
                .fetch(this::mapToTierRecord);
    }

    public List<CpanelTierRecord> findByPriceType(int priceTypeId) {
        return dsl.select(CPANEL_TIER.fields())
                .from(CPANEL_TIER)
                .where(CPANEL_TIER.IDZONA.eq(priceTypeId))
                .fetchInto(CpanelTierRecord.class);
    }

    public int bulkDelete(List<? extends CpanelTierRecord> tiers) {
        List<Integer> tierIds = tiers.stream().map(CpanelTierRecord::getIdtier).collect(Collectors.toList());
        return dsl.deleteFrom(CPANEL_TIER)
                .where(CPANEL_TIER.IDTIER.in(tierIds))
                .execute();
    }

    private TierRecord mapToTierRecord(Record record) {
        TierRecord r = new TierRecord();
        r.setIdtier(record.get(CPANEL_TIER.IDTIER));
        r.setIdzona(record.get(CPANEL_TIER.IDZONA));
        r.setPriceTypeName(record.get(CPANEL_ZONA_PRECIOS_CONFIG.DESCRIPCION));
        r.setNombre(record.get(CPANEL_TIER.NOMBRE));
        r.setFechaInicio(record.get(CPANEL_TIER.FECHA_INICIO));
        r.setPrecio(record.get(CPANEL_TIER.PRECIO));
        r.setVenta(record.get(CPANEL_TIER.VENTA));
        r.setLimite(record.get(CPANEL_TIER.LIMITE));
        r.setCondicion(record.get(CPANEL_TIER.CONDICION));
        r.setVenueTemplateId(record.get(CPANEL_ZONA_PRECIOS_CONFIG.IDCONFIGURACION));
        r.setTimezone(record.get(CPANEL_TIER.TIMEZONE));
        r.setTimeZoneOlsonId(record.get(CPANEL_TIME_ZONE_GROUP.OLSONID));
        r.setTimeZoneName(record.get(CPANEL_TIME_ZONE_GROUP.DISPLAYNAME));
        r.setTimeZoneOffset(record.get(CPANEL_TIME_ZONE_GROUP.RAWOFFSETMINS));
        return r;
    }

    public List<CpanelTierRecord> getByPriceType(int priceTypeId) {
        return dsl.select(CPANEL_TIER.fields())
                .from(Tables.CPANEL_TIER)
                .where(Tables.CPANEL_TIER.IDZONA.eq(priceTypeId))
                .fetchInto(CpanelTierRecord.class);
    }

    public List<CpanelTierRecord> getByIds(Collection<Integer> tierIds) {
        return dsl.select(CPANEL_TIER.fields())
                .from(CPANEL_TIER)
                .where(CPANEL_TIER.IDTIER.in(tierIds))
                .fetchInto(CpanelTierRecord.class);
    }
}
