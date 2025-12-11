package es.onebox.event.taxonomy.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelTaxonomiaPropiaRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.Tables.CPANEL_TAXONOMIA_PROPIA;

@Repository
public class CustomTaxonomyDao extends DaoImpl<CpanelTaxonomiaPropiaRecord, Integer> {

    protected CustomTaxonomyDao() {
        super(CPANEL_TAXONOMIA_PROPIA);
    }

    public BaseTaxonomyDao.TaxonomyInfo getTaxonomyInfo(Integer id) {
        return dsl.select(CPANEL_TAXONOMIA_PROPIA.IDTAXONOMIA, CPANEL_TAXONOMIA_PROPIA.IDTAXONOMIASUPERIOR,
                        CPANEL_TAXONOMIA_PROPIA.REFERENCIA, CPANEL_TAXONOMIA_PROPIA.DESCRIPCION)
                .from(CPANEL_TAXONOMIA_PROPIA)
                .where(CPANEL_TAXONOMIA_PROPIA.IDTAXONOMIA.eq(id))
                .fetchOne()
                .map(r -> new BaseTaxonomyDao.TaxonomyInfo(
                        r.get(CPANEL_TAXONOMIA_PROPIA.IDTAXONOMIA),
                        r.get(CPANEL_TAXONOMIA_PROPIA.IDTAXONOMIASUPERIOR),
                        r.get(CPANEL_TAXONOMIA_PROPIA.REFERENCIA),
                        r.get(CPANEL_TAXONOMIA_PROPIA.DESCRIPCION)));
    }

}
