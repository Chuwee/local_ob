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
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.jooq.Condition;
import org.jooq.TableField;
import org.springframework.stereotype.Repository;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Repository
public class SessionTicketCommunicationElementDao extends CommunicationElementDao<TicketCommunicationElementTagType, CpanelElementosComTicketRecord> {
    protected SessionTicketCommunicationElementDao() {
        super(Tables.CPANEL_ELEMENTOS_COM_TICKET);
    }

    public Map<TicketCommunicationElementTagType, List<CpanelDescPorIdiomaRecord>> findSessionTicketCommunicationElements(Long idSesion,
                                                                                    TicketCommunicationElementCategory type, TicketCommunicationElementFilter filter){
        CpanelElementosComTicketRecord comElements = dsl.select()
                .from(Tables.CPANEL_SESION)
                .innerJoin(Tables.CPANEL_ELEMENTOS_COM_TICKET). on(checkJoinField(type))
                .where(Tables.CPANEL_SESION.IDSESION.eq(idSesion.intValue()))
                .fetchOneInto(Tables.CPANEL_ELEMENTOS_COM_TICKET);

        if(Objects.isNull(comElements)){
            return new HashMap<>();
        }
        Map<TicketCommunicationElementTagType, Integer> tagKeys = this.buildTagKeyMap(comElements);

        Map<Integer, List<CpanelDescPorIdiomaRecord>> descByItems = dsl
                .select(Tables.CPANEL_DESC_POR_IDIOMA.fields())
                .from(Tables.CPANEL_DESC_POR_IDIOMA)
                .where(buildWhere(filter, tagKeys, TicketCommunicationElementTagType.class))
                .fetchGroups(r -> r.get(Tables.CPANEL_DESC_POR_IDIOMA.IDITEM),
                        r -> r.into(Tables.CPANEL_DESC_POR_IDIOMA));

        return buildTagKeyResult(descByItems, tagKeys, TicketCommunicationElementTagType.class);
    }

    private Condition checkJoinField(TicketCommunicationElementCategory type) {
        TableField<CpanelSesionRecord, Integer> field = CommunicationElementsUtils.checkSessionJoinField(type);
        if (Objects.nonNull(field)){
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
