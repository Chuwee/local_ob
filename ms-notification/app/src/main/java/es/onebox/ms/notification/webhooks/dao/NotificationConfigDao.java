package es.onebox.ms.notification.webhooks.dao;

import es.onebox.core.serializer.dto.request.Direction;
import es.onebox.core.serializer.dto.request.SortDirection;
import es.onebox.couchbase.annotation.CouchRepository;
import es.onebox.couchbase.dao.AbstractCouchDao;
import es.onebox.ms.notification.webhooks.dto.NotificationConfig;
import es.onebox.ms.notification.webhooks.dto.NotificationConfigs;
import es.onebox.ms.notification.webhooks.dto.SearchNotificationConfigFilterDTO;
import es.onebox.ms.notification.webhooks.enums.NotificationSortableField;
import es.onebox.ms.notification.webhooks.enums.NotificationVisible;
import es.onebox.ms.notification.webhooks.enums.NotificationsScope;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@CouchRepository(prefixKey = NotificationConfigDao.KEY,
        bucket = NotificationConfigDao.BUCKET,
        scope = NotificationConfigDao.SCOPE,
        collection = NotificationConfigDao.COLLECTION)
public class NotificationConfigDao extends AbstractCouchDao<NotificationConfig> {

    public static final String KEY = "webhookConfig";
    public static final String BUCKET = "onebox-operative";
    public static final String SCOPE = "webhooks";
    public static final String COLLECTION = "configs";

    public NotificationConfig get(String documentId) {
        return super.get(documentId);
    }

    public List<NotificationConfig> advancedGet(Long entityId) {
        String query = "SELECT " + this.collection() + ".*" +
                " FROM " + this.from() +
                " WHERE " +
                "  entityId = $entityId";

        Map<String, Object> params = new HashMap<>();
        params.put("entityId", entityId);

        return queryList(query, params, NotificationConfig.class);
    }

    public NotificationConfigs advancedGet(SearchNotificationConfigFilterDTO filter) {
        NotificationConfigs notificationConfigs = new NotificationConfigs();
        String queryList = generateQueryList(filter);
        String queryCount = generateQueryCount(filter);

        Map<String, Object> params = new HashMap<>();
        if (filter.getVisible() != null) {
            params.put("visible", filter.getVisible().name());
        } else {
            params.put("visible", NotificationVisible.VISIBLE.name());
        }

        if (filter.getEntityId() != null) {
            params.put("entityId", filter.getEntityId());
        }

        if (filter.getOperatorId() != null) {
            params.put("operatorId", filter.getOperatorId());
        }


        if (filter.getScope() != null && !filter.getScope().isEmpty()) {
            params.put("scope", filter.getScope().stream().map(Enum::name).collect(Collectors.toList()));

            if (filter.getScope().contains(NotificationsScope.CHANNEL)) {
                params.put("channelId", filter.getChannelId());
            }
        }

        if (filter.getStatus() != null) {
            params.put("status", filter.getStatus().name());
        }
        params.put("offset", filter.getOffset() != null ? filter.getOffset() : Long.valueOf(0));
        params.put("limit", filter.getLimit() != null ? filter.getLimit() : Long.valueOf(20));

        Integer count = queryObject(queryCount, params, Integer.class);
        notificationConfigs.setTotalElements(count.longValue());
        if (count > 0) {
            List<NotificationConfig> configs = queryList(queryList, params, NotificationConfig.class);
            notificationConfigs.setConfigs(configs);
        }
        return notificationConfigs;
    }

    private String generateQueryList(SearchNotificationConfigFilterDTO filter) {
        String query = "SELECT " + this.collection() + ".*" +
                " FROM " + this.from() +
                " WHERE " +
                "  visible = $visible";
        if (filter.getScope() != null && !filter.getScope().isEmpty()) {
            query += "  and `scope` IN $scope";
            if (filter.getScope().contains(NotificationsScope.CHANNEL)) {
                query += "  and channelId = $channelId";
            }
        }
        if (filter.getEntityId() == null) {
            query += "  and entityId is not null";
        } else {
            query += "  and entityId = $entityId";
        }
        if (filter.getOperatorId() != null) {
            query += "  and operatorId = $operatorId";
        }
        if (filter.getStatus() != null) {
            query += "  and status = $status";
        }
        query += buildOrderBy(filter);
        query += "  LIMIT $limit " + "  OFFSET $offset ";
        return query;
    }

    private String generateQueryCount(SearchNotificationConfigFilterDTO filter) {
        String query = "SELECT count(*)" +
                " FROM " + this.from() +
                " WHERE " +
                "  visible = $visible";

        if (filter.getScope() != null && !filter.getScope().isEmpty()) {
            query += "  and `scope` IN $scope";
            if (filter.getScope().contains(NotificationsScope.CHANNEL)) {
                query += "  and channelId = $channelId";
            }
        }

        if (filter.getEntityId() == null) {
            query += "  and entityId is not null";
        } else {
            query += "  and entityId = $entityId";
        }

        if(filter.getOperatorId() != null) {
            query += "  and operatorId = $operatorId";
        }

        if (filter.getStatus() != null) {
            query += "  and status = $status";
        }

        return query;
    }

    public static String buildOrderBy(SearchNotificationConfigFilterDTO filter) {
        StringBuilder sb = new StringBuilder(" order by ");
        if (filter != null && filter.getSort() != null && CollectionUtils.isNotEmpty(filter.getSort().getSortDirections())) {
            for (SortDirection<NotificationSortableField> sortDirection : filter.getSort().getSortDirections()) {
                Direction direction = sortDirection.getDirection();
                NotificationSortableField field = sortDirection.getValue();
                sb.append(field.getDtoName()).append(StringUtils.SPACE).append(direction.getDirection());
                sb.append(", ");
            }
            sb.deleteCharAt(sb.lastIndexOf(","));
        } else {
            sb.append("createdAt DESC");
        }
        return sb.toString();
    }

    public void upsert(NotificationConfig document) {
        super.upsert(document.getDocumentId(), document);
    }
}
