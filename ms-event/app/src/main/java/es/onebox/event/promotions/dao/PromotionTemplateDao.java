package es.onebox.event.promotions.dao;

import es.onebox.event.priceengine.simulation.record.PromotionCommElemRecord;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelDescPorIdioma;
import es.onebox.jooq.cpanel.tables.CpanelIdioma;
import es.onebox.jooq.cpanel.tables.records.CpanelPlantillaPromocionRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static es.onebox.jooq.cpanel.Tables.CPANEL_PLANTILLA_PROMOCION;


@Repository
public class PromotionTemplateDao extends DaoImpl<CpanelPlantillaPromocionRecord, Integer> {

    protected PromotionTemplateDao() {
        super(CPANEL_PLANTILLA_PROMOCION);
    }

    protected PromotionTemplateDao(Table<CpanelPlantillaPromocionRecord> table) {
        super(table);
    }

    public Map<Integer, List<PromotionCommElemRecord>> getCommunicationElementsByPromotionTemplateIds(final List<Integer> promotionTemplateIds) {
        CpanelDescPorIdioma descForName = Tables.CPANEL_DESC_POR_IDIOMA.as("CPANEL_DESC_POR_IDIOMA");
        CpanelDescPorIdioma descForDescription = Tables.CPANEL_DESC_POR_IDIOMA.as("ids2");
        CpanelIdioma language = Tables.CPANEL_IDIOMA;
        return dsl.select(
                CPANEL_PLANTILLA_PROMOCION.IDPLANTILLAPROMOCION,
                language.CODIGO,
                descForName.DESCRIPCION,
                descForDescription.DESCRIPCION
        ).from(CPANEL_PLANTILLA_PROMOCION)
                .innerJoin(descForName).on(descForName.IDITEM.eq(CPANEL_PLANTILLA_PROMOCION.NOMBREDESCRIPTIVO))
                .innerJoin(language).on(language.IDIDIOMA.eq(descForName.IDIDIOMA))
                .leftOuterJoin(descForDescription).on(descForDescription.IDITEM.eq(CPANEL_PLANTILLA_PROMOCION.DESCRIPCION).and(descForDescription.IDIDIOMA.eq(descForName.IDIDIOMA)))
                .where(CPANEL_PLANTILLA_PROMOCION.IDPLANTILLAPROMOCION.in(promotionTemplateIds))
                .fetchGroups(CPANEL_PLANTILLA_PROMOCION.IDPLANTILLAPROMOCION, (RecordMapper<Record, PromotionCommElemRecord>) record -> {
                    PromotionCommElemRecord result = new PromotionCommElemRecord();
                    result.setLanguageCode(record.getValue(language.CODIGO));
                    result.setName(record.getValue(descForName.DESCRIPCION));
                    result.setDescription(record.getValue(descForDescription.DESCRIPCION));
                    return result;
                });
    }
}
