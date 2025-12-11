package es.onebox.mgmt.seasontickets.service;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.ticketpreview.TicketPreviewDTO;
import es.onebox.mgmt.common.ticketpreview.converter.TicketPreviewConverter;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventLanguage;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketStatus;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicketTicketTemplates;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookPreviewRequest;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookTemplate;
import es.onebox.mgmt.datasources.ms.ticket.dto.TicketPreviewRequest;
import es.onebox.mgmt.datasources.ms.ticket.enums.TicketPreviewType;
import es.onebox.mgmt.datasources.ms.ticket.repository.PassbookRepository;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketPreviewRepository;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketsRepository;
import es.onebox.mgmt.events.EventPassbookTemplateHelper;
import es.onebox.mgmt.events.dto.TicketPrintResultDTO;
import es.onebox.mgmt.events.enums.EventTicketTemplateType;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtPassbookErrorCode;
import es.onebox.mgmt.seasontickets.converter.SeasonTicketTicketTemplatesConverter;
import es.onebox.mgmt.seasontickets.dto.SeasonTicketTicketTemplateDTO;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.tickettemplates.enums.TicketTemplateFormat;
import es.onebox.mgmt.validation.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.BAD_REQUEST_PARAMETER;

@Service
public class SeasonTicketTicketTemplatesService {

    private static final Pattern PASSBOOK_CODE_PATTERN = Pattern.compile("[a-zA-Z0-9_]{3,25}");

    private final TicketPreviewRepository ticketPreviewRepository;
    private final SeasonTicketService seasonTicketService;
    private final SeasonTicketRepository seasonTicketRepository;
    private final SecurityManager securityManager;
    private final TicketsRepository ticketsRepository;
    private final ValidationService validationService;
    private final PassbookRepository passbookRepository;
    private final EventPassbookTemplateHelper eventPassbookTemplateHelper;


    @Autowired
    public SeasonTicketTicketTemplatesService(TicketPreviewRepository ticketPreviewRepository,
                                              SeasonTicketService seasonTicketService,
                                              SeasonTicketRepository seasonTicketRepository,
                                              SecurityManager securityManager, TicketsRepository ticketsRepository,
                                              ValidationService validationService, PassbookRepository passbookRepository,
                                              EventPassbookTemplateHelper eventPassbookTemplateHelper) {
        this.ticketPreviewRepository = ticketPreviewRepository;
        this.seasonTicketService = seasonTicketService;
        this.seasonTicketRepository = seasonTicketRepository;
        this.securityManager = securityManager;
        this.ticketsRepository = ticketsRepository;
        this.validationService = validationService;
        this.passbookRepository = passbookRepository;
        this.eventPassbookTemplateHelper = eventPassbookTemplateHelper;
    }

    public List<SeasonTicketTicketTemplateDTO> getSeasonTicketTicketTemplates(Long seasonTicketId) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        return SeasonTicketTicketTemplatesConverter.convert(seasonTicket.getSeasonTicketTicketTemplatesDTO());
    }

    public void saveSeasonTicketTicketTemplate(Long seasonTicketId, EventTicketTemplateType type, TicketTemplateFormat format, IdDTO templateId) {
        seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        if (templateId != null && templateId.getId() != null) {
            SeasonTicket seasonTicket = new SeasonTicket();
            seasonTicket.setId(seasonTicketId);

            SeasonTicketTicketTemplates ticketTemplates = new SeasonTicketTicketTemplates();
            if (type == EventTicketTemplateType.SINGLE) {
                setTemplateId(templateId, format, ticketTemplates::setTicketPdfTemplateId, ticketTemplates::setTicketPrinterTemplateId);
            } else if (type == EventTicketTemplateType.GROUP) {
                setTemplateId(templateId, format, ticketTemplates::setGroupTicketPdfTemplateId, ticketTemplates::setGroupTicketPrinterTemplateId);
            } else if (type == EventTicketTemplateType.SINGLE_INVITATION) {
                setTemplateId(templateId, format, ticketTemplates::setIndividualInvitationPdfTemplateId, ticketTemplates::setIndividualInvitationPrinterTemplateId);
            } else if (type == EventTicketTemplateType.GROUP_INVITATION) {
                setTemplateId(templateId, format, ticketTemplates::setGroupInvitationPdfTemplateId, ticketTemplates::setGroupInvitationPrinterTemplateId);
            }
            seasonTicket.setSeasonTicketTicketTemplatesDTO(ticketTemplates);

            seasonTicketRepository.updateSeasonTicket(seasonTicketId, seasonTicket);
        }
    }

    public TicketPreviewDTO getTicketPdfPreview(Long seasonTicketId, EventTicketTemplateType ticketType, String language) {
        SeasonTicket seasonTicket = seasonTicketService.getAndCheckSeasonTicket(seasonTicketId);
        EventLanguage eventLanguage = Optional.ofNullable(language)
                .map(ConverterUtils::toLocale)
                .map(l -> getSeasonTicketLanguage(seasonTicket, lang -> lang.getCode().equals(l)))
                .orElseGet(() -> getSeasonTicketLanguage(seasonTicket, EventLanguage::getDefault));
        TicketPreviewRequest request = new TicketPreviewRequest();
        request.setEntityId(seasonTicket.getEntityId());
        request.setEventId(seasonTicketId);
        request.setLanguageId(eventLanguage.getId());
        request.setItemId(seasonTicketId);
        request.setType(TicketPreviewType.valueOf(ticketType.name()));
        return TicketPreviewConverter.toDTO(ticketPreviewRepository.getTicketPdfPreview(request));
    }

    public TicketPrintResultDTO getTicketPassbookPreview(Long seasonTicketId, String language) {
        SeasonTicket seasonTicket = getAndCheckSeasonTicket(seasonTicketId);
        Long entityId = seasonTicket.getEntityId();
        String passbookCode = eventPassbookTemplateHelper
                .getPassbookCodeForEvent(seasonTicketId, entityId, EventTicketTemplateType.SEASON_PACK);
        if (language == null) {
            language = SeasonTicketTicketTemplatesService.getSeasonTicketLanguage(seasonTicket, EventLanguage::getDefault).getCode();
        } else {
            language = validationService.convertAndCheckLanguageTag(language,
                    seasonTicket.getLanguages().stream().map(EventLanguage::getCode).collect(Collectors.toSet()));
        }
        PassbookPreviewRequest request = new PassbookPreviewRequest();
        request.setSeasonTicketId(seasonTicketId);
        request.setLanguageCode(language);
        request.setEntityId(entityId);
        return new TicketPrintResultDTO(ticketsRepository.getPassbookPreview(request, passbookCode).getDownloadUrl());
    }

    public void saveTicketTemplatePassbook(Long seasonTicketId, EventTicketTemplateType type, String passbookCode) {
        SeasonTicket seasonTicket = getAndCheckSeasonTicket(seasonTicketId);
        validatePassbookCode(passbookCode);
        PassbookTemplate passbookTemplate = passbookRepository.getPassbookTemplate(passbookCode, seasonTicket.getEntityId());
        if (passbookTemplate == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.PASSBOOK_TEMPLATE_NOT_FOUND);
        }
        if (passbookCode != null) {
            SeasonTicketTicketTemplates ticketTemplates = new SeasonTicketTicketTemplates();
            if (type == EventTicketTemplateType.SINGLE) {
                ticketTemplates.setIndividualTicketPassbookTemplateCode(passbookCode);
            } else if (type == EventTicketTemplateType.GROUP) {
                ticketTemplates.setGroupTicketPassbookTemplateCode(passbookCode);
            } else if (type == EventTicketTemplateType.SINGLE_INVITATION) {
                ticketTemplates.setIndividualInvitationPassbookTemplateCode(passbookCode);
            } else if (type == EventTicketTemplateType.GROUP_INVITATION) {
                ticketTemplates.setGroupInvitationPassbookTemplateCode(passbookCode);
            } else if (type == EventTicketTemplateType.SEASON_PACK) {
                ticketTemplates.setSessionPackPassbookTemplateCode(passbookCode);
            }
            seasonTicket.setSeasonTicketTicketTemplatesDTO(ticketTemplates);
            seasonTicketRepository.updateSeasonTicket(seasonTicketId, seasonTicket);
        }
    }

    private static void setTemplateId(IdDTO templateId, TicketTemplateFormat format, Consumer<Long> setterPdf,
            Consumer<Long> setterPinter) {
        if (TicketTemplateFormat.PDF.equals(format)) {
            setterPdf.accept(templateId.getId());
        } else if (TicketTemplateFormat.PRINTER.equals(format)) {
            setterPinter.accept(templateId.getId());
        }
    }

    private static EventLanguage getSeasonTicketLanguage(SeasonTicket seasonTicket, Predicate<EventLanguage> filter) {
        return seasonTicket.getLanguages().stream().filter(filter).findFirst()
                .orElseThrow(() -> ExceptionBuilder.build(ApiMgmtErrorCode.NOT_AVAILABLE_LANG));
    }

    private SeasonTicket getAndCheckSeasonTicket(Long seasonTicketId) {
        if (seasonTicketId == null || seasonTicketId <= 0) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "seasonTicketId must be a positive integer", null);
        }

        SeasonTicket seasonTicket = seasonTicketRepository.getSeasonTicket(seasonTicketId);
        if (seasonTicket == null || SeasonTicketStatus.DELETED.equals(seasonTicket.getStatus())) {
            throw new OneboxRestException(ApiMgmtErrorCode.SEASON_TICKET_NOT_FOUND, "No season ticket found with id: " + seasonTicketId, null);
        }
        securityManager.checkEntityAccessible(seasonTicket.getEntityId());
        return seasonTicket;
    }

    private static void validatePassbookCode(String code) {
        if (!PASSBOOK_CODE_PATTERN.matcher(code).matches()) {
            throw new OneboxRestException(ApiMgmtPassbookErrorCode.PASSBOOK_CODE_UNACCEPTABLE);
        }
    }
}
