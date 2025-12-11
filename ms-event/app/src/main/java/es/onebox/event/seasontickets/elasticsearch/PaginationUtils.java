package es.onebox.event.seasontickets.elasticsearch;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.elasticsearch.dao.Page;

import java.io.Serializable;
import java.util.List;

public class PaginationUtils {

    private PaginationUtils() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static Page buildPage(BaseRequestFilter request) {
        Page page = new Page(0, request.getLimit().intValue());
        page.setFromItem(request.getOffset().intValue());
        return page;
    }

    public static <T extends Serializable> void fillPaginationResult(
            BaseResponseCollection<T, Metadata> response, BaseRequestFilter request,
            SearchResponse queryResponse, List<T> data) {
        Metadata metadata = new Metadata();
        metadata.setLimit(request.getLimit());
        metadata.setOffset(request.getOffset());
        if (queryResponse != null && queryResponse.hits() != null) {
            metadata.setTotal(queryResponse.hits().total().value());
        } else {
            metadata.setTotal(0L);
        }
        response.setData(data);
        response.setMetadata(metadata);
    }
}
