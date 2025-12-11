package es.onebox.mgmt.events.converter;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventTicketTemplates;
import es.onebox.mgmt.events.dto.EventTicketTemplateDTO;
import es.onebox.mgmt.events.enums.EventTicketTemplateType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.products.enums.ProductTicketTemplateType;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormat;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormatPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class EventTicketTemplatesConverter {

    private EventTicketTemplatesConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static List<EventTicketTemplateDTO> convert(EventTicketTemplates templates) {
        if (templates == null) {
            return Collections.emptyList();
        }
        List<EventTicketTemplateDTO> ticketTemplates = new ArrayList<>();
        addPassbook(templates, ticketTemplates);
        addPdf(templates, ticketTemplates);
        addPrinter(templates, ticketTemplates);
        return ticketTemplates;
    }

    public static Event toEvent(Long eventId, EventTicketTemplateType type, TicketTemplateFormatPath templateFormat, IdDTO templateId) {
        Event event = new Event();
        event.setId(eventId);
        EventTicketTemplates ticketTemplates = new EventTicketTemplates();
        switch (type) {
            case SINGLE -> setTemplateId(templateId, templateFormat, ticketTemplates::setIndividualTicketPdfTemplateId,
                    ticketTemplates::setIndividualTicketPrinterTemplateId);
            case GROUP -> setTemplateId(templateId, templateFormat, ticketTemplates::setGroupTicketPdfTemplateId,
                    ticketTemplates::setGroupTicketPrinterTemplateId);
            case SINGLE_INVITATION -> setTemplateId(templateId, templateFormat, ticketTemplates::setIndividualInvitationPdfTemplateId,
                    ticketTemplates::setIndividualInvitationPrinterTemplateId);
            case GROUP_INVITATION -> setTemplateId(templateId, templateFormat, ticketTemplates::setGroupInvitationPdfTemplateId,
                    ticketTemplates::setGroupInvitationPrinterTemplateId);
            default -> throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);

        }
        event.setTicketTemplates(ticketTemplates);
        return event;
    }

    public static EventTicketTemplates toTicketTemplates(EventTicketTemplateType type, String passbookCode) {
        EventTicketTemplates ticketTemplates = new EventTicketTemplates();
        switch (type) {
            case SINGLE -> ticketTemplates.setIndividualTicketPassbookTemplateCode(passbookCode);
            case GROUP -> ticketTemplates.setGroupTicketPassbookTemplateCode(passbookCode);
            case SINGLE_INVITATION -> ticketTemplates.setIndividualInvitationPassbookTemplateCode(passbookCode);
            case GROUP_INVITATION -> ticketTemplates.setGroupInvitationPassbookTemplateCode(passbookCode);
            case SEASON_PACK -> ticketTemplates.setSessionPackPassbookTemplateCode(passbookCode);
            default -> throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);

        }
        return ticketTemplates;
    }

    private static void setTemplateId(IdDTO templateId, TicketTemplateFormatPath format, Consumer<Long> setterPdf,
            Consumer<Long> setterPinter) {
        if (TicketTemplateFormatPath.PDF.equals(format)) {
            setterPdf.accept(templateId.getId());
        } else if (TicketTemplateFormatPath.PRINTER.equals(format)) {
            setterPinter.accept(templateId.getId());
        }
    }

    private static void addPrinter(EventTicketTemplates templates, List<EventTicketTemplateDTO> ticketTemplates) {
        convertAndAdd(ticketTemplates, templates.getIndividualTicketPrinterTemplateId(), EventTicketTemplateType.SINGLE,
                TicketTemplateFormat.PRINTER);
        convertAndAdd(ticketTemplates, templates.getGroupTicketPrinterTemplateId(), EventTicketTemplateType.GROUP,
                TicketTemplateFormat.PRINTER);
        convertAndAdd(ticketTemplates, templates.getIndividualInvitationPrinterTemplateId(), EventTicketTemplateType.SINGLE_INVITATION,
                TicketTemplateFormat.PRINTER);
        convertAndAdd(ticketTemplates, templates.getGroupInvitationPrinterTemplateId(), EventTicketTemplateType.GROUP_INVITATION,
                TicketTemplateFormat.PRINTER);
    }

    private static void addPdf(EventTicketTemplates templates, List<EventTicketTemplateDTO> ticketTemplates) {
        convertAndAdd(ticketTemplates, templates.getIndividualTicketPdfTemplateId(), EventTicketTemplateType.SINGLE, TicketTemplateFormat.PDF);
        convertAndAdd(ticketTemplates, templates.getGroupTicketPdfTemplateId(), EventTicketTemplateType.GROUP, TicketTemplateFormat.PDF);
        convertAndAdd(ticketTemplates, templates.getIndividualInvitationPdfTemplateId(), EventTicketTemplateType.SINGLE_INVITATION,
                TicketTemplateFormat.PDF);
        convertAndAdd(ticketTemplates, templates.getGroupInvitationPdfTemplateId(), EventTicketTemplateType.GROUP_INVITATION,
                TicketTemplateFormat.PDF);
    }

    private static void addPassbook(EventTicketTemplates templates, List<EventTicketTemplateDTO> ticketTemplates) {
        convertAndAdd(ticketTemplates, templates.getIndividualTicketPassbookTemplateCode(), EventTicketTemplateType.SINGLE, TicketTemplateFormat.PASSBOOK);
        convertAndAdd(ticketTemplates, templates.getGroupTicketPassbookTemplateCode(), EventTicketTemplateType.GROUP, TicketTemplateFormat.PASSBOOK);
        convertAndAdd(ticketTemplates, templates.getIndividualInvitationPassbookTemplateCode(), EventTicketTemplateType.SINGLE_INVITATION, TicketTemplateFormat.PASSBOOK);
        convertAndAdd(ticketTemplates, templates.getGroupInvitationPassbookTemplateCode(), EventTicketTemplateType.GROUP_INVITATION, TicketTemplateFormat.PASSBOOK);
        convertAndAdd(ticketTemplates, templates.getSessionPackPassbookTemplateCode(), EventTicketTemplateType.SEASON_PACK,
                TicketTemplateFormat.PASSBOOK);
    }

    private static void convertAndAdd(List<EventTicketTemplateDTO> templates, String templateId, EventTicketTemplateType type, TicketTemplateFormat format) {
        if (templateId != null) {
            EventTicketTemplateDTO template = new EventTicketTemplateDTO();
            template.setId(templateId);
            template.setType(type);
            template.setFormat(format);
            templates.add(template);
        }
    }

    private static void convertAndAdd(List<EventTicketTemplateDTO> templates, Long templateId, EventTicketTemplateType type, TicketTemplateFormat format) {
        if (templateId != null) {
            convertAndAdd(templates, String.valueOf(templateId), type, format);
        }
    }
}
