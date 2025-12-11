package es.onebox.event.tickettemplates.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelLiteralTicketTraduccionRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import static es.onebox.jooq.cpanel.Tables.CPANEL_LITERAL_TICKET_TRADUCCION;

@Repository
public class TicketTemplateLiteralTranslationDao extends DaoImpl<CpanelLiteralTicketTraduccionRecord, Integer> {

    public TicketTemplateLiteralTranslationDao() {
        super(CPANEL_LITERAL_TICKET_TRADUCCION);
    }

}
