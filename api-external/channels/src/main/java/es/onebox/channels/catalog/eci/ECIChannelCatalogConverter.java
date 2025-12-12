package es.onebox.channels.catalog.eci;

import es.onebox.channels.catalog.CatalogEvent;
import es.onebox.channels.catalog.CatalogEvents;
import es.onebox.channels.catalog.ChannelCatalogContext;
import es.onebox.channels.catalog.eci.dto.ECIAddressDTO;
import es.onebox.channels.catalog.eci.dto.ECICatalogDTO;
import es.onebox.channels.catalog.eci.dto.ECICatalogMetadata;
import es.onebox.channels.catalog.eci.dto.ECICategoryThemeDTO;
import es.onebox.channels.catalog.eci.dto.ECIEventDTO;
import es.onebox.channels.catalog.eci.dto.ECIEventDetailDTO;
import es.onebox.channels.catalog.eci.dto.ECIOrganizerDTO;
import es.onebox.channels.catalog.eci.dto.ECISessionMonthDTO;
import es.onebox.channels.catalog.eci.dto.ECISessionYearDTO;
import es.onebox.channels.catalog.eci.dto.ECIThemeDTO;
import es.onebox.channels.catalog.eci.dto.ECITypeDTO;
import es.onebox.channels.catalog.eci.dto.ECIVenueDTO;
import es.onebox.common.datasources.catalog.dto.ChannelEventCategory;
import es.onebox.common.datasources.catalog.dto.ChannelEventDetail;
import es.onebox.common.datasources.catalog.dto.ChannelEventEntity;
import es.onebox.common.datasources.catalog.dto.ChannelEventTexts;
import es.onebox.common.datasources.catalog.dto.common.Dates;
import es.onebox.common.datasources.catalog.dto.common.Price;
import es.onebox.common.datasources.catalog.dto.common.Venue;
import es.onebox.common.datasources.catalog.dto.session.ChannelSession;
import es.onebox.common.datasources.catalog.dto.session.ChannelSessionTexts;
import es.onebox.common.datasources.common.dto.Metadata;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ECIChannelCatalogConverter {

    private static final String MONO_EVENT_TYPE = "Monoevento";

    private ECIChannelCatalogConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static ECICatalogDTO convert(CatalogEvents catalogEvents, ChannelCatalogContext context) {
        ECICatalogDTO eciCatalog = new ECICatalogDTO();
        eciCatalog.setMetadata(convertMetadata(catalogEvents.getMetadata()));
        eciCatalog.setRecords(ECIConverterUtils.map(catalogEvents.getEvents(), event -> convertEvent(event, context)));
        return eciCatalog;
    }

    private static ECICatalogMetadata convertMetadata(Metadata metadata) {
        if (metadata == null) {
            return null;
        }
        ECICatalogMetadata eciMetadata = new ECICatalogMetadata();
        eciMetadata.setLimit(metadata.getLimit());
        eciMetadata.setOffset(metadata.getOffset());
        eciMetadata.setTotal(metadata.getTotal());
        eciMetadata.setLinks(new ArrayList<>());
        return eciMetadata;
    }

    private static ECIEventDTO convertEvent(CatalogEvent catalogEvent, ChannelCatalogContext context) {
        var event = catalogEvent.getEvent();
        if (event == null) {
            return null;
        }
        String linkOutUrl = ECIConverterUtils.getEventLinkOutUrl(context, event.getId());
        List<String> languages = ECILanguageUtils.getLanguagesPriority(context, event);

        Map<String, String> descriptionsByLanguage = getDescriptions(event, languages);

        Map<String, String> names = ECILanguageUtils.getI18nTexts(event.getTexts(), ChannelEventTexts::getTitle, languages);

        Map<String, String> images = getImages(event, languages);

        ECIEventDTO eciEvent = new ECIEventDTO();
        eciEvent.setId(Objects.toString(event.getId(), null));
        eciEvent.setName(ECILanguageUtils.getText(event.getTexts(), ChannelEventTexts::getTitle, languages));
        eciEvent.setImage(images);
        eciEvent.setDescription(descriptionsByLanguage);
        eciEvent.setMultilingualName(names);
        eciEvent.setUrl(linkOutUrl);
        eciEvent.setType(convertEventType());
        eciEvent.setPromotions(ECIPromotionConverter.convert(event.getPromotions(), languages));
        var sessions = catalogEvent.getSessions();
        if (BooleanUtils.isTrue(event.getSupraEvent())) {
            eciEvent.setEvents(ECIConverterUtils.map(sessions, session -> convertEventDetail(event, session, names, descriptionsByLanguage, images, languages, context)));
        } else {
            eciEvent.setEvents(Collections.singletonList(convertEventDetail(event, sessions, linkOutUrl, names, descriptionsByLanguage, images, languages)));
        }
        var dates = event.getDate();
        if (dates != null) {
            eciEvent.setStartSaleDate(dates.getSaleStart());
            eciEvent.setEndSaleDate(dates.getSaleEnd());
        }
        return eciEvent;
    }

    private static Map<String, String> getDescriptions(ChannelEventDetail event, List<String> languages) {
        Map<String, String> shortDescriptions = ECILanguageUtils.getI18nTexts(event.getTexts(), ChannelEventTexts::getDescriptionShort, languages);
        Map<String, String> longDescriptions = ECILanguageUtils.getI18nTexts(event.getTexts(), ChannelEventTexts::getDescriptionLong, languages);
        Map<String, String> allDescriptions = new HashMap<>();

        for (String key : longDescriptions.keySet()) {
            if (!StringUtils.isBlank(longDescriptions.get(key))) {
                allDescriptions.put(key, longDescriptions.get(key));
            } else {
                allDescriptions.put(key, shortDescriptions.get(key));
            }
        }
        return allDescriptions;
    }

    private static Map<String, String> getImages(ChannelEventDetail event, List<String> languages) {
        return ECILanguageUtils.getI18nTexts(event.getImages(), imagesDTO -> {
            List<Map<String, String>> landscape = imagesDTO.getLandscape();
            return CollectionUtils.isNotEmpty(landscape) ? landscape.get(0) : null;
        }, languages, null);
    }

    private static ECITypeDTO convertEventType() {
        ECITypeDTO eciEventType = new ECITypeDTO();
        eciEventType.setId(0L);
        eciEventType.setName(MONO_EVENT_TYPE);
        return eciEventType;
    }

    private static ECIEventDetailDTO convertEventDetail(ChannelEventDetail event, List<ChannelSession> sessions,
                                                        String linkOutUrl,
                                                        Map<String, String> names, Map<String, String> descriptions,
                                                        Map<String, String> images, List<String> languages) {
        ECIEventDetailDTO eciEventDetail = prepareEventDetail(event, names, descriptions, images);
        eciEventDetail.setId(Objects.toString(event.getId(), null));
        eciEventDetail.setName(ECILanguageUtils.getText(event.getTexts(), ChannelEventTexts::getTitle, languages));
        eciEventDetail.setDuration(ECILanguageUtils.getText(event.getTexts(), ChannelEventTexts::getDuration, languages));

        if (sessions != null) {
            Map<Long, Venue> venues = new HashMap<>();
            Map<Long, List<ChannelSession>> sessionsByVenueId = new HashMap<>();
            sessions.forEach(session -> {
                var venue = session.getVenue();
                if (venue != null) {
                    Long venueId = venue.getId();
                    venues.put(venueId, venue);
                    sessionsByVenueId.computeIfAbsent(venueId, x -> new ArrayList<>()).add(session);
                }
            });
            eciEventDetail.setVenues(ECIConverterUtils.map(venues.values(), venue ->
                    convertVenue(venue, sessionsByVenueId.get(venue.getId()), linkOutUrl, languages), Comparator.comparing(ECIVenueDTO::getMinDate)));
            if (CollectionUtils.isNotEmpty(eciEventDetail.getVenues())) {
                eciEventDetail.setSchedule(eciEventDetail.getVenues().stream()
                        .map(ECIVenueDTO::getMinDate).filter(Objects::nonNull)
                        .min(Comparator.naturalOrder()).orElse(null));
            }
        }
        return eciEventDetail;
    }

    private static ECIEventDetailDTO convertEventDetail(ChannelEventDetail event, ChannelSession session,
                                                        Map<String, String> names, Map<String, String> descriptions, Map<String, String> images,
                                                        List<String> languages, ChannelCatalogContext context) {
        if (session == null) {
            return null;
        }
        Long sessionId = session.getId();
        String linkOutUrl = ECIConverterUtils.getSessionLinkOutUrl(context, sessionId, event.getId());
        ECIEventDetailDTO eciEventDetail = prepareEventDetail(event, names, descriptions, images);
        ECIVenueDTO eciVenue = convertVenue(session, linkOutUrl, languages);
        eciEventDetail.setId(Objects.toString(sessionId, null));
        eciEventDetail.setName(ECILanguageUtils.getText(session.getTexts(), ChannelSessionTexts::getTitle, languages, session.getName()));
        eciEventDetail.setDuration(StringUtils.EMPTY);
        eciEventDetail.setVenues(Collections.singletonList(eciVenue));
        if (eciVenue != null) {
            eciEventDetail.setSchedule(eciVenue.getMinDate());
        }
        return eciEventDetail;
    }

    private static ECIEventDetailDTO prepareEventDetail(ChannelEventDetail event, Map<String, String> names, Map<String, String> descriptions, Map<String, String> images) {
        ECIEventDetailDTO eciEventDetail = new ECIEventDetailDTO();
        eciEventDetail.setThemes(convertThemes(event));
        eciEventDetail.setImage(images);
        eciEventDetail.setDescription(descriptions);
        eciEventDetail.setMultilingualName(names);
        eciEventDetail.setDeliveryInfo(StringUtils.EMPTY);
        eciEventDetail.setAges(StringUtils.EMPTY);
        eciEventDetail.setLastMinuteRate(StringUtils.EMPTY);
        eciEventDetail.setDeliveryMethods(new ArrayList<>());
        eciEventDetail.setParticipants(new ArrayList<>());
        boolean usePromoterFiscalData = BooleanUtils.isTrue(event.getUsePromoterFiscalData());
        eciEventDetail.setOrganizer(convertOrganizer(usePromoterFiscalData ? event.getPromoter() : event.getEntity()));
        return eciEventDetail;
    }

    private static ECIOrganizerDTO convertOrganizer(ChannelEventEntity promoter) {
        if (promoter == null) {
            return null;
        }
        ECIOrganizerDTO eciOrganizer = new ECIOrganizerDTO();
        eciOrganizer.setName(Objects.toString(promoter.getFiscalName(), StringUtils.EMPTY));
        eciOrganizer.setFiscalIdentifier(Objects.toString(promoter.getFiscalIdentifier(), StringUtils.EMPTY));
        eciOrganizer.setAddress(convertAddress(promoter));
        return eciOrganizer;
    }

    private static ECIAddressDTO convertAddress(ChannelEventEntity address) {
        Optional<String> postalCode = Optional.ofNullable(address.getPostalCode());
        ECIAddressDTO eciAddress = new ECIAddressDTO();
        eciAddress.setAddress(Objects.toString(address.getAddress(), StringUtils.EMPTY));
        eciAddress.setPostalCode(postalCode.orElse(StringUtils.EMPTY));
        eciAddress.setCity(Objects.toString(address.getCity(), StringUtils.EMPTY));
        eciAddress.setCityINECode(StringUtils.EMPTY);
        var country = address.getCountry();
        if (country != null) {
            eciAddress.setCountry(Objects.toString(country.getName(), StringUtils.EMPTY));
            eciAddress.setCountryISOCode(Objects.toString(country.getCode(), StringUtils.EMPTY));
        }
        var countrySubdivision = address.getCountrySubdivision();
        if (countrySubdivision != null) {
            eciAddress.setProvince(Objects.toString(countrySubdivision.getName(), StringUtils.EMPTY));
            eciAddress.setProvinceINECode(postalCode.filter(x -> x.length() > 1).map(x -> x.substring(0, 2)).orElse(StringUtils.EMPTY));
        }
        return eciAddress;
    }

    private static List<ECIThemeDTO> convertThemes(ChannelEventDetail event) {
        List<ECIThemeDTO> themes = new ArrayList<>();
        addTheme(themes, event.getCategory());
        return themes;
    }

    private static void addTheme(List<ECIThemeDTO> themes, ChannelEventCategory category) {
        convertCategory(category).ifPresent(theme -> {
            themes.add(theme);
            addTheme(themes, category.getCustom());
        });
    }

    private static Optional<ECIThemeDTO> convertCategory(ChannelEventCategory category) {
        if (category == null) {
            return Optional.empty();
        }
        ECIThemeDTO theme = new ECIThemeDTO();
        theme.setId(Objects.toString(category.getId(), null));
        theme.setName(category.getDescription());
        if (category.getParent() != null) {
            ECICategoryThemeDTO categoryTheme = new ECICategoryThemeDTO();
            categoryTheme.setId(Objects.toString(category.getParent().getId()));
            categoryTheme.setName(category.getParent().getDescription());
            theme.setCategory(categoryTheme);
        }
        return Optional.of(theme);
    }

    private static ECIVenueDTO convertVenue(ChannelSession session, String linkOutUrl, List<String> languages) {
        var venue = session.getVenue();
        if (venue == null) {
            return null;
        }
        var price = getMinPrice(Collections.singletonList(session));

        ECIVenueDTO eciVenue = prepareVenue(venue, linkOutUrl, price, languages);

        var dates = session.getDate();
        if (dates != null) {
            eciVenue.setMinDate(dates.getStart());
            eciVenue.setMaxDate(ECIConverterUtils.getEndDay(dates.getStart(), venue.getLocation().getTimeZone()));
            eciVenue.setStartSaleDate(dates.getSaleStart());
            eciVenue.setEndSaleDate(dates.getSaleEnd());
        }

        eciVenue.setSessions(convertSessionDates(Collections.singletonList(session)));

        return eciVenue;
    }

    private static ECIVenueDTO convertVenue(Venue venue, List<ChannelSession> sessions, String linkOutUrl, List<String> languages) {
        if (venue == null) {
            return null;
        }
        var price = getMinPrice(sessions);

        ECIVenueDTO eciVenue = prepareVenue(venue, linkOutUrl, price, languages);

        var dates = sessions.stream().map(ChannelSession::getDate).collect(Collectors.toList());
        eciVenue.setMinDate(ECIConverterUtils.minDate(dates, Dates::getStart));
        ZonedDateTime maxDate = ECIConverterUtils.maxDate(dates, Dates::getStart);
        eciVenue.setMaxDate(ECIConverterUtils.getEndDay(maxDate, venue.getLocation().getTimeZone()));
        eciVenue.setStartSaleDate(ECIConverterUtils.minDate(dates, Dates::getSaleStart));
        eciVenue.setEndSaleDate(ECIConverterUtils.maxDate(dates, Dates::getSaleEnd));

        eciVenue.setSessions(convertSessionDates(sessions));

        return eciVenue;
    }

    private static ECIVenueDTO prepareVenue(Venue venue, String linkOutUrl, Price price, List<String> languages) {
        ECIVenueDTO eciVenue = new ECIVenueDTO();
        eciVenue.setId(Objects.toString(venue.getId(), null));
        eciVenue.setName(venue.getName());
        eciVenue.setUrl(linkOutUrl);
        eciVenue.setEmail(StringUtils.EMPTY);
        eciVenue.setPhone(StringUtils.EMPTY);
        eciVenue.setFax(StringUtils.EMPTY);
        eciVenue.setLocation(convertAddress(venue.getLocation()));
        eciVenue.setStartPrice(getPriceWithCharges(price));
        eciVenue.setStartPriceBase(getBasePrice(price));
        eciVenue.setDefaultStartPrice(getPriceWithCharges(price));
        eciVenue.setDefaultStartPriceBase(getBasePrice(price));
        if (StringUtils.isNotBlank(venue.getImage())) {
            eciVenue.setImage(buildVenueImages(venue.getImage(), languages));
        }
        return eciVenue;
    }

    private static BigDecimal getBasePrice(Price price) {
        return Optional.ofNullable(price)
                .map(p -> BigDecimal.valueOf(price.getValue()))
                .orElse(BigDecimal.ZERO);
    }

    private static BigDecimal getPriceWithCharges(Price price) {
        BigDecimal basePrice = getBasePrice(price);
        BigDecimal totalSurcharge = getTotalSurcharge(price);
        return basePrice.add(totalSurcharge);
    }

    private static BigDecimal getTotalSurcharge(Price price) {
        return Optional.ofNullable(price)
                .map(Price::getSurcharge)
                .map(surcharge -> {
                    BigDecimal channel = surcharge.getChannel() != null ? BigDecimal.valueOf(surcharge.getChannel()) : BigDecimal.ZERO;
                    BigDecimal promoter = surcharge.getPromoter() != null ? BigDecimal.valueOf(surcharge.getPromoter()) : BigDecimal.ZERO;
                    return channel.add(promoter);
                })
                .orElse(BigDecimal.ZERO);
    }

    private static Map<String, String> buildVenueImages(String image, List<String> languages) {
        return languages.stream().collect(Collectors.toMap(Function.identity(), elem -> image));
    }

    private static Price getMinPrice(List<ChannelSession> sessions) {
        return sessions.stream()
                .filter(Objects::nonNull)
                .map(ChannelSession::getPrice)
                .filter(Objects::nonNull)
                .map(prices -> prices.getMinPromoted() == null ? prices.getMin() : prices.getMinPromoted())
                .filter(Objects::nonNull)
                .min(Comparator.comparing(Price::getValue))
                .orElse(null);
    }

    private static List<ECISessionYearDTO> convertSessionDates(List<ChannelSession> sessions) {
        return sessions.stream()
                .map(ChannelSession::getDate)
                .map(Dates::getStart)
                .collect(Collector.of(ArrayList::new,
                        ECIChannelCatalogConverter::addSessionDate,
                        ECIChannelCatalogConverter::combineSessionDates));
    }

    private static void addSessionDate(List<ECISessionYearDTO> dates, ZonedDateTime date) {
        String year = String.valueOf(date.getYear());
        String month = StringUtils.leftPad(String.valueOf(date.getMonthValue()), 2, '0');
        String day = StringUtils.leftPad(String.valueOf(date.getDayOfMonth()), 2, '0');
        addSessionYear(dates, year, month, day);
    }

    private static void addSessionYear(List<ECISessionYearDTO> years, String year, String month, String day) {
        ECISessionYearDTO sessionYear = years.stream()
                .filter(item -> item.getYear().equals(year))
                .findFirst()
                .orElseGet(() -> {
                    ECISessionYearDTO sessionYearDTO = new ECISessionYearDTO(year);
                    years.add(sessionYearDTO);
                    Collections.sort(years);
                    return sessionYearDTO;
                });
        addSessionMonth(sessionYear.getMonths(), month, day);
    }

    private static void addSessionMonth(List<ECISessionMonthDTO> months, String month, String day) {
        ECISessionMonthDTO sessionMonth = months.stream()
                .filter(item -> item.getMonth().equals(month))
                .findFirst()
                .orElseGet(() -> {
                    ECISessionMonthDTO eciSessionMonthDTO = new ECISessionMonthDTO(month);
                    months.add(eciSessionMonthDTO);
                    Collections.sort(months);
                    return eciSessionMonthDTO;
                });
        addSessionDay(sessionMonth.getDays(), day);
    }

    private static void addSessionDay(List<String> days, String day) {
        if (!days.contains(day)) {
            days.add(day);
            Collections.sort(days);
        }
    }

    private static List<ECISessionYearDTO> combineSessionDates(List<ECISessionYearDTO> dates1, List<ECISessionYearDTO> dates2) {
        dates2.forEach(sessionYear -> sessionYear.getMonths()
                .forEach(sessionMonth -> sessionMonth.getDays()
                        .forEach(day -> addSessionYear(dates1, sessionYear.getYear(), sessionMonth.getMonth(), day))));
        return dates1;
    }

}
