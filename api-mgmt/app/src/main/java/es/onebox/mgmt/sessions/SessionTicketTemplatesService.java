package es.onebox.mgmt.sessions;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventLanguage;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookPreviewRequest;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.mgmt.events.EventPassbookTemplateHelper;
import es.onebox.mgmt.events.dto.TicketPrintResultDTO;
import es.onebox.mgmt.events.enums.EventTicketTemplateType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class SessionTicketTemplatesService {


    private final ValidationService validationService;
    private final TicketsRepository ticketsRepository;
    private final EventPassbookTemplateHelper eventPassbookTemplateHelper;

    @Autowired
    public SessionTicketTemplatesService(ValidationService validationService,
                                         TicketsRepository ticketsRepository,
                                         EventPassbookTemplateHelper eventPassbookTemplateHelper) {
        this.validationService = validationService;
        this.ticketsRepository = ticketsRepository;
        this.eventPassbookTemplateHelper = eventPassbookTemplateHelper;
    }

    public TicketPrintResultDTO getSessionPassbookPreview(Long eventId, Long sessionId, String language) {
        Event event = validationService.getAndCheckEvent(eventId);
        Long entityId = event.getEntityId();
        validationService.getAndCheckOnlySession(eventId, sessionId);
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
        request.setSessionId(sessionId);
        request.setLanguageCode(language);
        request.setEntityId(entityId);
        return new TicketPrintResultDTO(ticketsRepository.getPassbookPreview(request, passbookCode).getDownloadUrl());
    }

    private EventLanguage getEventLanguage(Event event, Predicate<EventLanguage> filter) {
        return event.getLanguages().stream()
                .filter(filter)
                .findFirst()
                .orElseThrow(() -> ExceptionBuilder.build(ApiMgmtErrorCode.NOT_AVAILABLE_LANG));
    }

}
