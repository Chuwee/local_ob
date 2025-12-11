package es.onebox.event.surcharges.product.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSurchargeRangePromotionProductRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_SURCHARGE_RANGE_PROMOTION_PRODUCT;

@Repository
public class RangeProductSurchargePromotionDao extends DaoImpl<CpanelSurchargeRangePromotionProductRecord, Integer> {

    protected RangeProductSurchargePromotionDao() {
        super(CPANEL_SURCHARGE_RANGE_PROMOTION_PRODUCT);
    }

    public List<CpanelRangoRecord> getByProductId(int productId) {
        return dsl.select(CPANEL_RANGO.fields())
                .from(CPANEL_RANGO)
                .innerJoin(CPANEL_SURCHARGE_RANGE_PROMOTION_PRODUCT)
                .on(CPANEL_RANGO.IDRANGO.eq(CPANEL_SURCHARGE_RANGE_PROMOTION_PRODUCT.RANGEID))
                .where(CPANEL_SURCHARGE_RANGE_PROMOTION_PRODUCT.PRODUCTID.eq(productId))
                .orderBy(CPANEL_RANGO.RANGOMINIMO)
                .fetch()
                .into(CpanelRangoRecord.class);
    }

    public int deleteByProductId(int productId) {
        return this.dsl.deleteFrom(CPANEL_SURCHARGE_RANGE_PROMOTION_PRODUCT)
                .where(CPANEL_SURCHARGE_RANGE_PROMOTION_PRODUCT.PRODUCTID.eq(productId))
                .execute();
    }
}