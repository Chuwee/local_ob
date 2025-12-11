package es.onebox.event.tickettemplates.dao;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.tickettemplates.dto.TicketLiteralElementFilter;
import es.onebox.jooq.cpanel.tables.records.CpanelLiteralTicketRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelLiteralTicketTraduccionRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static es.onebox.jooq.cpanel.Tables.CPANEL_LITERAL_TICKET;
import static es.onebox.jooq.cpanel.Tables.CPANEL_LITERAL_TICKET_TRADUCCION;

@Repository
public class TicketTemplateLiteralDao extends DaoImpl<CpanelLiteralTicketRecord, Integer> {

    public TicketTemplateLiteralDao() {
        super(CPANEL_LITERAL_TICKET);
    }

    public Map<CpanelLiteralTicketRecord, List<CpanelLiteralTicketTraduccionRecord>> findLiterals(Long ticketTemplateId, TicketLiteralElementFilter filter) {
        return dsl.select()
                .from(CPANEL_LITERAL_TICKET)
                .leftJoin(CPANEL_LITERAL_TICKET_TRADUCCION).on(
                        CPANEL_LITERAL_TICKET.IDLITERAL.eq(CPANEL_LITERAL_TICKET_TRADUCCION.IDLITERAL).and(
                                CPANEL_LITERAL_TICKET_TRADUCCION.IDPLANTILLA.eq(ticketTemplateId.intValue())
                        ))
                .where(buildWhere(filter))
                .fetchGroups(
                        l -> l.into(CPANEL_LITERAL_TICKET),
                        l -> l.into(CPANEL_LITERAL_TICKET_TRADUCCION)
                );
    }

    private static Condition buildWhere(TicketLiteralElementFilter filter) {
        Condition condition = DSL.noCondition();
        if (filter != null) {
            if (!CommonUtils.isEmpty(filter.getCodes())) {
                for (String code : filter.getCodes()) {
                    condition = condition.and(CPANEL_LITERAL_TICKET.CODIGO.eq(code));
                }
            }
            if (filter.getLanguageId() != null) {
                condition = condition.and(CPANEL_LITERAL_TICKET_TRADUCCION.IDIDIOMA.eq(filter.getLanguageId()));
            }
        }
        return condition;
    }

}
