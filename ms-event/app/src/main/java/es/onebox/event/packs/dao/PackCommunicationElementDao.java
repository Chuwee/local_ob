package es.onebox.event.packs.dao;

import es.onebox.event.packs.dto.PackCommunicationElementFilter;
import es.onebox.event.packs.enums.PackTagType;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.CpanelElementosComPack;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComPackRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class PackCommunicationElementDao extends DaoImpl<CpanelElementosComPackRecord, Integer> {

    private static final CpanelElementosComPack comElementPackTable = Tables.CPANEL_ELEMENTOS_COM_PACK;

    protected PackCommunicationElementDao() {
        super(comElementPackTable);
    }

    public List<CpanelElementosComPackRecord> findCommunicationElements(Long packId, PackCommunicationElementFilter filter) {
        return dsl.select()
                .from(comElementPackTable)
                .where(buildWhere(packId, filter))
                .orderBy(comElementPackTable.POSITION)
                .fetch().into(CpanelElementosComPackRecord.class);
    }

    private Condition buildWhere(Long packId, PackCommunicationElementFilter filter) {
        Condition where = DSL.trueCondition();
        if (packId != null) {
            where = where.and(comElementPackTable.IDPACK.eq(packId.intValue()));
        }
        if (filter == null) {
            return where;
        }
        if (filter.getTags() != null) {
            where = where.and(comElementPackTable.IDTAG.in(filter.getTags()
                    .stream()
                    .map(PackTagType::getId)
                    .collect(Collectors.toList())));
        }
        if (filter.getLanguageId() != null) {
            where = where.and(comElementPackTable.IDIOMA.eq(filter.getLanguageId()));
        }
        return where;
    }

    public void deletePackComElementByIds(List<Integer> rangeIds) {
        dsl.delete(comElementPackTable)
                .where(comElementPackTable.IDPACK.in(rangeIds))
                .execute();
    }
}
