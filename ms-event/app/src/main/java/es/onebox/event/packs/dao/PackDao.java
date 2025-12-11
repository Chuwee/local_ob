package es.onebox.event.packs.dao;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.packs.dao.domain.PackRecord;
import es.onebox.event.packs.dto.PacksFilterRequest;
import es.onebox.event.packs.enums.PackField;
import es.onebox.event.packs.enums.PackItemType;
import es.onebox.event.packs.enums.PackStatus;
import es.onebox.event.packs.enums.PackSubtype;
import es.onebox.event.packs.enums.PackType;
import es.onebox.event.packs.record.PackDetailRecord;
import es.onebox.event.priceengine.sorting.SortUtils;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelEntidad;
import es.onebox.jooq.cpanel.tables.CpanelImpuesto;
import es.onebox.jooq.cpanel.tables.CpanelPack;
import es.onebox.jooq.cpanel.tables.CpanelPackItem;
import es.onebox.jooq.cpanel.tables.CpanelSesion;
import es.onebox.jooq.cpanel.tables.CpanelTaxonomiaBase;
import es.onebox.jooq.cpanel.tables.CpanelTaxonomiaPropia;
import es.onebox.jooq.cpanel.tables.records.CpanelPackRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static es.onebox.jooq.cpanel.Tables.CPANEL_ENTIDAD_RECINTO_CONFIG;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PACK;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PACK_ITEM;
import static es.onebox.jooq.cpanel.Tables.CPANEL_SESION;

@Repository
public class PackDao extends DaoImpl<CpanelPackRecord, Integer> {

    private static final CpanelPack packTable = Tables.CPANEL_PACK;
    private static final CpanelSesion sessionTable = CPANEL_SESION;
    private static final CpanelEntidad entity = Tables.CPANEL_ENTIDAD;
    private static final CpanelPackItem packItemTable = CPANEL_PACK_ITEM;
    private static final CpanelTaxonomiaBase taxonomyTable = Tables.CPANEL_TAXONOMIA_BASE.as("taxonomiaBase");
    private static final CpanelTaxonomiaPropia customTaxonomyTable = Tables.CPANEL_TAXONOMIA_PROPIA.as("taxonomiaPropia");
    private static final CpanelImpuesto taxTable = CpanelImpuesto.CPANEL_IMPUESTO.as("tax");
    private static final Field<String> BASE_CATEGORY_DESC = taxonomyTable.DESCRIPCION.as("categoryDesc");
    private static final Field<String> BASE_CATEGORY_CODE = taxonomyTable.CODIGO.as("categoryCode");
    private static final Field<String> CUSTOM_CATEGORY_DESC = customTaxonomyTable.DESCRIPCION.as("customCategoryDesc");
    private static final Field<String> CUSTOM_CATEGORY_REF = customTaxonomyTable.REFERENCIA.as("customCategoryRef");
    private static final Field<String> TAX_NAME = taxTable.NOMBRE.as("taxName");


    private static final int PACK_PROMOTER_TYPE = 0;
    private static final int PACK_TYPE_AUTOMATIC = 1;
    private static final int PACK_STATUS_ACTIVE = 1;
    private static final int PACK_ITEM_TYPE_SESSION = 1;
    private static final int PACK_ITEM_TYPE_EVENT = 3;
    private static final int SESSION_STATUS_READY = 3;

    private static final Field<?>[] DETAILED_JOIN_FIELDS =
            ArrayUtils.addAll(packTable.fields(), entity.IDENTIDAD, entity.NOMBRE, entity.IDOPERADORA, BASE_CATEGORY_DESC, BASE_CATEGORY_CODE,
                    CUSTOM_CATEGORY_DESC, CUSTOM_CATEGORY_REF, TAX_NAME);

    protected PackDao() {
        super(packTable);
    }

    public List<PackRecord> getPackRecords(PacksFilterRequest filter) {
        Field<?>[] fields = ArrayUtils.addAll(packTable.fields(), entity.IDENTIDAD, entity.NOMBRE);

        SelectJoinStep<Record> query = dsl.select(fields).from(packTable);
        buildJoinClauses(query, filter);
        query.where(buildFromCondition(filter));
        query.orderBy(SortUtils.buildSort(filter.getSort(), PackField::byName));

        if (filter.getLimit() != null) {
            query.limit(filter.getLimit().intValue());
        }
        if (filter.getOffset() != null) {
            query.offset(filter.getOffset().intValue());
        }

        return query.fetch().map(this::convertToPackSaleRequestRecord);
    }

    private static Condition buildFromCondition(PacksFilterRequest filter) {
        //Default filters
        Condition condition = entity.IDOPERADORA.eq(filter.getOperatorId())
                .and(packTable.ESTADO.ne(PackStatus.DELETED.getId()))
                .and(packTable.TIPO.eq(PackSubtype.PROMOTER.getId()));

        if (filter.getEntityId() != null) {
            condition = condition.and(packTable.IDENTIDAD.eq(filter.getEntityId()));
        }
        if (CollectionUtils.isNotEmpty(filter.getIds())) {
            condition = condition.and(packTable.IDPACK.in(filter.getIds()));
        }
        if (filter.getName() != null) {
            condition = condition.and(packTable.NOMBRE.likeIgnoreCase("%" + filter.getName() + "%"));
        }
        if (CollectionUtils.isNotEmpty(filter.getStatus())) {
            condition = condition.and(packTable.ESTADO.in(filter.getStatus().stream().map(PackStatus::getId).toList()));
        }
        if (filter.getEventId() != null) { //Only support fot automatic packs
            condition = condition.and(packItemTable.PRINCIPAL.eq(Boolean.TRUE))
                    .and(packTable.SUBTIPO.eq(PackType.AUTOMATIC.getId()))
                    .and(
                        (
                        packItemTable.TIPOITEM.eq(PackItemType.EVENT.getId()).and(packItemTable.IDITEM.eq(filter.getEventId()))) //event
                        .or(
                            (packItemTable.TIPOITEM.eq(PackItemType.SESSION.getId()).and(packItemTable.IDITEM.eq(sessionTable.IDSESION)).and(sessionTable.IDEVENTO.eq(filter.getEventId()))) //Session
                        )
                    );
        }

        return condition;
    }

    private void buildJoinClauses(SelectJoinStep<Record> query, PacksFilterRequest filter) {
        //Default joins
        query.innerJoin(entity).on(packTable.IDENTIDAD.eq(entity.IDENTIDAD));

        //Extra joins by filters
        if (filter.getEventId() != null) {
            query.innerJoin(packItemTable).on(packItemTable.IDPACK.eq(packTable.IDPACK));
            query.innerJoin(sessionTable).on(sessionTable.IDSESION.eq(packItemTable.IDITEM));
        }
    }

    public Map<IdNameDTO, List<CpanelPackRecord>> getSessionPacks(Integer eventId) {
        Map<IdNameDTO, List<CpanelPackRecord>> sessionPacks = new HashMap<>();
        dsl.select(CPANEL_SESION.IDSESION, CPANEL_SESION.NOMBRE, CPANEL_PACK.IDPACK, CPANEL_PACK.NOMBRE, CPANEL_PACK.IDCANAL, CPANEL_PACK.TIPO)
                .from(CPANEL_SESION)
                .innerJoin(CPANEL_PACK_ITEM).on(
                        (
                                CPANEL_PACK_ITEM.IDITEM.eq(CPANEL_SESION.IDSESION).and(CPANEL_PACK_ITEM.TIPOITEM.eq(PACK_ITEM_TYPE_SESSION))
                        ).or(
                                CPANEL_PACK_ITEM.IDITEM.eq(CPANEL_SESION.IDEVENTO).and(CPANEL_PACK_ITEM.TIPOITEM.eq(PACK_ITEM_TYPE_EVENT))
                        )
                )
                .innerJoin(CPANEL_PACK).on(CPANEL_PACK.IDPACK.eq(CPANEL_PACK_ITEM.IDPACK))
                .innerJoin(CPANEL_ENTIDAD_RECINTO_CONFIG).on(CPANEL_ENTIDAD_RECINTO_CONFIG.IDRELACIONENTRECINTO.eq(CPANEL_SESION.IDRELACIONENTIDADRECINTO))
                .where((
                                CPANEL_PACK.SUBTIPO.eq(PACK_TYPE_AUTOMATIC)
                                        .and(CPANEL_PACK.ESTADO.eq(PACK_STATUS_ACTIVE))
                                        .and(CPANEL_SESION.IDEVENTO.eq(eventId))
                                        .and(CPANEL_SESION.ESTADO.eq(SESSION_STATUS_READY))
                        ).and((
                                        CPANEL_PACK_ITEM.TIPOITEM.eq(PACK_ITEM_TYPE_SESSION)
                                                .and(CPANEL_PACK_ITEM.PRINCIPAL.eq(Boolean.TRUE))
                                ).or(
                                        CPANEL_PACK_ITEM.TIPOITEM.eq(PACK_ITEM_TYPE_EVENT)
                                                .and(CPANEL_PACK_ITEM.IDCONFIGURACION.eq(CPANEL_ENTIDAD_RECINTO_CONFIG.IDCONFIGURACION))
                                )
                        )
                )
                .fetch().forEach(r -> {
                    Integer sessionId = r.getValue(CPANEL_SESION.IDSESION);
                    CpanelPackRecord pack = new CpanelPackRecord();
                    pack.setIdpack(r.getValue(CPANEL_PACK.IDPACK));
                    pack.setNombre(r.getValue(CPANEL_PACK.NOMBRE));
                    pack.setIdcanal(r.getValue(CPANEL_PACK.IDCANAL));
                    pack.setTipo(r.getValue(CPANEL_PACK.TIPO));

                    IdNameDTO idNameDTO = new IdNameDTO();
                    idNameDTO.setId(sessionId.longValue());
                    idNameDTO.setName(r.getValue(CPANEL_SESION.NOMBRE));
                    if (sessionPacks.keySet().stream().noneMatch(s -> s.getId().equals(sessionId.longValue()))) {
                        sessionPacks.put(idNameDTO, new ArrayList<>());
                    }

                    sessionPacks.get(idNameDTO).add(pack);
                });
        return sessionPacks;
    }

    private PackRecord convertToPackSaleRequestRecord(Record in) {
        if (in == null) return null;
        PackRecord packRecord = in.into(PackDetailRecord.class);
        packRecord.setEntity(in.into(entity));
        return packRecord;
    }

    public Long countByFilter(PacksFilterRequest filter) {
        SelectJoinStep<Record> query = dsl.select(packTable.fields()).from(packTable);
        buildJoinClauses(query, filter);
        query.where(buildFromCondition(filter));
        return (long) dsl.fetchCount(query);
    }

    public CpanelPackRecord getPackRecordById(Integer packId) {
        return dsl.select(packTable.fields())
                .from(packTable)
                .where(packTable.IDPACK.eq(packId))
                .and(packTable.TIPO.eq(PackSubtype.PROMOTER.getId()))
                .fetchOneInto(CpanelPackRecord.class);
    }

    public PackDetailRecord getPackDetailRecordById(Integer packId) {
        Record record = dsl.select(DETAILED_JOIN_FIELDS)
                .from(packTable)
                .innerJoin(entity).on(packTable.IDENTIDAD.eq(entity.IDENTIDAD))
                .leftJoin(taxonomyTable).on(taxonomyTable.IDTAXONOMIA.eq(packTable.TAXONOMYID))
                .leftJoin(customTaxonomyTable).on(customTaxonomyTable.IDTAXONOMIA.eq(packTable.CUSTOMTAXONOMYID))
                .leftJoin(taxTable).on(taxTable.IDIMPUESTO.eq(packTable.TAXID))
                .where(packTable.IDPACK.eq(packId))
                .and(packTable.TIPO.eq(PackSubtype.PROMOTER.getId()))
                .fetchOne();
        return toPackDetailRecord(record);
    }

    public PackDetailRecord toPackDetailRecord(Record record) {
        if (record == null) {
            return null;
        }

        PackDetailRecord packDetailRecord = record.into(PackDetailRecord.class);
        packDetailRecord.setEntity(record.into(entity));
        packDetailRecord.setBaseCategoryDescription(record.get(BASE_CATEGORY_DESC));
        packDetailRecord.setBaseCategoryCode(record.get(BASE_CATEGORY_CODE));
        packDetailRecord.setCustomCategoryDescription(record.get(CUSTOM_CATEGORY_DESC));
        packDetailRecord.setCustomCategoryCode(record.get(CUSTOM_CATEGORY_REF));
        packDetailRecord.setTaxName(record.get(TAX_NAME));
        return packDetailRecord;
    }

    public void deletePackById(Integer packId) {
        dsl.update(packTable)
                .set(packTable.ESTADO, 0)
                .where(packTable.IDPACK.eq(packId))
                .and(packTable.TIPO.eq(PackSubtype.PROMOTER.getId()))
                .execute();
    }


    public Set<Integer> getPackIdsWithMainItemTypeEvent(List<Integer> packIds) {
        List<Integer> ids = dsl.select(packTable.IDPACK)
                .from(packTable)
                .innerJoin(packItemTable).on(packItemTable.IDPACK.eq(packTable.IDPACK))
                .where(packTable.IDPACK.in(packIds))
                .and(packTable.TIPO.eq(PackSubtype.PROMOTER.getId()))
                .and(packItemTable.TIPOITEM.eq(PACK_ITEM_TYPE_EVENT))
                .and(packItemTable.PRINCIPAL.eq(Boolean.TRUE))
                .fetchInto(Integer.class);
        return CollectionUtils.isNotEmpty(ids) ? new HashSet<>(ids) : null;
    }

    public Set<Integer> getPackIdsRelatedToEventId(Integer eventId) {
        return dsl.selectDistinct(CPANEL_PACK.IDPACK)
                .from(CPANEL_PACK)
                .join(CPANEL_PACK_ITEM).on(CPANEL_PACK_ITEM.IDPACK.eq(CPANEL_PACK.IDPACK))
                .leftJoin(CPANEL_SESION).on(
                        CPANEL_PACK_ITEM.TIPOITEM.eq(PACK_ITEM_TYPE_SESSION).and(CPANEL_PACK_ITEM.IDITEM.eq(CPANEL_SESION.IDSESION))
                )
                .where(
                        CPANEL_PACK.TIPO.eq(PACK_PROMOTER_TYPE)
                                .and(CPANEL_PACK.SUBTIPO.eq(PACK_TYPE_AUTOMATIC))
                                .and(CPANEL_PACK.ESTADO.eq(PACK_STATUS_ACTIVE))
                                .and((
                                        CPANEL_PACK_ITEM.TIPOITEM.eq(PACK_ITEM_TYPE_EVENT).and(CPANEL_PACK_ITEM.IDITEM.eq(eventId))
                                ).or(
                                        CPANEL_PACK_ITEM.TIPOITEM.eq(PACK_ITEM_TYPE_SESSION).and(CPANEL_SESION.IDEVENTO.eq(eventId))
                                ))
                ).fetchSet(CPANEL_PACK.IDPACK);
    }
}

