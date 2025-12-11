package es.onebox.mgmt.channels.packs.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.packs.converter.PackTicketContentsConverter;
import es.onebox.mgmt.packs.dto.comelements.PackCommunicationElement;
import es.onebox.mgmt.packs.dto.comelements.PackContentTextFilter;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentImagePDFDTO;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentImagePDFFilter;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentImagePrinterDTO;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentImagePrinterFilter;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentTextDTO;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentTextPDFFilter;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentsImagePDFListDTO;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentsImagePrinterListDTO;
import es.onebox.mgmt.packs.dto.ticketcontents.PackTicketContentsTextListDTO;
import es.onebox.mgmt.packs.enums.PackContentImageType;
import es.onebox.mgmt.packs.enums.PackContentTextType;
import es.onebox.mgmt.packs.enums.PackTagType;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.FileUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentConverter;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageFilter;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePDFType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePrinterType;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.event.dto.packs.Pack;
import es.onebox.mgmt.datasources.ms.channel.dto.ticketcontent.ChannelTicketContent;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelContentsRepository;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.events.enums.TicketCommunicationElementCategory;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChannelPacksContentsService {

    private final ChannelContentsRepository contentsRepository;
    private final ChannelsHelper channelsHelper;
    private final ChannelsRepository channelsRepository;
    private final MasterdataService masterdataService;

    @Autowired
    public ChannelPacksContentsService(ChannelContentsRepository contentsRepository, ChannelsHelper channelsHelper,
                                       ChannelsRepository channelsRepository, MasterdataService masterdataService) {
        this.contentsRepository = contentsRepository;
        this.channelsHelper = channelsHelper;
        this.channelsRepository = channelsRepository;
        this.masterdataService = masterdataService;
    }

    public ChannelContentTextListDTO<PackContentTextType> getPackContentsTexts(Long channelId, Long packId, PackContentTextFilter filter) {
        channelsHelper.getAndCheckChannel(channelId);
        checkPack(channelId, packId);

        CommunicationElementFilter<PackTagType> communicationElementFilter
                = ChannelContentConverter.fromPackFilter(filter, masterdataService);

        List<PackCommunicationElement> comElements = contentsRepository.getPackCommunicationElements(channelId, packId,
                communicationElementFilter, PackTagType::isText);

        comElements.sort(Comparator.comparing(PackCommunicationElement::getLanguage).thenComparing(PackCommunicationElement::getTagId));

        return ChannelContentConverter.fromMsChannelText(comElements);
    }

    public void updatePackContentTexts(Long channelId, Long packId, List<ChannelContentTextDTO<PackContentTextType>> texts) {
        ChannelResponse channelResponse = channelsHelper.getAndCheckChannel(channelId);
        checkPack(channelId, packId);

        Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
        for (ChannelContentTextDTO<PackContentTextType> element : texts) {
            element.setLanguage(ChannelContentsUtils.checkElementLanguageForChannel(channelResponse, languagesByIds, element.getLanguage()));
        }
        contentsRepository.updatePackCommunicationElements(channelId, packId, ChannelContentConverter.toMsChannelText(texts));
    }

    public ChannelContentImageListDTO<PackContentImageType> getPackContentsImages(Long channelId, Long packId, ChannelContentImageFilter<PackContentImageType> filter) {
        channelsHelper.getAndCheckChannel(channelId);
        checkPack(channelId, packId);

        CommunicationElementFilter<PackTagType> communicationElementFilter
                = ChannelContentConverter.fromPackFilter(filter, masterdataService);
        List<PackCommunicationElement> comElements = contentsRepository.getPackCommunicationElements(channelId, packId,
                communicationElementFilter, PackTagType::isImage);

        comElements.sort(Comparator.comparing(PackCommunicationElement::getLanguage).
                thenComparing(PackCommunicationElement::getTagId).
                thenComparing(PackCommunicationElement::getPosition));

        return ChannelContentConverter.fromMsChannelImage(comElements);
    }

    public void updateChannelContentImages(Long channelId, Long packId, List<ChannelContentImageDTO<PackContentImageType>> images) {
        ChannelResponse channelResponse = channelsHelper.getAndCheckChannel(channelId);
        checkPack(channelId, packId);

        Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
        List<ChannelContentImageDTO<PackContentImageType>> results = images.stream()
                .peek(element -> element.setLanguage(ChannelContentsUtils.checkElementLanguageForChannel(channelResponse, languagesByIds, element.getLanguage())))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(results)) {
            contentsRepository.updatePackCommunicationElements(channelId, packId, ChannelContentConverter.toMsChannelImageList(results));
        }
    }

    public void deleteChannelContentImages(Long channelId, Long packId, String language, PackContentImageType type, Integer position) {
        channelsHelper.getAndCheckChannel(channelId);
        checkPack(channelId, packId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        PackCommunicationElement dto = ChannelContentConverter.buildPackCommunicationElementToDelete(language, type, position, languages);
        contentsRepository.updatePackCommunicationElements(channelId, packId, Collections.singletonList(dto));
    }

    public PackTicketContentsTextListDTO getPackTicketContentsTexts(Long channelId, Long packId, PackTicketContentTextPDFFilter filter, TicketCommunicationElementCategory category) {
        validateRequest(channelId, filter.getLanguage());
        channelsRepository.getPack(channelId, packId);

        String type = filter.getType() != null ? filter.getType().name() : null;
        List<ChannelTicketContent> response = contentsRepository.getPackTicketContent(channelId, packId, ConverterUtils.toLocale(filter.getLanguage()), type, category);
        PackTicketContentsTextListDTO result = PackTicketContentsConverter.toTextsDTO(response);
        result.sort(Comparator.comparing(PackTicketContentTextDTO::getLanguage));
        return result;
    }

    public void updatePackTicketContentsTexts(Long channelId, Long packId, PackTicketContentsTextListDTO contents, TicketCommunicationElementCategory category) {
        validateRequestTexts(channelId, contents);
        channelsRepository.getPack(channelId, packId);
        List<ChannelTicketContent> request = PackTicketContentsConverter.fromTextsDTO(contents);
        contentsRepository.updatePackTicketContent(channelId, packId, request, category);
    }

    public PackTicketContentsImagePDFListDTO getPackTicketContentsPDFImages(Long channelId, Long packId, PackTicketContentImagePDFFilter filter, TicketCommunicationElementCategory category) {
        validateRequest(channelId, filter.getLanguage());
        channelsRepository.getPack(channelId, packId);

        String type = filter.getType() != null ? filter.getType().name() : null;
        List<ChannelTicketContent> response = contentsRepository.getPackTicketContent(channelId, packId, ConverterUtils.toLocale(filter.getLanguage()), type, category);
        PackTicketContentsImagePDFListDTO result = PackTicketContentsConverter.toImagesPDFDTO(response);
        result.sort(Comparator.comparing(PackTicketContentImagePDFDTO::getLanguage));
        return result;
    }

    public void updatePackTicketContentsPDFImages(Long channelId, Long packId, PackTicketContentsImagePDFListDTO contents, TicketCommunicationElementCategory category) {
        validateRequestPDFImages(channelId, contents);
        channelsRepository.getPack(channelId, packId);
        List<ChannelTicketContent> request = PackTicketContentsConverter.fromImagesPDFDTO(contents);
        contentsRepository.updatePackTicketContent(channelId, packId, request, category);
    }

    public void deletePackTicketContentPDFImage(Long channelId, Long packId, String language, TicketContentImagePDFType type, TicketCommunicationElementCategory category) {
        validateRequest(channelId, language);
        channelsRepository.getPack(channelId, packId);
        contentsRepository.deletePackTicketContent(channelId, packId, ConverterUtils.toLocale(language), type.getTag(), category);
    }

    public PackTicketContentsImagePrinterListDTO getPackTicketContentsPrinterImages(Long channelId, Long packId, PackTicketContentImagePrinterFilter filter, TicketCommunicationElementCategory category) {
        validateRequest(channelId, filter.getLanguage());
        channelsRepository.getPack(channelId, packId);

        String type = filter.getType() != null ? filter.getType().name() : null;
        List<ChannelTicketContent> response = contentsRepository.getPackTicketContent(channelId, packId, ConverterUtils.toLocale(filter.getLanguage()), type, category);
        PackTicketContentsImagePrinterListDTO result = PackTicketContentsConverter.toImagesPrinterDTO(response);
        result.sort(Comparator.comparing(PackTicketContentImagePrinterDTO::getLanguage));
        return result;
    }

    public void updatePackTicketContentsPrinterImages(Long channelId, Long packId, PackTicketContentsImagePrinterListDTO contents, TicketCommunicationElementCategory category) {
        validateRequestPrinterImages(channelId, contents);
        channelsRepository.getPack(channelId, packId);
        List<ChannelTicketContent> request = PackTicketContentsConverter.fromImagesPrinterDTO(contents);
        contentsRepository.updatePackTicketContent(channelId, packId, request, category);
    }

    public void deletePackTicketContentPrinterImage(Long channelId, Long packId, String language, TicketContentImagePrinterType type, TicketCommunicationElementCategory category) {
        validateRequest(channelId, language);
        channelsRepository.getPack(channelId, packId);
        contentsRepository.deletePackTicketContent(channelId, packId, ConverterUtils.toLocale(language), type.getTag(), category);
    }

    private void validateRequest(final Long channelId, final String languageCode) {
        ChannelResponse channelResponse = channelsHelper.getAndCheckChannel(channelId);
        if (languageCode != null) {
            validateLanguage(languageCode, channelResponse);
        }
    }

    private void validateLanguage(String languageCode, ChannelResponse channel) {
        ChannelUtils.validateChannelLanguages(channel);
        Set<String> languageCodes = ChannelConverter.fromLanguageIdToLanguageCode(channel.getLanguages().getSelectedLanguages(),
                masterdataService.getLanguagesByIds());
        if (languageCodes.stream().noneMatch(l -> l.equals(languageCode))) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_LANG, "Invalid language " + languageCode, null);
        }
    }

    private void validateRequestTexts(final Long channelId, final PackTicketContentsTextListDTO request) {
        ChannelResponse channelResponse = channelsHelper.getAndCheckChannel(channelId);
        request.forEach(elem -> validateLanguage(elem.getLanguage(), channelResponse));
    }

    private void validateRequestPDFImages(final Long channelId, final PackTicketContentsImagePDFListDTO request) {
        ChannelResponse channelResponse = channelsHelper.getAndCheckChannel(channelId);
        request.forEach(elem -> {
            validateLanguage(elem.getLanguage(), channelResponse);
            FileUtils.checkImage(elem.getImageBinary(), elem.getType(), elem.getType().name());
        });
    }

    private void validateRequestPrinterImages(final Long channelId, final PackTicketContentsImagePrinterListDTO request) {
        ChannelResponse channelResponse = channelsHelper.getAndCheckChannel(channelId);
        request.forEach(elem -> {
            validateLanguage(elem.getLanguage(), channelResponse);
            FileUtils.checkImage(elem.getImageBinary(), elem.getType(), elem.getType().name());
        });
    }

    private void checkPack(Long channelId, Long eventId) {
        Pack pack = channelsRepository.getPack(channelId, eventId);
        if (pack == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.PACK_NOT_FOUND);
        }
    }
}
