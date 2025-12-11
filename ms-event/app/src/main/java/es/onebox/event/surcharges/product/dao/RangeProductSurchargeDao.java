package es.onebox.event.surcharges.product.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelRangoRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSurchargeRangeProductRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_RANGO;
import static es.onebox.jooq.cpanel.Tables.CPANEL_SURCHARGE_RANGE_PRODUCT;

@Repository
public class RangeProductSurchargeDao extends DaoImpl<CpanelSurchargeRangeProductRecord, Integer> {

    protected RangeProductSurchargeDao() {
        super(CPANEL_SURCHARGE_RANGE_PRODUCT);
    }

    public List<CpanelRangoRecord> getByProductId(Integer productId) {
        return dsl.select(CPANEL_RANGO.fields())
                .from(CPANEL_RANGO)
                .innerJoin(CPANEL_SURCHARGE_RANGE_PRODUCT)
                .on(CPANEL_RANGO.IDRANGO.eq(CPANEL_SURCHARGE_RANGE_PRODUCT.RANGEID))
                .where(CPANEL_SURCHARGE_RANGE_PRODUCT.PRODUCTID.eq(productId))
                .orderBy(CPANEL_RANGO.RANGOMINIMO)
                .fetch()
                .into(CpanelRangoRecord.class);
    }

    public void deleteByProductId(Integer productId) {
        this.dsl.deleteFrom(CPANEL_SURCHARGE_RANGE_PRODUCT)
                .where(CPANEL_SURCHARGE_RANGE_PRODUCT.PRODUCTID.eq(productId))
                .execute();
    }
}

