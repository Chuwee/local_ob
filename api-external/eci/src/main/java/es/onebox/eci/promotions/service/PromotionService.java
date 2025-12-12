package es.onebox.eci.promotions.service;

import es.onebox.common.datasources.catalog.dto.ChannelEvent;
import es.onebox.common.datasources.catalog.dto.ChannelEventDetail;
import es.onebox.common.datasources.catalog.repository.CatalogRepository;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;
import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.ms.channel.repository.ChannelConfigRepository;
import es.onebox.common.datasources.ms.entity.repository.UsersRepository;
import es.onebox.common.datasources.oauth2.repository.TokenRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.eci.promotions.converter.PromotionConverter;
import es.onebox.eci.promotions.dto.Promotion;
import es.onebox.eci.service.ChannelsHelper;
import es.onebox.eci.utils.AuthenticationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    private final TokenRepository tokenRepository;
    private final CatalogRepository catalogRepository;
    private final ChannelsHelper channelsHelper;
    private final ChannelConfigRepository channelConfigRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public PromotionService(TokenRepository tokenRepository, CatalogRepository catalogRepository,
                            ChannelsHelper channelsHelper, ChannelConfigRepository channelConfigRepository,
                            UsersRepository usersRepository) {
        this.tokenRepository = tokenRepository;
        this.catalogRepository = catalogRepository;
        this.channelsHelper = channelsHelper;
        this.channelConfigRepository = channelConfigRepository;
        this.usersRepository = usersRepository;
    }

    public List<Promotion> getPromotions(ZonedDateTime gte, ZonedDateTime lte, Long limit, Long offset, String channelIdentifier) {
        List<Promotion> promotions = new ArrayList<>();
        List<ChannelDTO> channelDetails = channelsHelper.getChannelDetails(channelIdentifier);
        if (CollectionUtils.isNotEmpty(channelDetails)) {
            promotions = getPromotions(gte, lte, limit, offset, channelDetails);
        }
        return promotions;
    }

    public Promotion getPromotion(String channelIdentifier, Long promotionId) {
        Promotion promotion = null;
        List<ChannelDTO> channelDetails = channelsHelper.getChannelDetails(channelIdentifier);
        if (CollectionUtils.isNotEmpty(channelDetails)) {
            promotion = getPromotion(channelDetails, promotionId);
        }
        return promotion;
    }

    private List<Promotion> getPromotions(ZonedDateTime gte, ZonedDateTime lte, Long limit, Long offset, List<ChannelDTO> channelDetails) {
        List<ChannelEvent> channelEvents;
        List<ChannelEventDetail> channelEventDetails;
        List<Promotion> promotions = new ArrayList<>();

        for (ChannelDTO channel : channelDetails) {
            ChannelConfigDTO channelConfig = channelConfigRepository.getChannelConfig(channel.getId());
            String token = AuthenticationUtils.getToken(channelConfig.getUserName(), channelConfig.getUserPassword(),
                    channel.getId(), channel.getEntityId(),
                    tokenRepository::getSellerChannelToken, usersRepository::getFilteredUsers);

            if (token != null) {
                channelEvents = catalogRepository.getEvents(token, gte, lte);
                if (CollectionUtils.isNotEmpty(channelEvents)) {
                    channelEventDetails = channelEvents.stream()
                            .map(channelEvent -> catalogRepository.getEventOrElseNull(token, channelEvent.getId()))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    promotions.addAll(channelEventDetails.stream()
                            .flatMap(channelEventDetail -> channelEventDetail.getPromotions().stream())
                            .map(PromotionConverter::convert)
                            .distinct()
                            .collect(Collectors.toList()));
                }
            }
        }

        return promotions.stream()
                .distinct().sorted(Comparator.comparing(Promotion::getName, String.CASE_INSENSITIVE_ORDER))
                .skip(offset).limit(limit)
                .collect(Collectors.toList());
    }

    private Promotion getPromotion(List<ChannelDTO> channelDetails, Long promotionId) {
        List<ChannelEvent> channelEvents;
        List<ChannelEventDetail> channelEventDetails;

        for (ChannelDTO channel : channelDetails) {
            ChannelConfigDTO channelConfig = channelConfigRepository.getChannelConfig(channel.getId());
            String token = AuthenticationUtils.getToken(channelConfig.getUserName(), channelConfig.getUserPassword(),
                    channel.getId(), channel.getEntityId(),
                    tokenRepository::getSellerChannelToken, usersRepository::getFilteredUsers);

            if (token != null) {
                channelEvents = catalogRepository.getEvents(token);
                if (CollectionUtils.isNotEmpty(channelEvents)) {
                    channelEventDetails = channelEvents.stream()
                            .map(channelEvent -> catalogRepository.getEventOrElseNull(token, channelEvent.getId()))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    es.onebox.common.datasources.catalog.dto.common.Promotion promotion =
                            channelEventDetails.stream()
                                    .map(ChannelEventDetail::getPromotions)
                                    .flatMap(Collection::stream)
                                    .filter(p -> p.getId().equals(promotionId))
                                    .findFirst().orElse(null);

                    if (promotion != null) {
                        return PromotionConverter.convert(promotion);
                    }
                }
            }
        }
        throw ExceptionBuilder.build(ApiExternalErrorCode.PROMOTION_NOT_FOUND);
    }
}
