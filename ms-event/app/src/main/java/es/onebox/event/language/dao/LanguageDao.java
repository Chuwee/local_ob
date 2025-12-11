package es.onebox.event.language.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.tables.CpanelIdioma.CPANEL_IDIOMA;

@Repository
public class LanguageDao extends DaoImpl<CpanelIdiomaRecord, Integer> {

    protected LanguageDao() {
        super(Tables.CPANEL_IDIOMA);
    }

    public List<CpanelIdiomaRecord> getIdiomasByCodes(List<String> codes) {
        return dsl.select(CPANEL_IDIOMA.fields())
                .from(CPANEL_IDIOMA)
                .where(CPANEL_IDIOMA.CODIGO.in(codes))
                .fetchInto(CpanelIdiomaRecord.class);
    }
}
