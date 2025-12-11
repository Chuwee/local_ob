package es.onebox.event.events.dao;

import es.onebox.core.exception.CoreErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.event.communicationelements.dao.CommunicationElementDao;
import es.onebox.event.communicationelements.enums.TicketCommunicationElementTagType;
import es.onebox.event.communicationelements.utils.CommunicationElementsUtils;
import es.onebox.event.events.enums.TicketCommunicationElementCategory;
import es.onebox.event.events.request.TicketCommunicationElementFilter;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelEventoRecord;
import org.jooq.Condition;
import org.jooq.TableField;
import org.springframework.stereotype.Repository;

import jakarta.validation.constraints.NotNull;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class EventTicketCommunicationElementDao extends CommunicationElementDao<TicketCommunicationElementTagType, CpanelElementosComTicketRecord> {

    protected EventTicketCommunicationElementDao() {
        super(Tables.CPANEL_ELEMENTOS_COM_TICKET);
    }

    public CpanelElementosComTicketRecord insertNew() {
        return this.dsl.insertInto(Tables.CPANEL_ELEMENTOS_COM_TICKET)
                .defaultValues()
                .returning(Tables.CPANEL_ELEMENTOS_COM_TICKET.IDINSTANCIA)
                .fetchOne();
    }

    public Map<TicketCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> findEventTicketCommunicationElements(
            final Integer eventId, TicketCommunicationElementFilter filter, @NotNull TicketCommunicationElementCategory type) {

        CpanelElementosComTicketRecord commElementes = dsl.select()
                .from(Tables.CPANEL_EVENTO)
                .innerJoin(Tables.CPANEL_ELEMENTOS_COM_TICKET).on(checkJoinField(type))
                .where(Tables.CPANEL_EVENTO.IDEVENTO.eq(eventId))
                .fetchOneInto(Tables.CPANEL_ELEMENTOS_COM_TICKET);

        if (commElementes == null) {
            return new HashMap<>();
        }

        Map<TicketCommunicationElementTagType, Integer> tagKeys = this.buildTagKeyMap(commElementes);

        Map<Integer, List<CpanelDescPorIdiomaRecord>> descByItems = dsl
                .select(Tables.CPANEL_DESC_POR_IDIOMA.fields())
                .from(Tables.CPANEL_DESC_POR_IDIOMA)
                .where(buildWhere(filter, tagKeys, TicketCommunicationElementTagType.class))
                .fetchGroups(r -> r.get(Tables.CPANEL_DESC_POR_IDIOMA.IDITEM),
                        r -> r.into(Tables.CPANEL_DESC_POR_IDIOMA));

        return buildTagKeyResult(descByItems, tagKeys, TicketCommunicationElementTagType.class);
    }

    private Condition checkJoinField(TicketCommunicationElementCategory type) {
        TableField<CpanelEventoRecord, Integer> field = CommunicationElementsUtils.checkJoinField(type);
        if (field != null) {
            return field.eq(Tables.CPANEL_ELEMENTOS_COM_TICKET.IDINSTANCIA);
        }
        throw ExceptionBuilder.build(CoreErrorCode.BAD_PARAMETER, "Unsupported ticket communication element category");
    }

    @Override
    public Map<TicketCommunicationElementTagType, Integer> buildTagKeyMap(CpanelElementosComTicketRecord record) {
        EnumMap<TicketCommunicationElementTagType, Integer> keyMap = new EnumMap<>(TicketCommunicationElementTagType.class);

        if (record.getPathimagencabecera() != null) {
            keyMap.put(TicketCommunicationElementTagType.HEADER, record.getPathimagencabecera());
        }
        if (record.getPathimagencuerpo() != null) {
            keyMap.put(TicketCommunicationElementTagType.BODY, record.getPathimagencuerpo());
        }
        if (record.getPathimagenlogo() != null) {
            keyMap.put(TicketCommunicationElementTagType.EVENT_LOGO, record.getPathimagenlogo());
        }
        if (record.getPathimagenbanner1() != null) {
            keyMap.put(TicketCommunicationElementTagType.BANNER_MAIN, record.getPathimagenbanner1());
        }
        if (record.getPathimagenbanner2() != null) {
            keyMap.put(TicketCommunicationElementTagType.BANNER_SECONDARY, record.getPathimagenbanner2());
        }
        if (record.getPathimagenbanner3() != null) {
            keyMap.put(TicketCommunicationElementTagType.BANNER_CHANNEL_LOGO, record.getPathimagenbanner3());
        }
        if (record.getOtrosdatos() != null) {
            keyMap.put(TicketCommunicationElementTagType.ADDITIONAL_DATA, record.getOtrosdatos());
        }
        if (record.getSubtitulo1() != null) {
            keyMap.put(TicketCommunicationElementTagType.TITLE, record.getSubtitulo1());
        }
        if (record.getSubtitulo2() != null) {
            keyMap.put(TicketCommunicationElementTagType.SUBTITLE, record.getSubtitulo2());
        }
        if (record.getTerminos() != null) {
            keyMap.put(TicketCommunicationElementTagType.TERMS, record.getTerminos());
        }
        return keyMap;
    }
}
