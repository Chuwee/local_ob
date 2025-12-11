package es.onebox.mgmt.salerequests.ticketcontents.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.FileUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.ticketcontents.TicketContentTextType;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsContentsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSearchFilter;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventTicketTemplates;
import es.onebox.mgmt.datasources.ms.event.dto.event.Events;
import es.onebox.mgmt.datasources.ms.event.dto.event.TicketCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplate;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateTagType;
import es.onebox.mgmt.datasources.ms.event.repository.EventTicketContentsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.TicketTemplatesRepository;
import es.onebox.mgmt.events.enums.TicketCommunicationElementCategory;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.salerequests.dto.SaleRequestDetailDTO;
import es.onebox.mgmt.salerequests.service.SaleRequestService;
import es.onebox.mgmt.salerequests.ticketcontents.converter.SaleRequestTicketImageContentConverter;
import es.onebox.mgmt.salerequests.ticketcontents.converter.SaleRequestTicketTextContentConverter;
import es.onebox.mgmt.salerequests.ticketcontents.dto.SaleRequestTicketImageContentsDTO;
import es.onebox.mgmt.salerequests.ticketcontents.dto.SaleRequestTicketPrinterImageContentsUpdateDTO;
import es.onebox.mgmt.salerequests.ticketcontents.dto.SaleRequestTicketTextContentsDTO;
import es.onebox.mgmt.salerequests.ticketcontents.enums.SaleRequestTicketContentImageType;
import es.onebox.mgmt.salerequests.ticketcontents.enums.SaleRequestTicketPrinterContentImageUpdateType;
import es.onebox.mgmt.tickettemplates.TicketTemplateConverter;
import es.onebox.mgmt.tickettemplates.dto.TicketTemplateDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SaleRequestTicketPrinterContentService {

    private final MasterdataService masterdataService;
    private final SaleRequestService saleRequestService;
    private final ChannelsHelper channelsHelper;
    private final SaleRequestsContentsRepository saleRequestsContentsRepository;
    private final EventTicketContentsRepository eventTicketContentsRepository;
    private final EventsRepository eventsRepository;
    private final TicketTemplatesRepository ticketTemplatesRepository;


    @Autowired
    public SaleRequestTicketPrinterContentService(MasterdataService masterdataService, SaleRequestService saleRequestService,
                                                  ChannelsHelper channelsHelper,
                                                  SaleRequestsContentsRepository saleRequestsContentsRepository,
                                                  EventTicketContentsRepository eventTicketContentsRepository,
                                                  EventsRepository eventsRepository,
                                                  TicketTemplatesRepository ticketTemplatesRepository) {
        this.masterdataService = masterdataService;
        this.saleRequestService = saleRequestService;
        this.channelsHelper = channelsHelper;
        this.saleRequestsContentsRepository = saleRequestsContentsRepository;
        this.eventTicketContentsRepository = eventTicketContentsRepository;
        this.eventsRepository = eventsRepository;
        this.ticketTemplatesRepository = ticketTemplatesRepository;
    }


    private SaleRequestDetailDTO validateSaleRequestAndChannel(final Long saleRequestId, final String languageCode) {
        SaleRequestDetailDTO saleRequest = saleRequestService.getSaleRequestDetail(saleRequestId);
        ChannelResponse channel = channelsHelper.getAndCheckChannel(saleRequest.getChannel().getId());
        if (languageCode != null) {
            validateLanguage(languageCode, channel);
        }
        return saleRequest;
    }

    private void validateLanguage(String languageCode, ChannelResponse channel) {
        ChannelUtils.validateChannelLanguages(channel);
        Set<String> languageCodes = ChannelConverter.fromLanguageIdToLanguageCode(channel.getLanguages().getSelectedLanguages(),
                masterdataService.getLanguagesByIds());
        if (languageCodes.stream().noneMatch(lang -> lang.equals(languageCode))) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_LANG, "Invalid language " + languageCode, null);
        }
    }


    public SaleRequestTicketImageContentsDTO getSaleRequestTicketPrinterImageContents(final Long saleRequestId, final String language) {

        SaleRequestDetailDTO saleRequest = validateSaleRequestAndChannel(saleRequestId, language);
        Event event = eventsRepository.getEvent(saleRequest.getEvent().getId());

        SaleRequestTicketImageContentsDTO saleRequestImgs = SaleRequestTicketImageContentConverter.toImageContentsDTO(
                saleRequestsContentsRepository.getSaleRequestTicketPrinterImages(saleRequestId, StringUtils.isNotBlank(language) ? ConverterUtils.toLocale(language) : null)
        );
        SaleRequestTicketImageContentsDTO eventImgs = SaleRequestTicketImageContentConverter.fromEventToImageContentsDTO(
                eventTicketContentsRepository.findCommunicationElements(saleRequest.getEvent().getId(), filterPrinterImages(language), TicketCommunicationElementCategory.TICKET_OFFICE)
        );

        SaleRequestTicketImageContentsDTO zplImages = new SaleRequestTicketImageContentsDTO();
        zplImages.addAll(saleRequestImgs);

        addTemplateOrEventImagesToZpl(eventImgs, event, zplImages);
        return zplImages;
    }

    private void addTemplateOrEventImagesToZpl(SaleRequestTicketImageContentsDTO eventImgs,
        Event event,
        SaleRequestTicketImageContentsDTO zplImages) {
        if (isEventImgsNullOrEmptyAndPrinterTemplateNonNull(eventImgs, event)) {
            EventTicketTemplates eventTicketTemplates = event.getTicketTemplates();
            SaleRequestTicketImageContentsDTO templateImgs =
                SaleRequestTicketImageContentConverter.fromTemplateToImageContentsDTO(
                    ticketTemplatesRepository
                        .getTicketTemplatesCommunicationElements(
                            eventTicketTemplates.getIndividualTicketPrinterTemplateId(),
                            null, TicketTemplateTagType::isImage));
            zplImages.addAll(templateImgs);
        } else if (!eventImgs.isEmpty()) {
            zplImages.addAll(eventImgs);
        }
    }

    private static boolean isEventImgsNullOrEmptyAndPrinterTemplateNonNull(
        SaleRequestTicketImageContentsDTO eventImgs,
        Event event) {
        return (eventImgs == null || eventImgs.isEmpty())
            && isIndividualTicketPrinterTemplateIdNonNull(event);
    }

    private static boolean isIndividualTicketPrinterTemplateIdNonNull(Event event) {
        EventTicketTemplates eventTicketTemplates = event.getTicketTemplates();
        return event.getTicketTemplates() != null
            && eventTicketTemplates.getIndividualTicketPrinterTemplateId() != null;

    }

    public SaleRequestTicketTextContentsDTO getSaleRequestTicketPrinterTextContents(Long saleRequestId, String language) {
        SaleRequestDetailDTO saleRequest = validateSaleRequestAndChannel(saleRequestId, language);

        return SaleRequestTicketTextContentConverter.toTextContentsDTO(
                eventTicketContentsRepository.findCommunicationElements(
                        saleRequest.getEvent().getId(),
                        filterPrinterTextContents(language),
                        TicketCommunicationElementCategory.TICKET_OFFICE)
        );
    }

    private CommunicationElementFilter<SaleRequestTicketContentImageType> filterPrinterImages(String language) {
        CommunicationElementFilter<SaleRequestTicketContentImageType> filter = new CommunicationElementFilter<>();
        if (StringUtils.isNotBlank(language)) {
            filter.setLanguage(ConverterUtils.toLocale(language));
        }
        filter.setTags(
                Arrays.stream(SaleRequestTicketContentImageType.values())
                        .filter(v -> v.equals(SaleRequestTicketContentImageType.BODY) || v.equals(SaleRequestTicketContentImageType.BANNER_MAIN))
                        .collect(Collectors.toSet())
        );
        return filter;
    }

    private CommunicationElementFilter<TicketContentTextType> filterPrinterTextContents(String language) {
        CommunicationElementFilter<TicketContentTextType> textsFilter = new CommunicationElementFilter<>();
        if (StringUtils.isNotBlank(language)) {
            textsFilter.setLanguage(ConverterUtils.toLocale(language));
        }
        textsFilter.setTags(Arrays.stream(TicketContentTextType.values()).collect(Collectors.toSet()));
        return textsFilter;
    }

    public void updateSaleRequestTicketPrinterImageContents(Long saleRequestId, SaleRequestTicketPrinterImageContentsUpdateDTO body) {
        SaleRequestDetailDTO saleRequest = validateSaleRequestAndChannel(saleRequestId, null);
        ChannelResponse channel = channelsHelper.getAndCheckChannel(saleRequest.getChannel().getId());
        body.forEach(image -> {
            validateLanguage(image.getLanguage(), channel);
            FileUtils.checkImage(image.getImageBinary(), image.getType(), image.getType().name());
        });

        List<TicketCommunicationElement> comTicketElements = SaleRequestTicketImageContentConverter.toTicketCommunicationElementsDTO(body);
        saleRequestsContentsRepository.updateSaleRequestTicketPrinterImages(saleRequestId, comTicketElements);
    }

    public void deleteSaleRequestTicketPrinterImageContents(Long saleRequestId, String language, SaleRequestTicketPrinterContentImageUpdateType type) {
        validateSaleRequestAndChannel(saleRequestId, language);

        TicketCommunicationElement comTicketElement = new TicketCommunicationElement();
        comTicketElement.setTag(type.name());
        comTicketElement.setLanguage(ConverterUtils.toLocale(language));
        comTicketElement.setImageBinary(Optional.of(""));
        saleRequestsContentsRepository.deleteSaleRequestTicketPrinterImages(saleRequestId, comTicketElement);
    }

    public TicketTemplateDTO getTicketTemplate(Long saleRequestId) {
        SaleRequestDetailDTO saleRequestDTO = validateSaleRequestAndChannel(saleRequestId, null);
        EventSearchFilter eventSearchFilter = new EventSearchFilter();
        eventSearchFilter.setId(List.of(saleRequestDTO.getEvent().getId()));
        eventSearchFilter.setIncludeSeasonTickets(true);
        Events events = eventsRepository.getEvents(eventSearchFilter);
        if(events == null || events.getData().isEmpty() || events.getData().size() > 1) {
            throw new OneboxRestException(ApiMgmtErrorCode.EVENT_NOT_FOUND);
        }
        if (events.getData().get(0).getTicketTemplates() == null || events.getData().get(0).getTicketTemplates().getIndividualTicketPrinterTemplateId() == null) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.TICKET_TEMPLATE_NOT_FOUND).build();
        }
        TicketTemplate ticketTemplate = ticketTemplatesRepository.getTicketTemplate(events.getData().get(0).getTicketTemplates().getIndividualTicketPrinterTemplateId());
        Map<Long, String> languages = masterdataService.getLanguagesByIds();

        return TicketTemplateConverter.fromMsEvent(ticketTemplate, languages);
    }
}