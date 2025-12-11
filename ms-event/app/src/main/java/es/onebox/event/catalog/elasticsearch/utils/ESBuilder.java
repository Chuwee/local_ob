package es.onebox.event.catalog.elasticsearch.utils;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.ChildScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;
import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.core.serializer.dto.request.Operator;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.event.catalog.elasticsearch.properties.ElasticProperty;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by mmolinero on 5/03/19.
 */
public class ESBuilder {

    private static final String QUERY_STRING_WILDCARD = "*";

    private ESBuilder() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static void addMustTerm(BoolQuery.Builder query, ElasticProperty field, Integer value) {
        if (value != null) {
            query.must(QueryBuilders.term(term -> term.field(field.getProperty()).value(value)));
        }
    }

    public static void addMustTerm(BoolQuery.Builder query, ElasticProperty field, Long value) {
        if (value != null) {
            query.must(QueryBuilders.term(term -> term.field(field.getProperty()).value(value)));
        }
    }

    public static void addMustTerm(BoolQuery.Builder query, ElasticProperty field, Boolean value) {
        if (value != null) {
            query.must(QueryBuilders.term(term -> term.field(field.getProperty()).value(value)));
        }
    }

    public static void addMustTerm(BoolQuery.Builder query, ElasticProperty field, String value) {
        if (value != null) {
            query.must(QueryBuilders.term(term -> term.field(field.getProperty()).value(value)));
        }
    }

    public static void addMustNotTerm(BoolQuery.Builder query, ElasticProperty field, Integer value) {
        if (value != null) {
            query.mustNot(QueryBuilders.term(term -> term.field(field.getProperty()).value(value)));
        }
    }

    public static void addMustTerms(BoolQuery.Builder query, ElasticProperty field, List values) {
        if (values != null) {
            query.must(QueryBuilders.terms(term -> term
                    .field(field.getProperty())
                    .terms(terms -> terms.value(values.stream().map(FieldValue::of).toList()))));
        }
    }

    public static void addMustNotTerms(BoolQuery.Builder query, ElasticProperty field, List values) {
        if (values != null) {
            query.mustNot(QueryBuilders.terms(term -> term
                    .field(field.getProperty())
                    .terms(terms -> terms.value(values.stream().map(FieldValue::of).toList()))));
        }
    }

    public static void addMustMatch(BoolQuery.Builder query, ElasticProperty field, String value) {
        if (value != null) {
            query.must(QueryBuilders.match(match -> match.field(field.getProperty()).query(value)));
        }
    }

    public static void addShouldTerm(BoolQuery.Builder query, ElasticProperty field, Integer value) {
        if (value != null) {
            query.should(QueryBuilders.term(term -> term.field(field.getProperty()).value(value)));
        }
    }

    public static void addShouldTerms(BoolQuery.Builder query, ElasticProperty field, List values) {
        if (values != null) {
            query.should(QueryBuilders.terms(term -> term
                    .field(field.getProperty())
                    .terms(terms -> terms.value(values.stream().map(FieldValue::of).toList()))));
        }
    }

    public static void addShouldMatch(BoolQuery.Builder query, ElasticProperty field, String value) {
        if (value != null) {
            query.should(QueryBuilders.match(match -> match.field(field.getProperty()).query(value)));
        }
    }

    public static <T extends Serializable> void addFiltersWithOperator(BoolQuery.Builder query, ElasticProperty field,
                                                                       List<FilterWithOperator<T>> operations) {
        addFiltersWithOperator(query, field, operations, null);
    }

    public static <T extends Serializable> void addFiltersWithOperator(BoolQuery.Builder query, ElasticProperty field,
                                                                       List<FilterWithOperator<T>> operations, Function<T, Object> valueParser) {
        if (operations != null) {
            Function<T, ?> valueMapper = valueParser == null ? Function.identity() : valueParser;
            operations.stream()
                    .collect(Collectors.groupingBy(FilterWithOperator::getOperator))
                    .forEach((operator, filters) -> {
                        List<?> values = filters.stream()
                                .map(FilterWithOperator::getValue)
                                .map(valueMapper)
                                .collect(Collectors.toList());
                        addFiltersWithOperator(query, field, operator, values);
                    });
        }
    }

    public static <T> void addFiltersWithOperator(BoolQuery.Builder query, ElasticProperty field, Operator operator, List<T> values) {
        if (operator == Operator.EQUALS) {
            addMustTerms(query, field, values);
        } else if (operator == Operator.NOT_EQUALS) {
            addMustNotTerms(query, field, values);
        } else if (values != null) {
            values.forEach(value -> addMustRange(query, field, operator, value));
        }
    }

    public static void addMustRange(BoolQuery.Builder query, ElasticProperty field, Operator operator, Object value) {
        if (value != null) {
            String property = field.getProperty();
            query.must(buildRangeQuery(property, operator, value));
        }
    }

    public static void addShouldRange(BoolQuery.Builder query, ElasticProperty field, Operator operator, Object value) {
        if (value != null) {
            String property = field.getProperty();
            query.should(buildRangeQuery(property, operator, value));
            query.minimumShouldMatch("1");
        }
    }

    public static void addShouldQueryStringFilter(BoolQuery.Builder queryBuilder, List<ElasticProperty> fields, String filter) {
        if (StringUtils.isNotEmpty(filter) && CollectionUtils.isNotEmpty(fields)) {
            Query queryString = QueryBuilders.queryString(builder -> builder
                    .query(QUERY_STRING_WILDCARD + filter + QUERY_STRING_WILDCARD)
                    .fields(fields.stream().map(ElasticProperty::getProperty).toList()));
            queryBuilder.should(queryString);
            queryBuilder.minimumShouldMatch("1");
        }
    }

    public static void shouldByTwoFields(BoolQuery.Builder builder, String field1, String value1, String field2, String value2) {
        Query query1 = buildTermQuery(field1, value1);
        Query query2 = buildTermQuery(field2, value2);
        if (query1 != null && query2 != null) {
            BoolQuery.Builder shouldQuery = QueryBuilders.bool();
            shouldQuery.should(query1);
            shouldQuery.should(query2);
            builder.must(shouldQuery.build()._toQuery());
        } else if (query1 != null) {
            builder.must(query1);
        } else if (query2 != null) {
            builder.must(query2);
        }
    }

    public static void addShouldNestedConditionedQueryStringFilter(
            BoolQuery.Builder builder, ElasticProperty path,
            String conditionField, List conditionValues,
            String filterField, Object filterValue) {

        if (filterValue == null) {
            return;
        }
        BoolQuery.Builder mustQuery = QueryBuilders.bool();
        mustQuery.must(QueryBuilders.terms(term -> term
                .field(conditionField)
                .terms(terms -> terms.value(conditionValues.stream().map(FieldValue::of).toList()))));
        mustQuery.must(QueryBuilders.queryString(query -> query
                .query(QUERY_STRING_WILDCARD + filterValue + QUERY_STRING_WILDCARD).fields(filterField)));
        builder.should(buildNestedTermQuery(path.getProperty(), mustQuery));
    }

    private static Query buildTermQuery(String field, String value) {
        if (value == null) {
            return null;
        }
        return QueryBuilders.term(term -> term.field(field).value(value));
    }

    private static Query buildNestedTermQuery(String path, BoolQuery.Builder queryBuilder) {
        if (queryBuilder == null) {
            return null;
        }
        return QueryBuilders.nested(nested -> nested
                .path(path)
                .query(queryBuilder.build()._toQuery())
                .scoreMode(ChildScoreMode.None));
    }

    public static void addShouldNestedQueryStringFilter(BoolQuery.Builder queryBuilder, ElasticProperty path,
                                                        List<? extends ElasticProperty> fields, String filter) {

        if (StringUtils.isNotEmpty(filter) && CollectionUtils.isNotEmpty(fields)) {
            Query queryString = QueryBuilders.queryString(builder -> builder
                    .query(QUERY_STRING_WILDCARD + filter + QUERY_STRING_WILDCARD)
                    .fields(fields.stream().map(ElasticProperty::getProperty).toList()));

            Query nestedQuery = QueryBuilders.nested(nested -> nested
                    .path(path.getProperty())
                    .query(queryString)
                    .scoreMode(ChildScoreMode.None)
            );

            queryBuilder.should(nestedQuery);
            queryBuilder.minimumShouldMatch("1");
        }
    }

    private static Query buildRangeQuery(String property, Operator operator, Object value) {
        Query rangeQuery = QueryBuilders.range(range -> {
            RangeQuery.Builder field = range.field(property);
            if (operator == Operator.GREATER_THAN) {
                field.gt(JsonData.of(value));
            } else if (operator == Operator.GREATER_THAN_OR_EQUALS) {
                field.gte(JsonData.of(value));
            } else if (operator == Operator.LESS_THAN) {
                field.lt(JsonData.of(value));
            } else if (operator == Operator.LESS_THAN_OR_EQUALS) {
                field.lte(JsonData.of(value));
            } else {
                throw new IllegalArgumentException("Cannot add a range query filter with operator " + operator);
            }
            return field;
        });
        return rangeQuery;
    }

    public static void addMustExists(BoolQuery.Builder query, ElasticProperty field) {
        String property = field.getProperty();
        query.must(QueryBuilders.exists(exists -> exists.field(property)));
    }

    public static void addMustNotExists(BoolQuery.Builder query, ElasticProperty field) {
        String property = field.getProperty();
        query.mustNot(QueryBuilders.exists(exists -> exists.field(property)));
    }

    public static void addNestedFilter(BoolQuery.Builder query, ElasticProperty path, ElasticProperty field, Integer value) {
        if (value != null) {
            query.must(QueryBuilders.nested(nested -> {
                BoolQuery.Builder valueQuery = QueryBuilders.bool().must(
                        QueryBuilders.term(term -> term.field(field.getProperty()).value(value)));
                return nested
                        .path(path.getProperty())
                        .query(valueQuery.build()._toQuery())
                        .scoreMode(ChildScoreMode.None);
            }));
        }
    }

    public static <T> void addNestedFilter(BoolQuery.Builder query, ElasticProperty path, ElasticProperty field, List value) {
        if (value != null) {
            query.must(QueryBuilders.nested(nested -> {
                BoolQuery.Builder valueQuery = QueryBuilders.bool().must(QueryBuilders.terms(term -> term
                        .field(field.getProperty())
                        .terms(terms -> terms.value(value.stream().map(FieldValue::of).toList()))));
                return nested
                        .path(path.getProperty())
                        .query(valueQuery.build()._toQuery())
                        .scoreMode(ChildScoreMode.None);
            }));
        }
    }

    public static void addNestedQueryStringFilter(BoolQuery.Builder query, ElasticProperty path, ElasticProperty field, List<String> filter) {
        if (CollectionUtils.isNotEmpty(filter)) {
            Query queryString = QueryBuilders.queryString(builder -> builder
                    .query(String.join(" OR ", filter))
                    .fields(field.getProperty()));

            query.must(QueryBuilders.nested(nested -> nested
                    .path(path.getProperty())
                    .query(queryString)
                    .scoreMode(ChildScoreMode.None)));
        }
    }

    public static void addMustNested(BoolQuery.Builder queryBuilder, ElasticProperty path, Query nestedQuery) {
        queryBuilder.must(QueryBuilders.nested(nested -> nested
                .path(path.getProperty())
                .query(nestedQuery)
                .scoreMode(ChildScoreMode.None)));
    }

    public static List<SortOptions> prepareSort(final SortOperator<?> sortOperator) {
        List<SortOptions> sortCriteria = new ArrayList<>();
        if (sortOperator != null) {
            sortCriteria.addAll(es.onebox.elasticsearch.utils.SortBuilder.addFieldsSorting(sortOperator));
        }
        return sortCriteria;
    }

    public static void addIds(BoolQuery.Builder query, List<String> values) {
        if (CollectionUtils.isNotEmpty(values)) {
            query.must(QueryBuilders.ids(ids -> ids.values(values)));
        }
    }
}
