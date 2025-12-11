package es.onebox.event.packs.dao;

import es.onebox.event.packs.dao.domain.ItemPackPriceInfoRecord;
import es.onebox.event.packs.enums.PackItemType;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelAsignacionZonaPrecios;
import es.onebox.jooq.cpanel.tables.CpanelConfigRecinto;
import es.onebox.jooq.cpanel.tables.CpanelEntidadRecintoConfig;
import es.onebox.jooq.cpanel.tables.CpanelPackItem;
import es.onebox.jooq.cpanel.tables.CpanelPackZonaPrecioMapping;
import es.onebox.jooq.cpanel.tables.CpanelSesion;
import es.onebox.jooq.cpanel.tables.CpanelTarifa;
import es.onebox.jooq.cpanel.tables.CpanelZonaPreciosConfig;
import es.onebox.jooq.cpanel.tables.records.CpanelPackZonaPrecioMappingRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PackPriceTypeMappingDao extends DaoImpl<CpanelPackZonaPrecioMappingRecord, Integer> {

    private static final CpanelPackZonaPrecioMapping packPriceTypeMapping = Tables.CPANEL_PACK_ZONA_PRECIO_MAPPING;
    private static final CpanelPackItem packItem = Tables.CPANEL_PACK_ITEM;
    private static final CpanelAsignacionZonaPrecios priceZoneRelation = Tables.CPANEL_ASIGNACION_ZONA_PRECIOS;
    private static final CpanelZonaPreciosConfig priceZoneConfig = Tables.CPANEL_ZONA_PRECIOS_CONFIG;
    private static final CpanelSesion session = Tables.CPANEL_SESION;
    private static final CpanelEntidadRecintoConfig entityVenueConfig = Tables.CPANEL_ENTIDAD_RECINTO_CONFIG;
    private static final CpanelConfigRecinto venueConfig = Tables.CPANEL_CONFIG_RECINTO;
    private static final CpanelTarifa rate = Tables.CPANEL_TARIFA;

    protected PackPriceTypeMappingDao() {
        super(packPriceTypeMapping);
    }

    public List<CpanelPackZonaPrecioMappingRecord> getPackSourceItemMappings(Integer sourcePackItemId) {
        return dsl.select(packPriceTypeMapping.fields())
                .from(packPriceTypeMapping)
                .where(packPriceTypeMapping.IDSOURCEPACKITEM.eq(sourcePackItemId))
                .fetchInto(CpanelPackZonaPrecioMappingRecord.class);
    }

    public List<CpanelPackZonaPrecioMappingRecord> getPackTargetItemMappings(Integer targetPackItemId) {
        return dsl.select(packPriceTypeMapping.fields())
                .from(packPriceTypeMapping)
                .where(packPriceTypeMapping.IDTARGETPACKITEM.eq(targetPackItemId))
                .fetchInto(CpanelPackZonaPrecioMappingRecord.class);
    }

    public void deleteTargetPackItem(Integer targetPackItemId) {
        dsl.delete(packPriceTypeMapping)
                .where(packPriceTypeMapping.IDTARGETPACKITEM.eq(targetPackItemId))
                .execute();
    }

    public List<ItemPackPriceInfoRecord> getItemPackPriceInfoRecordsByPackItemId(Integer packId) {
        Field<Integer> priceZoneJoinField = DSL.when(isMainPackItem(), packPriceTypeMapping.IDSOURCEZONAPRECIO)
                .otherwise(packPriceTypeMapping.IDTARGETZONAPRECIO);

        return dsl
                .select(packItem.IDPACKITEM, rate.IDTARIFA, packPriceTypeMapping.IDSOURCEZONAPRECIO,
                        priceZoneConfig.DESCRIPCION, priceZoneRelation.PRECIO)
                .from(packItem)

                .join(packPriceTypeMapping)
                .on(packItem.IDPACKITEM.eq(DSL.when(isMainPackItem(), packPriceTypeMapping.IDSOURCEPACKITEM)
                        .otherwise(packPriceTypeMapping.IDTARGETPACKITEM)))

                .leftJoin(session)
                .on(isSessionPackItem()
                        .and(session.IDSESION.eq(packItem.IDITEM)))

                .join(rate)
                .on(isPackItemEventId())

                .join(priceZoneRelation)
                .on(priceZoneRelation.IDTARIFA.eq(rate.IDTARIFA)
                        .and(priceZoneRelation.IDZONA.eq(priceZoneJoinField)))

                .join(priceZoneConfig)
                .on(priceZoneConfig.IDZONA.eq(priceZoneJoinField))

                .where(packItem.IDPACK.eq(packId))
                .and(DSL.when(packItem.PRINCIPAL.isFalse().and(isSessionPackItem()), rate.DEFECTO.isTrue())
                        .otherwise(DSL.trueCondition()))

                .fetch(record -> {
                    ItemPackPriceInfoRecord info = new ItemPackPriceInfoRecord();
                    info.setPackItemId(record.get(packItem.IDPACKITEM));
                    info.setItemRateId(record.get(rate.IDTARIFA));
                    info.setMainPriceZone(record.get(packPriceTypeMapping.IDSOURCEZONAPRECIO));
                    info.setMainPriceZoneName(record.get(priceZoneConfig.DESCRIPCION));
                    info.setItemPrice(record.get(priceZoneRelation.PRECIO));
                    return info;
                });
    }

    public List<ItemPackPriceInfoRecord> getItemPackPriceInfoRecordsByUnmappedPackItemId(Integer packId) {
        return dsl
                .select(packItem.IDPACKITEM, rate.IDTARIFA, priceZoneConfig.IDZONA,
                        priceZoneConfig.DESCRIPCION, priceZoneRelation.PRECIO)
                .from(packItem)

                .leftJoin(session)
                .on(isSessionPackItem()
                        .and(session.IDSESION.eq(packItem.IDITEM)))

                .join(rate)
                .on(isPackItemEventId()
                        .and(isMainPackItem().or(rate.DEFECTO.isTrue())))

                .leftJoin(entityVenueConfig)
                .on(isSessionPackItem()
                        .and(entityVenueConfig.IDRELACIONENTRECINTO.eq(session.IDRELACIONENTIDADRECINTO)))

                .join(venueConfig)
                .on(venueConfig.IDCONFIGURACION.eq(DSL.coalesce(entityVenueConfig.IDCONFIGURACION, packItem.IDCONFIGURACION))
                        .and(isSessionPackItem()).or(venueConfig.IDEVENTO.eq(packItem.IDITEM)))

                .join(priceZoneRelation)
                .on(priceZoneRelation.IDTARIFA.eq(rate.IDTARIFA)
                        .and(isMainPackItem().or(priceZoneRelation.IDZONA.eq(packItem.IDZONAPRECIO))))

                .join(priceZoneConfig)
                .on(priceZoneConfig.IDZONA.eq(priceZoneRelation.IDZONA)
                        .and(priceZoneConfig.IDCONFIGURACION.eq(venueConfig.IDCONFIGURACION)))

                .where(packItem.IDPACK.eq(packId))
                .fetch(record -> {
                    ItemPackPriceInfoRecord info = new ItemPackPriceInfoRecord();
                    info.setPackItemId(record.get(packItem.IDPACKITEM));
                    info.setItemRateId(record.get(rate.IDTARIFA));
                    info.setMainPriceZone(record.get(priceZoneConfig.IDZONA));
                    info.setMainPriceZoneName(record.get(priceZoneConfig.DESCRIPCION));
                    info.setItemPrice(record.get(priceZoneRelation.PRECIO));
                    return info;
                });
    }

    private static Condition isPackItemEventId() {
        return rate.IDEVENTO.eq(DSL.coalesce(session.IDEVENTO, packItem.IDITEM));
    }

    private static Condition isMainPackItem() {
        return packItem.PRINCIPAL.isTrue();
    }

    private static Condition isSessionPackItem() {
        return packItem.TIPOITEM.eq(PackItemType.SESSION.getId());
    }


}
