package es.onebox.mgmt.seasontickets.converter;

import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketTicketTemplates;
import es.onebox.mgmt.events.enums.EventTicketTemplateType;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketTicketTemplateDTO;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeasonTicketTicketTemplatesConverter {

    private SeasonTicketTicketTemplatesConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static List<SeasonTicketTicketTemplateDTO> convert(SeasonTicketTicketTemplates templates) {
        if (templates == null) {
            return Collections.emptyList();
        }
        List<SeasonTicketTicketTemplateDTO> ticketTemplates = new ArrayList<>();
        convertAndAdd(ticketTemplates, templates.getTicketPdfTemplateId(), EventTicketTemplateType.SINGLE, TicketTemplateFormat.PDF);
        convertAndAdd(ticketTemplates, templates.getTicketPrinterTemplateId(), EventTicketTemplateType.SINGLE, TicketTemplateFormat.PRINTER);
        convertAndAdd(ticketTemplates, templates.getIndividualTicketPassbookTemplateCode(), EventTicketTemplateType.SINGLE, TicketTemplateFormat.PASSBOOK);
        convertAndAdd(ticketTemplates, templates.getGroupTicketPdfTemplateId(), EventTicketTemplateType.GROUP, TicketTemplateFormat.PDF);
        convertAndAdd(ticketTemplates, templates.getGroupTicketPrinterTemplateId(), EventTicketTemplateType.GROUP, TicketTemplateFormat.PRINTER);
        convertAndAdd(ticketTemplates, templates.getGroupTicketPassbookTemplateCode(), EventTicketTemplateType.GROUP, TicketTemplateFormat.PASSBOOK);
        convertAndAdd(ticketTemplates, templates.getIndividualInvitationPdfTemplateId(), EventTicketTemplateType.SINGLE_INVITATION, TicketTemplateFormat.PDF);
        convertAndAdd(ticketTemplates, templates.getIndividualInvitationPrinterTemplateId(), EventTicketTemplateType.SINGLE_INVITATION, TicketTemplateFormat.PRINTER);
        convertAndAdd(ticketTemplates, templates.getIndividualInvitationPassbookTemplateCode(), EventTicketTemplateType.SINGLE_INVITATION, TicketTemplateFormat.PASSBOOK);
        convertAndAdd(ticketTemplates, templates.getGroupInvitationPdfTemplateId(), EventTicketTemplateType.GROUP_INVITATION, TicketTemplateFormat.PDF);
        convertAndAdd(ticketTemplates, templates.getGroupInvitationPrinterTemplateId(), EventTicketTemplateType.GROUP_INVITATION, TicketTemplateFormat.PRINTER);
        convertAndAdd(ticketTemplates, templates.getGroupInvitationPassbookTemplateCode(), EventTicketTemplateType.GROUP_INVITATION, TicketTemplateFormat.PASSBOOK);
        convertAndAdd(ticketTemplates, templates.getSessionPackPassbookTemplateCode(), EventTicketTemplateType.SEASON_PACK, TicketTemplateFormat.PASSBOOK);
        return ticketTemplates;
    }

    private static void convertAndAdd(List<SeasonTicketTicketTemplateDTO> templates, String templateId, EventTicketTemplateType type, TicketTemplateFormat format) {
        if (templateId != null) {
            SeasonTicketTicketTemplateDTO template = new SeasonTicketTicketTemplateDTO();
            template.setId(templateId);
            template.setType(type);
            template.setFormat(format);
            templates.add(template);
        }
    }

    private static void convertAndAdd(List<SeasonTicketTicketTemplateDTO> templates, Long templateId, EventTicketTemplateType type, TicketTemplateFormat format) {
        if (templateId != null) {
            convertAndAdd(templates, String.valueOf(templateId), type, format);
        }
    }
}
