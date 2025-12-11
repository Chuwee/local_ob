package es.onebox.event.catalog.converter;

import es.onebox.event.catalog.dto.CatalogCommunicationElementDTO;
import es.onebox.event.catalog.dto.CatalogPriceZoneOccupationDTO;
import es.onebox.event.catalog.dto.CatalogTaxInfoDTO;
import es.onebox.event.catalog.dto.ChannelCatalogEventSessionDTO;
import es.onebox.event.catalog.dto.ChannelCatalogRateDTO;
import es.onebox.event.catalog.dto.ChannelCatalogSessionDTO;
import es.onebox.event.catalog.dto.ChannelCatalogSessionDetailDTO;
import es.onebox.event.catalog.dto.ChannelTaxesDTO;
import es.onebox.event.catalog.dto.SessionPackSettingsDTO;
import es.onebox.event.catalog.dto.SessionRelatedDTO;
import es.onebox.event.catalog.dto.SessionRelatedDateDTO;
import es.onebox.event.catalog.dto.VenueTemplateType;
import es.onebox.event.catalog.dto.presales.ChannelSessionPresaleDTO;
import es.onebox.event.catalog.dto.presales.PresaleConstraintsDTO;
import es.onebox.event.catalog.dto.presales.PresaleValidatorType;
import es.onebox.event.catalog.dto.presales.PresaleValidityPeriodDTO;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDates;
import es.onebox.event.catalog.elasticsearch.dto.CustomersLimits;
import es.onebox.event.catalog.elasticsearch.dto.PriceZoneLimit;
import es.onebox.event.catalog.elasticsearch.dto.PriceZonePrice;
import es.onebox.event.catalog.elasticsearch.dto.RateRestrictions;
import es.onebox.event.catalog.elasticsearch.dto.VenueTemplatePrice;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventAgencyData;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventData;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgencyData;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgencyWithAll;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionAgencyWithParent;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionData;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionPriceTypeLimit;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionWithAll;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSessionWithParent;
import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelTaxes;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.catalog.elasticsearch.dto.session.PresalesRedirectionPolicy;
import es.onebox.event.catalog.elasticsearch.dto.session.PresalesSettings;
import es.onebox.event.catalog.elasticsearch.dto.session.Session;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionCommunicationElement;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionData;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionPackSettings;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionRate;
import es.onebox.event.catalog.elasticsearch.dto.session.SessionRelated;
import es.onebox.event.catalog.elasticsearch.dto.session.external.ExternalData;
import es.onebox.event.catalog.elasticsearch.dto.session.presaleconfig.PresaleConfig;
import es.onebox.event.catalog.elasticsearch.dto.session.presaleconfig.PresaleValidityPeriod;
import es.onebox.event.catalog.elasticsearch.enums.PresalesRedirectionLinkMode;
import es.onebox.event.catalog.elasticsearch.pricematrix.PriceMatrix;
import es.onebox.event.catalog.elasticsearch.pricematrix.PriceZonePrices;
import es.onebox.event.catalog.utils.CatalogUtils;
import es.onebox.event.catalog.utils.ChannelsUtils;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.datasources.ms.channel.dto.ChannelConfigDTO;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionPriceZoneOccupationDTO;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.enums.Provider;
import es.onebox.event.loyaltypoints.sessions.converter.SessionLoyaltyPointsConverter;
import es.onebox.event.packs.dto.PackItemDTO;
import es.onebox.event.packs.dto.RelatedPackDetailDTO;
import es.onebox.event.priceengine.taxes.domain.TaxInfo;
import es.onebox.event.promotions.dao.EventPromotionCouchDao;
import es.onebox.event.secondarymarket.dto.EventSecondaryMarketConfigDTO;
import es.onebox.event.sessions.dao.enums.PresaleStatus;
import es.onebox.event.sessions.dto.external.ExternalDataDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public final class ChannelCatalogSessionConverter {

    public static final String TAG_IMG_BANNER_WEB = "IMG_BANNER_WEB";

    private ChannelCatalogSessionConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static ChannelSessionWithAll convert(ChannelSessionData channelSessionData, EventData eventData, SessionData sessionData, ChannelEventData channelEvent) {
        ChannelSessionWithAll channelSessionWithAll = new ChannelSessionWithAll();
        channelSessionWithAll.setChannelSession(channelSessionData.getChannelSession());
        channelSessionWithAll.setSessionData(sessionData);
        channelSessionWithAll.setEventData(eventData);
        channelSessionWithAll.setChannelEventData(channelEvent);
        return channelSessionWithAll;
    }

    public static ChannelSessionAgencyWithAll convert(ChannelSessionAgencyData channelSessionData, EventData eventData, SessionData sessionData, ChannelEventAgencyData channelEvent) {
        ChannelSessionAgencyWithAll channelSessionWithAll = new ChannelSessionAgencyWithAll();
        channelSessionWithAll.setChannelSessionAgency(channelSessionData.getChannelSessionAgency());
        channelSessionWithAll.setSessionData(sessionData);
        channelSessionWithAll.setEventData(eventData);
        channelSessionWithAll.setChannelEventAgencyData(channelEvent);
        return channelSessionWithAll;
    }

    public static List<ChannelCatalogEventSessionDTO> convert(final List<ChannelSessionWithParent> list, final EventData eventData, final String s3Repository) {
        if (list == null || eventData == null || eventData.getEvent() == null) {
            return Collections.emptyList();
        }
        Event event = eventData.getEvent();
        return list.stream().map(s -> ChannelCatalogSessionConverter.convert(new ChannelCatalogEventSessionDTO(), s, event, s3Repository)).collect(Collectors.toList());
    }

    public static List<ChannelCatalogEventSessionDTO> fromAgency(final List<ChannelSessionAgencyWithParent> list, final EventData eventData, final String s3Repository) {
        if (list == null || eventData == null || eventData.getEvent() == null) {
            return Collections.emptyList();
        }
        Event event = eventData.getEvent();
        return list.stream().map(s -> ChannelCatalogSessionConverter.convert(new ChannelCatalogEventSessionDTO(), s, event, s3Repository)).collect(Collectors.toList());
    }

    public static List<ChannelCatalogSessionDTO> convert(List<ChannelSessionWithAll> list, String s3Repository, Map<Integer, Boolean> secondaryMarketByEntity) {
        return list.stream()
                .map(s -> {
                    final Session session = s.getSessionData().getSession();
                    final Event event = s.getEventData().getEvent();
                    final ChannelEvent channelEvent = s.getChannelEventData().getChannelEvent();
                    ChannelCatalogSessionDTO dto = ChannelCatalogSessionConverter.convert(new ChannelCatalogSessionDTO(), s, event, s3Repository);
                    if (dto == null) {
                      return null;
                    }
                    dto.setEventId(event.getEventId());
                    dto.setEventName(event.getEventName());
                    dto.setRates(toCatalogRates(session.getRates()));
                    List<CatalogCommunicationElementDTO> eventCommElements = CatalogCommunicationElementConverter.convert(event.getCommunicationElements(),
                            S3URLResolver.builder()
                                    .withUrl(s3Repository)
                                    .withEntityId(event.getEntityId())
                                    .withOperatorId(event.getOperatorId())
                                    .withEventId(event.getEventId())
                                    .withType(S3URLResolver.S3ImageType.EVENT_IMAGE)
                                    .build());
                    dto.setVenueConfigId(session.getVenueConfigId());
                    dto.setEventDefaultLanguage(getEventDefaultLanguage(s.getEventData(), s.getSessionData()));
                    dto.setEventCommunicationElements(eventCommElements);
                    dto.setTicketHandling(channelEvent.getTicketHandling());
                    dto.setSecondaryMarketConfig(s.getChannelSession().getSecondaryMarketConfig());
                    dto.setVenueTemplateType(VenueTemplateType.byId(session.getVenueTemplateType()));
                    if (dto.getSecondaryMarketConfig() != null && Boolean.FALSE.equals(secondaryMarketByEntity.get(event.getEntityId()))) {
                        dto.getSecondaryMarketConfig().setEnabled(false);
                    }
                    if (EventType.AVET.equals(EventType.byId(event.getEventType()))) {
                        dto.setExternalData(convertExternalData(session.getExternalData()));
                    }
                    return dto;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<ChannelCatalogSessionDTO> fromAgency(List<ChannelSessionAgencyWithAll> list, String s3Repository, Map<Integer, Boolean> secondaryMarketByEntity) {
        return list.stream()
                .map(s -> {
                    final Session session = s.getSessionData().getSession();
                    final Event event = s.getEventData().getEvent();
                    final ChannelEventAgency channelEvent = s.getChannelEventAgencyData().getChannelEventAgency();
                    ChannelCatalogSessionDTO dto = ChannelCatalogSessionConverter.convert(new ChannelCatalogSessionDTO(), s, event, s3Repository);
                    dto.setEventId(event.getEventId());
                    dto.setEventName(event.getEventName());
                    dto.setRates(toCatalogRates(session.getRates()));
                    List<CatalogCommunicationElementDTO> eventCommElements = CatalogCommunicationElementConverter.convert(event.getCommunicationElements(),
                            S3URLResolver.builder()
                                    .withUrl(s3Repository)
                                    .withEntityId(event.getEntityId())
                                    .withOperatorId(event.getOperatorId())
                                    .withEventId(event.getEventId())
                                    .withType(S3URLResolver.S3ImageType.EVENT_IMAGE)
                                    .build());
                    dto.setEventCommunicationElements(eventCommElements);
                    dto.setEventDefaultLanguage(getEventDefaultLanguage(s.getEventData(), s.getSessionData()));
                    dto.setTicketHandling(channelEvent.getTicketHandling());
                    dto.setSecondaryMarketConfig(s.getChannelSessionAgency().getSecondaryMarketConfig());
                    if (dto.getSecondaryMarketConfig() != null && Boolean.FALSE.equals(secondaryMarketByEntity.get(event.getEntityId()))) {
                        dto.getSecondaryMarketConfig().setEnabled(false);
                    }
                    if (EventType.AVET.equals(EventType.byId(event.getEventType()))) {
                        dto.setExternalData(convertExternalData(session.getExternalData()));
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public static<T extends ChannelEvent> ChannelCatalogSessionDetailDTO convert(final ChannelSessionWithParent hit,
                                                         final EventData eventData,
                                                         final T channelEvent,
                                                         final String s3Repository, EventPromotionCouchDao eventPromotionCouchDao,
                                                         final EventSecondaryMarketConfigDTO eventSecondaryMarketConfig,
                                                         final ChannelConfigDTO channelConfig, Map<Long, Provider> presaleProviders) {
        if (hit == null || hit.getChannelSession() == null || hit.getSessionData() == null || hit.getSessionData().getSession() == null) {
            return null;
        }
        ChannelCatalogSessionDetailDTO outDto = new ChannelCatalogSessionDetailDTO();
        Session session = hit.getSessionData().getSession();
        ChannelSession channelSession = hit.getChannelSession();
        Event event = eventData.getEvent();
        fill(outDto, session, channelSession, event, s3Repository);
        fill(outDto, event, session, channelSession, s3Repository);
        List<CatalogPriceZoneOccupationDTO> occupation = convert(channelSession.getPriceZoneOccupations(), event.getPrices(), channelSession.getPrices().getPrices());
        outDto.setPriceZoneOccupations(occupation);
        List<PriceZonePrices> channelSessionPrices = Optional.ofNullable(channelSession.getPrices()).map(PriceMatrix::getPrices).orElse(Collections.emptyList());
        VenueTemplatePrice venueTemplatePrice = event.getPrices().stream()
                .filter(venueTemplatePrice1 -> venueTemplatePrice1.getId().equals(session.getVenueConfigId().intValue()))
                .findFirst().orElse(null);
        outDto.setPromotions(CatalogPromotionConverter.convert(event.getPromotions(), channelSession.getPromotions(), venueTemplatePrice, channelSessionPrices, eventPromotionCouchDao));
        outDto.setPrices(CatalogPricesConverter.convert(channelSession.getPrices()));
        outDto.setRates(session.getRates().stream().map(ChannelCatalogSessionConverter::fill).collect(Collectors.toList()));
        outDto.setVenueConfigId(session.getVenueConfigId());
        outDto.setChannelEntityId(channelEvent.getChannelEntityId());
        outDto.setSeasonPackSession(session.getSeasonPackSession());
        outDto.setEventId(event.getEventId());
        outDto.setCurrencyId(event.getCurrency());
        outDto.setEventName(event.getEventName());
        outDto.setEventEntityId(event.getEntityId().longValue());
        outDto.setUseCaptcha(session.getUseCaptcha());
        outDto.setShowDate(session.getShowDate());
        outDto.setShowDateTime(session.getShowDateTime());
        outDto.setShowUnconfirmedDate(session.getShowUnconfirmedDate());
        outDto.setCheckOrphanSeats(session.getCheckOrphanSeats());
        outDto.setGraphic(session.getGraphic());
        outDto.setSessionMaxTickets(session.getSessionMaxTickets());
        outDto.setOrderMaxTickets(session.getOrderMaxTickets());
        outDto.setIpRestrictedCountries(session.getIpRestrictedCountries());
        outDto.setPriceZonesRestrictions(session.getPriceZonesRestrictions());
        if (!ChannelsUtils.isB2BChannel(channelConfig)) {
            outDto.setRatesRestrictions(session.getRatesRestrictions());
        } else {
            if (session.getRatesRestrictions() != null) {
                Map<Long, RateRestrictions> b2bRateRestrictions = new HashMap<>();
                session.getRatesRestrictions().forEach((rateId, rateRestrictions) -> {
                    RateRestrictions b2bRateRestriction = new RateRestrictions();
                    if (rateRestrictions.getRelationRestriction() != null
                            && BooleanUtils.isTrue(rateRestrictions.getRelationRestriction().getApplyToB2b())) {
                        b2bRateRestriction.setRelationRestriction(rateRestrictions.getRelationRestriction());
                        b2bRateRestriction.getRelationRestriction().setApplyToB2b(null);
                    }
                    if (rateRestrictions.getRatePriceZonesRestriction() != null && BooleanUtils.isTrue(rateRestrictions.getRatePriceZonesRestriction().getApplyToB2b())) {
                        b2bRateRestriction.setRatePriceZonesRestriction(rateRestrictions.getRatePriceZonesRestriction());
                        b2bRateRestriction.getRatePriceZonesRestriction().setApplyToB2b(null);
                    }

                    if (rateRestrictions.getRateChannelRestriction() != null){
                        b2bRateRestriction.setRateChannelRestriction(rateRestrictions.getRateChannelRestriction());
                    }

                if (b2bRateRestriction.getRatePriceZonesRestriction() != null || b2bRateRestriction.getRelationRestriction() != null || b2bRateRestriction.getRateChannelRestriction() != null) {
                        b2bRateRestrictions.put(rateId, b2bRateRestriction);
                    }
                });
                if(!b2bRateRestrictions.isEmpty()) {
                    outDto.setRatesRestrictions(b2bRateRestrictions);
                }
            }
        }
        outDto.setPriceZoneLimit(session.getPriceZoneLimit());
        outDto.setCustomersLimits(session.getCustomersLimits());
        outDto.setVirtualQueue(session.getVirtualQueue());
        outDto.setPromoter(session.getPromoter());
        outDto.setVenueProviderConfig(session.getVenueProviderConfig());
        outDto.setEventType(EventType.byId(event.getEventType()));
        outDto.setSmartBooking(session.getSmartBooking());
        outDto.setVenueTemplateType(VenueTemplateType.byId(session.getVenueTemplateType()));
        outDto.setPresalesSettings(convertPresalesSettings(session.getPresalesSettings(), event.getEventLanguages(), event.getEventDefaultLanguage(), session.getPresales()));

        fillPresales(outDto, hit.getSessionData().getSession().getPresales(), hit.getChannelSession(), presaleProviders);
        if (Boolean.TRUE.equals(channelSession.getMandatoryAttendants())) {
            outDto.setAttendantsConfig(event.getAttendantsConfig());
            outDto.setEventAttendantField(event.getAttendantFields());
        }
        outDto.setTicketHandling(channelEvent.getTicketHandling());
        S3URLResolver els = S3URLResolver.builder()
                .withUrl(s3Repository)
                .withEntityId(event.getEntityId())
                .withOperatorId(event.getOperatorId())
                .withEventId(event.getEventId())
                .withType(S3URLResolver.S3ImageType.EVENT_IMAGE)
                .build();
        List<CatalogCommunicationElementDTO> eventCommElements = CatalogCommunicationElementConverter.convert(event.getCommunicationElements(),
                els);
        outDto.setEventCommunicationElements(eventCommElements);
        outDto.setPurchaseChannelEvent(channelEvent.getPurchaseChannelEvent());
        outDto.setPurchaseSecondaryMarketChannelEvent(channelEvent.getPurchaseSecondaryMarketChannelEvent());
        outDto.setSecondaryMarketConfig(channelSession.getSecondaryMarketConfig());
        outDto.setEventSecondaryMarketConfig(eventSecondaryMarketConfig);
        outDto.setLoyaltyPointsConfig(SessionLoyaltyPointsConverter.toDTO(session.getLoyaltyPointsConfig()));
        outDto.setEventDefaultLanguage(getEventDefaultLanguage(eventData, hit.getSessionData()));
        outDto.setTaxes(buildTaxes(session.getTaxes()));
        outDto.setSurchargesTaxes(buildTaxes(session.getSurchargesTaxes()));
        outDto.setHasProducts(CollectionUtils.isNotEmpty(channelSession.getProductIds()));
        if (channelSession.getCustomersLimits() != null) {
            outDto.setCustomersLimits(new CustomersLimits());
            outDto.getCustomersLimits().setMin(channelSession.getCustomersLimits().getMin() != null ? channelSession.getCustomersLimits().getMin().longValue() : null);
            outDto.getCustomersLimits().setMax(channelSession.getCustomersLimits().getMax() != null ? channelSession.getCustomersLimits().getMax().longValue() : null);
            if (CollectionUtils.isNotEmpty(channelSession.getCustomersLimits().getPriceTypeLimits())) {
                outDto.getCustomersLimits().setPriceZoneLimit(new HashMap<>());
                for(ChannelSessionPriceTypeLimit channelSessionPriceTypeLimit : channelSession.getCustomersLimits().getPriceTypeLimits()) {
                    if(!outDto.getCustomersLimits().getPriceZoneLimit().containsKey(channelSessionPriceTypeLimit.getId())) {
                        PriceZoneLimit priceZoneLimit = new PriceZoneLimit(channelSessionPriceTypeLimit.getMax() != null ? channelSessionPriceTypeLimit.getMax().longValue() : null, channelSessionPriceTypeLimit.getMin() != null ? channelSessionPriceTypeLimit.getMin().longValue() : null);
                        outDto.getCustomersLimits().getPriceZoneLimit().put(channelSessionPriceTypeLimit.getId(), priceZoneLimit);
                    }
                }
            }
        }
        fillRelatedPacks(channelSession, outDto);

        outDto.setPriceZonesTemplatesZones(session.getEntityTemplatesZonesByPriceZoneId());
        return outDto;
    }

    private static void fillRelatedPacks(ChannelSession channelSession, ChannelCatalogEventSessionDTO outDto) {
        if (MapUtils.isNotEmpty(channelSession.getRelatedPacksByPackId())) {
            List<RelatedPackDetailDTO> relatedPackDetailDTO = new ArrayList<>();
            channelSession.getRelatedPacksByPackId().forEach((packId, items) -> {
                RelatedPackDetailDTO relatedPackDetail = new RelatedPackDetailDTO();
                relatedPackDetail.setId(packId);
                relatedPackDetail.setName(items.getName());
                relatedPackDetail.setSuggested(items.getSuggested());
                relatedPackDetail.setOnSaleForLoggedUsers(items.getOnSaleForLoggedUsers());

                List<PackItemDTO> packItemDTOs = new ArrayList<>();
                items.getItems().forEach(item -> {
                    PackItemDTO packItem = new PackItemDTO();
                    packItem.setItemId(item.getItemId());
                    packItem.setName(item.getName());
                    packItem.setMain(BooleanUtils.isTrue(item.getMain()) ? true : null);
                    packItem.setType(item.getType());
                    packItemDTOs.add(packItem);
                });
                relatedPackDetail.setItems(packItemDTOs);
                relatedPackDetailDTO.add(relatedPackDetail);
            });
            outDto.setRelatedPacks(relatedPackDetailDTO);
        }
    }

    private static String getEventDefaultLanguage(EventData channelEvent, SessionData session) {
        return session.getSession().getEventDefaultLanguage() != null ? session.getSession().getEventDefaultLanguage() : channelEvent.getEvent().getEventDefaultLanguage();
    }

    private static PresalesSettings convertPresalesSettings(PresalesSettings presalesSettings, List<String> eventLanguages, String eventDefaultLanguage, List<PresaleConfig> presales) {
        if (CollectionUtils.isEmpty(presales)) {
            return null;
        }

        else if (presalesSettings == null || presalesSettings.getRedirectPolicy() == null ||
                presalesSettings.getRedirectPolicy().getMode() != PresalesRedirectionLinkMode.CUSTOM) {
            return presalesSettings;
        }

        PresalesRedirectionPolicy policy = presalesSettings.getRedirectPolicy();

        Map<String, String> urlMap = new HashMap<>(policy.getUrl());

        eventDefaultLanguage = eventDefaultLanguage.replace("_", "-");
        List<String> normalizedLanguages = eventLanguages.stream()
                .map(lang -> lang.replace("_", "-"))
                .toList();

        String defaultUrl = urlMap.get(eventDefaultLanguage);

        normalizedLanguages.forEach(language -> urlMap.putIfAbsent(language, defaultUrl));

        PresalesRedirectionPolicy updatedPolicy = new PresalesRedirectionPolicy();
        updatedPolicy.setMode(policy.getMode());
        updatedPolicy.setUrl(urlMap);

        PresalesSettings updatedSettings = new PresalesSettings();
        updatedSettings.setRedirectPolicy(updatedPolicy);

        return updatedSettings;
    }


    private static <T extends ChannelCatalogEventSessionDTO> T convert(final T in, final ChannelSessionWithParent hit, Event event, final String s3Repository) {
        if (hit == null || hit.getChannelSession() == null || hit.getSessionData() == null || hit.getSessionData().getSession() == null) {
            return null;
        }
        ChannelSession channelSession = hit.getChannelSession();
        List<CatalogPriceZoneOccupationDTO> occupation = convert(channelSession.getPriceZoneOccupations(), event.getPrices(), channelSession.getPrices().getPrices());
        in.setPriceZoneOccupations(occupation);
        Session session = hit.getSessionData().getSession();
        fill(in, session, channelSession, event, s3Repository);
        fill(in, event, session, channelSession, s3Repository);
        in.setPrices(CatalogPricesConverter.convert(channelSession.getPrices()));
        in.setPriceZonesTemplatesZones(session.getEntityTemplatesZonesByPriceZoneId());
        return in;
    }

    private static <T extends ChannelCatalogEventSessionDTO> T convert(final T in, final ChannelSessionAgencyWithParent hit, Event event, final String s3Repository) {
        if (hit == null || hit.getChannelSessionAgency() == null || hit.getSessionData() == null || hit.getSessionData().getSession() == null) {
            return null;
        }
        ChannelSessionAgency channelSession = hit.getChannelSessionAgency();
        List<CatalogPriceZoneOccupationDTO> occupation = convert(channelSession.getPriceZoneOccupations(), event.getPrices(), channelSession.getPrices().getPrices());
        in.setPriceZoneOccupations(occupation);
        Session session = hit.getSessionData().getSession();
        fill(in, session, channelSession, event, s3Repository);
        fill(in, event, session, channelSession, s3Repository);
        in.setPrices(CatalogPricesConverter.convert(channelSession.getPrices()));
        in.setPriceTypeTags(channelSession.getPriceTypeTags());
        in.setPriceZonesTemplatesZones(session.getEntityTemplatesZonesByPriceZoneId());

        fillRelatedPacks(channelSession, in);
        return in;
    }

    private static <T extends ChannelCatalogEventSessionDTO> void fill(T outDto, Event event, Session session, ChannelSession channelSession, final String s3Repository) {
        outDto.setName(session.getSessionName());
        outDto.setVenue(CatalogVenueConverter.convert(session.getVenueId(), event.getVenues(), event.getOperatorId(), s3Repository));
        outDto.setCommunicationElements(buildEventOrSessionCommElement(event, session, channelSession, s3Repository));
    }

    public static List<CatalogCommunicationElementDTO> buildEventOrSessionCommElement(Event event, Session session, ChannelSession channelSession, String s3Repository) {

        List<SessionCommunicationElement> combinedCommunicationElements = new ArrayList<>();
        if (channelSession.getCommunicationElements() != null) {
            combinedCommunicationElements.addAll(channelSession.getCommunicationElements());
        }
        if (session.getCommunicationElements() != null) {
            combinedCommunicationElements.addAll(session.getCommunicationElements());
        }

        var commElements = CatalogCommunicationElementConverter.convert(combinedCommunicationElements,
                S3URLResolver.builder()
                        .withEntityId(event.getEntityId())
                        .withOperatorId(event.getOperatorId())
                        .withEventId(event.getEventId())
                        .withSessionId(session.getSessionId())
                        .withType(S3URLResolver.S3ImageType.SESSION_IMAGE)
                        .withUrl(s3Repository)
                        .build());
        commElements = new ArrayList<>(commElements);
        boolean hasSessionSlider = session.getCommunicationElements().stream().anyMatch(cE -> TAG_IMG_BANNER_WEB.equals(cE.getTag()));
        if (!hasSessionSlider) {
            var eventCommElements = CatalogCommunicationElementConverter.convert(event.getCommunicationElements(),
                    S3URLResolver.builder()
                            .withEntityId(event.getEntityId())
                            .withOperatorId(event.getOperatorId())
                            .withEventId(event.getEventId())
                            .withType(S3URLResolver.S3ImageType.EVENT_IMAGE)
                            .withUrl(s3Repository)
                            .build());
            var eventImgsCommElements = eventCommElements.stream().filter(e -> TAG_IMG_BANNER_WEB.equals(e.getTag())).collect(Collectors.toList());
            commElements.addAll(eventImgsCommElements);
        }
        return commElements;

    }

    private static List<CatalogPriceZoneOccupationDTO> convert(List<SessionPriceZoneOccupationDTO> in, List<VenueTemplatePrice> rates, List<PriceZonePrices> pricesZonePrices) {
        if (CollectionUtils.isEmpty(in)) {
            return null;
        }
        var priceZones = rates.stream()
                .map(VenueTemplatePrice::getPriceZones)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(PriceZonePrice::getId, PriceZonePrice::getDescription, (k, v) -> v));
        return in.stream()
                .map(s -> ChannelCatalogSessionConverter.convert(s, priceZones, pricesZonePrices))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static CatalogPriceZoneOccupationDTO convert(SessionPriceZoneOccupationDTO in, Map<Integer, String> priceZones, List<PriceZonePrices> pricesZonePrices) {
        CatalogPriceZoneOccupationDTO out = new CatalogPriceZoneOccupationDTO();
        Long id = in.getPriceZoneId();
        out.setId(id);
        if (MapUtils.isNotEmpty(priceZones)) {
            if (!priceZones.containsKey(id.intValue())) {
                return null;
            }
            out.setName(priceZones.get(id.intValue()));
        }
        out.setStatus(in.getStatus());
        out.setUnlimited(in.getUnlimited());
        if (CollectionUtils.isNotEmpty(pricesZonePrices)) {
            out.setDynamicPriceTranslations(pricesZonePrices.stream()
                    .filter(priceZonePrice -> priceZonePrice.getPriceZoneId().equals(id)).findFirst()
                    .map(PriceZonePrices::getDynamicPriceTranslations)
                    .orElse(null));
        }
        out.setLimit(in.getLimit());
        out.setAdditionalProperties(in.getAdditionalProperties());
        return out;
    }

    private static <T extends ChannelCatalogEventSessionDTO, C extends ChannelSession> void fill(T outDto, Session session, C hit, Event event, String s3Repository) {
        outDto.setId(hit.getSessionId());
        outDto.setSoldOut(hit.getSoldOut());
        outDto.setForSale(hit.getForSale());
        outDto.setPreview(hit.getPreview());
        outDto.setHasProducts(CollectionUtils.isNotEmpty(hit.getProductIds()));
        outDto.setMandatoryAttendants(hit.getMandatoryAttendants());
        ChannelCatalogDates date = hit.getDate();
        if (date == null) {
            return;
        }
        outDto.setTimeZone(hit.getTimeZone());
        outDto.setPublishDate(CatalogUtils.toZonedDateTime(date.getPublish()));
        outDto.setEndSaleDate(CatalogUtils.toZonedDateTime(date.getSaleEnd()));
        outDto.setStartSaleDate(CatalogUtils.toZonedDateTime(date.getSaleStart()));
        outDto.setStartDate(CatalogUtils.toZonedDateTime(date.getStart()));
        outDto.setEndDate(CatalogUtils.toZonedDateTime(date.getEnd()));
        outDto.setStatus(session.getSessionStatus());
        outDto.setOrderMaxTickets(session.getOrderMaxTickets());
        outDto.setRelatedSessionId(session.getRelatedSessionId());
        outDto.setSmartBooking(session.getSmartBooking());
        outDto.setStartBookingDate(CatalogUtils.toZonedDateTime(session.getBeginBookingDate()));
        outDto.setEndBookingDate(CatalogUtils.toZonedDateTime(session.getEndBookingDate()));
        outDto.setExternalReference(session.getExternalReference());
        outDto.setReference(session.getReference());
        outDto.setNoFinalDate(session.getNoFinalDate());
        outDto.setShowDate(session.getShowDate());
        outDto.setShowDateTime(session.getShowDateTime());
        outDto.setShowUnconfirmedDate(session.getShowUnconfirmedDate());
        outDto.setSeasonPackSession(session.getSeasonPackSession());
        outDto.setGraphic(session.getGraphic());
        outDto.setSessionPackSettings(fill(session.getSessionPackSettings(), event, session, s3Repository));
        outDto.setLoyaltyPointsConfig(SessionLoyaltyPointsConverter.toDTO(session.getLoyaltyPointsConfig()));
        outDto.setEventEntityId(event.getEntityId().longValue());
        outDto.setTaxes(buildTaxes(session.getTaxes()));
        outDto.setInvitationTaxes(buildTaxes(session.getInvitationTaxes()));
        outDto.setSurchargesTaxes(buildTaxes(session.getSurchargesTaxes()));
        outDto.setChannelTaxes(buildChannelTaxes(hit.getChannelTaxes()));
    }

    private static ChannelTaxesDTO buildChannelTaxes(ChannelTaxes channelTaxes) {
        if (channelTaxes == null) {
            return null;
        }
        ChannelTaxesDTO out = new ChannelTaxesDTO();
        out.setSurcharges(buildTaxes(channelTaxes.getSurcharges()));
        return out;
    }

    private static ChannelCatalogRateDTO fill(final SessionRate rate) {
        ChannelCatalogRateDTO outDto = new ChannelCatalogRateDTO();
        outDto.setId(rate.getId());
        outDto.setName(rate.getName());
        outDto.setDefaultRate(rate.getDefaultRate());
        outDto.setPosition(rate.getPosition());
        return outDto;
    }

    private static SessionPackSettingsDTO fill(final SessionPackSettings in, final Event event, final Session session, final String s3Repository){
        if (in == null) {
            return null;
        }
        SessionPackSettingsDTO out = new SessionPackSettingsDTO();
        List<SessionRelatedDTO> outSessions = in.getSessions().stream()
                .map(s-> fillSessionRelated(s, event, session, s3Repository)).collect(Collectors.toList());
        out.setNumberOfDays(in.getNumberOfDays());
        out.setNumberOfSessions(in.getNumberOfSessions());
        out.setSessions(outSessions);
        return out;
    }

    private static SessionRelatedDateDTO fillSessionRelatedDate(SessionRelated in){
        if(in == null || in.getDate() == null){
            return null;
        }
        SessionRelatedDateDTO out = new SessionRelatedDateDTO();
        out.setBeginSessionDate(CatalogUtils.toZonedDateTime(in.getDate().getBeginSessionDate()));
        out.setShowDate(in.getDate().getShowDate());
        out.setShowDateTime(in.getDate().getShowDateTime());
        return out;
    }

    private static SessionRelatedDTO fillSessionRelated(SessionRelated in, Event event, Session session, String s3Repository){
        if(in == null){
            return null;
        }
        SessionRelatedDTO out = new SessionRelatedDTO();
        out.setId(in.getId());
        out.setName(in.getName());
        out.setCommunicationElements(CatalogCommunicationElementConverter.convert(in.getCommunicationElements(),
                S3URLResolver.builder()
                        .withEntityId(event.getEntityId())
                        .withOperatorId(event.getOperatorId())
                        .withEventId(event.getEventId())
                        .withSessionId(session.getSessionId())
                        .withType(S3URLResolver.S3ImageType.SESSION_IMAGE)
                        .withUrl(s3Repository)
                        .build()));
        out.setDate(fillSessionRelatedDate(in));
        return out;
    }

    private static <T extends ChannelSession> void fillPresales(ChannelCatalogSessionDetailDTO outDto, List<PresaleConfig> presaleConfig, T channelSession, Map<Long, Provider> presaleProviders) {
        if (CollectionUtils.isNotEmpty(presaleConfig) && BooleanUtils.isTrue(channelSession.getPresale())) {
            List<ChannelSessionPresaleDTO> presales = presaleConfig.stream()
                    .filter(presale -> presale.getChannelIds().contains(channelSession.getChannelId()))
                    .map(presale -> {
                        Long presaleId = presale.getId().longValue();
                        Provider provider = presaleProviders != null ? presaleProviders.get(presaleId) : null;
                        return convertToChannelSessionPresale(presale, provider);
                    })
                    .toList();
            outDto.setPresales(CollectionUtils.isNotEmpty(presales) ? presales : null);
        }
    }

    private static ChannelSessionPresaleDTO convertToChannelSessionPresale(PresaleConfig presaleConfig, Provider provider) {
        ChannelSessionPresaleDTO presale = new ChannelSessionPresaleDTO();
        presale.setNumInputs(presaleConfig.getNumInputs());
        presale.setId(presaleConfig.getId().longValue());
        presale.setName(presaleConfig.getName());
        presale.setStatus(PresaleStatus.getById(presaleConfig.getStatus()));
        presale.setValidatorId(presaleConfig.getValidatorId());
        presale.setValidatorType(PresaleValidatorType.getById(presaleConfig.getValidatorType()));
        presale.setGeneralTicketsLimit(presaleConfig.getGeneralTicketsLimit());
        presale.setMemberTicketsLimit(presaleConfig.getMemberTicketsLimit());
        presale.setValidityPeriod(convertToPresaleValidityPeriodDTO(presaleConfig.getValidityPeriod()));
        presale.setConstraints(convertPresaleConstraints(presaleConfig));
        if (provider != null) {
            Map<String, String> additionalInfo = new HashMap<>();
            additionalInfo.put("provider", provider.name());
            presale.setAdditionalInfo(additionalInfo);
        }
        return presale;
    }

    private static PresaleValidityPeriodDTO convertToPresaleValidityPeriodDTO(PresaleValidityPeriod validityPeriod) {
        PresaleValidityPeriodDTO presaleValidityPeriod = new PresaleValidityPeriodDTO();
        presaleValidityPeriod.setTo(validityPeriod.getTo());
        presaleValidityPeriod.setFrom(validityPeriod.getFrom());
        presaleValidityPeriod.setType(validityPeriod.getType());
        return presaleValidityPeriod;
    }

    private static PresaleConstraintsDTO convertPresaleConstraints(PresaleConfig presaleConfig) {
        PresaleConstraintsDTO presaleConstraints = new PresaleConstraintsDTO();
        presaleConstraints.setCustomerTypes(presaleConfig.getCustomerTypes());
        if (presaleConfig.getLoyaltyProgram() != null && BooleanUtils.isTrue(presaleConfig.getLoyaltyProgram().getEnabled())) {
            presaleConstraints.setPoints(presaleConfig.getLoyaltyProgram().getPoints());
        }
        return presaleConstraints;
    }

    private static List<ChannelCatalogRateDTO> toCatalogRates(final List<SessionRate> rates) {
        return rates.stream().map(ChannelCatalogSessionConverter::fill).collect(Collectors.toList());
    }

    private static ExternalDataDTO convertExternalData(ExternalData externalData) {
        return Optional.ofNullable(externalData).map(e -> {
            var dto = new ExternalDataDTO();
            dto.setCapacityId(e.getCapacityId());
            return dto;
        }).orElse(null);
    }

    private static List<CatalogTaxInfoDTO> buildTaxes(List< ? extends TaxInfo> taxes) {
        if (CollectionUtils.isEmpty(taxes)) {
            return null;
        }
        return taxes.stream()
                .map(t -> {
                    CatalogTaxInfoDTO tax = new CatalogTaxInfoDTO();
                    tax.setId(t.getId());
                    tax.setName(t.getName());
                    tax.setValue(t.getValue());
                    tax.setDescription(t.getDescription());
                    tax.setProgressive(t.getProgressive());
                    tax.setProgressiveMin(t.getProgressiveMin());
                    tax.setProgressiveMax(t.getProgressiveMax());
                    tax.setMinRange(t.getMinRange());
                    tax.setMaxRange(t.getMaxRange());
                    tax.setCapacityTypeId(t.getCapacityTypeId());
                    tax.setCapacityMin(t.getCapacityMin());
                    tax.setCapacityMax(t.getCapacityMax());
                    tax.setStartDate(t.getStartDate());
                    tax.setEndDate(t.getEndDate());
                    return tax;
                })
                .collect(Collectors.toList());
    }

}
