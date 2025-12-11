package es.onebox.event.catalog.converter;

import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.event.catalog.dto.CatalogCommunicationElementDTO;
import es.onebox.event.catalog.dto.CatalogLocationDTO;
import es.onebox.event.catalog.dto.CatalogVenueDTO;
import es.onebox.event.catalog.dto.product.ProductCatalogCommunicationElementDTO;
import es.onebox.event.catalog.dto.product.ProductCatalogCommunicationElementsDTO;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDatesWithTimeZones;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDates;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelCatalogEventInfo;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.utils.CatalogUtils;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dto.EventWhitelabelSettingsDTO;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.events.service.EventConfigService;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPack;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPackCommElement;
import es.onebox.event.catalog.elasticsearch.context.PackIndexationContext;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPackDates;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPackItem;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPackItemDates;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPackItemVenueDTO;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPackItemVenueLocationDTO;
import es.onebox.event.catalog.elasticsearch.dto.channelpack.ChannelPackNextEventSessionDTO;
import es.onebox.event.packs.converter.PackConverter;
import es.onebox.event.packs.dao.domain.PackRecord;
import es.onebox.event.packs.dto.PriceTypeRange;
import es.onebox.event.packs.enums.PackPricingType;
import es.onebox.event.packs.enums.PackRangeType;
import es.onebox.event.packs.enums.PackSubtype;
import es.onebox.event.packs.enums.PackTagType;
import es.onebox.event.packs.enums.PackType;
import es.onebox.event.packs.record.PackDetailRecord;
import es.onebox.event.packs.utils.PackUtils;
import es.onebox.event.products.dao.couch.DeliveryPoint;
import es.onebox.event.products.dao.couch.ProductCatalogCommElement;
import es.onebox.event.products.dao.couch.ProductCatalogDocument;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComPackRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelPackZonaPrecioMappingRecord;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class ChannelCatalogPackConverter {

    private ChannelCatalogPackConverter() {
        //  Cannot be instantiated
    }

    public static ChannelPack buildChannelPack(PackIndexationContext ctx, Long channelId) {
        PackDetailRecord packRecord = ctx.getPackDetailRecord();

        ChannelPack channelPack = new ChannelPack();
        channelPack.setId(packRecord.getIdpack().longValue());
        channelPack.setName(packRecord.getNombre());
        channelPack.setType(PackType.from(packRecord));
        channelPack.setSubtype(PackSubtype.getById(packRecord.getTipo()));
        channelPack.setChannelId(channelId);
        channelPack.setSoldOut(BooleanUtils.isTrue(ctx.getPackSoldOutByChannelId().get(channelId)));
        channelPack.setForSale(BooleanUtils.isTrue(ctx.getPackForSaleByChannelId().get(channelId)));
        channelPack.setOnSale(BooleanUtils.isTrue(ctx.getPackOnSaleByChannelId().get(channelId)));
        Optional.ofNullable(packRecord.getIdpromocion()).map(Integer::longValue).ifPresent(channelPack::setPromotionId);
        channelPack.setPricingType(PackPricingType.getById(packRecord.getIdtipopricing()));
        channelPack.setPackRangeType(PackRangeType.getById(packRecord.getTiporangopack()));
        channelPack.setCustomStartSaleDate(CommonUtils.timestampToZonedDateTime(packRecord.getFechainiciopack()));
        channelPack.setCustomEndSaleDate(CommonUtils.timestampToZonedDateTime(packRecord.getFechafinpack()));

        channelPack.setShowDate(ConverterUtils.isByteAsATrue(packRecord.getShowdate()));
        channelPack.setShowDateTime(ConverterUtils.isByteAsATrue(packRecord.getShowdatetime()));
        channelPack.setShowMainDate(ConverterUtils.isByteAsATrue(packRecord.getShowmaindate()));
        channelPack.setShowMainVenue(ConverterUtils.isByteAsATrue(packRecord.getShowmainvenue()));

        channelPack.setStatus(ctx.getPackStatus());
        channelPack.setItems(buildItems(ctx, channelId));
        channelPack.setDates(buildPackDates(ctx, channelId));
        channelPack.setCommunicationElements(buildCommunicationElements(ctx));
        channelPack.setUnifiedPrice(ConverterUtils.isByteAsATrue(packRecord.getUnifiedprice()));
        channelPack.setShowUnconfirmedDate(buildShowUnconfirmedDate(ctx, channelId));
        channelPack.setCustomCategoryCode(packRecord.getCustomCategoryCode());
        channelPack.setSuggested(buildSuggested(ctx, channelId));
        channelPack.setOnSaleForLoggedUsers(buildOnSaleForLoggedUsers(ctx, channelId));
        channelPack.setPrices(ctx.getPriceMatrixByChannelId().get(channelId));

        return channelPack;
    }

    public static ChannelPackDates buildPackDates(PackIndexationContext ctx, Long channelId) {
        List<ChannelSession> channelSessions = ctx.getChannelSessionListByChannelId().get(channelId);
        ChannelEvent channelEvent = ctx.getChannelEventsByChannelId().get(channelId);
        CpanelPackRecord packRecord = ctx.getPackDetailRecord();
        ChannelPackDates dates = new ChannelPackDates();
        boolean isCustomRangeType = PackRangeType.CUSTOM.equals(PackRangeType.getById(packRecord.getTiporangopack()));

        if (channelEvent != null) {
            ChannelCatalogDatesWithTimeZones eventDates = channelEvent.getCatalogInfo().getDate();
            dates.setStart(CatalogUtils.toZonedDateTime(eventDates.getStart()));
            dates.setEnd(CatalogUtils.toZonedDateTime(eventDates.getEnd()));
            if (!isCustomRangeType) {
                dates.setSaleStart(CatalogUtils.toZonedDateTime(eventDates.getSaleStart()));
                dates.setSaleEnd(CatalogUtils.toZonedDateTime(eventDates.getSaleEnd()));
            }
        }

        if (CollectionUtils.isNotEmpty(channelSessions)) {
            for (ChannelSession channelSession : channelSessions) {
                ChannelCatalogDates sessionDates = channelSession.getDate();
                if (sessionDates == null) {
                    continue;
                }

                ZonedDateTime sessionStart = CatalogUtils.toZonedDateTime(sessionDates.getStart());
                if (dates.getStart() == null || sessionStart.isBefore(dates.getStart())) {
                    dates.setStart(sessionStart);

                    Session session = ctx.getSessionsById().get(channelSession.getSessionId());
                    dates.setStartUnconfirmed(session.getNoFinalDate());
                }

                ZonedDateTime sessionEnd = CatalogUtils.toZonedDateTime(sessionDates.getEnd());
                if (dates.getEnd() == null || sessionEnd.isAfter(dates.getEnd())) {
                    dates.setEnd(sessionEnd);
                }

                if (!isCustomRangeType) {
                    ZonedDateTime sessionSaleStart = CatalogUtils.toZonedDateTime(sessionDates.getSaleStart());
                    if (dates.getSaleStart() == null || sessionSaleStart.isBefore(dates.getSaleStart())) {
                        dates.setSaleStart(sessionSaleStart);
                    }

                    ZonedDateTime sessionSaleEnd = CatalogUtils.toZonedDateTime(sessionDates.getSaleEnd());
                    if (dates.getSaleEnd() == null || sessionSaleEnd.isAfter(dates.getSaleEnd())) {
                        dates.setSaleEnd(sessionSaleEnd);
                    }
                }
            }
        }

        ZonedDateTime now = ZonedDateTime.now();
        if (isCustomRangeType) {
            dates.setSaleStart(CommonUtils.timestampToZonedDateTime(packRecord.getFechainiciopack()));
            dates.setSaleEnd(CommonUtils.timestampToZonedDateTime(packRecord.getFechafinpack()));
            if (now.isBefore(dates.getSaleStart()) || now.isAfter(dates.getSaleEnd())) {
                ctx.getPackOnSaleByChannelId().put(channelId, Boolean.FALSE);
            }
        } else if (dates.getSaleEnd() == null || dates.getSaleEnd().isAfter(dates.getStart())) {
            dates.setSaleEnd(dates.getStart());
        }

        return dates;
    }

    private static List<ChannelPackItem> buildItems(PackIndexationContext ctx, Long channelId) {
        List<ChannelPackItem> channelPackItems = new ArrayList<>();

        for (CpanelPackItemRecord packItemRecord : ctx.getPackItemRecords()) {
            ChannelPackItem channelPackItem = buildItem(ctx, channelId, packItemRecord);
            channelPackItems.add(channelPackItem);
        }

        return channelPackItems;
    }

    @NotNull
    private static ChannelPackItem buildItem(PackIndexationContext ctx, Long channelId, CpanelPackItemRecord packItemRecord) {
        ChannelPackItem channelPackItem = new ChannelPackItem();
        Integer packItemId = packItemRecord.getIdpackitem();
        channelPackItem.setItemId(packItemRecord.getIditem().longValue());
        channelPackItem.setMain(BooleanUtils.isTrue(packItemRecord.getPrincipal()));
        channelPackItem.setType(PackUtils.getType(packItemRecord));
        channelPackItem.setDisplayItemInChannels(BooleanUtils.toBoolean(packItemRecord.getMostraritemenchannels()));
        channelPackItem.setInformativePrice(packItemRecord.getPrecioinformativo());
        PriceTypeRange priceTypeRange = PriceTypeRange.getByType(packItemRecord.getZonapreciotiposeleccion());
        channelPackItem.setPriceTypeRange(priceTypeRange);
        channelPackItem.setPriceTypes(buildPriceTypes(priceTypeRange, ctx.getPriceTypesByPackItemId().get(packItemId)));

        switch (channelPackItem.getType()) {
            case EVENT -> {
                Event event = ctx.getMainEvent();
                ChannelEvent channelEvent = ctx.getChannelEventsByChannelId().get(channelId);
                channelPackItem.setName(event.getEventName());
                channelPackItem.setDefaultLanguage(event.getEventDefaultLanguage());
                channelPackItem.setEventWhitelabelSettings(buildEventWhitelabelSettings(event, channelEvent.getChannelSubtype()));

                channelPackItem.setShowDate(channelEvent.getSessionsShowDate());
                channelPackItem.setShowDateTime(channelEvent.getSessionsShowDateTime());
                channelPackItem.setDates(buildDates(channelEvent));
                channelPackItem.setCommunicationElements(buildCommunicationElements(event, channelEvent, event.getOperatorId(), ctx.getS3Repository()));
                channelPackItem.setVenue(buildVenue(channelEvent, event, ctx.getS3Repository(), ctx.getMainEventVenueId()));

                ChannelSession mainEventFirstChannelSession = ctx.getChannelSessionOfMainEventByChannelId().get(channelId);
                channelPackItem.setNextSession(buildNextSession(mainEventFirstChannelSession));

                channelPackItem.setVenueTemplateId(packItemRecord.getIdconfiguracion());
                channelPackItem.setSessionsFilter(ctx.getSessionsFilter());
            }
            case SESSION -> {
                long sessionId = channelPackItem.getItemId();

                Session session = ctx.getSessionsById().get(sessionId);
                channelPackItem.setName(session.getSessionName());
                channelPackItem.setShowDate(session.getShowDate());
                channelPackItem.setShowDateTime(session.getShowDateTime());
                channelPackItem.setUseCaptcha(session.getUseCaptcha());
                channelPackItem.setShowUnconfirmedDate(session.getShowUnconfirmedDate());

                ChannelSession channelSession = ctx.getChannelSessionsBySessionIdByChannelId().get(channelId).get(sessionId);
                channelPackItem.setDates(buildDates(channelSession, session));

                Event event = ctx.getEventsBySessionId().get(sessionId);
                channelPackItem.setDefaultLanguage(event.getEventDefaultLanguage());
                channelPackItem.setCommunicationElements(buildCommunicationElements(event, session, channelSession, ctx.getS3Repository()));
                channelPackItem.setVenue(buildVenue(session, event, ctx.getS3Repository()));

                if (CommonUtils.isTrue(packItemRecord.getZonapreciomapping())) {
                    List<CpanelPackZonaPrecioMappingRecord> mappings = ctx.getPriceTypesMappingByPackItemId().get(packItemId);
                    channelPackItem.setPriceTypeMapping(PackConverter.getPriceTypeMapping(mappings));
                } else {
                    channelPackItem.setPriceTypeId(packItemRecord.getIdzonaprecio());
                }
            }
            case PRODUCT -> {
                long productId = channelPackItem.getItemId();

                ProductCatalogDocument catalogProduct = ctx.getCatalogProductsById().get(productId);
                channelPackItem.setName(catalogProduct.getName());
                channelPackItem.setCommunicationElements(buildCommunicationElements(catalogProduct.getCommElements(), ctx.getS3Repository()));
                channelPackItem.setDefaultLanguage(catalogProduct.getDefaultLanguage());
                channelPackItem.setDeliveryPointName(buildDeliveryPointName(catalogProduct, packItemRecord.getIdpuntoentrega()));
                channelPackItem.setDates(buildPackItemDates(catalogProduct));
                channelPackItem.setHideDeliveryPoint(catalogProduct.getHideDeliveryPoint());
                channelPackItem.setHideDeliveryDateTime(catalogProduct.getHideDeliveryDateTime());

                channelPackItem.setVariantId(packItemRecord.getIdvariante());
                channelPackItem.setDeliveryPointId(packItemRecord.getIdpuntoentrega());
                channelPackItem.setSharedBarcode(packItemRecord.getCodigodebarrascompartido());
            }
        }
        return channelPackItem;
    }

    private static Set<Integer> buildPriceTypes(PriceTypeRange priceTypeRange, Set<Integer> integers) {
        return PriceTypeRange.RESTRICTED.equals(priceTypeRange) ? integers : null;
    }

    private static ChannelPackItemDates buildPackItemDates(ProductCatalogDocument catalogProduct) {
        ChannelPackItemDates dates = new ChannelPackItemDates();
        dates.setStartTimeUnit(catalogProduct.getStartTimeUnit());
        dates.setStartTimeValue(catalogProduct.getStartTimeValue());
        dates.setEndTimeUnit(catalogProduct.getEndTimeUnit());
        dates.setEndTimeValue(catalogProduct.getEndTimeValue());
        return dates;
    }

    private static String buildDeliveryPointName(ProductCatalogDocument catalogProduct, Integer deliveryPointId) {
        if (catalogProduct == null || CollectionUtils.isEmpty(catalogProduct.getDeliveryPoints()) || deliveryPointId == null) {
            return null;
        }
        return catalogProduct.getDeliveryPoints().stream()
                .filter(deliveryPoint -> deliveryPoint.getId().equals(deliveryPointId.longValue()))
                .map(DeliveryPoint::getName)
                .findFirst()
                .orElse(null);
    }

    private static List<CatalogCommunicationElementDTO> buildCommunicationElements(ProductCatalogCommElement productCommunicationElements, String s3Repository) {
        ProductCatalogCommunicationElementsDTO communicationElements = CatalogProductConverter.convertCommunicationElements(productCommunicationElements, s3Repository);

        return Stream.of(communicationElements.getTexts(), communicationElements.getImages())
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .map(ChannelCatalogPackConverter::buildCommunicationElement)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toList();
    }

    private static List<CatalogCommunicationElementDTO> buildCommunicationElement(ProductCatalogCommunicationElementDTO productCommunicationElements) {
        if (productCommunicationElements.getValue() == null) {
            return null;
        }

        return productCommunicationElements.getValue().entrySet().stream()
                .map(entry -> {
                    CatalogCommunicationElementDTO dto = new CatalogCommunicationElementDTO();
                    dto.setTag(productCommunicationElements.getType());
                    dto.setLanguage(entry.getKey());
                    dto.setValue(entry.getValue());
                    dto.setPosition(productCommunicationElements.getPosition());
                    return dto;
                })
                .toList();
    }


    private static EventWhitelabelSettingsDTO buildEventWhitelabelSettings(Event event, ChannelSubtype channelSubtype) {
        return EventConfigService.extractEventWhitelabelSettings(event.getSupraEvent(), event.getWhitelabelSettings(), channelSubtype);
    }

    private static List<CatalogCommunicationElementDTO> buildCommunicationElements(Event event, ChannelEvent channelEvent, Integer operatorId, String s3Repository) {
        List<CatalogCommunicationElementDTO> eventCommunicationElements =
                CatalogCommunicationElementConverter.convert(event, s3Repository);
        List<CatalogCommunicationElementDTO> channelEventCommunicationElements =
                CatalogCommunicationElementConverter.convert(channelEvent.getCommunicationElements(), operatorId, s3Repository);
        return Stream.concat(eventCommunicationElements.stream(), channelEventCommunicationElements.stream()).toList();
    }

    private static List<CatalogCommunicationElementDTO> buildCommunicationElements(Event event, Session session, ChannelSession channelSession, String s3Repository) {
        return ChannelCatalogSessionConverter.buildEventOrSessionCommElement(event, session, channelSession, s3Repository);
    }

    private static ChannelPackItemDates buildDates(ChannelEvent channelEvent) {
        ChannelCatalogEventInfo catalogEventInfo = channelEvent.getCatalogInfo();
        if (catalogEventInfo == null || catalogEventInfo.getDate() == null) {
            return null;
        }
        return buildDates(catalogEventInfo.getDate());
    }

    private static ChannelPackItemDates buildDates(ChannelSession channelSession, Session session) {
        if (channelSession.getDate() == null) {
            return null;
        }
        ChannelPackItemDates dates = buildDates(channelSession.getDate());
        dates.setStartUnconfirmed(session.getNoFinalDate());
        return dates;
    }

    private static ChannelPackItemDates buildDates(ChannelCatalogDates channelCatalogDates) {
        ChannelPackItemDates channelPackItemDates = new ChannelPackItemDates();
        channelPackItemDates.setStart(CatalogUtils.toZonedDateTime(channelCatalogDates.getStart()));
        channelPackItemDates.setEnd(CatalogUtils.toZonedDateTime(channelCatalogDates.getEnd()));
        channelPackItemDates.setSaleStart(CatalogUtils.toZonedDateTime(channelCatalogDates.getSaleStart()));
        channelPackItemDates.setSaleEnd(CatalogUtils.toZonedDateTime(channelCatalogDates.getSaleEnd()));
        return channelPackItemDates;
    }

    private static ChannelPackItemVenueDTO buildVenue(ChannelEvent channelEvent, Event event, String s3Repository, Integer venueId) {
        if (CollectionUtils.isEmpty(channelEvent.getVenueIds()) || CollectionUtils.isEmpty(event.getVenues())) {
            return null;
        }

        List<CatalogVenueDTO> venues = CatalogVenueConverter.convert(channelEvent.getVenueIds(), event.getVenues(), event.getOperatorId(), s3Repository);
        if (CollectionUtils.isEmpty(venues)) {
            return null;
        }

        return venues.stream()
                .filter(venue -> venue.getId().equals(venueId.longValue()))
                .map(venue -> buildVenue(venue, null))
                .findFirst()
                .orElse(null);
    }

    private static ChannelPackItemVenueDTO buildVenue(Session session, Event event, String s3Repository) {
        if (session.getVenueId() == null || CollectionUtils.isEmpty(event.getVenues())) {
            return null;
        }
        CatalogVenueDTO venue = CatalogVenueConverter.convert(session.getVenueId(), event.getVenues(), event.getOperatorId(), s3Repository);
        return buildVenue(venue, session.getGraphic());
    }

    private static ChannelPackItemVenueDTO buildVenue(CatalogVenueDTO venue, Boolean isGraphic) {
        if (venue == null) {
            return null;
        }

        ChannelPackItemVenueDTO packItemVenue = new ChannelPackItemVenueDTO();
        packItemVenue.setId(venue.getId());
        packItemVenue.setName(venue.getName());
        packItemVenue.setImage(venue.getImage());
        packItemVenue.setGraphic(isGraphic);
        packItemVenue.setTimeZone(venue.getTimeZone());

        ChannelPackItemVenueLocationDTO location = new ChannelPackItemVenueLocationDTO();
        location.setCity(venue.getCity());
        location.setAddress(venue.getAddress());
        location.setPostalCode(venue.getPostalCode());
        location.setTimeZone(venue.getTimeZone());
        location.setCountry(buildCodeName(venue.getCountry()));
        location.setCountrySubdivision(buildCodeName(venue.getCountrySubdivision()));
        packItemVenue.setLocation(location);

        return packItemVenue;
    }

    private static CodeNameDTO buildCodeName(CatalogLocationDTO location) {
        if (location == null || (location.getCode() == null && location.getName() == null)) {
            return null;
        }

        return new CodeNameDTO(location.getCode(), location.getName());
    }

    private static ChannelPackNextEventSessionDTO buildNextSession(ChannelSession firstSession) {
        if (firstSession == null) {
            return null;
        }

        ChannelPackNextEventSessionDTO nextEventSessionDTO = new ChannelPackNextEventSessionDTO();
        nextEventSessionDTO.setId(firstSession.getSessionId());
        if (firstSession.getDate() != null) {
            nextEventSessionDTO.setStartDate(CatalogUtils.toZonedDateTime(firstSession.getDate().getStart()));
        }
        return nextEventSessionDTO;
    }

    private static List<ChannelPackCommElement> buildCommunicationElements(PackIndexationContext ctx) {
        List<CpanelElementosComPackRecord> elementosComPackRecords = ctx.getCommunicationElements();
        if (CollectionUtils.isEmpty(elementosComPackRecords)) {
            return null;
        }

        PackRecord packRecord = ctx.getPackDetailRecord();
        S3URLResolver builder = S3URLResolver.builder()
                .withUrl(ctx.getS3Repository())
                .withType(S3URLResolver.S3ImageType.PACK_IMAGE)
                .withOperatorId(packRecord.getEntity().getIdoperadora())
                .withEntityId(packRecord.getIdentidad())
                .withPackId(packRecord.getIdpack())
                .withEntityId(packRecord.getIdentidad())
                .build();


        List<ChannelPackCommElement> communicationElements = new ArrayList<>();
        List<Integer> invalidCommElementsToRemove = new ArrayList<>();
        elementosComPackRecords.forEach(element -> {
            ChannelPackCommElement communicationElement = new ChannelPackCommElement();
            communicationElement.setId(element.getIdelemento().longValue());
            communicationElement.setPosition(element.getPosition());
            communicationElement.setValue(element.getValor());
            communicationElement.setTagId(element.getIdtag());
            communicationElement.setTag(ctx.getGetTag().apply(element.getIdtag()));
            communicationElement.setLanguageCode(ctx.getGetLanguageCode().apply(element.getIdioma()));
            PackTagType tagTypeById = PackTagType.getTagTypeById(communicationElement.getTagId());
            if (tagTypeById != null && tagTypeById.isImage()) {
                if (element.getValor() != null) {
                    communicationElement.setUrl(builder.buildPath(element.getValor()));
                    communicationElements.add(communicationElement);
                } else {
                    invalidCommElementsToRemove.add(element.getIdelemento());
                }
            } else {
                communicationElements.add(communicationElement);
            }
        });

        if (CollectionUtils.isNotEmpty(invalidCommElementsToRemove)) {
            ctx.getDeletePackComElementByIdsMethod().accept(invalidCommElementsToRemove);
        }

        return communicationElements;
    }

    private static Boolean buildShowUnconfirmedDate(PackIndexationContext ctx, Long channelId) {
        List<ChannelSession> channelSessions = ctx.getChannelSessionListByChannelId().get(channelId);
        if (CollectionUtils.isEmpty(channelSessions)) {
            return null;
        }

        ZonedDateTime packStart = null;
        ChannelEvent channelEvent = ctx.getChannelEventsByChannelId().get(channelId);
        if (channelEvent != null) {
            packStart = CatalogUtils.toZonedDateTime(channelEvent.getCatalogInfo().getDate().getStart());
        }

        Boolean showUnconfirmedDate = null;
        for (ChannelSession channelSession : channelSessions) {
            ZonedDateTime sessionStart = CatalogUtils.toZonedDateTime(channelSession.getDate().getStart());
            if (packStart == null || sessionStart.isBefore(packStart)) {
                packStart = sessionStart;

                Session session = ctx.getSessionsById().get(channelSession.getSessionId());
                showUnconfirmedDate = session.getShowUnconfirmedDate();
            }
        }
        return showUnconfirmedDate;
    }

    private static Boolean buildSuggested(PackIndexationContext ctx, Long channelId) {
        CpanelPackCanalRecord packChannelRecord = ctx.getPackChannelRecordsByChannelId().get(channelId);
        return ConverterUtils.isByteAsATrue(packChannelRecord.getSugerirpack());
    }

    private static Boolean buildOnSaleForLoggedUsers(PackIndexationContext ctx, Long channelId) {
        CpanelPackCanalRecord packChannelRecord = ctx.getPackChannelRecordsByChannelId().get(channelId);
        return ConverterUtils.isByteAsATrue(packChannelRecord.getOnsaleforloggedusers());
    }

}
