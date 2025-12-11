package es.onebox.event.tickettemplates.dao;

import es.onebox.jooq.cpanel.tables.CpanelModeloTicket;
import es.onebox.jooq.cpanel.tables.records.CpanelModeloTicketRecord;
import es.onebox.jooq.dao.DaoImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TicketTemplateModelDao extends DaoImpl<CpanelModeloTicketRecord, Integer> {

    protected TicketTemplateModelDao() {
        super(CpanelModeloTicket.CPANEL_MODELO_TICKET);
    }

    private static final CpanelModeloTicket MODEL = CpanelModeloTicket.CPANEL_MODELO_TICKET.as("model");

    public List<CpanelModeloTicketRecord> findTemplateModels() {
        return dsl.select(MODEL.fields()).
                from(MODEL).
                orderBy(MODEL.NOMBRE.asc()).
                fetchInto(CpanelModeloTicketRecord.class);
    }

}
