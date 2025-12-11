package es.onebox.event.communicationelements.dao;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.jooq.Condition;
import org.jooq.Table;
import org.jooq.UpdatableRecord;
import org.jooq.impl.DSL;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.communicationelements.dto.CommunicationElementFilter;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.dao.DaoImpl;

public abstract class CommunicationElementDao<E extends Enum<E>, R extends UpdatableRecord<R>> extends DaoImpl<R, Integer> {
    
    protected CommunicationElementDao(Table<R> table) {
        super(table);
    }

    protected abstract Map<E, Integer> buildTagKeyMap(R record);
    
    protected Condition buildWhere(CommunicationElementFilter<E> filter, Map<E, Integer> tagKeys, Class<E> clazz) {
        Condition condition = DSL.noCondition();
        boolean noTagFilter = filter == null || CommonUtils.isEmpty(filter.getTags());
        for(E tag : EnumSet.allOf(clazz) ) {
            condition = checkTag(filter, tagKeys, condition, noTagFilter, tag);
        }
        if ( filter != null && filter.getLanguageId() != null) {
            condition = condition.and(Tables.CPANEL_DESC_POR_IDIOMA.IDIDIOMA.eq(filter.getLanguageId()));
        }
        return condition;
    }

    protected Condition checkTag(CommunicationElementFilter<E> filter, Map<E, Integer> tagKeys,
                               Condition condition, boolean noTagFilter, E header) {
        
        if (noTagFilter || filter.getTags().contains(header)) {
            condition = condition.or(Tables.CPANEL_DESC_POR_IDIOMA.IDITEM.eq(tagKeys.get(header)));
        }
        return condition;
    }
    
    protected Map<E, List<CpanelDescPorIdiomaRecord>> buildTagKeyResult(Map<Integer, List<CpanelDescPorIdiomaRecord>> tagItems, Map<E, Integer> tagKeys, Class<E> clazz) {
        EnumMap<E, List<CpanelDescPorIdiomaRecord>> tagResults = new EnumMap<>(clazz);
        for (Map.Entry<E, Integer> tag : tagKeys.entrySet()) {
            if (tagItems.containsKey(tag.getValue())) {
                tagResults.put(tag.getKey(), tagItems.get(tag.getValue()));
            }
        }
        return tagResults;
    }
}