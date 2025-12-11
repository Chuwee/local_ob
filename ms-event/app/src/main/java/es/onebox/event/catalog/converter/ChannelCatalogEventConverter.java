package es.onebox.event.catalog.converter;

import es.onebox.event.catalog.dto.CatalogCommunicationElementDTO;
import es.onebox.event.catalog.dto.CatalogContactInfo;
import es.onebox.event.catalog.dto.CatalogEntityDTO;
import es.onebox.event.catalog.dto.CatalogInvoicePrefixDTO;
import es.onebox.event.catalog.dto.ChannelCatalogEventDTO;
import es.onebox.event.catalog.dto.ChannelCatalogEventDetailDTO;
import es.onebox.event.catalog.dto.SeasonPackSettingsDTO;
import es.onebox.event.catalog.dto.TourInfoDTO;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogDatesWithTimeZones;
import es.onebox.event.catalog.elasticsearch.dto.ChannelCatalogInfo;
import es.onebox.event.catalog.elasticsearch.dto.Entity;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelCatalogEventInfo;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventAgency;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventAgencyWithParent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventWithParent;
import es.onebox.event.catalog.elasticsearch.dto.event.Event;
import es.onebox.event.catalog.elasticsearch.dto.event.EventData;
import es.onebox.event.catalog.elasticsearch.dto.event.SeasonPackSettings;
import es.onebox.event.catalog.elasticsearch.pricematrix.PriceMatrix;
import es.onebox.event.catalog.elasticsearch.pricematrix.PriceZonePrices;
import es.onebox.event.catalog.utils.CatalogUtils;
import es.onebox.event.common.services.S3URLResolver;
import es.onebox.event.events.converter.AttendantFieldConverter;
import es.onebox.event.events.dto.TaxonomyDTO;
import es.onebox.event.events.enums.EventStatus;
import es.onebox.event.events.enums.EventType;
import es.onebox.event.events.service.EventConfigService;
import es.onebox.event.promotions.dao.EventPromotionCouchDao;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ChannelCatalogEventConverter {

    private ChannelCatalogEventConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static List<ChannelCatalogEventDTO> from(List<ChannelEventWithParent> list, String s3Repository) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().map(s -> ChannelCatalogEventConverter.convert(s, s3Repository)).collect(Collectors.toList());
    }

    public static List<ChannelCatalogEventDTO> fromAgency(List<ChannelEventAgencyWithParent> list, String s3Repository) {
        if (list == null) {
            return Collections.emptyList();
        }
        return list.stream().map(s -> ChannelCatalogEventConverter.convert(s, s3Repository)).collect(Collectors.toList());
    }

    public static ChannelCatalogEventDetailDTO convertWithDetails(ChannelEventWithParent parent, String s3Repository,
                                                                  EventPromotionCouchDao eventPromotionCouchDao) {
        if (parent == null) {
            return null;
        }
        EventData eventData = parent.getEventData();
        ChannelEvent channelEvent = parent.getChannelEvent();
        if (eventData == null || channelEvent == null) {
            return null;
        }
        Event event = eventData.getEvent();
        ChannelCatalogEventDTO channelCatalogEventDTO = convert(channelEvent, event, s3Repository);
        return addDetails(channelCatalogEventDTO, event, channelEvent, eventPromotionCouchDao);
    }

    public static ChannelCatalogEventDetailDTO convertWithDetailsFromAgency(ChannelEventAgencyWithParent parent, String s3Repository,
                                                                  EventPromotionCouchDao eventPromotionCouchDao) {
        if (parent == null) {
            return null;
        }
        EventData eventData = parent.getEventData();
        ChannelEventAgency channelEvent = parent.getChannelEventAgency();
        if (eventData == null || channelEvent == null) {
            return null;
        }
        Event event = eventData.getEvent();
        ChannelCatalogEventDTO channelCatalogEventDTO = convert(channelEvent, event, s3Repository);
        if (channelCatalogEventDTO != null) {
            channelCatalogEventDTO.setProfessionalClientConditions(parent.getChannelEventAgency().getAgencyConditions());
        }
        return addDetails(channelCatalogEventDTO, event, channelEvent, eventPromotionCouchDao);
    }

    private static ChannelCatalogEventDTO convert(ChannelEventWithParent channelEventWithParent, final String s3Repository) {
        if (channelEventWithParent == null) {
            return null;
        }
        EventData eventData = channelEventWithParent.getEventData();
        if (eventData == null) {
            return null;
        }
        return convert(channelEventWithParent.getChannelEvent(), eventData.getEvent(), s3Repository);
    }

    private static ChannelCatalogEventDTO convert(ChannelEventAgencyWithParent channelEventWithParent, final String s3Repository) {
        if (channelEventWithParent == null) {
            return null;
        }
        EventData eventData = channelEventWithParent.getEventData();
        if (eventData == null) {
            return null;
        }
        ChannelCatalogEventDTO event =  convert(channelEventWithParent.getChannelEventAgency(), eventData.getEvent(), s3Repository);
        if (event != null) {
            event.setProfessionalClientConditions(channelEventWithParent.getChannelEventAgency().getAgencyConditions());
        }
        return event;
    }

    private static <T extends ChannelEvent> ChannelCatalogEventDetailDTO addDetails(ChannelCatalogEventDTO channelCatalogEventDTO,
                                                                                    Event event,
                                                                                    T channelEvent,
                                                                                    EventPromotionCouchDao eventPromotionCouchDao) {
        if (channelCatalogEventDTO == null) {
            return null;
        }
        ChannelCatalogEventDetailDTO details = new ChannelCatalogEventDetailDTO();
        BeanUtils.copyProperties(channelCatalogEventDTO, details);

        List<Long> channelPromotions = Optional.ofNullable(channelEvent.getCatalogInfo())
                .map(ChannelCatalogInfo::getPromotions)
                .orElse(Collections.emptyList());

        List<PriceZonePrices> channelEventPrices = Optional.ofNullable(channelEvent.getCatalogInfo())
                .map(ChannelCatalogInfo::getPrices)
                .map(PriceMatrix::getPrices).orElse(Collections.emptyList());

        details.setEventEntity(buildEntityData(event.getEntity()));
        details.setPromoterEntity(buildEntityData(event.getPromoter()));
        details.setUsePromoterFiscalData(event.getUsePromoterFiscalData());

        details.setPromotions(CatalogPromotionConverter.convert(event.getPromotions(), channelPromotions, null, channelEventPrices, eventPromotionCouchDao));
        fillContactInfo(details, event);
        if (event.getInvoicePrefixId() != null) {
            details.setInvoicePrefix(new CatalogInvoicePrefixDTO(event.getInvoicePrefixId(), event.getInvoicePrefix()));
        }
        details.setEventWhitelabelSettings(EventConfigService.extractEventWhitelabelSettings(event.getSupraEvent(),
                event.getWhitelabelSettings(), channelEvent.getChannelSubtype()));
        details.setInfoBannerSaleRequest(channelEvent.getInfoBannerSaleRequest());
        details.setAttendantsConfig(event.getAttendantsConfig());
        details.setAttendantFields(AttendantFieldConverter.attendantFieldToDTO(event.getAttendantFields()));
        details.setMandatoryLogin(event.getMandatoryLogin());
        details.setCustomerMaxSeats(event.getCustomerMaxSeats());
        details.setPhoneValidationRequired(channelEvent.getPhoneValidationRequired());
        details.setAttendantVerificationRequired(channelEvent.getAttendantVerificationRequired());
        details.setPostBookingQuestions(channelEvent.getPostBookingQuestions());
        details.setEventChangeSeatConfig(event.getChangeSeatConfig());
        return details;
    }


    private static <T extends ChannelEvent> ChannelCatalogEventDTO convert(T channelEvent, Event event, final String s3Repository) {
        if (channelEvent == null || event == null) {
            return null;
        }
        ChannelCatalogEventDTO channelCatalogEvent = new ChannelCatalogEventDTO();
        fill(channelCatalogEvent, event, s3Repository);
        fill(channelCatalogEvent, channelEvent, event.getOperatorId(), s3Repository);
        channelCatalogEvent.setVenues(CatalogVenueConverter.convert(channelEvent.getVenueIds(), event.getVenues(), event.getOperatorId(), s3Repository));
        return channelCatalogEvent;
    }

    private static void fill(ChannelCatalogEventDTO out, Event event, final String s3Repository) {
        out.setId(event.getEventId());
        out.setName(event.getEventName());
        out.setCurrencyId(event.getCurrency());
        out.setDescription(event.getEventDescription());
        out.setType(EventType.byId(event.getEventType()));
        out.setEventStatus(EventStatus.byId(event.getEventStatus()));
        out.setStartBookingDate(CatalogUtils.toZonedDateTime(event.getBeginBookingEventDate()));
        out.setEndBookingDate(CatalogUtils.toZonedDateTime(event.getEndBookingEventDate()));
        out.setSupraEvent(event.getSupraEvent());
        out.setGiftTicket(event.getGiftTicket());

        S3URLResolver eventImageResolver = S3URLResolver.builder()
                .withUrl(s3Repository)
                .withEntityId(event.getEntityId())
                .withOperatorId(event.getOperatorId())
                .withEventId(event.getEventId())
                .withType(S3URLResolver.S3ImageType.EVENT_IMAGE)
                .build();


        List<CatalogCommunicationElementDTO> commElements = CatalogCommunicationElementConverter.convert(event.getCommunicationElements(),
                eventImageResolver);
        appendCommElements(out, commElements);

        out.setDefaultLanguage(event.getEventDefaultLanguage());
        out.setLanguages(event.getEventLanguages());
        out.setExternalReference(event.getExternalReference());
        out.setPromoterRef(event.getPromoterRef());
        out.setTaxonomy(TaxonomyConverter.convertTaxonomy(event));
        out.setParentTaxonomy(TaxonomyConverter.convertParentTaxonomy(event));
        out.setCustomTaxonomy(TaxonomyConverter.convertCustomTaxonomy(event));
        out.setUseTieredPricing(event.getUseTieredPricing());
        out.setEventAttributesId(event.getEventAttributesId());
        out.setEventAttributesValueId(event.getEventAttributesValueId());
        out.setEntityId(Long.valueOf(event.getEntity().getId()));
        out.setSeasonPackSettings(fillSeasonPackInfo(event.getSeasonPackSettings()));
        out.setTour(fillTour(event));
    }

    private static <T extends ChannelEvent> void fill(ChannelCatalogEventDTO out,
                             T channelEvent,
                             final Integer operatorId,
                             final String s3Repository) {
        out.setMultiVenue(channelEvent.getMultiVenue());
        out.setMultiLocation(channelEvent.getMultiLocation());
        TaxonomyDTO customChannelEventTaxonomy = TaxonomyConverter.convertCustomTaxonomy(channelEvent);
        if (out.getCustomTaxonomy() == null && customChannelEventTaxonomy != null) {
            out.setCustomTaxonomy(customChannelEventTaxonomy);
        }
        TaxonomyDTO customParentChannelEventTaxonomy = TaxonomyConverter.convertCustomParentTaxonomy(channelEvent);
        if (out.getCustomParentTaxonomy() == null && customParentChannelEventTaxonomy != null) {
            out.setCustomParentTaxonomy(customParentChannelEventTaxonomy);
        }
        List<CatalogCommunicationElementDTO> commElements = CatalogCommunicationElementConverter.convert(channelEvent.getCommunicationElements(), operatorId, s3Repository);
        appendCommElements(out, commElements);

        out.setSessionsShowDate(channelEvent.getSessionsShowDate());
        out.setSessionsShowDateTime(channelEvent.getSessionsShowDateTime());
        out.setSessionsShowSchedule(channelEvent.getSessionsShowSchedule());
        out.setSessionsNoFinalDate(channelEvent.getSessionsNoFinalDate());
        out.setTicketHandling(channelEvent.getTicketHandling());

        out.setEndDate(CatalogUtils.toZonedDateTime(channelEvent.getEndChannelEventDate()));
        fill(out, channelEvent.getCatalogInfo());

        Boolean hasSessions = channelEvent.getHasSessions() == null ? null : channelEvent.getHasSessions() && (channelEvent.getFirstPublishedSession() == null || ZonedDateTime.now().isAfter(channelEvent.getFirstPublishedSession()));
        Boolean hasSessionPacks = channelEvent.getHasSessionPacks() == null ? null :  channelEvent.getHasSessionPacks() && (channelEvent.getFirstPublishedSessionPack() == null || ZonedDateTime.now().isAfter(channelEvent.getFirstPublishedSessionPack()));

        out.setHasSessions(hasSessions);
        out.setHasSessionPacks(hasSessionPacks);
        out.setPhoneValidationRequired(channelEvent.getPhoneValidationRequired());
        out.setAttendantVerificationRequired(channelEvent.getAttendantVerificationRequired());
        out.setPostBookingQuestions(channelEvent.getPostBookingQuestions());
    }

    private static void fill(ChannelCatalogEventDTO out, ChannelCatalogEventInfo catalogInfo) {
        out.setForSale(catalogInfo.getForSale());
        out.setSoldOut(catalogInfo.getSoldOut());
        out.setHighlighted(catalogInfo.getHighlighted());
        out.setCarouselPosition(catalogInfo.getCarouselPosition());
        out.setExtended(catalogInfo.getExtended());
        fill(out, catalogInfo.getDate());
        out.setPrices(CatalogPricesConverter.convert(catalogInfo.getPrices()));
    }

    private static void fill(ChannelCatalogEventDTO out, ChannelCatalogDatesWithTimeZones dates) {
        if (dates != null) {
            out.setPublishDate(CatalogUtils.toZonedDateTime(dates.getPublish()));
            out.setPublishDateTimeZone(dates.getPublishTimeZone());
            out.setStartDate(CatalogUtils.toZonedDateTime(dates.getStart()));
            out.setStartDateTimeZone(dates.getStartTimeZone());
            if (!Objects.nonNull(out.getEndDate())) {
                out.setEndDate(CatalogUtils.toZonedDateTime(dates.getEnd()));
            }
            out.setEndDateTimeZone(dates.getEndTimeZone());
            out.setStartSaleDate(CatalogUtils.toZonedDateTime(dates.getSaleStart()));
            out.setStartSaleDateTimeZone(dates.getSaleStartTimeZone());
            out.setEndSaleDate(CatalogUtils.toZonedDateTime(dates.getSaleEnd()));
            out.setEndSaleDateTimeZone(dates.getSaleEndTimeZone());
        }
    }

    private static void fillContactInfo(ChannelCatalogEventDetailDTO details, Event event) {
        CatalogContactInfo data = new CatalogContactInfo();
        data.setName(event.getChargePersonName());
        data.setSurname(event.getChargePersonSurname());
        data.setEmail(event.getChargePersonEmail());
        data.setPhone(event.getChargePersonPhone());
        details.setContact(data);
    }

    private static SeasonPackSettingsDTO fillSeasonPackInfo(SeasonPackSettings in) {
        if(in == null){
            return null;
        }
        SeasonPackSettingsDTO out = new SeasonPackSettingsDTO();
        out.setSessionId(in.getSessionId());
        return out;
    }

    private static void appendCommElements(ChannelCatalogEventDTO out, List<CatalogCommunicationElementDTO> commElements) {
        if (CollectionUtils.isEmpty(commElements)) {
            return;
        }

        if (CollectionUtils.isNotEmpty(out.getCommunicationElements())) {
            out.getCommunicationElements().addAll(commElements);
        } else {
            out.setCommunicationElements(commElements);
        }
    }

    private static TourInfoDTO fillTour(Event event) {
        if (event.getTourId() != null && StringUtils.isNotBlank(event.getTourName())) {
            TourInfoDTO result = new TourInfoDTO();
            result.setId(event.getTourId().longValue());
            result.setName(event.getTourName());
            return result;
        }
        return null;
    }

    private static CatalogEntityDTO buildEntityData(Entity in) {
        if (in == null) {
            return null;
        }
        CatalogEntityDTO result = new CatalogEntityDTO();
        result.setId(in.getId());
        result.setName(in.getName());
        result.setAddress(in.getAddress());
        result.setCity(in.getCity());
        result.setCorporateName(in.getCorporateName());
        result.setCountryCode(in.getCountryCode());
        result.setCountryName(in.getCountryName());
        result.setCountryId(in.getCountryId());
        result.setCountrySubdivisionCode(in.getCountrySubdivisionCode());
        result.setCountrySubdivisionName(in.getCountrySubdivisionName());
        result.setCountrySubdivisionId(in.getCountrySubdivisionId());
        result.setFiscalCode(in.getFiscalCode());
        result.setPostalCode(in.getPostalCode());
        result.setEmail(in.getEmail());
        return result;
    }
}
