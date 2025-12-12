package es.onebox.internal.automaticsales.filemanagement.dao;

import es.onebox.internal.automaticsales.filemanagement.dto.AutomaticSaleFileData;
import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
@CouchRepository(prefixKey = AutomaticSalesCouchDao.PREFIX,
        bucket = AutomaticSalesCouchDao.ONEBOX_OPERATIVE,
        scope = AutomaticSalesCouchDao.SCOPE,
        collection = AutomaticSalesCouchDao.COLLECTION)
public class AutomaticSalesCouchDao extends AbstractCouchDao<AutomaticSaleFileData> {

    public static final String ONEBOX_OPERATIVE = "onebox-operative";
    public static final String SCOPE = "sessions";
    public static final String COLLECTION = "automatic-sales";
    public static final String PREFIX = "session";
    public static final String FAILED_KEY = "failed";
    public static final String KEY = "session";
    private static final String DEFAULT_LANGUAGE = "en_US";

    public AutomaticSaleFileData getFileData(String sessionId, String filename) {
        return get(sessionId, filename);
    }

    public List<AutomaticSaleFileData> getBySessionId(String sessionId, Integer offset, Integer limit, String codeFreeSearch) {
        Map<String, Object> params = new HashMap<>();
        params.put("sessionId", Long.valueOf(sessionId));
        params.put("limit", Long.valueOf(limit));
        params.put("offset", Long.valueOf(offset));

        String whereCondition = "";
        if (Objects.nonNull(codeFreeSearch)) {
            whereCondition = "AND filename like $filename";
            params.put("filename", "%" + codeFreeSearch + "%");
        }

        String query = """
                SELECT %s.*
                FROM %s
                WHERE
                 sessionId = $sessionId
                 %s
                ORDER BY sessionId ASC
                LIMIT $limit
                OFFSET $offset
                """.formatted(
                this.collection(),
                this.from(),
                whereCondition);

        return queryList(query, params, AutomaticSaleFileData.class);
    }

}
