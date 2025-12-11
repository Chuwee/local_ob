package es.onebox.event.language.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.Tables.CPANEL_DESC_POR_IDIOMA;

@Repository
public class DescPorIdiomaDao extends DaoImpl<CpanelDescPorIdiomaRecord, Integer> {
    
    public DescPorIdiomaDao() {
        super(Tables.CPANEL_DESC_POR_IDIOMA);
    }

    public CpanelDescPorIdiomaRecord getByKey(Integer langId, Integer itemId) {
        return dsl.select().from(CPANEL_DESC_POR_IDIOMA).where(CPANEL_DESC_POR_IDIOMA.IDIDIOMA.eq(langId))
                .and(CPANEL_DESC_POR_IDIOMA.IDITEM.eq(itemId)).fetchOneInto(CpanelDescPorIdiomaRecord.class);
    }

    public int upsert(Integer itemId, Integer langId, String description) {
        return dsl
                .insertInto(Tables.CPANEL_DESC_POR_IDIOMA, CPANEL_DESC_POR_IDIOMA.IDITEM,
                        CPANEL_DESC_POR_IDIOMA.IDIDIOMA, CPANEL_DESC_POR_IDIOMA.DESCRIPCION)
                .values(itemId, langId, description)
                .onDuplicateKeyUpdate().set(CPANEL_DESC_POR_IDIOMA.DESCRIPCION, description).execute();
    }

    public int upsert(Integer itemId, Integer langId, String description, String altText) {
        return dsl
                .insertInto(Tables.CPANEL_DESC_POR_IDIOMA,
                        CPANEL_DESC_POR_IDIOMA.IDITEM,
                        CPANEL_DESC_POR_IDIOMA.IDIDIOMA,
                        CPANEL_DESC_POR_IDIOMA.DESCRIPCION,
                        CPANEL_DESC_POR_IDIOMA.ALTTEXT)
                .values(itemId, langId, description, altText)
                .onDuplicateKeyUpdate()
                .set(CPANEL_DESC_POR_IDIOMA.DESCRIPCION, description)
                .set(CPANEL_DESC_POR_IDIOMA.ALTTEXT, altText)
                .execute();
    }

    public int delete(Integer itemId, Integer langId) {
        CpanelDescPorIdiomaRecord record = new CpanelDescPorIdiomaRecord();
        record.setIdidioma(langId);
        record.setIditem(itemId);
        return this.delete(record);
    }
}
