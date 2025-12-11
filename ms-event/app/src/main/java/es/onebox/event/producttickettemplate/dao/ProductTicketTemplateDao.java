package es.onebox.event.producttickettemplate.dao;

import static es.onebox.event.producttickettemplate.dao.TableConstants.COMMON_FIELDS;
import static es.onebox.event.producttickettemplate.dao.TableConstants.ENTITY;
import static es.onebox.event.producttickettemplate.dao.TableConstants.JOIN_FIELDS;
import static es.onebox.event.producttickettemplate.dao.TableConstants.LANGUAGE;
import static es.onebox.event.producttickettemplate.dao.TableConstants.PRODUCT_MODEL;
import static es.onebox.event.producttickettemplate.dao.TableConstants.PRODUCT_TEMPLATE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.ArrayUtils;
import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectJoinStep;
import org.jooq.SortField;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import es.onebox.event.events.enums.TicketTemplateStatus;
import es.onebox.event.priceengine.sorting.SortUtils;
import es.onebox.event.producttickettemplate.controller.request.ProductTicketTemplateFilter;
import es.onebox.event.producttickettemplate.dao.mapper.ProductTicketTemplateLanguageMapper;
import es.onebox.event.producttickettemplate.dao.mapper.ProductTicketTemplateMapper;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateDTO;
import es.onebox.event.producttickettemplate.domain.dto.ProductTicketTemplateLanguageDTO;
import es.onebox.event.producttickettemplate.domain.enums.ProductTicketTemplateStatus;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelProductTicketTemplateRecord;
import es.onebox.jooq.dao.DaoImpl;
import es.onebox.jooq.exception.EntityNotFoundException;

@Repository
public class ProductTicketTemplateDao extends DaoImpl<CpanelProductTicketTemplateRecord, Integer> {

	private final ProductTicketTemplateMapper productTicketTemplateMapper = new ProductTicketTemplateMapper();
	private final ProductTicketTemplateLanguageMapper productTicketTemplateLanguageMapper = new ProductTicketTemplateLanguageMapper();
	private static final Map<String, Field<?>> FIELD_MAPPINGS = Map.of(
			"name", PRODUCT_TEMPLATE.NAME,
			"entityName", ENTITY.NOMBRE,
			"modelName", PRODUCT_MODEL.NAME,
			"status", PRODUCT_TEMPLATE.STATUS,
			"created", PRODUCT_TEMPLATE.CREATE_DATE,
			"updated", PRODUCT_TEMPLATE.UPDATE_DATE);

	protected ProductTicketTemplateDao() {
		super(Tables.CPANEL_PRODUCT_TICKET_TEMPLATE);
	}

	public Optional<ProductTicketTemplateDTO> findOneById(Integer id) {

		SelectFieldOrAsterisk[] selectFields = ArrayUtils.addAll(COMMON_FIELDS, JOIN_FIELDS);
		return Optional.ofNullable(dsl.select(selectFields)
				.from(PRODUCT_TEMPLATE)
				.innerJoin(ENTITY).on(PRODUCT_TEMPLATE.ENTITYID.eq(ENTITY.IDENTIDAD))
				.innerJoin(PRODUCT_MODEL).on(PRODUCT_TEMPLATE.MODELID.eq(PRODUCT_MODEL.MODELID))
				.where(buildBaseConditionById(id))
				.fetchOne(productTicketTemplateMapper));
	}

	public CpanelProductTicketTemplateRecord getByIdSimple(Integer id) {

		CpanelProductTicketTemplateRecord found = dsl.selectFrom(PRODUCT_TEMPLATE)
				.where(buildBaseConditionById(id))
				.fetchOne();

		if (found == null) {
			throw new EntityNotFoundException("No product ticket template with id " + id);
		}

		return found;
	}

	private Condition buildBaseConditionById(Integer id) {
		return PRODUCT_TEMPLATE.TEMPLATEID.eq(id)
				.and(PRODUCT_TEMPLATE.STATUS.notEqual(ProductTicketTemplateStatus.DELETED.getId().byteValue()));
	}

	public List<ProductTicketTemplateLanguageDTO> getLanguages(Long id) {
		return dsl.selectFrom(LANGUAGE).where(LANGUAGE.TEMPLATEID.eq(id.intValue()))
				.fetch(productTicketTemplateLanguageMapper);
	}

	public List<ProductTicketTemplateDTO> find(ProductTicketTemplateFilter filter) {

		SelectFieldOrAsterisk[] selectFields = ArrayUtils.addAll(COMMON_FIELDS, JOIN_FIELDS);
		SelectJoinStep<Record> baseQuery = dsl.select(selectFields)
				.from(PRODUCT_TEMPLATE)
				.innerJoin(ENTITY).on(PRODUCT_TEMPLATE.ENTITYID.eq(ENTITY.IDENTIDAD))
				.innerJoin(PRODUCT_MODEL).on(PRODUCT_TEMPLATE.MODELID.eq(PRODUCT_MODEL.MODELID));

		SelectConditionStep<Record> conditionQuery = baseQuery.where(buildConditions(filter));

		List<SortField<String>> sorts = SortUtils.buildSort(filter.getSort(),
				fieldName -> FIELD_MAPPINGS.getOrDefault(fieldName, PRODUCT_TEMPLATE.NAME));
		return conditionQuery
				.orderBy(sorts)
				.limit(filter.getLimit())
				.offset(filter.getOffset())
				.fetch(productTicketTemplateMapper);
	}

	public Long getTotalCount(ProductTicketTemplateFilter filter) {

		return dsl.selectCount()
				.from(PRODUCT_TEMPLATE)
				.innerJoin(ENTITY).on(PRODUCT_TEMPLATE.ENTITYID.eq(ENTITY.IDENTIDAD))
				.innerJoin(PRODUCT_MODEL).on(PRODUCT_TEMPLATE.MODELID.eq(PRODUCT_MODEL.MODELID))
				.where(buildConditions(filter))
				.fetchOneInto(Long.class);
	}

	private Condition buildConditions(ProductTicketTemplateFilter filter) {
		List<Condition> conditions = new ArrayList<>();

		if (filter.getFreeSearch() != null) {
			String freeSearch = filter.getFreeSearch();
			conditions.add(PRODUCT_TEMPLATE.NAME.likeIgnoreCase("%" + freeSearch + "%"));
		}
		if (filter.getModelType() != null) {
			conditions.add(PRODUCT_MODEL.MODELTYPE.eq(filter.getModelType().getModelTypeId()));
		}

		conditions.add(PRODUCT_TEMPLATE.STATUS.eq(ProductTicketTemplateStatus.ACTIVE.getId().byteValue()));

		return conditions.stream()
				.reduce(Condition::and)
				.orElse(DSL.trueCondition());
	}

	public boolean nameAlreadySetForEntity(String name, Integer entityId) {

		return dsl.fetchExists(dsl.selectOne().from(PRODUCT_TEMPLATE)
				.where(PRODUCT_TEMPLATE.NAME.equalIgnoreCase(name).and(
						PRODUCT_TEMPLATE.ENTITYID.eq(entityId)).and(
								PRODUCT_TEMPLATE.STATUS.eq(TicketTemplateStatus.ACTIVE.getId().byteValue()))));

	}
}
