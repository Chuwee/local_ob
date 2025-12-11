package es.onebox.event.taxonomy.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelTaxonomiaBaseRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.Tables.CPANEL_TAXONOMIA_BASE;

@Repository
public class BaseTaxonomyDao extends DaoImpl<CpanelTaxonomiaBaseRecord, Integer> {

    protected BaseTaxonomyDao() {
        super(CPANEL_TAXONOMIA_BASE);
    }

    public record TaxonomyInfo(Integer id,
                               Integer parentId,
                               String code,
                               String desc) {
    }
}
