package es.onebox.channels.catalog.generic;


import es.onebox.channels.catalog.ChannelCatalogContext;
import es.onebox.channels.catalog.ChannelCatalogConverter;
import es.onebox.channels.catalog.ChannelCatalogService;
import es.onebox.channels.catalog.ChannelCatalogUtils;
import es.onebox.channels.catalog.generic.dto.CatalogMetadata;
import es.onebox.channels.catalog.generic.dto.ChannelCatalogDTO;
import es.onebox.channels.catalog.generic.dto.ChannelEventTourDTO;
import es.onebox.channels.catalog.generic.dto.EventDTO;
import es.onebox.channels.catalog.generic.dto.LocationDTO;
import es.onebox.channels.catalog.generic.dto.SessionDTO;
import es.onebox.channels.catalog.generic.dto.SessionDatesDTO;
import es.onebox.channels.catalog.generic.dto.SessionPriceDTO;
import es.onebox.channels.catalog.generic.dto.VenueDTO;
import es.onebox.common.datasources.catalog.dto.ChannelEventDetail;
import es.onebox.common.datasources.catalog.dto.TourInfo;
import es.onebox.common.datasources.catalog.dto.common.Price;
import es.onebox.common.datasources.catalog.dto.session.ChannelSession;
import es.onebox.common.datasources.catalog.dto.session.request.SessionsRequestDTOBuilder;
import es.onebox.core.utils.common.NumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Component("GenericCatalogConverter")
public final class GenericCatalogConverter implements ChannelCatalogConverter<ChannelCatalogDTO> {


    private final ChannelCatalogService channelCatalogService;

    @Autowired
    public GenericCatalogConverter(ChannelCatalogService channelCatalogService) {
        this.channelCatalogService = channelCatalogService;
    }

    @Override
    public ChannelCatalogDTO convert(ChannelCatalogContext context, Long limit, Long offset, Map parameters) {
        final Long channelId = context.getId();

        var params = SessionsRequestDTOBuilder.builder()
                .eventIds(Arrays.asList(ChannelCatalogUtils.getEventId(parameters)))
                .limit(limit)
                .offset(offset).build();
        var sessions = channelCatalogService.getSessions(channelId, context.getApiKey(), params);

        var eventsMap = sessions.getData().stream()
                .map(s -> s.getEvent().getId())
                .distinct()
                .collect(Collectors.toList())
                .stream().map(eventId -> channelCatalogService.getEvent(channelId, context.getApiKey(), eventId))
                .collect(Collectors.toMap(ChannelEventDetail::getId, Function.identity()));
        ChannelCatalogDTO response = new ChannelCatalogDTO();

        response.setMetadata(new CatalogMetadata());
        response.getMetadata().setLimit(sessions.getMetadata().getLimit());
        response.getMetadata().setOffset(sessions.getMetadata().getOffset());
        response.getMetadata().setTotal(sessions.getMetadata().getTotal());

        List<SessionDTO> events = sessions.getData().stream()
                .map(sessionCatalog -> {
                    SessionDTO session = new SessionDTO();
                    session.setId(sessionCatalog.getId());
                    session.setReference(sessionCatalog.getReference());

                    SessionDatesDTO dates = new SessionDatesDTO();
                    dates.setStart(sessionCatalog.getDate().getStart());
                    dates.setSaleStart(sessionCatalog.getDate().getSaleStart());
                    dates.setSaleEnd(sessionCatalog.getDate().getSaleEnd());
                    session.setDate(dates);
                    session.setName(sessionCatalog.getName());
                    session.setSoldOut(sessionCatalog.getSoldOut());

                    ChannelEventDetail event = eventsMap.get(sessionCatalog.getEvent().getId());
                    String defaultLanguage = event.getLanguage().getDefaultLang();
                    session.setUrl(new HashMap<>());
                    session.getUrl().put(defaultLanguage, ChannelCatalogUtils.getSessionSelectUrl(context, sessionCatalog.getId(), event.getId(), context.getDefaultLanguage()));

                    if (event.getLanguage() != null &&
                            CollectionUtils.isNotEmpty(event.getLanguage().getOthers())) {
                        event.getLanguage().getOthers().forEach(s -> {
                            session.getUrl().put(s, ChannelCatalogUtils.getSessionSelectUrl(context, sessionCatalog.getId(), event.getId(), s));
                        });
                    }
                    session.setEvent(fillEvent(event, context));
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
                        session.setPromotions(session1.getPromotions().stream().peek(p -> p.setValidityPeriod(null)).collect(Collectors.toList()));
                    }
                    if (sessionCatalog.getTexts() != null) {
                        Map<String, String> description = sessionCatalog.getTexts().getDescription();
                        if (MapUtils.isNotEmpty(description)) {
                            session.setDescription(description);
                        }
                    }

                    return session;
                }).collect(Collectors.toList());
        response.setData(events);

        return response;
    }

    private static Price convertPrice(Price price) {
        Price p = new Price();
        p.setValue(price != null ? NumberUtils.zeroIfNull(price.getValue()) : 0d);

        return p;
    }

    private static EventDTO fillEvent(ChannelEventDetail in, ChannelCatalogContext context) {
        EventDTO out = new EventDTO();
        out.setId(in.getId());
        out.setName(in.getName());
        out.setUrl(new HashMap<>());
        out.setCategory(in.getCategory());
        out.setReference(in.getReference());
        fillTour(in, out);
        String defaultLanguage = in.getLanguage().getDefaultLang();
        out.getUrl().put(defaultLanguage, ChannelCatalogUtils.getEventCardUrl(context, in.getId(), defaultLanguage));

        if (in.getLanguage() != null &&
                CollectionUtils.isNotEmpty(in.getLanguage().getOthers())) {
            in.getLanguage().getOthers().forEach(s -> {
                out.getUrl().put(s, ChannelCatalogUtils.getEventCardUrl(context, in.getId(), s));
            });
        }

        if (in.getTexts() != null) {
            out.setTitle(in.getTexts().getTitle());
            out.setShortDescription(in.getTexts().getDescriptionShort());
            out.setLongDescription(in.getTexts().getDescriptionLong());
        }

        out.setImage(in.getImages());

        return out;
    }

    private static void fillTour(ChannelEventDetail in, EventDTO out) {
        TourInfo tourInfo = in.getTour();
        if (tourInfo != null) {
            ChannelEventTourDTO tour = new ChannelEventTourDTO();
            tour.setId(tourInfo.getId());
            tour.setName(tourInfo.getName());
            out.setTour(tour);
        }
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

}
