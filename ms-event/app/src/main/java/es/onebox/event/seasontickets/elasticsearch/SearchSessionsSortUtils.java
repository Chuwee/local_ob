package es.onebox.event.seasontickets.elasticsearch;

import co.elastic.clients.elasticsearch._types.ScriptSortType;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOptionsBuilders;
import co.elastic.clients.elasticsearch._types.SortOrder;
import es.onebox.core.serializer.dto.request.Direction;
import es.onebox.core.serializer.dto.request.SortDirection;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.event.catalog.elasticsearch.properties.SessionElasticProperty;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchSessionsSortUtils {

    public static List<SortOptions> prepareSort(final SortOperator<SearchSessionsSortableField> sortOperator, Long sessionId) {
        List<SortOptions> sortCriteria = new ArrayList<>();

        if (sortOperator != null && CollectionUtils.isNotEmpty(sortOperator.getSortDirections())) {
            for (SortDirection direction : sortOperator.getSortDirections()) {

                SearchSessionsSortableField field = (SearchSessionsSortableField) direction.getValue();
                if (SearchSessionsSortableField.ASSIGNATION_STATUS.equals(field)) {
                    String query = "doc['" + SessionElasticProperty.RELATED_SEASON_SESSION_IDS.getProperty() + "'].contains(" + sessionId + "L) ? 0 : 1";
                    SortOptions sortBuilder = SortOptionsBuilders
                            .script(script -> script.script(s -> s.inline(v -> v.source(query)))
                                    .type(ScriptSortType.Number)
                                    .order(parseDirection(direction.getDirection())));
                    sortCriteria.add(sortBuilder);
                } else {
                    sortCriteria.add(es.onebox.elasticsearch.utils.SortBuilder.addFieldSorting(direction));
                }
            }
        }
        // Add always a sort by ID in last case
        sortCriteria.add(SortOptionsBuilders.field(sort ->
                sort.field(SessionElasticProperty.ID.getProperty()).order(SortOrder.Desc)));

        return sortCriteria;
    }

    private static SortOrder parseDirection(Direction direction) {
        if (direction != null) {
            switch (direction) {
                case ASC:
                    return SortOrder.Asc;
                case DESC:
                    return SortOrder.Desc;
            }
        }

        return null;
    }
}
