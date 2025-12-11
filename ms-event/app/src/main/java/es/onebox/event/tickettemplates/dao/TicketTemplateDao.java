package es.onebox.event.tickettemplates.dao;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.events.enums.TicketTemplateStatus;
import es.onebox.event.events.enums.TourStatus;
import es.onebox.event.priceengine.sorting.SortUtils;
import es.onebox.event.tickettemplates.dto.DesignType;
import es.onebox.event.tickettemplates.dto.TicketTemplatesFilter;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelEntidad;
import es.onebox.jooq.cpanel.tables.CpanelEntidadAdminEntidades;
import es.onebox.jooq.cpanel.tables.CpanelModeloTicket;
import es.onebox.jooq.cpanel.tables.CpanelPlantillaTicket;
import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaPlantillaTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPlantillaTicketRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectJoinStep;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static es.onebox.jooq.cpanel.Tables.CPANEL_IDIOMA_PLANTILLA_TICKET;
import static es.onebox.jooq.cpanel.Tables.CPANEL_MODELO_TICKET;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PLANTILLA_TICKET;

@Repository
public class TicketTemplateDao extends DaoImpl<CpanelPlantillaTicketRecord, Integer> {

    protected TicketTemplateDao() {
        super(CpanelPlantillaTicket.CPANEL_PLANTILLA_TICKET);
    }

    private static final CpanelPlantillaTicket TICKET_TEMPLATE = CpanelPlantillaTicket.CPANEL_PLANTILLA_TICKET.as("template");
    private static final CpanelEntidad ENTITY = CpanelEntidad.CPANEL_ENTIDAD.as("entity");

    private static final CpanelModeloTicket MODEL = CpanelModeloTicket.CPANEL_MODELO_TICKET.as("model");
    private static final Field<String> JOIN_ENTITY_NAME = ENTITY.NOMBRE.as("entityName");
    private static final Field<Integer> JOIN_OPERATOR_ID = ENTITY.IDOPERADORA.as("operatorId");
    private static final Field<String> JOIN_MODEL_NAME = MODEL.NOMBRE.as("modelName");
    private static final Field<String> JOIN_MODEL_DESCRIPTION = MODEL.DESCRIPCION.as("modelDescription");
    private static final Field<Byte> JOIN_MODEL_FORMAT = MODEL.FORMATO.as("modelFormat");
    private static final Field<String> JOIN_MODEL_PRINTER = MODEL.TIPOIMPRESORA.as("modelPrinter");
    private static final Field<String> JOIN_MODEL_PAPER = MODEL.TIPOHOJA.as("modelPaper");
    private static final Field<String> JOIN_MODEL_ORIENTATION = MODEL.ORIENTACION.as("modelOrientation");
    private static final Field<String> JOIN_MODEL_JASPER_MODEL = MODEL.MODELOJASPER.as("modelJasperModel");
    private static final Field<Integer> JOIN_MODEL_MODEL_TYPE = MODEL.TIPOMODELO.as("modelType");
    private static final CpanelEntidadAdminEntidades entityAdmin = Tables.CPANEL_ENTIDAD_ADMIN_ENTIDADES.as("entityAdmin");

    private static final SelectFieldOrAsterisk[] JOIN_FIELDS = {
            JOIN_ENTITY_NAME,
            JOIN_OPERATOR_ID,
            JOIN_MODEL_NAME,
            JOIN_MODEL_DESCRIPTION,
            JOIN_MODEL_FORMAT,
            JOIN_MODEL_PRINTER,
            JOIN_MODEL_PAPER,
            JOIN_MODEL_ORIENTATION,
            JOIN_MODEL_JASPER_MODEL,
            JOIN_MODEL_MODEL_TYPE
    };

    public TicketTemplateRecord find(Integer ticketTemplateId) {
        SelectFieldOrAsterisk[] fields = ArrayUtils.addAll(TICKET_TEMPLATE.fields(), JOIN_FIELDS);
        return dsl.select(fields).
                from(TICKET_TEMPLATE).
                innerJoin(ENTITY).on(TICKET_TEMPLATE.IDENTIDAD.eq(ENTITY.IDENTIDAD)).
                innerJoin(MODEL).on(TICKET_TEMPLATE.IDMODELO.eq(MODEL.IDMODELO)).
                where(TICKET_TEMPLATE.IDPLANTILLA.eq(ticketTemplateId)).
                fetchOne(r -> buildTicketTemplateRecord(r, fields.length));
    }

    public List<TicketTemplateRecord> find(TicketTemplatesFilter filter) {
        SelectFieldOrAsterisk[] fields = buildFields(filter);
        SelectJoinStep<Record> baseQuery = dsl.select(fields)
                .from(TICKET_TEMPLATE)
                .innerJoin(ENTITY).on(TICKET_TEMPLATE.IDENTIDAD.eq(ENTITY.IDENTIDAD))
                .innerJoin(MODEL).on(TICKET_TEMPLATE.IDMODELO.eq(MODEL.IDMODELO));

        SelectJoinStep<Record> query = baseQuery;
        if(Objects.nonNull(filter.getEntityAdminId()))  {
            query = baseQuery.leftJoin(entityAdmin).on(entityAdmin.IDENTIDAD.eq(ENTITY.IDENTIDAD));
        }

        query.where(buildWhere(filter));
        query.orderBy(SortUtils.buildSort(filter.getSort(), TicketTemplateField::byName));

        if (filter.getLimit() != null) {
            query.limit(filter.getLimit().intValue());
        }
        if (filter.getOffset() != null) {
            query.offset(filter.getOffset().intValue());
        }

        return query.fetch(r -> buildTicketTemplateRecord(r, fields.length));
    }

    public Long countByFilter(TicketTemplatesFilter filter) {
        Condition where = buildWhere(filter);

        SelectJoinStep<?> baseQuery = dsl.selectCount()
                .from(TICKET_TEMPLATE)
                .join(ENTITY).on(TICKET_TEMPLATE.IDENTIDAD.eq(ENTITY.IDENTIDAD))
                .join(MODEL).on(TICKET_TEMPLATE.IDMODELO.eq(MODEL.IDMODELO));
        SelectJoinStep<?> query = baseQuery;

        if(Objects.nonNull(filter.getEntityAdminId()))  {
            query = baseQuery.leftJoin(entityAdmin).on(entityAdmin.IDENTIDAD.eq(ENTITY.IDENTIDAD));
        }
        return query.
                where(where).
                fetchOne().
                into(Long.class);
    }

    public Long countByNameAndEntity(String name, Integer entityId) {
        return dsl.selectCount()
                .from(TICKET_TEMPLATE)
                .where(TICKET_TEMPLATE.NOMBRE.eq(name).and(
                        TICKET_TEMPLATE.IDENTIDAD.eq(entityId)).and(
                        TICKET_TEMPLATE.ESTADO.eq(TicketTemplateStatus.ACTIVE.getId())
                ))
                .fetchOne()
                .into(Long.class);
    }

    public List<CpanelIdiomaPlantillaTicketRecord> getTicketTemplateLanguages(Long ticketTemplateId) {
        return dsl.select().
                from(CPANEL_IDIOMA_PLANTILLA_TICKET).
                where(CPANEL_IDIOMA_PLANTILLA_TICKET.IDPLANTILLA.eq(ticketTemplateId.intValue())).
                fetchInto(CpanelIdiomaPlantillaTicketRecord.class);
    }

    public List<TicketTemplateRecord> getDefaultTemplates(Integer entityId) {
        return dsl.select(CPANEL_PLANTILLA_TICKET.IDPLANTILLA, CPANEL_PLANTILLA_TICKET.NOMBRE, CPANEL_MODELO_TICKET.FORMATO)
                .from(CPANEL_PLANTILLA_TICKET)
                .innerJoin(CPANEL_MODELO_TICKET).on(CPANEL_PLANTILLA_TICKET.IDMODELO.eq(CPANEL_MODELO_TICKET.IDMODELO))
                .where(CPANEL_PLANTILLA_TICKET.IDENTIDAD.eq(entityId)
                        .and(CPANEL_PLANTILLA_TICKET.ESTADO.notEqual(TicketTemplateStatus.DELETED.getId())
                                .and(CPANEL_PLANTILLA_TICKET.ASIGNACIONAUTOMATICA.eq((byte) 1))))
                .fetch()
                .map(record -> {
                    TicketTemplateRecord ticketTemplateRecord = new TicketTemplateRecord();
                    ticketTemplateRecord.setIdplantilla(record.get(Tables.CPANEL_PLANTILLA_TICKET.IDPLANTILLA));
                    ticketTemplateRecord.setNombre(record.get(Tables.CPANEL_PLANTILLA_TICKET.NOMBRE));
                    ticketTemplateRecord.setModelFormat(record.get(Tables.CPANEL_MODELO_TICKET.FORMATO).intValue());
                    return ticketTemplateRecord;
                });
    }

    private static TicketTemplateRecord buildTicketTemplateRecord(Record record, int fields) {
        TicketTemplateRecord templateRecord = record.into(TICKET_TEMPLATE).into(TicketTemplateRecord.class);

        //Add join fields only if has been added to base event fields
        if (fields > TICKET_TEMPLATE.fields().length) {
            templateRecord.setEntityName(record.getValue(JOIN_ENTITY_NAME));
            templateRecord.setOperatorId(record.getValue(JOIN_OPERATOR_ID));
            templateRecord.setModelName(record.getValue(JOIN_MODEL_NAME));
            templateRecord.setModelDescription(record.getValue(JOIN_MODEL_DESCRIPTION));
            templateRecord.setModelFormat(record.getValue(JOIN_MODEL_FORMAT).intValue());
            templateRecord.setModelPrinter(record.getValue(JOIN_MODEL_PRINTER));
            templateRecord.setModelPaper(record.getValue(JOIN_MODEL_PAPER));
            templateRecord.setModelOrientation(record.getValue(JOIN_MODEL_ORIENTATION));
            templateRecord.setJasperModel(record.getValue(JOIN_MODEL_JASPER_MODEL));
            templateRecord.setModelType(record.getValue(JOIN_MODEL_MODEL_TYPE));
        }

        return templateRecord;
    }

    private static Condition buildWhere(TicketTemplatesFilter filter) {
        Condition where = TICKET_TEMPLATE.ESTADO.notEqual(TourStatus.DELETED.getId());
        if (filter.getOperatorId() != null) {
            where = where.and(ENTITY.IDOPERADORA.eq(filter.getOperatorId().intValue()));
        }
        if (filter.getEntityAdminId() != null) {
            where = where.and(entityAdmin.IDENTIDADADMIN.eq(filter.getEntityAdminId().intValue()));
        }
        if (filter.getEntityId() != null) {
            where = where.and(TICKET_TEMPLATE.IDENTIDAD.eq(filter.getEntityId().intValue()));
        }
        if (filter.getDesignId() != null) {
            where = where.and(TICKET_TEMPLATE.IDMODELO.eq(filter.getDesignId().intValue()));
        }
        if (CollectionUtils.isNotEmpty(filter.getFormat())) {
            where = where.and(MODEL.FORMATO.in(filter.getFormat()));
        }
        if (filter.getPrinter() != null) {
            where = where.and(MODEL.TIPOIMPRESORA.eq(filter.getPrinter()));
        }
        if (filter.getPaperType() != null) {
            where = where.and(MODEL.TIPOHOJA.eq(filter.getPaperType()));
        }

        if (filter.getDesignType() != null) {
            where = where.and(MODEL.TIPOMODELO.eq(filter.getDesignType().getValue()));
        } else {
            // IF WE DON'T FILTER BY DESIGN TYPE, RETURN TICKET TEMPLATES WITHOUT PRODUCT TICKET TEMPLATES.
            where = where.and(MODEL.TIPOMODELO.notEqual(DesignType.PRODUCT.getValue()));
        }
        if (StringUtils.isNotBlank(filter.getFreeSearch())) {
            String freeSearch = filter.getFreeSearch();
            where = where.and(TICKET_TEMPLATE.NOMBRE.like("%" + freeSearch + "%"));
        }
        return where;
    }

    private static SelectFieldOrAsterisk[] buildFields(TicketTemplatesFilter filter) {
        if (filter == null || CommonUtils.isEmpty(filter.getFields())) {
            return ArrayUtils.addAll(TICKET_TEMPLATE.fields(), JOIN_FIELDS);
        }
        Set<SelectFieldOrAsterisk> selectFieldOrAsterisks = buildFilteredFields(filter.getFields());
        return selectFieldOrAsterisks.toArray(new SelectFieldOrAsterisk[0]);
    }

    private static Set<SelectFieldOrAsterisk> buildFilteredFields(List<String> fields) {
        Set<SelectFieldOrAsterisk> selectFieldOrAsterisks = new HashSet<>();
        selectFieldOrAsterisks.add(TICKET_TEMPLATE.IDPLANTILLA);
        for (String field : fields) {
            Field dbField = TicketTemplateField.byName(field);
            if (dbField != null) {
                selectFieldOrAsterisks.add(dbField);
            }
        }
        return selectFieldOrAsterisks;
    }

}
