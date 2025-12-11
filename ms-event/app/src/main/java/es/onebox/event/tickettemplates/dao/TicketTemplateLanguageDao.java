package es.onebox.event.tickettemplates.dao;

import static es.onebox.jooq.cpanel.Tables.CPANEL_IDIOMA_PLANTILLA_TICKET;

import java.util.List;

import org.springframework.stereotype.Repository;

import es.onebox.jooq.cpanel.tables.records.CpanelIdiomaPlantillaTicketRecord;
import es.onebox.jooq.dao.DaoImpl;

@Repository
public class TicketTemplateLanguageDao extends DaoImpl<CpanelIdiomaPlantillaTicketRecord, Integer> {

    public TicketTemplateLanguageDao() {
        super(CPANEL_IDIOMA_PLANTILLA_TICKET);
    }

    public void deleteByTicketTemplate(Long templateId) {
        dsl.delete(CPANEL_IDIOMA_PLANTILLA_TICKET).
                where(CPANEL_IDIOMA_PLANTILLA_TICKET.IDPLANTILLA.eq(templateId.intValue())).
                execute();
    }

    public List<CpanelIdiomaPlantillaTicketRecord> findByTicketTemplateId(Integer templateId) {
        return this.dsl.select().from(CPANEL_IDIOMA_PLANTILLA_TICKET).where(CPANEL_IDIOMA_PLANTILLA_TICKET.IDPLANTILLA.eq(templateId))
                .fetchInto(CpanelIdiomaPlantillaTicketRecord.class);
    }
}
