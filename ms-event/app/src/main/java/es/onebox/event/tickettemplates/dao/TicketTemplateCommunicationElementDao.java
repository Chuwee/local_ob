package es.onebox.event.tickettemplates.dao;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import es.onebox.event.communicationelements.dao.CommunicationElementDao;
import es.onebox.event.tickettemplates.dto.TicketCommunicationElementFilter;
import es.onebox.event.tickettemplates.dto.TicketTemplateTagType;
import es.onebox.jooq.cpanel.Tables;
import es.onebox.jooq.cpanel.tables.records.CpanelDescPorIdiomaRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComTicketRecord;

@Repository
public class TicketTemplateCommunicationElementDao extends CommunicationElementDao<TicketTemplateTagType, CpanelElementosComTicketRecord> {

    protected TicketTemplateCommunicationElementDao() {
        super(Tables.CPANEL_ELEMENTOS_COM_TICKET);
    }

    public Map<TicketTemplateTagType, List<CpanelDescPorIdiomaRecord>> findCommunicationElements(Long ticketTemplateId, TicketCommunicationElementFilter filter) {
        CpanelElementosComTicketRecord ticketComElements = dsl.select()
                .from(Tables.CPANEL_ELEMENTOS_COM_TICKET)
                .innerJoin(Tables.CPANEL_PLANTILLA_TICKET).on(Tables.CPANEL_PLANTILLA_TICKET.ELEMENTOCOMTICKET.eq(Tables.CPANEL_ELEMENTOS_COM_TICKET.IDINSTANCIA))
                .where(Tables.CPANEL_PLANTILLA_TICKET.IDPLANTILLA.eq(ticketTemplateId.intValue()))
                .fetchOneInto(Tables.CPANEL_ELEMENTOS_COM_TICKET);

        if (ticketComElements == null) {
            return new HashMap<>();
        }

        Map<TicketTemplateTagType, Integer> tagKeys = this.buildTagKeyMap(ticketComElements);

        Map<Integer, List<CpanelDescPorIdiomaRecord>> descByItems = dsl
                .select(Tables.CPANEL_DESC_POR_IDIOMA.fields())
                .from(Tables.CPANEL_DESC_POR_IDIOMA)
                .where(buildWhere(filter, tagKeys, TicketTemplateTagType.class))
                .fetchGroups(r -> r.get(Tables.CPANEL_DESC_POR_IDIOMA.IDITEM),
                        r -> r.into(Tables.CPANEL_DESC_POR_IDIOMA));

        return buildTagKeyResult(descByItems, tagKeys, TicketTemplateTagType.class);
    }

    @Override
    public Map<TicketTemplateTagType, Integer> buildTagKeyMap(CpanelElementosComTicketRecord record) {
        EnumMap<TicketTemplateTagType, Integer> keyMap = new EnumMap<>(TicketTemplateTagType.class);
        if (record.getPathimagencabecera() != null) {
            keyMap.put(TicketTemplateTagType.HEADER, record.getPathimagencabecera());
        }
        if (record.getPathimagencuerpo() != null) {
            keyMap.put(TicketTemplateTagType.BODY, record.getPathimagencuerpo());
        }
        if (record.getPathimagenlogo() != null) {
            keyMap.put(TicketTemplateTagType.EVENT_LOGO, record.getPathimagenlogo());
        }
        if (record.getPathimagenbanner1() != null) {
            keyMap.put(TicketTemplateTagType.BANNER_MAIN, record.getPathimagenbanner1());
        }
        if (record.getPathimagenbanner2() != null) {
            keyMap.put(TicketTemplateTagType.BANNER_SECONDARY, record.getPathimagenbanner2());
        }
        if (record.getPathimagenbanner3() != null) {
            keyMap.put(TicketTemplateTagType.BANNER_CHANNEL_LOGO, record.getPathimagenbanner3());
        }
        if (record.getTerminos() != null) {
            keyMap.put(TicketTemplateTagType.TERMS_AND_CONDITIONS, record.getTerminos());
        }
        return keyMap;
    }

}
