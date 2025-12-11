package es.onebox.event.catalog.elasticsearch.utils;

import co.elastic.clients.elasticsearch._types.SearchType;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import es.onebox.elasticsearch.dao.AbstractElasticDao;
import es.onebox.elasticsearch.dao.ElasticDocument;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ElasticSearchUtils {

    public static final int PAGE_SIZE = 100000;

    private ElasticSearchUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static <T extends ElasticDocument> List<T> getAll(AbstractElasticDao<T> dao, BoolQuery.Builder query, String routingKey) {
        return getAllFilteredFields(dao, query, routingKey, null);
    }

    public static <T extends ElasticDocument> List<T> getAllFilteredFields(AbstractElasticDao<T> dao, BoolQuery.Builder query,
                                                                           String routingKey, List<String> fields) {
        SearchRequest.Builder request = new SearchRequest.Builder()
                .index(dao.getIndexName())
                .searchType(SearchType.QueryThenFetch)
                .query(query.build()._toQuery())
                .trackTotalHits(totalHits -> totalHits.enabled(true))
                .size(PAGE_SIZE);
        if (CollectionUtils.isNotEmpty(fields)) {
            request.source(src -> src.filter(f -> f.includes(fields)));
        }
        if (StringUtils.isNotEmpty(routingKey)) {
            request.routing(routingKey);
        }

        SearchResponse<T> response = dao.search(request.build());
        return response.hits().hits().stream().map(Hit::source).collect(Collectors.toList());
    }

}
