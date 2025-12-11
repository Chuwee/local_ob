package es.onebox.event.packs.dao;

import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.packs.dao.domain.PackChannelItemsRecord;
import es.onebox.event.packs.dto.PackChannelSearchFilter;
import es.onebox.event.packs.enums.PackChannelField;
import es.onebox.event.packs.enums.PackChannelStatus;
import es.onebox.event.packs.record.PackChannelRecord;
import es.onebox.event.priceengine.request.ChannelStatus;
import es.onebox.event.priceengine.request.ChannelSubtype;
import es.onebox.event.priceengine.request.StatusRequestType;
import es.onebox.event.priceengine.sorting.SortUtils;
import es.onebox.jooq.cpanel.tables.CpanelAsignacionCanal;
import es.onebox.jooq.cpanel.tables.CpanelCanal;
import es.onebox.jooq.cpanel.tables.CpanelEntidad;
import es.onebox.jooq.cpanel.tables.CpanelPack;
import es.onebox.jooq.cpanel.tables.CpanelPackCanal;
import es.onebox.jooq.cpanel.tables.CpanelPackItem;
import es.onebox.jooq.cpanel.tables.records.CpanelPackCanalRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectJoinStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static es.onebox.jooq.cpanel.Tables.CPANEL_PACK_CANAL;

@Repository
public class PackChannelDao extends DaoImpl<CpanelPackCanalRecord, Integer> {

    private static final CpanelPackCanal packChannel = CpanelPackCanal.CPANEL_PACK_CANAL;
    private static final CpanelPack pack = CpanelPack.CPANEL_PACK.as("pack");
    private static final CpanelCanal channel = CpanelCanal.CPANEL_CANAL.as("channel");
    private static final CpanelEntidad entity = CpanelEntidad.CPANEL_ENTIDAD.as("entity");
    private static final CpanelAsignacionCanal favoriteChannels = CpanelAsignacionCanal.CPANEL_ASIGNACION_CANAL.as("favoriteChannels");
    private static final CpanelPackItem packItem = CpanelPackItem.CPANEL_PACK_ITEM;

    private static final Field<Byte> favorite = DSL.iif(favoriteChannels.IDENTIDAD.isNull(), (byte) 0, (byte) 1).as("favorite");

    private static final SelectFieldOrAsterisk[] JOIN_FIELDS = {
            pack.NOMBRE,
            pack.ESTADO,
            channel.IDCANAL,
            channel.NOMBRECANAL,
            channel.IDENTIDAD,
            channel.IDSUBTIPOCANAL,
            entity.NOMBRE,
            entity.IDOPERADORA,
            entity.PATHIMAGEN,
            favorite
    };

    protected PackChannelDao() {
        super(CPANEL_PACK_CANAL);
    }

    public Optional<CpanelPackCanalRecord> getPackChannel(Long packId, Long channelId) {
        return dsl.select(packChannel.fields())
                .from(packChannel)
                .where(packChannel.IDPACK.eq(packId.intValue()))
                .and(packChannel.IDCANAL.eq(channelId.intValue()))
                .fetchOptional().map(record -> record.into(CpanelPackCanalRecord.class));
    }

    public PackChannelRecord getPackChannelDetailed(Long packId, Long channelId) {
        SelectJoinStep<Record> query = buildSelect();
        return query.where(packChannel.IDPACK.eq(packId.intValue()))
                .and(packChannel.IDCANAL.eq(channelId.intValue()))
                .fetchOne(this::buildChanelPackRecord);
    }

    public List<CpanelPackCanalRecord> getPackChannels(Long packId) {
        return dsl.select(packChannel.fields())
                .from(packChannel)
                .where(packChannel.IDPACK.eq(packId.intValue()))
                .fetch().into(CpanelPackCanalRecord.class);
    }

    public List<CpanelPackCanalRecord> getPackChannels(Long packId, List<Long> channelIds) {
        return dsl.select(packChannel.fields())
                .from(packChannel)
                .where(packChannel.IDPACK.eq(packId.intValue()))
                .and(packChannel.IDCANAL.in(channelIds))
                .fetch().into(CpanelPackCanalRecord.class);
    }

    public List<PackChannelItemsRecord> getAcceptedPackChannelsByPackIdWithItems(List<Integer> packIds) {
        List<PackChannelItemsRecord> result = new ArrayList<>();
        dsl.select(packChannel.IDPACK, packChannel.IDCANAL, packChannel.ESTADO, packChannel.SUGERIRPACK,
                        packItem.IDITEM, packItem.TIPOITEM, packItem.PRINCIPAL, packChannel.ONSALEFORLOGGEDUSERS)
                .from(packChannel)
                .innerJoin(packItem).on(packItem.IDPACK.eq(packChannel.IDPACK))
                .where(packChannel.IDPACK.in(packIds)
                        .and(packChannel.ESTADO.eq(PackChannelStatus.ACCEPTED.getId())))
                .fetch().forEach(r -> {
                    PackChannelItemsRecord out = new PackChannelItemsRecord();
                    out.setIdpack(r.getValue(packChannel.IDPACK));
                    out.setIdcanal(r.getValue(packChannel.IDCANAL));
                    out.setEstado(r.getValue(packChannel.ESTADO));
                    out.setSugerirpack(r.getValue(packChannel.SUGERIRPACK));
                    out.setOnsaleforloggedusers(r.getValue(packChannel.ONSALEFORLOGGEDUSERS));
                    out.setItemId(r.getValue(packItem.IDITEM).longValue());
                    out.setItemType(r.getValue(packItem.TIPOITEM));
                    out.setMainItem(r.getValue(packItem.PRINCIPAL));
                    result.add(out);
                });
        return result;
    }

    public List<PackChannelRecord> findPackChannels(Long packId, PackChannelSearchFilter filter) {
        SelectJoinStep<Record> query = buildSelect();

        Condition condition = channel.ESTADO.ne(ChannelStatus.DELETED.getId())
                .and(packChannel.IDPACK.eq(packId.intValue()));

        if (CollectionUtils.isNotEmpty(filter.getId())) {
            condition = condition.and(packChannel.IDCANAL.in(filter.getId()));
        }

        if (CollectionUtils.isNotEmpty(filter.getSubtype())) {
            condition = condition.and(channel.IDSUBTIPOCANAL
                    .in(filter.getSubtype().stream().map(ChannelSubtype::getIdSubtipo).toList()));
        }

        if (CollectionUtils.isNotEmpty(filter.getRequestStatus())) {
            condition = condition.and(packChannel.ESTADO
                    .in(filter.getRequestStatus().stream().map(StatusRequestType::getId).toList()));
        }

        if (filter.getEntityId() != null) {
            condition = condition.and(channel.IDENTIDAD.eq(filter.getEntityId().intValue()));
        }

        if (StringUtils.isNotEmpty(filter.getQ())) {
            condition = condition.and(channel.NOMBRECANAL.like("%" + filter.getQ() + "%"));
        }

        query.where(condition);

        query.orderBy(SortUtils.buildSort(filter.getSort(), PackChannelField::byName));

        if (filter.getLimit() != null) {
            query.limit(filter.getLimit().intValue());
        }
        if (filter.getOffset() != null) {
            query.offset(filter.getOffset().intValue());
        }

        return query.fetch(this::buildChanelPackRecord);
    }

    private SelectJoinStep<Record> buildSelect() {
        SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(packChannel.fields(), JOIN_FIELDS);
        return dsl.select(fields)
                .from(packChannel)
                .join(pack).on(pack.IDPACK.eq(packChannel.IDPACK))
                .join(channel).on(packChannel.IDCANAL.eq(channel.IDCANAL))
                .join(entity).on(entity.IDENTIDAD.eq(channel.IDENTIDAD))
                .leftJoin(favoriteChannels).on(favoriteChannels.IDCANAL.eq(channel.IDCANAL)
                        .and(favoriteChannels.IDENTIDAD.eq(pack.IDENTIDAD)));
    }

    public Long countByFilter(Long packId, PackChannelSearchFilter filter) {
        return dsl.selectCount()
                .from(packChannel)
                .join(channel).on(packChannel.IDCANAL.eq(channel.IDCANAL))
                .join(entity).on(entity.IDENTIDAD.eq(channel.IDENTIDAD))
                .where(builderWhereClause(packId, filter))
                .fetchOne(0, Long.class);
    }

    private Condition builderWhereClause(Long packId, PackChannelSearchFilter filter) {
        Condition conditions = DSL.trueCondition();
        conditions = conditions.and(channel.ESTADO.ne(ChannelStatus.DELETED.getId()));
        conditions = conditions.and(packChannel.IDPACK.eq(packId.intValue()));
        if (filter.getRequestStatus() != null) {
            conditions = conditions.and(packChannel.ESTADO.in(filter.getRequestStatus().stream().map(StatusRequestType::getId).toList()));
        }
        return conditions;
    }


    private PackChannelRecord buildChanelPackRecord(Record record) {
        PackChannelRecord packChannelRecord = new PackChannelRecord();

        packChannelRecord.setId(record.get(packChannel.IDPACKCANAL).longValue());
        packChannelRecord.setRequestStatus(record.get(packChannel.ESTADO));

        packChannelRecord.setPackId(record.get(packChannel.IDPACK).longValue());
        packChannelRecord.setPackName(record.get(pack.NOMBRE));
        packChannelRecord.setPackStatus(record.get(pack.ESTADO));
        packChannelRecord.setChannelId(record.get(channel.IDCANAL).longValue());
        packChannelRecord.setChannelName(record.get(channel.NOMBRECANAL));
        packChannelRecord.setChannelType(record.get(channel.IDSUBTIPOCANAL));
        packChannelRecord.setEntityId(record.get(channel.IDENTIDAD).longValue());
        packChannelRecord.setEntityName(record.get(entity.NOMBRE));
        packChannelRecord.setEntityLogoPath(record.get(entity.PATHIMAGEN));
        packChannelRecord.setOperatorId(record.get(entity.IDOPERADORA).longValue());
        packChannelRecord.setSuggested(ConverterUtils.isByteAsATrue(record.get(packChannel.SUGERIRPACK)));
        packChannelRecord.setOnSaleForLoggedUsers(ConverterUtils.isByteAsATrue(record.get(packChannel.ONSALEFORLOGGEDUSERS)));

        return packChannelRecord;
    }
}
