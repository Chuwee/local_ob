package es.onebox.event.entity.templateszones.dao;

import es.onebox.event.entity.templateszones.enums.TemplatesZonesStatus;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelTemplatesZonesEntity;
import es.onebox.jooq.cpanel.tables.records.CpanelTemplatesZonesEntityRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_TEMPLATES_ZONES_ENTITY;

@Repository
public class EntityTemplatesZonesDao extends DaoImpl<CpanelTemplatesZonesEntityRecord, Integer> {

    private static final CpanelTemplatesZonesEntity templatesZones = Tables.CPANEL_TEMPLATES_ZONES_ENTITY.as("templatesZones");

    public EntityTemplatesZonesDao() {
        super(CPANEL_TEMPLATES_ZONES_ENTITY);
    }

    public List<CpanelTemplatesZonesEntityRecord> getTemplatesZones(Integer entityId, List<Integer> templateZonesIds) {
        return dsl.select(templatesZones.fields())
                .from(templatesZones)
                .where(templatesZones.ENTITYID.eq(entityId))
                .and(templatesZones.TEMPLATEZONEID.in(templateZonesIds))
                .and(templatesZones.STATUS.eq(TemplatesZonesStatus.ENABLED.getId().byteValue()))
                .fetchInto(CpanelTemplatesZonesEntityRecord.class);
    }
}
