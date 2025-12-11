package es.onebox.mgmt.salerequests.communicationcontents.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.channels.utils.ChannelsUrlUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.FileUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentUrlListDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelType;
import es.onebox.mgmt.datasources.ms.channel.enums.WhitelabelType;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSessionSaleRequestResponseDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.PaymentBenefitCommunicationElementDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.PurchaseCommunicationElementDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.SaleRequestCommunicationElementDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.CommunicationElementType;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsContentsRepository;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.salerequests.communicationcontents.converter.SaleRequestCommElementsConverter;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestChannelContentTextListDTO;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestEventChannelContentPublishedLinksDTO;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestEventChannelContentSessionLinkDTO;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestPaymentBenefitContentTagListDTO;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestPurchaseContentTextListDTO;
import es.onebox.mgmt.salerequests.communicationcontents.dto.SaleRequestSessionsLinksResponse;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestPaymentBenefitTagContentType;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestPurchaseImageContentRequestType;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestPurchaseImageContentResponseType;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestPurchaseUrlContentType;
import es.onebox.mgmt.salerequests.dto.FiltersSessionLinksSaleRequest;
import es.onebox.mgmt.salerequests.dto.SearchSaleRequestSessionsFilter;
import es.onebox.mgmt.security.SecurityManager;
import es.onebox.mgmt.security.SecurityUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SaleRequestCommunicationContentService {

    private final SaleRequestsRepository saleRequestsRepository;
    private final SaleRequestsContentsRepository saleRequestsContentsRepository;
    private final ChannelsRepository channelsRepository;
    private final SecurityManager securityManager;
    private final MasterdataService masterdataService;
    private final EventsRepository eventsRepository;
    @Value("${onebox.portal}")
    private String urlPortal;

    @Value("${onebox.webapps.channels.url}")
    private String urlChannel;

    @Autowired
    public SaleRequestCommunicationContentService(SecurityManager securityManager, SaleRequestsRepository saleRequestsRepository,
                                                  ChannelsRepository channelsRepository, MasterdataService masterdataService,
                                                  SaleRequestsContentsRepository saleRequestsContentsRepository, EventsRepository eventsRepository) {
        this.securityManager = securityManager;
        this.saleRequestsRepository = saleRequestsRepository;
        this.channelsRepository = channelsRepository;
        this.masterdataService = masterdataService;
        this.saleRequestsContentsRepository = saleRequestsContentsRepository;
        this.eventsRepository = eventsRepository;
    }

    public SaleRequestChannelContentTextListDTO getChannelTextsBySaleRequest(Long saleRequestId) {
        validateAndGetLanguages(saleRequestId);
        SaleRequestCommunicationElementDTO response = saleRequestsContentsRepository.getCommunicationElements(saleRequestId);
        return SaleRequestCommElementsConverter.fromMsToChannelContentTexts(response);
    }

    public void updateChannelTextsBySaleRequest(Long saleRequestId, SaleRequestChannelContentTextListDTO body) {
        MsSaleRequestDTO saleRequestDetailResponse = checkSaleRequestAndEntity(saleRequestId);
        ChannelResponse channelResponse = channelsRepository.getChannel(saleRequestDetailResponse.getChannel().getId());
        body.forEach(elem -> validateLanguage(elem.getLanguage(), channelResponse));
        SaleRequestCommunicationElementDTO comPurchaseElements = SaleRequestCommElementsConverter.toMS(body);
        saleRequestsContentsRepository.updateCommunicationElementsBySaleRequest(saleRequestId, comPurchaseElements);
    }

    public List<SaleRequestEventChannelContentPublishedLinksDTO> getChannelLinksBySaleRequest(Long saleRequestId) {
        MsSaleRequestDTO saleRequestDetailResponse = checkSaleRequestAndEntity(saleRequestId);
        ChannelLanguagesDTO languages = languages(saleRequestDetailResponse);

        Event event = eventsRepository.getEvent(saleRequestDetailResponse.getEvent().getId());

        Long channelId = saleRequestDetailResponse.getChannel().getId();
        ChannelResponse channelResponse = channelsRepository.getChannel(channelId);

        ChannelConfig channelConfig = channelsRepository.getChannelConfig(channelId);
        Boolean v4Enabled = channelConfig != null && BooleanUtils.isTrue(channelConfig.getV4Enabled());
        Boolean externalWhitelabel = getAndCheckExternalWhitelabel(channelConfig, channelResponse);

        List<SaleRequestEventChannelContentPublishedLinksDTO> comElementLinks = null;
        if (ChannelType.OB_PORTAL.equals(channelResponse.getType())) {
            comElementLinks = SaleRequestCommElementsConverter.convertToEventChannelContentLinkList(urlChannel,
                    urlPortal, channelResponse, languages, event, v4Enabled, externalWhitelabel);
        }
        return comElementLinks;
    }

    public SaleRequestSessionsLinksResponse getSessionLinksBySaleRequest(Long saleRequestId,
                                                                         String language,
                                                                         FiltersSessionLinksSaleRequest filter) {

        MsSaleRequestDTO saleRequestDetailResponse = checkSaleRequestAndEntity(saleRequestId);
        ChannelLanguagesDTO languages = languages(saleRequestDetailResponse);

        String currentLang = languages.getSelectedLanguageCode().stream().filter(e-> e.equals(language)).findFirst().orElse(null);

        if(currentLang == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.LANGUAGE_NOT_IN_EVENT);
        }

        SearchSaleRequestSessionsFilter searchSaleRequestSessionsFilter = new SearchSaleRequestSessionsFilter();
        searchSaleRequestSessionsFilter.setSort(filter.getSort());
        searchSaleRequestSessionsFilter.setLimit(filter.getLimit());
        searchSaleRequestSessionsFilter.setOffset(filter.getOffset());
        searchSaleRequestSessionsFilter.setPublished(true);
        searchSaleRequestSessionsFilter.setStatus(
                Arrays.asList(
                        es.onebox.mgmt.sessions.enums.SessionStatus.SCHEDULED,
                        es.onebox.mgmt.sessions.enums.SessionStatus.READY)
                );

        Event event = eventsRepository.getEvent(saleRequestDetailResponse.getEvent().getId());
        MsSessionSaleRequestResponseDTO msSessionSaleRequestResponse =
                saleRequestsRepository.getSessions(SecurityUtils.getUserOperatorId(), saleRequestId,
                        searchSaleRequestSessionsFilter);

        Long channelId = saleRequestDetailResponse.getChannel().getId();
        ChannelResponse channelResponse = channelsRepository.getChannel(channelId);

        ChannelConfig channelConfig = channelsRepository.getChannelConfig(channelId);
        String finalUrl= ChannelsUrlUtils.selectUrlByChannelConfig(channelConfig.getV4Enabled(), urlChannel, urlPortal);
        Boolean externalWhitelabel = getAndCheckExternalWhitelabel(channelConfig, channelResponse);

        List<SaleRequestEventChannelContentSessionLinkDTO> comElementLinks = null;
        if (ChannelType.OB_PORTAL.equals(channelResponse.getType())) {
            comElementLinks = SaleRequestCommElementsConverter.convertToEventChannelContentSessionLinkList(
                msSessionSaleRequestResponse, finalUrl, channelResponse, currentLang, event,
                    channelConfig.getV4Enabled(), externalWhitelabel);
        }

        return SaleRequestCommElementsConverter.convertToEventChannelContentSessionLinksResponse(comElementLinks, msSessionSaleRequestResponse.getMetadata());
    }

    public ChannelContentImageListDTO<SaleRequestPurchaseImageContentResponseType> getPurchaseImagesBySaleRequest(Long saleRequestId) {
        ChannelLanguagesDTO languages = validateAndGetLanguages(saleRequestId);

        SaleRequestCommunicationElementDTO response = saleRequestsContentsRepository.getCommunicationElements(saleRequestId);

        sort(languages, Optional.ofNullable(response).orElseGet(SaleRequestCommunicationElementDTO::new));

        return SaleRequestCommElementsConverter.fromMs(response, CommunicationElementType.IMAGE).getImages();
    }

    public ChannelContentUrlListDTO<SaleRequestPurchaseUrlContentType> getPurchaseLinksBySaleRequest(Long saleRequestId) {
        ChannelLanguagesDTO languages = validateAndGetLanguages(saleRequestId);

        SaleRequestCommunicationElementDTO response = saleRequestsContentsRepository.getCommunicationElements(saleRequestId);

        sort(languages, Optional.ofNullable(response).orElseGet(SaleRequestCommunicationElementDTO::new));

        return SaleRequestCommElementsConverter.fromMs(response, CommunicationElementType.LINK).getUrls();
    }

    public SaleRequestPurchaseContentTextListDTO getPurchaseTexts(Long saleRequestId) {
        ChannelLanguagesDTO languages = validateAndGetLanguages(saleRequestId);

        SaleRequestCommunicationElementDTO response = saleRequestsContentsRepository.getCommunicationElements(saleRequestId);

        sort(languages, Optional.ofNullable(response).orElseGet(SaleRequestCommunicationElementDTO::new));

        return SaleRequestCommElementsConverter.fromMs(response, CommunicationElementType.TEXT).getTexts();
    }

    public SaleRequestPaymentBenefitContentTagListDTO getPaymentBenefitTagsBySaleRequest(Long saleRequestId) {
        ChannelLanguagesDTO languages = validateAndGetLanguages(saleRequestId);

        SaleRequestCommunicationElementDTO response = saleRequestsContentsRepository.getCommunicationElements(saleRequestId);

        sort(languages, Optional.ofNullable(response).orElseGet(SaleRequestCommunicationElementDTO::new));

        return SaleRequestCommElementsConverter.fromMsToPaymentBenefitTags(response);
    }

    public void updatePaymentBenefitTagsBySaleRequest(Long saleRequestId, SaleRequestPaymentBenefitContentTagListDTO body) {
        MsSaleRequestDTO saleRequestDetailResponse = checkSaleRequestAndEntity(saleRequestId);
        ChannelResponse channelResponse = channelsRepository.getChannel(saleRequestDetailResponse.getChannel().getId());
        body.forEach(elem -> validateLanguage(elem.getLanguage(), channelResponse));
        SaleRequestCommunicationElementDTO elements = SaleRequestCommElementsConverter.toMS(body);

        saleRequestsContentsRepository.updateCommunicationElementsBySaleRequest(saleRequestId, elements);
    }

    public void deletePaymentBenefitsTagsBySaleRequest(Long saleRequestId, String language, SaleRequestPaymentBenefitTagContentType type) {
        checkSaleRequestAndEntity(saleRequestId);
        PaymentBenefitCommunicationElementDTO comPurchaseElement = SaleRequestCommElementsConverter.toDTO(language, type);
        SaleRequestCommunicationElementDTO communicationElementRequestDTO = new SaleRequestCommunicationElementDTO();
        communicationElementRequestDTO.setCommunicationPaymentBenefitElement(List.of(comPurchaseElement));
        saleRequestsContentsRepository.deleteCommunicationElementsBySaleRequest(saleRequestId, communicationElementRequestDTO);
    }

    public void updatePurchaseImagesBySaleRequest(Long saleRequestId, ChannelContentImageListDTO<SaleRequestPurchaseImageContentRequestType> body) {
        MsSaleRequestDTO saleRequestDetailResponse = checkSaleRequestAndEntity(saleRequestId);
        ChannelResponse channelResponse = channelsRepository.getChannel(saleRequestDetailResponse.getChannel().getId());
        body.forEach(elem -> {
            validateLanguage(elem.getLanguage(), channelResponse);
            FileUtils.checkImage(elem.getImageBinary(), elem.getType(), elem.getType().name());
        });

        SaleRequestCommunicationElementDTO comPurchaseElements = SaleRequestCommElementsConverter.convertImagesToMsCommunicationElementResponse(body);
        saleRequestsContentsRepository.updateCommunicationElementsBySaleRequest(saleRequestId, comPurchaseElements);
    }

    public void updatePurchaseLinksBySaleRequest(Long saleRequestId, ChannelContentUrlListDTO<SaleRequestPurchaseUrlContentType> body) {
        MsSaleRequestDTO saleRequestDetailResponse = checkSaleRequestAndEntity(saleRequestId);
        ChannelResponse channelResponse = channelsRepository.getChannel(saleRequestDetailResponse.getChannel().getId());
        body.forEach(elem -> validateLanguage(elem.getLanguage(), channelResponse));
        SaleRequestCommunicationElementDTO comPurchaseElements = SaleRequestCommElementsConverter.toMS(body);
        saleRequestsContentsRepository.updateCommunicationElementsBySaleRequest(saleRequestId, comPurchaseElements);
    }

    public void updatePurchaseTexts(Long saleRequestId, SaleRequestPurchaseContentTextListDTO body) {
        MsSaleRequestDTO saleRequestDetailResponse = checkSaleRequestAndEntity(saleRequestId);
        ChannelResponse channelResponse = channelsRepository.getChannel(saleRequestDetailResponse.getChannel().getId());
        body.forEach(elem -> validateLanguage(elem.getLanguage(), channelResponse));
        SaleRequestCommunicationElementDTO comPurchaseElements = SaleRequestCommElementsConverter.toMS(body);
        saleRequestsContentsRepository.updateCommunicationElementsBySaleRequest(saleRequestId, comPurchaseElements);
    }

    public void deletePurchaseImagesBySaleRequest(Long saleRequestId, String language, SaleRequestPurchaseImageContentRequestType type) {
        checkSaleRequestAndEntity(saleRequestId);
        PurchaseCommunicationElementDTO comPurchaseElement = SaleRequestCommElementsConverter.toDTO(language, type);
        SaleRequestCommunicationElementDTO communicationElementRequestDTO = new SaleRequestCommunicationElementDTO();
        communicationElementRequestDTO.setCommunicationPurchaseElement(Arrays.asList(comPurchaseElement));
        saleRequestsContentsRepository.deleteCommunicationElementsBySaleRequest(saleRequestId, communicationElementRequestDTO);
    }

    private ChannelLanguagesDTO validateAndGetLanguages(Long saleRequestId) {
        MsSaleRequestDTO saleRequestDetailResponse = checkSaleRequestAndEntity(saleRequestId);
        return languages(saleRequestDetailResponse);
    }

    private MsSaleRequestDTO checkSaleRequestAndEntity(Long saleRequestId) {

        MsSaleRequestDTO response = Optional.ofNullable(saleRequestsRepository.getSaleRequestDetail(saleRequestId))
                .orElseThrow(() -> new OneboxRestException(ApiMgmtErrorCode.SALE_REQUESTS_NOT_FOUND));

        Long entityId = response.getChannel().getEntity().getId();
        securityManager.checkEntityAccessible(entityId);
        return response;
    }

    private Boolean getAndCheckExternalWhitelabel(ChannelConfig channelConfig, ChannelResponse channel) {
        return channelConfig != null && channelConfig.getWhitelabelType() != null &&
                (WhitelabelType.EXTERNAL.equals(channelConfig.getWhitelabelType()));
    }

    private ChannelLanguagesDTO languages(MsSaleRequestDTO saleRequestDetailResponse) {
        Long channelId = saleRequestDetailResponse.getChannel().getId();
        ChannelResponse channelResponse = channelsRepository.getChannel(channelId);
        Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
        return ChannelConverter.convertToLanguageDTO(channelResponse.getLanguages(), languagesByIds);
    }

    private void validateLanguage(String languageCode, ChannelResponse channel) {
        ChannelUtils.validateChannelLanguages(channel);
        Set<String> languageCodes = ChannelConverter.fromLanguageIdToLanguageCode(channel.getLanguages().getSelectedLanguages(),
                masterdataService.getLanguagesByIds());
        if (languageCodes.stream().noneMatch(lang -> lang.equals(languageCode))) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_LANG, "Invalid language " + languageCode, null);
        }
    }

    private static void sort(ChannelLanguagesDTO languages, SaleRequestCommunicationElementDTO response) {
        List<PurchaseCommunicationElementDTO> communicationElements = response.getCommunicationPurchaseElement();
        if (CollectionUtils.isEmpty(communicationElements)) {
            return;
        }
        response.setCommunicationPurchaseElement(communicationElements.stream()
                .filter(c -> languages.getSelectedLanguageCode().contains(ConverterUtils.toLanguageTag(c.getLanguage())))
                .collect(Collectors.toList()));
        response.getCommunicationPurchaseElement().sort(
                Comparator.comparing(PurchaseCommunicationElementDTO::getLanguage).thenComparing(PurchaseCommunicationElementDTO::getId));
    }

}