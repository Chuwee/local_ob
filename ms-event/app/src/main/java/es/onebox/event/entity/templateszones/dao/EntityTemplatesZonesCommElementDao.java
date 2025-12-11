package es.onebox.event.entity.templateszones.dao;

import es.onebox.event.entity.templateszones.enums.TemplateZonesTagType;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelTemplatesZonesElementsCom;
import es.onebox.jooq.cpanel.tables.records.CpanelTemplatesZonesElementsComRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EntityTemplatesZonesCommElementDao extends DaoImpl<CpanelTemplatesZonesElementsComRecord, Integer> {

    private static final CpanelTemplatesZonesElementsCom comElementTemplateZones = Tables.CPANEL_TEMPLATES_ZONES_ELEMENTS_COM;

    public EntityTemplatesZonesCommElementDao() {
        super(Tables.CPANEL_TEMPLATES_ZONES_ELEMENTS_COM);
    }

    public List<CpanelTemplatesZonesElementsComRecord> getCommElements(List<Integer> templatesZonesIds) {
        return dsl.select()
                .from(comElementTemplateZones)
                .where(comElementTemplateZones.TEMPLATEZONEID.in(templatesZonesIds))
                .and(comElementTemplateZones.TAGID.in(TemplateZonesTagType.NAME.getId(),TemplateZonesTagType.DESCRIPTION.getId()))
                .fetchInto(CpanelTemplatesZonesElementsComRecord.class);
    }
}
