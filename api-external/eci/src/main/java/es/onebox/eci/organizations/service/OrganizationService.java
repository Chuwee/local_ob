package es.onebox.eci.organizations.service;

import es.onebox.common.datasources.catalog.dto.ChannelEvent;
import es.onebox.common.datasources.catalog.dto.ChannelEventDetail;
import es.onebox.common.datasources.catalog.dto.ChannelEventEntity;
import es.onebox.common.datasources.catalog.repository.CatalogRepository;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;
import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.ms.channel.repository.ChannelConfigRepository;
import es.onebox.common.datasources.ms.entity.repository.UsersRepository;
import es.onebox.common.datasources.oauth2.repository.TokenRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.eci.organizations.converter.OrganizationConverter;
import es.onebox.eci.organizations.dto.Brand;
import es.onebox.eci.organizations.dto.Organization;
import es.onebox.eci.organizations.dto.OrganizationType;
import es.onebox.eci.service.ChannelsHelper;
import es.onebox.eci.utils.AuthenticationUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class OrganizationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrganizationService.class);

    private final CatalogRepository catalogRepository;
    private final TokenRepository tokenRepository;
    private final ChannelConfigRepository channelConfigRepository;
    private final ChannelsHelper channelsHelper;
    private final UsersRepository usersRepository;

    @Autowired
    public OrganizationService(CatalogRepository catalogRepository, TokenRepository tokenRepository,
                               ChannelsHelper channelsHelper, ChannelConfigRepository channelConfigRepository,
                               UsersRepository usersRepository) {
        this.catalogRepository = catalogRepository;
        this.tokenRepository = tokenRepository;
        this.channelConfigRepository = channelConfigRepository;
        this.channelsHelper = channelsHelper;
        this.usersRepository = usersRepository;
    }

    public List<Organization> getOrganizations(ZonedDateTime gte, ZonedDateTime lte, Long limit, Long offset, String channelIdentifier) {
        List<Organization> organizations = new ArrayList<>();
        List<ChannelDTO> channelDetails = channelsHelper.getChannelDetails(channelIdentifier);
        if (CollectionUtils.isNotEmpty(channelDetails)) {
            organizations = getOrganizations(gte, lte, limit, offset, channelDetails);
        }
        return organizations;
    }

    public Organization getOrganization(String channelIdentifier, String organizationId) {
        Organization organization = null;
        List<ChannelDTO> channelDetails = channelsHelper.getChannelDetails(channelIdentifier);
        if (CollectionUtils.isNotEmpty(channelDetails)) {
            organization = getOrganization(channelDetails, organizationId);
        }
        return organization;
    }

    private List<Organization> getOrganizations(ZonedDateTime gte, ZonedDateTime lte, Long limit, Long offset, List<ChannelDTO> channelDetail) {
        List<ChannelEvent> channelEvents;
        List<ChannelEventDetail> channelEventDetails;
        Set<Organization> organizations = new HashSet<>();

        for (ChannelDTO channel : channelDetail) {
            List<ChannelDTO> channels = channelsHelper.getChannels(null, channel.getEntityId());
            ChannelConfigDTO channelConfig = channelConfigRepository.getChannelConfig(channel.getId());
            String token = AuthenticationUtils.getToken(channelConfig.getUserName(), channelConfig.getUserPassword(),
                    channel.getId(), channel.getEntityId(),
                    tokenRepository::getSellerChannelToken, usersRepository::getFilteredUsers);
            // search event promoters as an organizers
            if (token != null) {
                channelEvents = catalogRepository.getEvents(token, gte, lte);

                if (CollectionUtils.isNotEmpty(channelEvents)) {
                    channelEventDetails = channelEvents.stream()
                            .map(channelEvent -> catalogRepository.getEventOrElseNull(token, channelEvent.getId()))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());

                    organizations.addAll(OrganizationConverter.getOrganizations(channelEventDetails, channel, channelConfig, channels));
                }
            }
        }

        return organizations.stream()
                .sorted(Comparator.comparing(Organization::getName, String.CASE_INSENSITIVE_ORDER))
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private Organization getOrganization(List<ChannelDTO> channelDetail, String organizationId) {

        OrganizationType organizationType = OrganizationType.findByInitial(organizationId.charAt(0));
        Long id = Long.valueOf(organizationId.substring(1));

        for (ChannelDTO channel : channelDetail) {
            ChannelConfigDTO channelConfig = channelConfigRepository.getChannelConfig(channel.getId());
            String token = AuthenticationUtils.getToken(channelConfig.getUserName(), channelConfig.getUserPassword(),
                    channel.getId(), channel.getEntityId(),
                    tokenRepository::getSellerChannelToken, usersRepository::getFilteredUsers);

            if (token != null) {
                List<ChannelEvent> channelEvents = catalogRepository.getEvents(token);

                if (CollectionUtils.isNotEmpty(channelEvents)) {
                    List<ChannelEventDetail> channelEventDetails = channelEvents.stream()
                            .map(channelEvent -> catalogRepository.getEventOrElseNull(token, channelEvent.getId()))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    List<Brand> brands = channelsHelper.getChannels(null, channel.getEntityId())
                            .stream().map(OrganizationConverter::fillBrand).collect(Collectors.toList());

                    ChannelEventEntity channelEventEntity = OrganizationConverter.fillEntity(channel, channelConfig);
                    Organization organization = null;
                    switch (organizationType) {
                        case SPONSOR ->
                                organization = OrganizationConverter.convert(
                                        getChannelEventEntity(id, channelEventDetails.stream().map(ChannelEventDetail::getEntity)),
                                        OrganizationType.SPONSOR);
                        case ORGANIZER ->
                                organization = OrganizationConverter.convert(
                                        getChannelEventEntity(id, channelEventDetails.stream().map(ChannelEventDetail::getPromoter)),
                                        OrganizationType.ORGANIZER);
                        case PROVIDER ->
                                organization = OrganizationConverter.convert(
                                        getChannelEventEntity(id, Stream.of(channelEventEntity)),
                                        OrganizationType.PROVIDER, brands);
                    }

                    if (organization != null) {
                        if (organization.getTaxId().equals(channelConfig.getEntityNif())) {
                            organization.getOrganizationTypes().add(OrganizationType.PROVIDER);
                            organization.setBrands(brands);
                        }
                        return organization;
                    }
                }
            }
        }

        throw ExceptionBuilder.build(ApiExternalErrorCode.NOT_FOUND);
    }

    private ChannelEventEntity getChannelEventEntity(Long organizationId, Stream<ChannelEventEntity> channelEventEntityStream) {
        return channelEventEntityStream
                .filter(channelEventEntity -> channelEventEntity.getId().equals(organizationId))
                .findFirst()
                .orElse(null);
    }
}
