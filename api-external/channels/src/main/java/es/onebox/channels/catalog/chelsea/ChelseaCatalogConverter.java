package es.onebox.channels.catalog.chelsea;


import es.onebox.channels.catalog.ChannelCatalogContext;
import es.onebox.channels.catalog.ChannelCatalogConverter;
import es.onebox.channels.catalog.ChannelCatalogService;
import es.onebox.channels.catalog.ChannelCatalogUtils;
import es.onebox.channels.catalog.chelsea.dto.ChelseaCatalogDTO;
import es.onebox.channels.catalog.chelsea.dto.ChelseaEventDTO;
import es.onebox.channels.catalog.chelsea.dto.ChelseaSessionDTO;
import es.onebox.channels.catalog.chelsea.dto.ChelseaZoneAvailabilityDTO;
import es.onebox.channels.catalog.chelsea.dto.ChelseaZoneCapacityDTO;
import es.onebox.channels.catalog.generic.dto.CatalogMetadata;
import es.onebox.channels.catalog.generic.dto.LocationDTO;
import es.onebox.channels.catalog.generic.dto.SessionDatesDTO;
import es.onebox.channels.catalog.generic.dto.SessionPriceDTO;
import es.onebox.channels.catalog.generic.dto.VenueDTO;
import es.onebox.common.datasources.catalog.dto.ChannelEventDetail;
import es.onebox.common.datasources.catalog.dto.common.Price;
import es.onebox.common.datasources.catalog.dto.session.ChannelSession;
import es.onebox.common.datasources.catalog.dto.session.availability.ChannelSessionAvailabilityResponse;
import es.onebox.common.datasources.catalog.dto.session.availability.ChannelSessionPricetype;
import es.onebox.common.datasources.catalog.dto.session.availability.ChannelSessionSector;
import es.onebox.common.datasources.catalog.dto.session.availability.ChannelSessionVenueMapLink;
import es.onebox.common.datasources.catalog.dto.session.availability.ChannelSessionVenueMapResponse;
import es.onebox.common.datasources.catalog.dto.session.prices.CatalogPriceType;
import es.onebox.common.datasources.catalog.dto.session.prices.CatalogRate;
import es.onebox.common.datasources.catalog.dto.session.prices.SessionPrices;
import es.onebox.common.datasources.catalog.dto.session.request.SessionsRequestDTOBuilder;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.NumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component("ChelseaCatalogConverter")
public final class ChelseaCatalogConverter implements ChannelCatalogConverter<ChelseaCatalogDTO> {


    private final ChannelCatalogService channelCatalogService;

    @Autowired
    public ChelseaCatalogConverter(ChannelCatalogService channelCatalogService) {
        this.channelCatalogService = channelCatalogService;
    }

    @Override
    public ChelseaCatalogDTO convert(ChannelCatalogContext context, Long limit, Long offset, Map parameters) {
        final Long channelId = context.getId();

        var params = SessionsRequestDTOBuilder.builder()
                .eventIds(Arrays.asList(getParameter(parameters, "event-id")))
                .sessionIds(Arrays.asList(getParameter(parameters, "session-id")))
                .limit(limit)
                .offset(offset).build();
        var sessions = channelCatalogService.getSessions(channelId, context.getApiKey(), params);

        var eventsMap = sessions.getData().stream()
                .map(s -> s.getEvent().getId())
                .distinct()
                .collect(Collectors.toList())
                .stream().map(eventId -> channelCatalogService.getEvent(channelId, context.getApiKey(), eventId))
                .collect(Collectors.toMap(ChannelEventDetail::getId, Function.identity()));
        ChelseaCatalogDTO channelCatalog = new ChelseaCatalogDTO();

        channelCatalog.setMetadata(new CatalogMetadata());
        channelCatalog.getMetadata().setLimit(sessions.getMetadata().getLimit());
        channelCatalog.getMetadata().setOffset(sessions.getMetadata().getOffset());
        channelCatalog.getMetadata().setTotal(sessions.getMetadata().getTotal());

        List<ChelseaSessionDTO> events = sessions.getData().stream()
                .map(sessionCatalog -> {
                    ChelseaSessionDTO session = new ChelseaSessionDTO();
                    session.setId(sessionCatalog.getId());

                    SessionDatesDTO sessionDatesDTO = new SessionDatesDTO();
                    sessionDatesDTO.setStart(sessionCatalog.getDate().getStart());
                    sessionDatesDTO.setSaleStart(sessionCatalog.getDate().getSaleStart());
                    sessionDatesDTO.setSaleEnd(sessionCatalog.getDate().getSaleEnd());
                    session.setDate(sessionDatesDTO);
                    session.setName(sessionCatalog.getName());
                    session.setReference(sessionCatalog.getReference());
                    session.setSoldOut(sessionCatalog.getSoldOut());

                    var eventCatalog = eventsMap.get(sessionCatalog.getEvent().getId());
                    String defaultLanguage = eventCatalog.getLanguage().getDefaultLang();
                    session.setUrl(new HashMap<>());
                    session.getUrl().put(defaultLanguage, ChannelCatalogUtils.getSessionSelectUrl(context, sessionCatalog.getId(), eventCatalog.getId(), context.getDefaultLanguage()));

                    if (eventCatalog.getLanguage() != null &&
                            CollectionUtils.isNotEmpty(eventCatalog.getLanguage().getOthers())) {
                        eventCatalog.getLanguage().getOthers().forEach(s -> {
                            session.getUrl().put(s, ChannelCatalogUtils.getSessionSelectUrl(context, sessionCatalog.getId(), eventCatalog.getId(), s));
                        });
                    }

                    session.setEvent(fillEvent(eventCatalog, context));
                    session.setVenue(fillVenue(sessionCatalog));


                    var prices = sessionCatalog.getPrice();

                    if (prices != null) {
                        SessionPriceDTO sessionPriceDTO = new SessionPriceDTO();
                        if (prices.getMinPromoted() != null) {
                            sessionPriceDTO.setMinPromoted(convertPrice(prices.getMinPromoted()));
                        }
                        sessionPriceDTO.setMin(convertPrice(prices.getMin()));
                        sessionPriceDTO.setMax(convertPrice(prices.getMax()));
                        session.setPrice(sessionPriceDTO);
                    }

                    var session1 = channelCatalogService.getSession(channelId, context.getApiKey(), sessionCatalog.getEvent().getId(), sessionCatalog.getId());
                    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(session1.getPromotions())) {
                        session.setPromotions(session1.getPromotions().stream().map(p -> {
                            p.setValidityPeriod(null);
                            return p;
                        }).collect(Collectors.toList()));
                    }

                    ChannelSessionAvailabilityResponse sessionAvailability = channelCatalogService
                            .getSessionAvailability(context.getId(), context.getApiKey(), sessionCatalog.getId());
                    SessionPrices sessionPrices = channelCatalogService
                            .getSessionPrices(context.getId(), context.getApiKey(), sessionCatalog.getId());
                    ChannelSessionVenueMapResponse sessionVenueMap = channelCatalogService
                            .getSessionVenueMap(context.getId(), context.getApiKey(), sessionCatalog.getId());
                    session.setZones(fillCapacity(sessionAvailability, sessionPrices, sessionVenueMap, session));
                    return session;
                }).collect(Collectors.toList());
        channelCatalog.setData(events);

        return channelCatalog;
    }

    private static Price convertPrice(Price price) {
        Price p = new Price();
        p.setValue(price != null ? NumberUtils.zeroIfNull(price.getValue()) : 0d);

        return p;
    }

    private static ChelseaEventDTO fillEvent(ChannelEventDetail eventCatalog, ChannelCatalogContext context) {
        ChelseaEventDTO event = new ChelseaEventDTO();
        event.setId(eventCatalog.getId());
        event.setName(eventCatalog.getName());
        event.setReference(eventCatalog.getReference());
        event.setUrl(new HashMap<>());
        event.setCategory(eventCatalog.getCategory());

        String defaultLanguage = eventCatalog.getLanguage().getDefaultLang();
        event.getUrl().put(defaultLanguage, ChannelCatalogUtils.getEventCardUrl(context, eventCatalog.getId(), defaultLanguage));

        if (eventCatalog.getLanguage() != null &&
                CollectionUtils.isNotEmpty(eventCatalog.getLanguage().getOthers())) {
            eventCatalog.getLanguage().getOthers().forEach(s -> {
                event.getUrl().put(s, ChannelCatalogUtils.getEventCardUrl(context, eventCatalog.getId(), s));
            });
        }

        if (eventCatalog.getTexts() != null) {
            event.setTitle(eventCatalog.getTexts().getTitle());
            event.setShortDescription(eventCatalog.getTexts().getDescriptionShort());
            event.setLongDescription(eventCatalog.getTexts().getDescriptionLong());
        }

        event.setImage(eventCatalog.getImages());

        return event;
    }

    private static VenueDTO fillVenue(ChannelSession sessionCatalog) {
        VenueDTO venue = new VenueDTO();
        venue.setId(sessionCatalog.getVenue().getId());
        venue.setName(sessionCatalog.getVenue().getName());

        LocationDTO location = new LocationDTO();
        location.setAddress(sessionCatalog.getVenue().getLocation().getAddress());
        location.setCity(sessionCatalog.getVenue().getLocation().getCity());
        location.setPostalCode(sessionCatalog.getVenue().getLocation().getPostalCode());
        location.setTimeZone(sessionCatalog.getVenue().getLocation().getTimeZone());
        location.setCountry(sessionCatalog.getVenue().getLocation().getCountry().getCode());
        location.setCountrySubdivision(sessionCatalog.getVenue().getLocation().getCountrySubdivision().getCode());
        venue.setLocation(location);

        return venue;
    }

    private Long getParameter(Map parameters, String field) {
        Object value = parameters.get(field);
        var s = value != null ? (String[]) value : null;

        if (s != null && StringUtils.isNumeric(s[0])) {
            return Long.parseLong(s[0]);
        } else if (s != null && !StringUtils.isNumeric(s[0])) {
            throw new OneboxRestException(ApiExternalErrorCode.BAD_REQUEST_PARAMETER);
        } else {
            return null;
        }
    }

    private static List<ChelseaZoneCapacityDTO> fillCapacity(ChannelSessionAvailabilityResponse availability,
                                                             SessionPrices sessionPrices,
                                                             ChannelSessionVenueMapResponse venueMap,
                                                             ChelseaSessionDTO session) {

        List<ChelseaZoneCapacityDTO> zones = new ArrayList<>();
        Map<String, String> sectorViewMapping = new HashMap<>();
        for (ChannelSessionVenueMapLink link : venueMap.getElement().getLinks()) {
            Optional<IdNameCodeDTO> anySector = link.getSectors().stream().findAny();
            anySector.ifPresent(idNameCodeDTO -> sectorViewMapping.put(idNameCodeDTO.getCode(),
                    link.getTargetView().getCode()));
        }

        Map<Long, SessionPriceDTO> prices = fillPrices(sessionPrices);
        for (ChannelSessionSector sector : availability.getSectors()) {
            ChelseaZoneCapacityDTO capacityDTO = new ChelseaZoneCapacityDTO();
            Optional<ChannelSessionPricetype> anyPriceType = sector.getPriceTypes().stream().findAny();
            if (anyPriceType.isPresent()) {
                IdNameCodeDTO sectorData = new IdNameCodeDTO();
                IdNameDTO priceTypeData = new IdNameDTO();
                sectorData.setId(sector.getId());
                sectorData.setName(sector.getName());
                sectorData.setCode(sector.getCode());
                priceTypeData.setId(anyPriceType.get().getId());
                priceTypeData.setName(anyPriceType.get().getName());
                capacityDTO.setSector(sectorData);
                capacityDTO.setPriceType(priceTypeData);
                capacityDTO.setAvailability(fillAvailability(anyPriceType.get()));
                capacityDTO.setUrl(fillViewUrls(session.getUrl(), sectorViewMapping.get(sector.getCode())));
                capacityDTO.setPrice(prices.get(anyPriceType.get().getId()));
            }
            zones.add(capacityDTO);
        }
        return zones;
    }

    private static Map<String, String> fillViewUrls(Map<String, String> sessionUrls, String viewCode) {
        Map<String, String> newUrls = new HashMap<>();
        sessionUrls.forEach((key, value) -> newUrls.put(key, value + "&viewCode=" + viewCode));
        return newUrls;
    }

    private static ChelseaZoneAvailabilityDTO fillAvailability(ChannelSessionPricetype priceType) {
        ChelseaZoneAvailabilityDTO availabilityDTO = new ChelseaZoneAvailabilityDTO();
        availabilityDTO.setTotal(priceType.getAvailability().getTotal());
        availabilityDTO.setAvailable(priceType.getAvailability().getAvailable());
       return availabilityDTO;
    }

    private static Map<Long, SessionPriceDTO> fillPrices(SessionPrices sessionPrices) {
        Map<Long, SessionPriceDTO> prices = new HashMap<>();
        for (CatalogRate rate : sessionPrices.getRates()) {
            for (CatalogPriceType priceType : rate.getPriceTypes()) {
                SessionPriceDTO sessionPriceDTO = new SessionPriceDTO();
                if (prices.containsKey(priceType.getId())) {
                    sessionPriceDTO = prices.get(priceType.getId());
                    sessionPriceDTO.getMin().setValue(Math.min(sessionPriceDTO.getMin().getValue(),
                            priceType.getPrice().getTotal()));
                    sessionPriceDTO.getMax().setValue(Math.max(sessionPriceDTO.getMax().getValue(),
                            priceType.getPrice().getTotal()));
                } else {
                    Price min = new Price();
                    min.setValue(priceType.getPrice().getTotal());
                    Price max = new Price();
                    max.setValue(priceType.getPrice().getTotal());
                    sessionPriceDTO.setMin(min);
                    sessionPriceDTO.setMax(max);
                }
                prices.put(priceType.getId(), sessionPriceDTO);
            }

        }
        return prices;
    }

}
