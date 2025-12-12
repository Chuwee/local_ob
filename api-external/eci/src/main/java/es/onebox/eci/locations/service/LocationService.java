package es.onebox.eci.locations.service;

import es.onebox.common.datasources.catalog.dto.ChannelEvent;
import es.onebox.common.datasources.catalog.dto.common.Venue;
import es.onebox.common.datasources.catalog.repository.CatalogRepository;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;
import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.ms.channel.repository.ChannelConfigRepository;
import es.onebox.common.datasources.ms.entity.repository.UsersRepository;
import es.onebox.common.datasources.oauth2.repository.TokenRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.eci.locations.converter.LocationConverter;
import es.onebox.eci.locations.dto.Location;
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
import java.util.stream.Collectors;

@Service
public class LocationService {

    private final CatalogRepository catalogRepository;
    private final TokenRepository tokenRepository;
    private final ChannelsHelper channelsHelper;
    private final ChannelConfigRepository channelConfigRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public LocationService(CatalogRepository catalogRepository,
                           TokenRepository tokenRepository,
                           ChannelsHelper channelsHelper,
                           ChannelConfigRepository channelConfigRepository,
                           UsersRepository usersRepository) {
        this.catalogRepository = catalogRepository;
        this.tokenRepository = tokenRepository;
        this.channelsHelper = channelsHelper;
        this.channelConfigRepository = channelConfigRepository;
        this.usersRepository = usersRepository;
    }

    public List<Location> getLocations(ZonedDateTime gte, ZonedDateTime lte, Long limit, Long offset, String channelIdentifier) {
        List<Location> locations = new ArrayList<>();
        List<ChannelDTO> channelDetails = channelsHelper.getChannelDetails(channelIdentifier);
        if (CollectionUtils.isNotEmpty(channelDetails)) {
            locations = getLocations(gte, lte, limit, offset, channelDetails);
        }
        return locations;
    }

    public Location getLocation(String channelIdentifier, Long locationId) {
        Location location = null;
        List<ChannelDTO> channelDetails = channelsHelper.getChannelDetails(channelIdentifier);
        if (CollectionUtils.isNotEmpty(channelDetails)) {
            location = getLocation(channelDetails, locationId);
        }
        return location;
    }

    private List<Location> getLocations(ZonedDateTime gte, ZonedDateTime lte, Long limit, Long offset, List<ChannelDTO> channelDetails) {
        List<ChannelEvent> channelEvents;
        List<Location> locations = new ArrayList<>();

        for (ChannelDTO channel : channelDetails) {
            ChannelConfigDTO channelConfig = channelConfigRepository.getChannelConfig(channel.getId());
            String token = AuthenticationUtils.getToken(channelConfig.getUserName(), channelConfig.getUserPassword(),
                    channel.getId(), channel.getEntityId(),
                    tokenRepository::getSellerChannelToken, usersRepository::getFilteredUsers);
            if (token != null) {
                channelEvents = catalogRepository.getEvents(token, gte, lte);

                locations.addAll(channelEvents.stream()
                        .map(channelEvent -> LocationConverter.convert(channelEvent.getVenues()))
                        .flatMap(location -> location.stream())
                        .collect(Collectors.toList()));
            }
        }

        return locations.stream()
                .distinct()
                .sorted(Comparator.comparing(Location::getName, String.CASE_INSENSITIVE_ORDER))
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private Location getLocation(List<ChannelDTO> channelDetails, Long locationId) {
        List<ChannelEvent> channelEvents;

        for (ChannelDTO channel : channelDetails) {
            ChannelConfigDTO channelConfig = channelConfigRepository.getChannelConfig(channel.getId());
            String token = AuthenticationUtils.getToken(channelConfig.getUserName(), channelConfig.getUserPassword(),
                    channel.getId(), channel.getEntityId(),
                    tokenRepository::getSellerChannelToken, usersRepository::getFilteredUsers);

            if (token != null) {
                channelEvents = catalogRepository.getEvents(token, locationId);

                Venue venue = channelEvents.stream()
                        .map(ChannelEvent::getVenues)
                        .flatMap(Collection::stream)
                        .filter(v -> v.getId().equals(locationId))
                        .findFirst().orElse(null);

                if (venue != null) {
                    return LocationConverter.convert(venue);
                }
            }
        }
        throw ExceptionBuilder.build(ApiExternalErrorCode.NOT_FOUND);
    }
}
