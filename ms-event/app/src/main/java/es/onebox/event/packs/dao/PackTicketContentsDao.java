package es.onebox.event.packs.dao;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.event.events.enums.TicketCommunicationElementCategory;
import es.onebox.event.packs.enums.PackTicketContentTagType;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Condition;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static es.onebox.jooq.cpanel.Tables.CPANEL_DESC_POR_IDIOMA;
import static es.onebox.jooq.cpanel.Tables.CPANEL_ELEMENTOS_COM_TICKET;
import static es.onebox.jooq.cpanel.Tables.CPANEL_PACK;

@Repository
public class PackTicketContentsDao extends DaoImpl<CpanelElementosComTicketRecord, Integer> {

    protected PackTicketContentsDao() {
        super(CPANEL_ELEMENTOS_COM_TICKET);
    }

    public Map<PackTicketContentTagType, List<CpanelDescPorIdiomaRecord>> getPackTicketContents(
            Integer packId, TicketCommunicationElementCategory category,
            PackTicketContentTagType type, Integer languageId) {
        EnumMap<PackTicketContentTagType, Integer> typeKeys = getTypeKeysMap(packId, category);

        Map<Integer, List<CpanelDescPorIdiomaRecord>> descByItems = dsl.select()
                .from(CPANEL_DESC_POR_IDIOMA)
                .where(buildCondition(typeKeys, type, languageId))
                .fetchGroups(
                        r -> r.get(CPANEL_DESC_POR_IDIOMA.IDITEM),
                        r -> r.into(CPANEL_DESC_POR_IDIOMA)
                );

        return buildResult(descByItems, typeKeys);
    }

    public CpanelElementosComTicketRecord insertNew() {
        return this.dsl.insertInto(CPANEL_ELEMENTOS_COM_TICKET)
                .defaultValues()
                .returning(Tables.CPANEL_ELEMENTOS_COM_TICKET.IDINSTANCIA)
                .fetchOne();
    }

    private EnumMap<PackTicketContentTagType, Integer> getTypeKeysMap(Integer packId, TicketCommunicationElementCategory category) {
        CpanelElementosComTicketRecord comElements =
                dsl.select()
                        .from(CPANEL_PACK)
                        .innerJoin(CPANEL_ELEMENTOS_COM_TICKET).on(checkJoinField(category))
                        .where(CPANEL_PACK.IDPACK.eq(packId))
                        .fetchOneInto(CPANEL_ELEMENTOS_COM_TICKET);

        EnumMap<PackTicketContentTagType, Integer> typeKeys = new EnumMap<>(PackTicketContentTagType.class);
        if (comElements != null) {
            typeKeys.put(PackTicketContentTagType.BODY, comElements.getPathimagencuerpo());
            typeKeys.put(PackTicketContentTagType.BANNER_MAIN, comElements.getPathimagenbanner1());
            typeKeys.put(PackTicketContentTagType.BANNER_SECONDARY, comElements.getPathimagenbanner2());
            typeKeys.put(PackTicketContentTagType.TITLE, comElements.getSubtitulo1());
            typeKeys.put(PackTicketContentTagType.SUBTITLE, comElements.getSubtitulo2());
            typeKeys.put(PackTicketContentTagType.TERMS, comElements.getTerminos());
            typeKeys.put(PackTicketContentTagType.ADDITIONAL_DATA, comElements.getOtrosdatos());
        }
        return typeKeys;
    }

    private static Condition buildCondition(EnumMap<PackTicketContentTagType, Integer> keyMap, PackTicketContentTagType type, Integer languageId) {
        Condition condition = DSL.noCondition();

        if (languageId != null) {
            condition = condition.and(CPANEL_DESC_POR_IDIOMA.IDIDIOMA.eq(languageId));
        }

        if (type != null) {
            return condition.and(CPANEL_DESC_POR_IDIOMA.IDITEM.eq(keyMap.get(type)));
        } else {
            return condition.and(CPANEL_DESC_POR_IDIOMA.IDITEM.in(keyMap.values()));
        }
    }

    private static Map<PackTicketContentTagType, List<CpanelDescPorIdiomaRecord>> buildResult(Map<Integer, List<CpanelDescPorIdiomaRecord>> typeItems, Map<PackTicketContentTagType, Integer> typeKeys) {
        EnumMap<PackTicketContentTagType, List<CpanelDescPorIdiomaRecord>> tagResults = new EnumMap<>(PackTicketContentTagType.class);
        for (Map.Entry<PackTicketContentTagType, Integer> tag : typeKeys.entrySet()) {
            if (typeItems.containsKey(tag.getValue())) {
                tagResults.put(tag.getKey(), typeItems.get(tag.getValue()));
            }
        }
        return tagResults;
    }

    private Condition checkJoinField(TicketCommunicationElementCategory category) {
        TableField<CpanelPackRecord, Integer> field = switch (category) {
            case PDF -> CPANEL_PACK.ELEMENTOCOMTICKET;
            case TICKET_OFFICE -> CPANEL_PACK.ELEMENTOCOMTICKETTAQUILLA;
            default -> null;
        };

        if (field != null) {
            return field.eq(Tables.CPANEL_ELEMENTOS_COM_TICKET.IDINSTANCIA);
        }
        throw ExceptionBuilder.build(CoreErrorCode.BAD_PARAMETER, "Unsupported ticket communication element category");
    }
}
