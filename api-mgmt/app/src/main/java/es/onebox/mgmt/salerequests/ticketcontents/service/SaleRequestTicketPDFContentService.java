package es.onebox.mgmt.salerequests.ticketcontents.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.FileUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.ticketcontents.TicketContentTextType;
import es.onebox.mgmt.common.ticketpreview.TicketPreviewDTO;
import es.onebox.mgmt.common.ticketpreview.converter.TicketPreviewConverter;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsContentsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.TicketCommunicationElement;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.SeasonTicket;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplate;
import es.onebox.mgmt.datasources.ms.event.dto.tickettemplate.TicketTemplateTagType;
import es.onebox.mgmt.datasources.ms.event.repository.EventTicketContentsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SeasonTicketTicketContentsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.TicketTemplatesRepository;
import es.onebox.mgmt.datasources.ms.ticket.dto.TicketPreviewRequest;
import es.onebox.mgmt.datasources.ms.ticket.enums.TicketPreviewType;
import es.onebox.mgmt.datasources.ms.ticket.repository.TicketPreviewRepository;
import es.onebox.mgmt.events.enums.TicketCommunicationElementCategory;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.salerequests.dto.SaleRequestDetailDTO;
import es.onebox.mgmt.salerequests.enums.SaleRequestEventType;
import es.onebox.mgmt.salerequests.service.SaleRequestService;
import es.onebox.mgmt.salerequests.ticketcontents.converter.SaleRequestTicketImageContentConverter;
import es.onebox.mgmt.salerequests.ticketcontents.converter.SaleRequestTicketTextContentConverter;
import es.onebox.mgmt.salerequests.ticketcontents.dto.SaleRequestTicketImageContentsDTO;
import es.onebox.mgmt.salerequests.ticketcontents.dto.SaleRequestTicketPdfImageContentsUpdateDTO;
import es.onebox.mgmt.salerequests.ticketcontents.dto.SaleRequestTicketTextContentsDTO;
import es.onebox.mgmt.salerequests.ticketcontents.enums.SaleRequestTicketContentImageType;
import es.onebox.mgmt.salerequests.ticketcontents.enums.SaleRequestTicketPdfContentImageUpdateType;
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
public class SaleRequestTicketPDFContentService {

    private final MasterdataService masterdataService;
    private final SaleRequestService saleRequestService;
    private final ChannelsHelper channelsHelper;
    private final TicketPreviewRepository ticketPreviewRepository;
    private final EventsRepository eventsRepository;
    private final TicketTemplatesRepository ticketTemplatesRepository;
    private final SaleRequestsContentsRepository saleRequestsContentsRepository;
    private final EventTicketContentsRepository eventTicketContentsRepository;
    private final SeasonTicketTicketContentsRepository seasonTicketTicketContentsRepository;
    private final SeasonTicketRepository seasonTicketRepository;


    @Autowired
    public SaleRequestTicketPDFContentService(MasterdataService masterdataService,
                                              SaleRequestService saleRequestService,
                                              ChannelsHelper channelsHelper,
                                              SaleRequestsContentsRepository saleRequestsContentsRepository,
                                              EventTicketContentsRepository eventTicketContentsRepository,
                                              TicketPreviewRepository ticketPreviewRepository,
                                              EventsRepository eventsRepository,
                                              TicketTemplatesRepository ticketTemplatesRepository,
                                              SeasonTicketTicketContentsRepository seasonTicketTicketContentsRepository,
                                              SeasonTicketRepository seasonTicketRepository) {
        this.masterdataService = masterdataService;
        this.saleRequestService = saleRequestService;
        this.channelsHelper = channelsHelper;
        this.saleRequestsContentsRepository = saleRequestsContentsRepository;
        this.eventTicketContentsRepository = eventTicketContentsRepository;
        this.ticketPreviewRepository = ticketPreviewRepository;
        this.eventsRepository = eventsRepository;
        this.ticketTemplatesRepository = ticketTemplatesRepository;
        this.seasonTicketTicketContentsRepository = seasonTicketTicketContentsRepository;
        this.seasonTicketRepository = seasonTicketRepository;
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

    public SaleRequestTicketImageContentsDTO getSaleRequestTicketPDFImageContents(final Long saleRequestId, final String language) {

        SaleRequestDetailDTO saleRequest = validateSaleRequestAndChannel(saleRequestId, language);
        Long individualTemplateId = getIndividualTemplateId(saleRequest);

        SaleRequestTicketImageContentsDTO saleRequestImgs = SaleRequestTicketImageContentConverter.toImageContentsDTO(
                saleRequestsContentsRepository.getSaleRequestTicketPdfImages(saleRequestId,
                        StringUtils.isNotBlank(language) ? ConverterUtils.toLocale(language) : null)
        );
        SaleRequestTicketImageContentsDTO eventImgs = SaleRequestTicketImageContentConverter.fromEventToImageContentsDTO(
                getCommunicationElements(saleRequest, filterPdfImages(language), TicketCommunicationElementCategory.PDF)
        );

        SaleRequestTicketImageContentsDTO pdfImages = new SaleRequestTicketImageContentsDTO();
        pdfImages.addAll(saleRequestImgs);

        if (eventImgs == null || eventImgs.isEmpty()) {
            if (individualTemplateId != null) {
                SaleRequestTicketImageContentsDTO templateImgs = SaleRequestTicketImageContentConverter
                        .fromTemplateToImageContentsDTO(ticketTemplatesRepository
                        .getTicketTemplatesCommunicationElements(individualTemplateId,null,
                                TicketTemplateTagType::isImage));
                pdfImages.addAll(templateImgs);
            }
        } else {
            pdfImages.addAll(eventImgs);
        }
        return pdfImages;
    }

    public SaleRequestTicketTextContentsDTO getSaleRequestTicketPDFTextContents(Long saleRequestId, String language) {
        SaleRequestDetailDTO saleRequest = validateSaleRequestAndChannel(saleRequestId, language);

        return SaleRequestTicketTextContentConverter.toTextContentsDTO(
                getCommunicationElements(saleRequest, filterPdfTextContents(language),
                        TicketCommunicationElementCategory.PDF)
        );
    }

    private List<TicketCommunicationElement> getCommunicationElements(SaleRequestDetailDTO saleRequest,
                                                                      CommunicationElementFilter<?> communicationElementFilter,
                                                                      TicketCommunicationElementCategory category) {
        if (SaleRequestEventType.SEASON_TICKET.equals(saleRequest.getEvent().getEventType())) {
            return seasonTicketTicketContentsRepository.findCommunicationElements(saleRequest.getEvent().getId(),
                    communicationElementFilter, category);
        } else {
            return eventTicketContentsRepository.findCommunicationElements(saleRequest.getEvent().getId(),
                    communicationElementFilter, category);
        }
    }

    private CommunicationElementFilter<SaleRequestTicketContentImageType> filterPdfImages(String language) {
        CommunicationElementFilter<SaleRequestTicketContentImageType> imagesFilter = new CommunicationElementFilter<>();
        if (StringUtils.isNotBlank(language)) {
            imagesFilter.setLanguage(ConverterUtils.toLocale(language));
        }
        imagesFilter.setTags(Arrays.stream(SaleRequestTicketContentImageType.values())
                .filter(v -> !v.equals(SaleRequestTicketContentImageType.EVENT_BANNER_SECONDARY))
                .collect(Collectors.toSet()));
        return imagesFilter;
    }

    private CommunicationElementFilter<TicketContentTextType> filterPdfTextContents(String language) {
        CommunicationElementFilter<TicketContentTextType> textsFilter = new CommunicationElementFilter<>();
        if (StringUtils.isNotBlank(language)) {
            textsFilter.setLanguage(ConverterUtils.toLocale(language));
        }
        textsFilter.setTags(Arrays.stream(TicketContentTextType.values()).collect(Collectors.toSet()));
        return textsFilter;
    }

    public void updateSaleRequestTicketPDFImageContents(Long saleRequestId, SaleRequestTicketPdfImageContentsUpdateDTO body) {
        SaleRequestDetailDTO saleRequest = validateSaleRequestAndChannel(saleRequestId, null);
        ChannelResponse channel = channelsHelper.getAndCheckChannel(saleRequest.getChannel().getId());
        body.forEach(image -> {
            validateLanguage(image.getLanguage(), channel);
            FileUtils.checkImage(image.getImageBinary(), image.getType(), image.getType().name());
        });

        List<TicketCommunicationElement> comTicketElements = SaleRequestTicketImageContentConverter.toTicketCommunicationElementsDTO(body);
        saleRequestsContentsRepository.updateSaleRequestTicketPdfImages(saleRequestId, comTicketElements);
    }

    public void deleteSaleRequestTicketPDFImageContents(Long saleRequestId, String language, SaleRequestTicketPdfContentImageUpdateType type) {
        validateSaleRequestAndChannel(saleRequestId, language);

        TicketCommunicationElement comTicketElement = new TicketCommunicationElement();
        comTicketElement.setTag(type.name());
        comTicketElement.setLanguage(ConverterUtils.toLocale(language));
        comTicketElement.setImageBinary(Optional.of(""));
        saleRequestsContentsRepository.deleteSaleRequestTicketPdfImages(saleRequestId, comTicketElement);
    }

    public TicketPreviewDTO getTicketPdfPreview(Long saleRequestId, String language) {
        SaleRequestDetailDTO saleRequestDTO = validateSaleRequestAndChannel(saleRequestId, language);

        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();

        TicketPreviewRequest request = new TicketPreviewRequest();
        request.setEntityId(saleRequestDTO.getEvent().getEntity().getId());
        request.setEventId(saleRequestDTO.getEvent().getId());
        request.setType(TicketPreviewType.EVENT_CHANNEL);
        request.setItemId(saleRequestId);
        request.setLanguageId(languages.get(ConverterUtils.toLocale(language)));
        return TicketPreviewConverter.toDTO(ticketPreviewRepository.getTicketPdfPreview(request));
    }

    public TicketTemplateDTO getTicketTemplate(Long saleRequestId) {
        SaleRequestDetailDTO saleRequestDTO = validateSaleRequestAndChannel(saleRequestId, null);
        Long individualTemplateId = getIndividualTemplateId(saleRequestDTO);

        if (individualTemplateId == null) {
            throw OneboxRestException.builder(ApiMgmtErrorCode.TICKET_TEMPLATE_NOT_FOUND).build();
        }
        TicketTemplate ticketTemplate = ticketTemplatesRepository.getTicketTemplate(individualTemplateId);
        Map<Long, String> languages = masterdataService.getLanguagesByIds();

        return TicketTemplateConverter.fromMsEvent(ticketTemplate, languages);
    }

    private Long getIndividualTemplateId(SaleRequestDetailDTO saleRequestDetailDTO) {
        Long templateId = null;
        if (SaleRequestEventType.SEASON_TICKET.equals(saleRequestDetailDTO.getEvent().getEventType())) {
            SeasonTicket seasonTicket = seasonTicketRepository.getSeasonTicket(saleRequestDetailDTO.getEvent().getId());
            if (seasonTicket != null && seasonTicket.getSeasonTicketTicketTemplatesDTO() != null) {
                templateId = seasonTicket.getSeasonTicketTicketTemplatesDTO().getTicketPdfTemplateId();
            }
        } else {
            Event event = eventsRepository.getEvent(saleRequestDetailDTO.getEvent().getId());
            if (event != null && event.getTicketTemplates() != null) {
                templateId = event.getTicketTemplates().getIndividualTicketPdfTemplateId();
            }
        }
        return templateId;
    }
}
