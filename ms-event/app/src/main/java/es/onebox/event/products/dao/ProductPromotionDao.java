package es.onebox.event.products.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelPromocionProductoRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Record2;
import org.springframework.stereotype.Repository;

import java.util.List;

import static es.onebox.jooq.cpanel.Tables.CPANEL_PROMOCION_PRODUCTO;

@Repository
public class ProductPromotionDao extends DaoImpl<CpanelPromocionProductoRecord, Record2<Integer, Integer>> {

    public ProductPromotionDao() {
        super(CPANEL_PROMOCION_PRODUCTO);
    }

    public List<CpanelPromocionProductoRecord> getEnabledProductPromotions(Long productId) {
        return dsl.selectFrom(CPANEL_PROMOCION_PRODUCTO)
                .where(CPANEL_PROMOCION_PRODUCTO.IDPRODUCTO.eq(productId.intValue())
                        .and(CPANEL_PROMOCION_PRODUCTO.ACTIVADA.eq((byte) 1))
                        .and(CPANEL_PROMOCION_PRODUCTO.ESTADO.eq((byte) 1))
                )
                .fetch();
    }

}
