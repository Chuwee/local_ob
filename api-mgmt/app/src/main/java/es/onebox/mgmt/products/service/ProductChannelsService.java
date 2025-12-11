package es.onebox.mgmt.products.service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.User;
import es.onebox.mgmt.datasources.ms.entity.repository.UsersRepository;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductChannels;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductChannelsResponse;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannel;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannelInfo;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannelSessions;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannelSessionsFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannels;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductChannel;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.products.converter.ProductChannelsConverter;
import es.onebox.mgmt.products.converter.ProductSessionConverter;
import es.onebox.mgmt.products.dto.CreateProductChannelsDTO;
import es.onebox.mgmt.products.dto.CreateProductChannelsResponseDTO;
import es.onebox.mgmt.products.dto.ProductChannelDTO;
import es.onebox.mgmt.products.dto.ProductChannelLinksFilter;
import es.onebox.mgmt.products.dto.ProductChannelSessionLinkDTO;
import es.onebox.mgmt.products.dto.ProductChannelsDTO;
import es.onebox.mgmt.products.dto.UpdateProductChannelDTO;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.sessions.converters.SessionStatusConverter;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ProductChannelsService {

    private final ValidationService validationService;
    private final MasterdataService masterdataService;
    private final ChannelsHelper channelsHelper;
    private final ProductsRepository productsRepository;
    private final ChannelsRepository channelsRepository;
    private final UsersRepository usersRepository;

    @Value("${onebox.webapps.channels.url}")
    private String urlChannel;

    @Autowired
    public ProductChannelsService(ProductsRepository productsRepository, ValidationService validationService,
                                  MasterdataService masterdataService, ChannelsHelper channelsHelper,
                                  ChannelsRepository channelsRepository, UsersRepository usersRepository) {
        this.productsRepository = productsRepository;
        this.validationService = validationService;
        this.masterdataService = masterdataService;
        this.channelsHelper = channelsHelper;
        this.channelsRepository = channelsRepository;
        this.usersRepository = usersRepository;
    }

    public ProductChannelsDTO getProductChannels(Long productId) {
        validationService.getAndCheckProduct(productId);
        ProductChannels productChannels = productsRepository.getProductChannels(productId);

        Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
        Map<Long, ChannelLanguagesDTO> languagesByChannelId = new HashMap<>();

        for (ProductChannel productChannel : productChannels) {
            ProductChannelInfo channelInfo = productChannel.getChannel();
            if (channelInfo == null) {
                continue;
            }

            Long channelId = channelInfo.getId();
            ChannelResponse channelResponse = channelsRepository.getChannel(channelId);
            ChannelLanguagesDTO languages = ChannelConverter.convertToLanguageDTO(channelResponse.getLanguages(), languagesByIds);

            languagesByChannelId.put(channelId, languages);
        }

        return ProductChannelsConverter.toDto(productChannels, languagesByChannelId);
    }

    public CreateProductChannelsResponseDTO createProductChannels(Long productId, CreateProductChannelsDTO createProductChannelsDTO) {
        validationService.getAndCheckProduct(productId);
        channelsHelper.getAndCheckChannels(createProductChannelsDTO.getChannelIds());

        CreateProductChannels createProductChannels = ProductChannelsConverter.toMs(createProductChannelsDTO);
        CreateProductChannelsResponse productChannels = productsRepository.createProductChannels(productId, createProductChannels);
        return ProductChannelsConverter.toDto(productChannels);
    }

    public void deleteProductChannel(Long productId, Long channelId) {
        validationService.getAndCheckProduct(productId);
        productsRepository.deleteProductChannel(productId, channelId);
        channelsRepository.deleteProductSaleRequestByProductAndChannel(productId, channelId);
    }

    public void updateProductChannel(Long productId, Long channelId, UpdateProductChannelDTO updateProductChannelDTO) {
        validationService.getAndCheckProduct(productId);
        UpdateProductChannel updateProductChannel = ProductChannelsConverter.toMs(updateProductChannelDTO);
        productsRepository.updateProductChannel(productId, channelId, updateProductChannel);
    }

    public ListWithMetadata<ProductChannelSessionLinkDTO> getProductChannelSessionLinks(Long productId, Long channelId,
                                                                                        String language,
                                                                                        ProductChannelLinksFilter filter) {
        validationService.getAndCheckProduct(productId);
        ChannelResponse channel = channelsRepository.getChannel(channelId);

        String currentLang = getLanguages(channel).getSelectedLanguageCode()
                .stream()
                .filter(e -> e.equals(language))
                .findFirst()
                .orElse(null);
        if (currentLang == null) {
            throw new OneboxRestException(ApiMgmtErrorCode.LANGUAGE_NOT_IN_CHANNEL);
        }

        ProductChannelSessionsFilter sessionsFilter = new ProductChannelSessionsFilter();
        sessionsFilter.setLimit(filter.getLimit());
        sessionsFilter.setOffset(filter.getOffset());
        sessionsFilter.setStatus(SessionStatusConverter.toMs(filter.getSessionStatus()));

        ProductChannelSessions sessions = productsRepository.getProductChannelSessions(productId, channelId, sessionsFilter);
        ListWithMetadata<ProductChannelSessionLinkDTO> response = new ListWithMetadata<>();
        if (sessions != null && CollectionUtils.isNotEmpty(sessions.getData())) {
            ChannelConfig channelConfig = channelsRepository.getChannelConfig(channelId);
            if (!ChannelUtils.isBoxOffice(ChannelSubtype.getById(channelConfig.getChannelType()))) {
                response.setMetadata(sessions.getMetadata());
                response.setData(ProductSessionConverter.convertSessionToLinks(sessions, channelConfig, currentLang, urlChannel));
            }
        }
        return response;
    }

    public ProductChannelDTO getProductChannel(Long productId, Long channelId) {
        validationService.getAndCheckProduct(productId);
        ChannelResponse channelResponse = channelsRepository.getChannel(channelId);

        Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
        ChannelLanguagesDTO languages = ChannelConverter.convertToLanguageDTO(channelResponse.getLanguages(), languagesByIds);

        ProductChannel productChannel = productsRepository.getProductChannel(productId, channelId);

        return ProductChannelsConverter.toDto(productChannel, languages);
    }

    public void requestChannelApproval(Long productId, Long channelId) {
        validationService.getAndCheckProduct(productId);

        ProductChannels productChannels = productsRepository.getProductChannels(productId);
        if (productChannels.stream().noneMatch(pc -> pc.getChannel().getId().equals(channelId))) {
            throw new OneboxRestException(ApiMgmtErrorCode.PRODUCT_CHANNEL_NOT_FOUND);
        }

        User user = usersRepository.getUser(SecurityUtils.getUsername(), SecurityUtils.getUserOperatorId(),
                SecurityUtils.getApiKey());
        channelsRepository.requestChannelProductApproval(productId, channelId, user.getId());
    }

    private ChannelLanguagesDTO getLanguages(ChannelResponse channel) {
        Map<Long, String> languagesByIds = masterdataService.getLanguagesByIds();
        return ChannelConverter.convertToLanguageDTO(channel.getLanguages(), languagesByIds);
    }
}
