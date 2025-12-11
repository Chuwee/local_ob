package es.onebox.event.packs.dao;

import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelCanal;
import es.onebox.jooq.cpanel.tables.CpanelEntidad;
import es.onebox.jooq.cpanel.tables.CpanelPack;
import es.onebox.jooq.cpanel.tables.CpanelPackCanalSolicitudVenta;
import es.onebox.jooq.cpanel.tables.records.CpanelPackCanalSolicitudVentaRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Field;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PackChannelSaleRequestDao extends DaoImpl<CpanelPackCanalSolicitudVentaRecord, Integer> {

    private static final String ALIAS_CHANNEL_TABLE = "channel";
    private static final String ALIAS_PACK_TABLE = "pack";
    private static final String ALIAS_CHANNEL_ENTITY_TABLE = "channelEntity";
    private static final String ALIAS_PACK_ENTITY_TABLE = "packEntity";
    private static final String ALIAS_SALE_REQUEST_PACK_TABLE = "saleRequestPack";
    private static final String ALIAS_CHANNEL_CHANNEL_ID = "channelId";
    private static final CpanelCanal channel = Tables.CPANEL_CANAL.as(ALIAS_CHANNEL_TABLE);
    private static final CpanelPack pack = Tables.CPANEL_PACK.as(ALIAS_PACK_TABLE);
    private static final CpanelEntidad packEntity = Tables.CPANEL_ENTIDAD.as(ALIAS_PACK_ENTITY_TABLE);
    private static final CpanelEntidad channelEntity = Tables.CPANEL_ENTIDAD.as(ALIAS_CHANNEL_ENTITY_TABLE);
    private static final CpanelPackCanalSolicitudVenta packSaleRequest = Tables.CPANEL_PACK_CANAL_SOLICITUD_VENTA.as(ALIAS_SALE_REQUEST_PACK_TABLE);

    protected static final Field<?>[] JOIN_FIELDS = {
            packSaleRequest.IDSOLICITUD,
            packSaleRequest.IDPACK,
            packSaleRequest.IDCANAL.as(ALIAS_CHANNEL_CHANNEL_ID),
            packSaleRequest.ESTADO,
            packSaleRequest.CREATE_DATE,
            pack.IDPACK,
            pack.NOMBRE,
            pack.IDENTIDAD,
            packEntity.NOMBRE,
            packEntity.IDENTIDAD,
            channel.NOMBRECANAL,
            channel.IDCANAL,
            channel.IDENTIDAD,
            channelEntity.NOMBRE,
            channelEntity.IDENTIDAD,
    };

    protected PackChannelSaleRequestDao() {
        super(packSaleRequest);
    }

    public List<CpanelPackCanalSolicitudVentaRecord> getPackSaleRequests(Long packId, List<Long> channelIds) {

        return dsl.select(packSaleRequest.fields())
                .from(packSaleRequest)
                .where(packSaleRequest.IDPACK.eq(packId.intValue()))
                .and(packSaleRequest.IDCANAL.in(channelIds))
                .fetchInto(CpanelPackCanalSolicitudVentaRecord.class);
    }
}
