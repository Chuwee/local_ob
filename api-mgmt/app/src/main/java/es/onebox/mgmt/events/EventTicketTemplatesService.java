package es.onebox.mgmt.events;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.ticketpreview.TicketPreviewDTO;
import es.onebox.mgmt.common.ticketpreview.converter.TicketPreviewConverter;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventLanguage;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventTicketTemplates;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplate;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookPreviewRequest;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookTemplate;
import es.onebox.mgmt.datasources.ms.ticket.dto.TicketPreviewRequest;
import es.onebox.mgmt.datasources.ms.ticket.repository.PassbookRepository;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketPreviewRepository;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.mgmt.events.converter.EventTicketTemplatesConverter;
import es.onebox.mgmt.events.dto.EventTicketTemplateDTO;
import es.onebox.mgmt.events.dto.TicketPrintResultDTO;
import es.onebox.mgmt.events.enums.EventTicketTemplateType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtPassbookErrorCode;
import es.onebox.mgmt.tickettemplates.TicketTemplatesService;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormat;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormatPath;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class EventTicketTemplatesService {

    private static final Pattern PASSBOOK_CODE_PATTERN = Pattern.compile("[a-zA-Z0-9_]{3,25}");

    private final EventsRepository eventsRepository;
    private final TicketsRepository ticketsRepository;
    private final ValidationService validationService;
    private final PassbookRepository passbookRepository;
    private final EntitiesRepository entitiesRepository;
    private final TicketTemplatesService ticketTemplatesService;
    private final TicketPreviewRepository ticketPreviewRepository;
    private final EventPassbookTemplateHelper eventPassbookTemplateHelper;

    @Autowired
    public EventTicketTemplatesService(EventsRepository eventsRepository, TicketsRepository ticketsRepository,
                                       ValidationService validationService, PassbookRepository passbookRepository,
                                       EntitiesRepository entitiesRepository, TicketTemplatesService ticketTemplatesService,
                                       TicketPreviewRepository ticketPreviewRepository, EventPassbookTemplateHelper eventPassbookTemplateHelper) {
        this.eventsRepository = eventsRepository;
        this.ticketsRepository = ticketsRepository;
        this.validationService = validationService;
        this.passbookRepository = passbookRepository;
        this.entitiesRepository = entitiesRepository;
        this.ticketTemplatesService = ticketTemplatesService;
        this.ticketPreviewRepository = ticketPreviewRepository;
        this.eventPassbookTemplateHelper = eventPassbookTemplateHelper;
    }

    public List<EventTicketTemplateDTO> getEventTicketTemplates(Long eventId) {
        Event event = validationService.getAndCheckEvent(eventId);
        List<EventTicketTemplateDTO>  eventTicketTemplateDTOList = EventTicketTemplatesConverter.convert(event.getTicketTemplates());
        if (CollectionUtils.isNotEmpty(eventTicketTemplateDTOList)
                && eventTicketTemplateDTOList.stream().map(EventTicketTemplateDTO::getFormat).anyMatch(TicketTemplateFormat.PRINTER::equals)
                && BooleanUtils.isTrue(entityHasHardTicketPDFAllowed(event.getEntityId()))) {
            for (EventTicketTemplateDTO eventTicketTemplateDTO : eventTicketTemplateDTOList) {
                if (TicketTemplateFormat.PRINTER.equals(eventTicketTemplateDTO.getFormat())) {
                    TicketTemplate template = ticketTemplatesService.getAndCheckTicketTemplate(Long.valueOf(eventTicketTemplateDTO.getId()));
                    if (template != null && template.getDesign() != null
                            && TicketTemplateFormat.HARD_TICKET_PDF.equals(TicketTemplateFormat.byId(template.getDesign().getFormat()))) {
                        eventTicketTemplateDTO.setFormat(TicketTemplateFormat.HARD_TICKET_PDF);
                    }
                }
            }
        }
        return eventTicketTemplateDTOList;
    }

    private Boolean entityHasHardTicketPDFAllowed(Long entityId) {
       return entitiesRepository.getCachedEntity(entityId).getAllowHardTicketPDF();
    }

    public void saveEventTicketTemplate(Long eventId, EventTicketTemplateType type, TicketTemplateFormatPath templateFormat,
            IdDTO templateId) {
        validationService.getAndCheckEvent(eventId);
        if (templateId != null && templateId.getId() != null) {
            Event eventToUpdate = EventTicketTemplatesConverter.toEvent(eventId, type, templateFormat, templateId);
            eventsRepository.updateEvent(eventToUpdate);
        }
    }

    public TicketPreviewDTO getTicketPdfPreview(Long eventId, EventTicketTemplateType ticketType, String language) {
        Event event = validationService.getAndCheckEvent(eventId);
        Long languageId = Optional.ofNullable(language)
                .map(ConverterUtils::toLocale)
                .map(l -> getEventLanguage(event, lang -> lang.getCode().equals(l)).getId())
                .orElseGet(() -> getEventLanguage(event, EventLanguage::getDefault).getId());
        TicketPreviewRequest request = TicketPreviewConverter.toRequest(eventId, ticketType, event, languageId);
        return TicketPreviewConverter.toDTO(ticketPreviewRepository.getTicketPdfPreview(request));
    }

    public TicketPrintResultDTO getTicketPassbookPreview(Long eventId, String language) {
        Event event = validationService.getAndCheckEvent(eventId);
        Long entityId = event.getEntityId();
        String passbookCode = eventPassbookTemplateHelper
                .getPassbookCodeForEvent(eventId, entityId, EventTicketTemplateType.SINGLE);
        if (language == null) {
            language = getEventLanguage(event, EventLanguage::getDefault).getCode();
        } else {
            language = validationService.convertAndCheckLanguageTag(language,
                    event.getLanguages().stream().map(EventLanguage::getCode).collect(Collectors.toSet()));
        }
        PassbookPreviewRequest request = new PassbookPreviewRequest();
        request.setEventId(eventId);
        request.setLanguageCode(language);
        request.setEntityId(entityId);
        return new TicketPrintResultDTO(ticketsRepository.getPassbookPreview(request, passbookCode).getDownloadUrl());
    }

    public void saveEventTicketTemplatePassbook(Long eventId, EventTicketTemplateType type, String passbookCode) {
        validatePassbookCode(passbookCode);
        Event event = validationService.getAndCheckEvent(eventId);
        PassbookTemplate passbookTemplate = passbookRepository.getPassbookTemplate(passbookCode, event.getEntityId());
        if (passbookTemplate == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.PASSBOOK_TEMPLATE_NOT_FOUND);
        }
        if (passbookCode != null) {
            Event outputEvent = new Event();
            outputEvent.setId(eventId);
            outputEvent.setStatus(event.getStatus());
            EventTicketTemplates ticketTemplates = EventTicketTemplatesConverter.toTicketTemplates(type, passbookCode);
            outputEvent.setTicketTemplates(ticketTemplates);
            eventsRepository.updateEvent(outputEvent);
        }
    }

    private static void validatePassbookCode(String code) {
        if (!PASSBOOK_CODE_PATTERN.matcher(code).matches()) {
            throw new OneboxRestException(ApiMgmtPassbookErrorCode.PASSBOOK_CODE_UNACCEPTABLE);
        }
    }

    private static EventLanguage getEventLanguage(Event event, Predicate<EventLanguage> filter) {
        return event.getLanguages().stream().filter(filter).findFirst()
                .orElseThrow(() -> ExceptionBuilder.build(ApiMgmtErrorCode.NOT_AVAILABLE_LANG));
    }
}
