package es.onebox.event.products.dao;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.event.datasources.ms.entity.dto.EntityState;
import es.onebox.event.priceengine.sorting.SortUtils;
import es.onebox.event.products.domain.DeliveryPointRecord;
import es.onebox.event.products.dto.SearchDeliveryPointFilterDTO;
import es.onebox.event.products.enums.DeliveryPointStatus;
import es.onebox.event.sorting.DeliveryPointField;
import es.onebox.jooq.cpanel.tables.CpanelDeliveryPoint;
import es.onebox.jooq.cpanel.tables.CpanelEntidad;
import es.onebox.jooq.cpanel.tables.CpanelPais;
import es.onebox.jooq.cpanel.tables.CpanelProvincia;
import es.onebox.jooq.cpanel.tables.records.CpanelDeliveryPointRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectJoinStep;
import org.jooq.SelectSeekStepN;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static es.onebox.jooq.cpanel.Tables.CPANEL_DELIVERY_POINT;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ENTIDAD;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PAIS;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PROVINCIA;

@Repository
public class DeliveryPointDao extends DaoImpl<CpanelDeliveryPointRecord, Integer> {

    private static final CpanelDeliveryPoint deliveryPoint = CPANEL_DELIVERY_POINT.as("deliveryPoint");
    private static final CpanelPais country = CPANEL_PAIS.as("country");
    private static final CpanelProvincia countrySubdivision = CPANEL_PROVINCIA.as("countrySubdivision");
    private static final CpanelEntidad entity = CPANEL_ENTIDAD.as("entity");

    private static final Field<String> JOIN_COUNTRY_NAME = country.NOMBRE.as("countryName");
    private static final Field<String> JOIN_COUNTRY_CODE = country.CODIGO.as("countryCode");
    private static final Field<String> JOIN_COUNTRY_SUBDIVISION_NAME = countrySubdivision.NOMBRE.as("countrySubdivisionName");
    private static final Field<String> JOIN_COUNTRY_SUBDIVISION_CODE = countrySubdivision.CODIGO.as("countrySubdivisionCode");
    private static final Field<String> JOIN_ENTITY_NAME = entity.NOMBRE.as("entityName");

    private static final SelectFieldOrAsterisk[] JOIN_FIELDS = {
            JOIN_COUNTRY_NAME,
            JOIN_COUNTRY_CODE,
            JOIN_COUNTRY_SUBDIVISION_NAME,
            JOIN_COUNTRY_SUBDIVISION_CODE,
            JOIN_ENTITY_NAME
    };

    protected DeliveryPointDao() {
        super(CPANEL_DELIVERY_POINT);
    }

    public Long getTotalProductDeliveryPoints(SearchDeliveryPointFilterDTO searchDeliveryPointFilterDTO, Long id) {
        try {
            SelectJoinStep query = dsl.selectCount()
                    .from(deliveryPoint)
                    .innerJoin(entity).on(deliveryPoint.ENTITYID.eq(entity.IDENTIDAD))
                    .leftJoin(country).on(deliveryPoint.COUNTRYID.eq(country.IDPAIS))
                    .leftJoin(countrySubdivision).on(deliveryPoint.COUNTRYSUBDIVISIONID.eq(countrySubdivision.IDPROVINCIA));

            query.where(builderWhereClause(searchDeliveryPointFilterDTO, id));

            return query.fetchOne().into(Long.class);
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }

    public List<DeliveryPointRecord> getProductDeliveryPoints(SearchDeliveryPointFilterDTO searchDeliveryPointFilterDTO, Long id) {
        List<DeliveryPointRecord> result = new ArrayList<>();
        try {
            SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(deliveryPoint.fields(), JOIN_FIELDS);

            SelectSeekStepN query = dsl.select(fields)
                    .from(deliveryPoint)
                    .innerJoin(entity).on(deliveryPoint.ENTITYID.eq(entity.IDENTIDAD))
                    .leftJoin(country).on(deliveryPoint.COUNTRYID.eq(country.IDPAIS))
                    .leftJoin(countrySubdivision).on(deliveryPoint.COUNTRYSUBDIVISIONID.eq(countrySubdivision.IDPROVINCIA))
                    .where(builderWhereClause(searchDeliveryPointFilterDTO, id))
                    .orderBy(SortUtils.buildSort(searchDeliveryPointFilterDTO != null ? searchDeliveryPointFilterDTO.getSort() : null, DeliveryPointField::byName));

            if (id == null) {
                if (searchDeliveryPointFilterDTO.getLimit() != null) {
                    query.limit(searchDeliveryPointFilterDTO.getLimit().intValue());
                }
                if (searchDeliveryPointFilterDTO.getOffset() != null) {
                    query.offset(searchDeliveryPointFilterDTO.getOffset().intValue());
                }
            }

            List<Record> records = query.fetch();
            for (Record record : records) {
                DeliveryPointRecord productDeliveryPointRecord = buildProductDeliveryPointRecord(record, fields.length);
                result.add(productDeliveryPointRecord);
            }
            return result;
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }

    public List<CpanelDeliveryPointRecord> findDeliveryPoints(Long entityId, String name) {
        try {
            SelectConditionStep<Record> query = dsl.select(deliveryPoint.fields())
                    .from(deliveryPoint)
                    .where(deliveryPoint.ENTITYID.eq(entityId.intValue()))
                    .and(deliveryPoint.NAME.eq(name))
                    .and(deliveryPoint.DELIVERYPOINTSTATUS.ne(DeliveryPointStatus.DELETED.getId()));

            return query.fetch().into(CpanelDeliveryPointRecord.class);
        } catch (Exception e) {
            throw new OneboxRestException(CoreErrorCode.GENERIC_ERROR, "Error executing query", e);
        }
    }

    public List<DeliveryPointRecord> getProductDeliveryPointsByIds(List<Integer> ids) {
        List<DeliveryPointRecord> result = new ArrayList<>();
        SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(deliveryPoint.fields(), JOIN_FIELDS);

        SelectConditionStep<Record> query = dsl.select(fields)
                .from(deliveryPoint)
                .innerJoin(entity).on(deliveryPoint.ENTITYID.eq(entity.IDENTIDAD))
                .leftJoin(country).on(deliveryPoint.COUNTRYID.eq(country.IDPAIS))
                .leftJoin(countrySubdivision).on(deliveryPoint.COUNTRYSUBDIVISIONID.eq(countrySubdivision.IDPROVINCIA))
                .where(deliveryPoint.DELIVERYPOINTID.in(ids)
                        .and(deliveryPoint.DELIVERYPOINTSTATUS.eq(DeliveryPointStatus.ACTIVE.getId())));

        List<Record> records = query.fetch();
        for (Record record : records) {
            DeliveryPointRecord productDeliveryPointRecord = buildProductDeliveryPointRecord(record, fields.length);
            result.add(productDeliveryPointRecord);
        }
        return result;
    }

    private Condition builderWhereClause(SearchDeliveryPointFilterDTO filter, Long id) {
        Condition conditions = DSL.trueCondition()
                .and(deliveryPoint.DELIVERYPOINTSTATUS.ne(DeliveryPointStatus.DELETED.getId()))
                .and(entity.ESTADO.ne(EntityState.DELETED.getState()));

        if (id != null) {
            conditions = conditions.and(deliveryPoint.DELIVERYPOINTID.eq(id.intValue()));
            return conditions;
        }
        if (filter.getEntityIds() != null) {
            conditions = conditions.and(deliveryPoint.ENTITYID.in(filter.getEntityIds()));
        }
        if (filter.getOperatorId() != null) {
            conditions = conditions.and(entity.IDOPERADORA.eq(filter.getOperatorId().intValue()));
        }
        if (filter.getName() != null) {
            conditions = conditions.and(deliveryPoint.NAME.like("%" + filter.getName() + "%"));
        }
        if (filter.getCountry() != null) {
            conditions = conditions.and(country.CODIGO.eq(filter.getCountry()));
        }
        if (filter.getStatus() != null) {
            conditions = conditions.and(deliveryPoint.DELIVERYPOINTSTATUS.in(filter.getStatus().stream().map(DeliveryPointStatus::getId).collect(Collectors.toList())));
        }
        if (filter.getCountrySubdivision() != null) {
            conditions = conditions.and(countrySubdivision.CODIGO.eq(filter.getCountrySubdivision()));
        }
        return conditions;
    }

    private static DeliveryPointRecord buildProductDeliveryPointRecord(Record deliveryPointRecord, int fields) {
        DeliveryPointRecord deliveryPoint = deliveryPointRecord.into(DeliveryPointRecord.class);

        //Add join fields only if has been added to base event fields
        if (fields > deliveryPoint.fields().length) {
            deliveryPoint.setEntityName(deliveryPointRecord.getValue(JOIN_ENTITY_NAME));
            deliveryPoint.setCountryCode(deliveryPointRecord.getValue(JOIN_COUNTRY_CODE));
            deliveryPoint.setCountryName(deliveryPointRecord.getValue(JOIN_COUNTRY_NAME));
            deliveryPoint.setCountrySubdivisionCode(deliveryPointRecord.getValue(JOIN_COUNTRY_SUBDIVISION_CODE));
            deliveryPoint.setCountrySubdivisionName(deliveryPointRecord.getValue(JOIN_COUNTRY_SUBDIVISION_NAME));
        }

        return deliveryPoint;
    }

}
