package es.onebox.event.products.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelProductVariantSessionRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Record2;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_VARIANT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PRODUCT_VARIANT_SESSION;

@Repository
public class ProductVariantSessionDao extends DaoImpl<CpanelProductVariantSessionRecord, Record2<Integer, Integer>> {

    protected ProductVariantSessionDao() {
        super(CPANEL_PRODUCT_VARIANT_SESSION);
    }

    public List<CpanelProductVariantSessionRecord> getProductVariantSessions(Long productId) {
        return dsl
                .select()
                .from(CPANEL_PRODUCT_VARIANT_SESSION)
                .innerJoin(CPANEL_PRODUCT_VARIANT).on(CPANEL_PRODUCT_VARIANT.VARIANTID.eq(CPANEL_PRODUCT_VARIANT_SESSION.VARIANTID))
                .where(CPANEL_PRODUCT_VARIANT.PRODUCTID.eq(productId.intValue()))
                .fetchInto(CpanelProductVariantSessionRecord.class);
    }

    public List<CpanelProductVariantSessionRecord> getProductVariantSessions(Long productId, List<Integer> sessions) {
        return dsl
                .select()
                .from(CPANEL_PRODUCT_VARIANT_SESSION)
                .innerJoin(CPANEL_PRODUCT_VARIANT).on(CPANEL_PRODUCT_VARIANT.VARIANTID.eq(CPANEL_PRODUCT_VARIANT_SESSION.VARIANTID))
                .where(CPANEL_PRODUCT_VARIANT.PRODUCTID.eq(productId.intValue()))
                .and(CPANEL_PRODUCT_VARIANT_SESSION.SESSIONID.in(sessions))
                .fetchInto(CpanelProductVariantSessionRecord.class);
    }

}
