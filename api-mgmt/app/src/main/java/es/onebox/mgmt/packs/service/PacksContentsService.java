package es.onebox.mgmt.packs.service;

import es.onebox.core.exception.OneboxRestException;
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
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.FileUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentConverter;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageFilter;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePDFType;
import es.onebox.mgmt.common.ticketcontents.TicketContentImagePrinterType;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.event.dto.packs.Pack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackDetail;
import es.onebox.mgmt.datasources.ms.channel.dto.ticketcontent.ChannelTicketContent;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.dto.IdValueCodeDTO;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.datasources.ms.event.repository.PacksRepository;
import es.onebox.mgmt.events.enums.TicketCommunicationElementCategory;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.exception.ApiMgmtEntitiesErrorCode;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PacksContentsService {

    private final PacksRepository packsRepository;
    private final EntitiesRepository entitiesRepository;
    private final MasterdataService masterdataService;

    @Autowired
    public PacksContentsService(PacksRepository packsRepository,
                                EntitiesRepository entitiesRepository,
                                MasterdataService masterdataService) {
        this.packsRepository = packsRepository;
        this.entitiesRepository = entitiesRepository;
        this.masterdataService = masterdataService;
    }

    public ChannelContentTextListDTO<PackContentTextType> getPackContentsTexts(Long packId, PackContentTextFilter filter) {
        checkPack(packId);

        CommunicationElementFilter<PackTagType> communicationElementFilter
                = ChannelContentConverter.fromPackFilter(filter, masterdataService);

        List<PackCommunicationElement> comElements = packsRepository.getPackCommunicationElements(packId,
                communicationElementFilter, PackTagType::isText);

        comElements.sort(Comparator.comparing(PackCommunicationElement::getLanguage).thenComparing(PackCommunicationElement::getTagId));

        return ChannelContentConverter.fromMsChannelText(comElements);
    }

    public void updatePackContentTexts(Long packId, List<ChannelContentTextDTO<PackContentTextType>> texts) {
        Pack pack = checkPack(packId);

        Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
        Entity entity = entitiesRepository.getEntity(pack.getEntityId());
        for (ChannelContentTextDTO<PackContentTextType> element : texts) {
            element.setLanguage(checkElementLanguage(entity, languagesByIds, element.getLanguage()));
        }
        packsRepository.updatePackCommunicationElements(packId, ChannelContentConverter.toMsChannelText(texts));
    }

    public ChannelContentImageListDTO<PackContentImageType> getPackContentsImages(Long packId, ChannelContentImageFilter<PackContentImageType> filter) {
        checkPack(packId);

        CommunicationElementFilter<PackTagType> communicationElementFilter
                = ChannelContentConverter.fromPackFilter(filter, masterdataService);
        List<PackCommunicationElement> comElements = packsRepository.getPackCommunicationElements(packId,
                communicationElementFilter, PackTagType::isImage);

        comElements.sort(Comparator.comparing(PackCommunicationElement::getLanguage).
                thenComparing(PackCommunicationElement::getTagId).
                thenComparing(PackCommunicationElement::getPosition));

        return ChannelContentConverter.fromMsChannelImage(comElements);
    }

    public void updateChannelContentImages(Long packId, List<ChannelContentImageDTO<PackContentImageType>> images) {
        Pack pack = checkPack(packId);

        Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
        Entity entity = entitiesRepository.getEntity(pack.getEntityId());
        List<ChannelContentImageDTO<PackContentImageType>> results = images.stream()
                .peek(element -> element.setLanguage(checkElementLanguage(entity, languagesByIds, element.getLanguage())))
                .collect(Collectors.toList());

        if (CollectionUtils.isNotEmpty(results)) {
            packsRepository.updatePackCommunicationElements(packId, ChannelContentConverter.toMsChannelImageList(results));
        }
    }

    public void deleteChannelContentImages(Long packId, String language, PackContentImageType type, Integer position) {
        checkPack(packId);
        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        PackCommunicationElement dto = ChannelContentConverter.buildPackCommunicationElementToDelete(language, type, position, languages);
        packsRepository.updatePackCommunicationElements(packId, Collections.singletonList(dto));
    }

    public PackTicketContentsTextListDTO getPackTicketContentsTexts(Long packId, PackTicketContentTextPDFFilter filter, TicketCommunicationElementCategory category) {
        PackDetail pack = packsRepository.getPack(packId);
        validateRequest(pack.getEntityId(), filter.getLanguage());

        String type = filter.getType() != null ? filter.getType().name() : null;
        List<ChannelTicketContent> response = packsRepository.getPackTicketContent(packId, ConverterUtils.toLocale(filter.getLanguage()), type, category);
        PackTicketContentsTextListDTO result = PackTicketContentsConverter.toTextsDTO(response);
        result.sort(Comparator.comparing(PackTicketContentTextDTO::getLanguage));
        return result;
    }

    public void updatePackTicketContentsTexts(Long packId, PackTicketContentsTextListDTO contents, TicketCommunicationElementCategory category) {
        final PackDetail pack = packsRepository.getPack(packId);
        validateRequestTexts(pack.getEntityId(), contents);
        List<ChannelTicketContent> request = PackTicketContentsConverter.fromTextsDTO(contents);
        packsRepository.updatePackTicketContent(packId, request, category);
    }

    public PackTicketContentsImagePDFListDTO getPackTicketContentsPDFImages(Long packId, PackTicketContentImagePDFFilter filter, TicketCommunicationElementCategory category) {
        PackDetail pack = packsRepository.getPack(packId);
        validateRequest(pack.getEntityId(), filter.getLanguage());

        String type = filter.getType() != null ? filter.getType().name() : null;
        List<ChannelTicketContent> response = packsRepository.getPackTicketContent(packId, ConverterUtils.toLocale(filter.getLanguage()), type, category);
        PackTicketContentsImagePDFListDTO result = PackTicketContentsConverter.toImagesPDFDTO(response);
        result.sort(Comparator.comparing(PackTicketContentImagePDFDTO::getLanguage));
        return result;
    }

    public void updatePackTicketContentsPDFImages(Long packId, PackTicketContentsImagePDFListDTO contents, TicketCommunicationElementCategory category) {
        PackDetail pack = packsRepository.getPack(packId);
        validateRequestPDFImages(pack.getEntityId(), contents);
        List<ChannelTicketContent> request = PackTicketContentsConverter.fromImagesPDFDTO(contents);
        packsRepository.updatePackTicketContent(packId, request, category);
    }

    public void deletePackTicketContentPDFImage(Long packId, String language, TicketContentImagePDFType type, TicketCommunicationElementCategory category) {
        packsRepository.getPack(packId);
        packsRepository.deletePackTicketContent(packId, ConverterUtils.toLocale(language), type.getTag(), category);
    }

    public PackTicketContentsImagePrinterListDTO getPackTicketContentsPrinterImages(Long packId, PackTicketContentImagePrinterFilter filter, TicketCommunicationElementCategory category) {
        PackDetail pack = packsRepository.getPack(packId);
        validateRequest(pack.getEntityId(), filter.getLanguage());

        String type = filter.getType() != null ? filter.getType().name() : null;
        List<ChannelTicketContent> response = packsRepository.getPackTicketContent(packId, ConverterUtils.toLocale(filter.getLanguage()), type, category);
        PackTicketContentsImagePrinterListDTO result = PackTicketContentsConverter.toImagesPrinterDTO(response);
        result.sort(Comparator.comparing(PackTicketContentImagePrinterDTO::getLanguage));
        return result;
    }

    public void updatePackTicketContentsPrinterImages(Long packId, PackTicketContentsImagePrinterListDTO contents, TicketCommunicationElementCategory category) {
        PackDetail pack = packsRepository.getPack(packId);
        validateRequestPrinterImages(pack.getEntityId(), contents);
        List<ChannelTicketContent> request = PackTicketContentsConverter.fromImagesPrinterDTO(contents);
        packsRepository.updatePackTicketContent(packId, request, category);
    }

    public void deletePackTicketContentPrinterImage(Long packId, String language, TicketContentImagePrinterType type, TicketCommunicationElementCategory category) {
        PackDetail pack = packsRepository.getPack(packId);
        validateRequest(pack.getEntityId(), language);
        packsRepository.deletePackTicketContent(packId, ConverterUtils.toLocale(language), type.getTag(), category);
    }

    private void validateRequest(Long entityId, String languageCode) {
        Entity entity = entitiesRepository.getEntity(entityId);
        if (languageCode != null) {
            validateLanguage(entity, languageCode);
        }
    }

    private void validateLanguage(Entity entity, String... languageCodes) {
        List<String> languages = Arrays.stream(languageCodes).filter(Objects::nonNull).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(languages)) {
            return;
        }
        Set<String> entityLanguages = fromLanguageIdToLanguageCode(entity.getSelectedLanguages(), masterdataService.getLanguagesByIds());
        if (!entityLanguages.containsAll(languages)) {
            throw new OneboxRestException(ApiMgmtEntitiesErrorCode.ENTITY_UNSUPPORTED_LANGUAGE);
        }
    }

    private Set<String> fromLanguageIdToLanguageCode(List<IdValueCodeDTO> languageIds, Map<Long, String> masterLanguages) {
        return languageIds.stream()
                .map(IdValueCodeDTO::getId)
                .filter(masterLanguages::containsKey)
                .map(masterLanguages::get)
                .map(ConverterUtils::toLanguageTag)
                .collect(Collectors.toSet());
    }

    public static String checkElementLanguage(Entity entity, Map<Long, String> languagesByIds, String language) {
        String locale = ConverterUtils.checkLanguageByIds(language, languagesByIds);
        if (entity.getSelectedLanguages().stream().noneMatch(l -> languagesByIds.get(l.getId()).equals(locale))) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_LANG, "Invalid language " + language, null);
        }
        return locale;
    }

    private void validateRequestTexts(final Long entityId, final PackTicketContentsTextListDTO request) {
        Entity entity = entitiesRepository.getEntity(entityId);
        request.forEach(elem -> validateLanguage(entity, elem.getLanguage()));
    }

    private void validateRequestPDFImages(Long entityId, PackTicketContentsImagePDFListDTO request) {
        Entity entity = entitiesRepository.getEntity(entityId);
        request.forEach(elem -> {
            validateLanguage(entity, elem.getLanguage());
            FileUtils.checkImage(elem.getImageBinary(), elem.getType(), elem.getType().name());
        });
    }

    private void validateRequestPrinterImages(final Long entityId, final PackTicketContentsImagePrinterListDTO request) {
        Entity entity = entitiesRepository.getEntity(entityId);
        request.forEach(elem -> {
            validateLanguage(entity, elem.getLanguage());
            FileUtils.checkImage(elem.getImageBinary(), elem.getType(), elem.getType().name());
        });
    }

    private Pack checkPack(Long packId) {
        Pack pack = packsRepository.getPack(packId);
        if (pack == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.PACK_NOT_FOUND);
        }
        return pack;
    }
}
