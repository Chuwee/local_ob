package es.onebox.mgmt.sessions.converters;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.LimitlessValueDTO;
import es.onebox.mgmt.common.groups.GroupAttendeeDTO;
import es.onebox.mgmt.common.groups.GroupCompanionDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.Tier;
import es.onebox.mgmt.datasources.ms.event.dto.session.AccessScheduleType;
import es.onebox.mgmt.datasources.ms.event.dto.session.CloneSessionData;
import es.onebox.mgmt.datasources.ms.event.dto.session.CreateSessionData;
import es.onebox.mgmt.datasources.ms.event.dto.session.CreateSessionSettings;
import es.onebox.mgmt.datasources.ms.event.dto.session.LinkedSession;
import es.onebox.mgmt.datasources.ms.event.dto.session.LoyaltyPointsConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.PointGain;
import es.onebox.mgmt.datasources.ms.event.dto.session.PresalesLinkMode;
import es.onebox.mgmt.datasources.ms.event.dto.session.PresalesRedirectionPolicy;
import es.onebox.mgmt.datasources.ms.event.dto.session.PriceType;
import es.onebox.mgmt.datasources.ms.event.dto.session.PriceTypeAdditionalConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.Rate;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionDate;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionExternalConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionExternalSessions;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionGroupConfig;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionStreamingDTO;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionsGroup;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionsGroups;
import es.onebox.mgmt.datasources.ms.ticket.dto.CapacityRelocationRequest;
import es.onebox.mgmt.datasources.ms.ticket.dto.LinkSeatStatus;
import es.onebox.mgmt.datasources.ms.ticket.dto.NotNumberedZoneLinkDTO;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeatLinkDTO;
import es.onebox.mgmt.datasources.ms.ticket.dto.SeatRelocation;
import es.onebox.mgmt.datasources.ms.ticket.dto.SessionOccupationDTO;
import es.onebox.mgmt.datasources.ms.ticket.dto.SessionPriceZoneOccupationDTO;
import es.onebox.mgmt.datasources.ms.ticket.dto.SessionPriceZoneOccupationResponseDTO;
import es.onebox.mgmt.datasources.ms.ticket.dto.TicketStatus;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTagNotNumberedZoneDTO;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTagSeatDTO;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.events.dto.PriceTypeTierDTO;
import es.onebox.mgmt.events.enums.TicketType;
import es.onebox.mgmt.exception.ApiErrorDTO;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.sessions.dto.BaseSessionDTO;
import es.onebox.mgmt.sessions.dto.CapacityRelocationRequestDTO;
import es.onebox.mgmt.sessions.dto.CloneSessionRequestDTO;
import es.onebox.mgmt.sessions.dto.CreateLoyaltyPointsConfigDTO;
import es.onebox.mgmt.sessions.dto.CreatePointGainDTO;
import es.onebox.mgmt.sessions.dto.CreateSessionDates;
import es.onebox.mgmt.sessions.dto.CreateSessionPointsTypeDTO;
import es.onebox.mgmt.sessions.dto.CreateSessionRequestDTO;
import es.onebox.mgmt.sessions.dto.CreateSessionSettingsDTO;
import es.onebox.mgmt.sessions.dto.LinkedSessionDTO;
import es.onebox.mgmt.sessions.dto.PackBlockingActionsDTO;
import es.onebox.mgmt.sessions.dto.PresalesRedirectionPolicyDTO;
import es.onebox.mgmt.sessions.dto.PriceTypeAdditionalConfigDTO;
import es.onebox.mgmt.sessions.dto.PriceTypeDTO;
import es.onebox.mgmt.sessions.dto.PriceTypeRequestDTO;
import es.onebox.mgmt.sessions.dto.QuotaTierInfoDTO;
import es.onebox.mgmt.sessions.dto.RateDTO;
import es.onebox.mgmt.sessions.dto.SearchSessionsDTO;
import es.onebox.mgmt.sessions.dto.SeatRelocationDTO;
import es.onebox.mgmt.sessions.dto.SessionAvailabilityDTO;
import es.onebox.mgmt.sessions.dto.SessionAvailabilityDetailDTO;
import es.onebox.mgmt.sessions.dto.SessionChannelSettingsDTO;
import es.onebox.mgmt.sessions.dto.SessionCountryFilterDTO;
import es.onebox.mgmt.sessions.dto.SessionDTO;
import es.onebox.mgmt.sessions.dto.SessionExternalConfigDTO;
import es.onebox.mgmt.sessions.dto.SessionGroupDTO;
import es.onebox.mgmt.sessions.dto.SessionPackNotNumberedZoneLinkDTO;
import es.onebox.mgmt.sessions.dto.SessionPackSeatLinkDTO;
import es.onebox.mgmt.sessions.dto.SessionPackSettingsDTO;
import es.onebox.mgmt.sessions.dto.SessionPriceTypesAvailabilityDTO;
import es.onebox.mgmt.sessions.dto.SessionReleaseFlagStatus;
import es.onebox.mgmt.sessions.dto.SessionSaleFlagStatus;
import es.onebox.mgmt.sessions.dto.SessionSaleType;
import es.onebox.mgmt.sessions.dto.SessionSearchFilter;
import es.onebox.mgmt.sessions.dto.SessionSettingsDTO;
import es.onebox.mgmt.sessions.dto.SessionSettingsLimitsDTO;
import es.onebox.mgmt.sessions.dto.SessionSettingsLimitsMembersLoginsDTO;
import es.onebox.mgmt.sessions.dto.SessionSettingsLimitsTicketsDTO;
import es.onebox.mgmt.sessions.dto.SessionSmartBookingDTO;
import es.onebox.mgmt.sessions.dto.SessionSubscriptionListDTO;
import es.onebox.mgmt.sessions.dto.SessionVenueTagNotNumberedZoneRequestDTO;
import es.onebox.mgmt.sessions.dto.SessionVenueTagSeatRequestDTO;
import es.onebox.mgmt.sessions.dto.SessionVirtualQueueDTO;
import es.onebox.mgmt.sessions.dto.SessionsGroupDTO;
import es.onebox.mgmt.sessions.dto.SessionsGroupsDTO;
import es.onebox.mgmt.sessions.dto.SettingsAccessControlDTO;
import es.onebox.mgmt.sessions.dto.SettingsAccessControlDatesDTO;
import es.onebox.mgmt.sessions.dto.SettingsAccessControlSpaceDTO;
import es.onebox.mgmt.sessions.dto.SettingsBookingsDTO;
import es.onebox.mgmt.sessions.dto.SettingsLiveStreamingDTO;
import es.onebox.mgmt.sessions.dto.SettingsSalesDTO;
import es.onebox.mgmt.sessions.dto.SettingsSecondaryMarketDTO;
import es.onebox.mgmt.sessions.dto.SettingsSessionsSearchDTO;
import es.onebox.mgmt.sessions.dto.SetttingsReleaseDTO;
import es.onebox.mgmt.sessions.dto.StreamingVendor;
import es.onebox.mgmt.sessions.dto.TaxesDTO;
import es.onebox.mgmt.sessions.dto.TaxesDataDTO;
import es.onebox.mgmt.sessions.dto.TaxesDataTypeDTO;
import es.onebox.mgmt.sessions.dto.TierInfoDTO;
import es.onebox.mgmt.sessions.dto.TierQuotaAvailabilityDTO;
import es.onebox.mgmt.sessions.dto.UpdateSessionExternalSessionsRequestDTO;
import es.onebox.mgmt.sessions.dto.UpdateSessionRequestDTO;
import es.onebox.mgmt.sessions.dto.UpdateSessionResponseDTO;
import es.onebox.mgmt.sessions.dto.UpdateSessionSettingsDTO;
import es.onebox.mgmt.sessions.dto.VenueDTO;
import es.onebox.mgmt.sessions.dto.VenueTemplateDTO;
import es.onebox.mgmt.sessions.dto.VenueTemplateTypeDTO;
import es.onebox.mgmt.sessions.enums.PresalesLinkDestinationMode;
import es.onebox.mgmt.sessions.enums.SessionField;
import es.onebox.mgmt.sessions.enums.SessionGenerationStatus;
import es.onebox.mgmt.sessions.enums.SessionPointsType;
import es.onebox.mgmt.sessions.enums.SessionSmartBookingType;
import es.onebox.mgmt.sessions.enums.SessionStatus;
import es.onebox.mgmt.sessions.enums.SessionType;
import es.onebox.mgmt.sessions.enums.SessionVirtualQueueVersion;
import es.onebox.mgmt.sessions.enums.SubscriptionListType;
import es.onebox.mgmt.sessions.enums.UpdateSessionStatus;
import es.onebox.mgmt.venues.converter.VenueTagConverter;
import es.onebox.mgmt.venues.utils.VenueTemplateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SessionConverter {

    private SessionConverter() {
    }

    public static SessionDTO fromMsEvent(Session session) {
        if (session == null) {
            return null;
        }
        SessionDTO sessionDTO = new SessionDTO();
        return fromMsEvent(session, sessionDTO);
    }

    public static BaseSessionDTO fromMsEvent(Session session, BaseSessionDTO target) {
        if (session == null) {
            return null;
        }
        target.setId(session.getId());
        target.setName(session.getName());
        if (session.getSessionType() != null) {
            target.setType(SessionType.getById(session.getSessionType().getType()));
        }
        if (session.getStatus() != null) {
            target.setStatus(SessionStatus.valueOf(session.getStatus().name()));
        }
        if (session.getGenerationStatus() != null) {
            target.setGenerationStatus(SessionGenerationStatus.valueOf(session.getGenerationStatus().name()));
        }
        if (session.getRelease() != null) {
            target.setRelease(SessionReleaseFlagStatus.valueOf(session.getRelease().name()));
        }
        if (session.getSale() != null) {
            target.setSale(SessionSaleFlagStatus.valueOf(session.getSale().name()));
        }
        if (session.getDate() != null) {
            target.setStartDate(session.getDate().getStart());
            target.setEndDate(session.getDate().getEnd());
        }
        if (session.getEventId() != null) {
            target.setEvent(new IdNameDTO(session.getEventId(), session.getEventName()));
        }
        if (session.getEntityId() != null) {
            target.setEntity(new IdNameDTO(session.getEntityId(), session.getEntityName()));
        }
        fillSessionRelationship(session, target);
        fillVenueTemplate(session, target);

        target.setReference(session.getReference());
        target.setExternalReference(session.getExternalReference());
        target.setPublicationCancelledReason(session.getPublicationCancelledReason());
        target.setReleaseEnabled(session.getEnableChannels());
        target.setArchived(session.getArchived());

        return target;
    }

    public static CreateSessionData toMsEventSessionData(CreateSessionRequestDTO sessionData, Entity entity) {
        return toMsEventSessionData(sessionData, null, entity);
    }

    public static CreateSessionData toMsEventSessionData(CreateSessionRequestDTO sessionData, Long entityId, Entity entity) {
        CreateSessionData session = new CreateSessionData();
        session.setName(sessionData.getName());
        session.setVenueConfigId(sessionData.getVenueTemplateId());
        session.setTaxId(sessionData.getTaxTicketId());
        session.setChargeTaxId(sessionData.getTaxChargesId());

        CreateSessionDates dates = sessionData.getDates();
        session.setSessionStartDate(dates.getStartDate());
        session.setSessionEndDate(determineEndDate(dates, entity));
        session.setPublishDate(dates.getChannelsDate());
        session.setSalesStartDate(dates.getSalesStartDate());
        session.setSalesEndDate(dates.getSalesEndDate());
        session.setBookingStartDate(dates.getBookingsStartDate());
        session.setBookingEndDate(dates.getBookingsEndDate());
        session.setSecondaryMarketStartDate(dates.getSecondaryMarketSaleStartDate());
        session.setSecondaryMarketEndDate(dates.getSecondaryMarketSaleEndDate());
        session.setSmartBooking(sessionData.getEnableSmartBooking()); //TODO validate entity has enabled SB flag

        if (sessionData.getAdditionalConfig() != null) {
            session.setExternalId(sessionData.getAdditionalConfig().getAvetMatchId());
        }

        session.setRates(new ArrayList<>());
        if (sessionData.getRates() != null) { //In case of AVET events
            for (RateDTO rate : sessionData.getRates()) {
                session.getRates().add(new Rate(rate.getId(), BooleanUtils.isTrue(rate.getDefaultRate())));
            }
        }
        if (sessionData.getActivitySaleType() != null) {
            session.setSaleType(sessionData.getActivitySaleType().getId());
        }

        if (sessionData.getPackConfig() != null) {
            session.setSeasonPass(true);
            session.setSeasonSessions(sessionData.getPackConfig().getPackSessionIds());
            if (!CommonUtils.isEmpty(sessionData.getPackConfig().getPackBlockingActions())) {
                session.setSeasonPassBlockingActions(sessionData.getPackConfig().getPackBlockingActions().stream().collect(
                        Collectors.toMap(PackBlockingActionsDTO::getId, a -> a.getAction().getId())));
            }
            session.setColor(sessionData.getPackConfig().getColor());
            session.setAllowPartialRefund(sessionData.getPackConfig().getAllowPartialRefund());
        }

        session.setReference(sessionData.getReference());

        if (sessionData.getAdditionalConfig() != null) {
            session.setExternalSessionId(sessionData.getAdditionalConfig().getExternalSessionId());
        }
        if (sessionData.getLoyaltyPointsConfig() != null) {
            session.setLoyaltyPointsConfig(toMs(sessionData.getLoyaltyPointsConfig()));
        }
        if (sessionData.getSettings() != null) {
            session.setSettings(toMs(sessionData.getSettings()));
        }

        session.setEntityId(entityId);

        return session;
    }

    public static CloneSessionData toMsEventCloneSessionBody(CloneSessionRequestDTO sessionData, Long targetId) {
        CloneSessionData session = new CloneSessionData();
        session.setName(sessionData.getName());
        session.setStartDate(sessionData.getStartDate());
        if (sessionData.getEndDate() != null) {
            session.setEndDate(sessionData.getEndDate());
        }

        if (sessionData.getReference() != null) {
            session.setReference(sessionData.getReference());
        }

        if (sessionData.getSessionPackSeatsTarget() != null) {
            if (targetId != null) {
                session.setTargetBlockingReasonId(targetId);
            } else {
                session.setTargetFreeStatus(true);
            }
        }
        return session;
    }

    public static SessionDTO fromMsEvent(Session session, SessionDTO target) {
        if (session == null) {
            return null;
        }

        //Convert to BaseSessionDTO
        target = (SessionDTO) fromMsEvent(session, (BaseSessionDTO) target);

        //Fill SessionDTO details
        target.setSettings(getSessionSettingsDTO(session));
        target.setExternalData(session.getExternalData());

        return target;
    }

    public static SessionAvailabilityDTO fromSessionOccupation(SessionOccupationDTO occupation) {
        SessionAvailabilityDTO availability = new SessionAvailabilityDTO();

        Long total = occupation.getStatus().values()
                .stream()
                .reduce(0L, Long::sum);

        availability.setTotal(total.intValue());
        occupation.getStatus().putIfAbsent(TicketStatus.AVAILABLE, 0L);
        availability.setAvailable(occupation.getStatus().get(TicketStatus.AVAILABLE).intValue());

        return availability;
    }

    public static List<SessionPriceTypesAvailabilityDTO> fromSessionAvailability(
            List<SessionPriceZoneOccupationResponseDTO> sessionOccupation, boolean activitySession) {
        List<SessionPriceTypesAvailabilityDTO> availability = new ArrayList<>();
        for (SessionPriceZoneOccupationResponseDTO occupation : sessionOccupation) {

            IdNameDTO quota = null;
            if (occupation.getSession().getQuotas() != null) {
                quota = new IdNameDTO(occupation.getSession().getQuotas().get(0));
            }

            for (SessionPriceZoneOccupationDTO priceZoneQuotaOccupation : occupation.getOccupation()) {
                SessionPriceTypesAvailabilityDTO availabilityDTO = new SessionPriceTypesAvailabilityDTO();
                availabilityDTO.setPriceType(new IdNameDTO(priceZoneQuotaOccupation.getPriceZoneId()));
                availabilityDTO.setQuota(quota);
                availabilityDTO.setTicketType(activitySession ? TicketType.INDIVIDUAL : null);

                fillAvailability(availabilityDTO, priceZoneQuotaOccupation.getLimit(), priceZoneQuotaOccupation.getStatus(), activitySession);

                availability.add(availabilityDTO);
            }
        }
        return availability;
    }

    public static SessionPriceTypesAvailabilityDTO fromSessionGroupAvailability(SessionOccupationDTO groupsOccupation) {
        if (groupsOccupation == null || groupsOccupation.getStatus() == null) {
            return null;
        }

        SessionPriceTypesAvailabilityDTO availabilityDTO = new SessionPriceTypesAvailabilityDTO();
        availabilityDTO.setTicketType(TicketType.GROUP);

        fillAvailability(availabilityDTO, null, groupsOccupation.getStatus(), true);

        return availabilityDTO;
    }

    private static void fillAvailability(SessionPriceTypesAvailabilityDTO availabilityDTO, Long limit, Map<TicketStatus, Long> status, boolean activitySession) {
        SessionAvailabilityDetailDTO availabilityDetailDTO = new SessionAvailabilityDetailDTO();
        availabilityDetailDTO.setAvailable(NumberUtils.zeroIfNull(status.get(TicketStatus.AVAILABLE)));
        availabilityDetailDTO.setPurchase(NumberUtils.zeroIfNull(status.get(TicketStatus.SOLD)));
        availabilityDetailDTO.setInvitation(NumberUtils.zeroIfNull(status.get(TicketStatus.INVITATION)));
        availabilityDetailDTO.setBooking(NumberUtils.zeroIfNull(status.get(TicketStatus.BOOKED)));
        availabilityDetailDTO.setIssue(NumberUtils.zeroIfNull(status.get(TicketStatus.ISSUED)));
        availabilityDetailDTO.setInProgress(NumberUtils.zeroIfNull(status.get(TicketStatus.BLOCKED_SYSTEM)) +
                NumberUtils.zeroIfNull(status.get(TicketStatus.BLOCKED_PRESALE)) +
                NumberUtils.zeroIfNull(status.get(TicketStatus.BLOCKED_SALE)));
        if (activitySession) {
            availabilityDetailDTO.setTotal(new LimitlessValueDTO(limit));
        } else {
            availabilityDetailDTO.setKill(NumberUtils.zeroIfNull(status.get(TicketStatus.KILL)));
            availabilityDetailDTO.setPromoterBlocked(NumberUtils.zeroIfNull(status.get(TicketStatus.BLOCKED_PROMOTER)));
            availabilityDetailDTO.setSessionPack(NumberUtils.zeroIfNull(status.get(TicketStatus.BLOCKED_SEASON_TICKET)));
            availabilityDetailDTO.setTotal(new LimitlessValueDTO(getTotalValue(availabilityDetailDTO)));
        }
        availabilityDTO.setAvailability(availabilityDetailDTO);
    }

    public static Long getTotalValue(SessionAvailabilityDetailDTO dto) {
        return dto.getAvailable() + dto.getPurchase() + dto.getInProgress() + dto.getBooking() +
                dto.getIssue() + dto.getInvitation() + dto.getKill() + dto.getPromoterBlocked() + dto.getSessionPack();
    }

    public static Session toMsEvent(UpdateSessionRequestDTO source) {
        Session target = new Session();
        target.setName(source.getName());
        if (source.getStatus() != null) {
            target.setStatus(es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus.valueOf(source.getStatus().name()));
        }

        target.setDate(new SessionDate());
        target.getDate().setStart(source.getStartDate());
        target.getDate().setEnd(source.getEndDate());

        UpdateSessionSettingsDTO settings = source.getSettings();
        if (settings != null) {
            fillSessionSettings(target, settings);
        }

        target.setReference(source.getReference());

        return target;
    }

    public static SessionExternalSessions toMsEvent(UpdateSessionExternalSessionsRequestDTO source) {
        SessionExternalSessions target = new SessionExternalSessions();
        target.setGeneralAdmission(source.getGeneralAdmission());

        return target;
    }

    public static List<UpdateSessionResponseDTO> fromBulkUpdate(Map<Long, String> response, List<Long> sessionIds) {
        return sessionIds.stream()
                .map(UpdateSessionResponseDTO::new)
                .peek(elem -> {
                    elem.setStatus(UpdateSessionStatus.OK);
                    if (response.containsKey(elem.getId())) {
                        elem.setStatus(UpdateSessionStatus.ERROR);
                        elem.setDetail(new ApiErrorDTO(MsEventDatasource.getErrorCode(response.get(elem.getId()))));
                    }
                })
                .toList();
    }

    public static SeatLinkDTO buildSeatLinks(SessionPackSeatLinkDTO seats, Long targetId) {
        SeatLinkDTO seatLinkDTO = new SeatLinkDTO();
        seatLinkDTO.setIds(seats.getIds());
        if (targetId != null) {
            seatLinkDTO.setToBlockingReason(targetId);
            seatLinkDTO.setToStatus(LinkSeatStatus.PROMOTOR_LOCKED);
        } else {
            seatLinkDTO.setToStatus(LinkSeatStatus.FREE);
        }
        seatLinkDTO.setToQuota(seats.getQuota());
        return seatLinkDTO;
    }

    public static NotNumberedZoneLinkDTO buildNNZLinks(SessionPackNotNumberedZoneLinkDTO nnz, Long sourceId, Long targetId) {
        NotNumberedZoneLinkDTO notNumberedZoneLinkDTO = new NotNumberedZoneLinkDTO();
        notNumberedZoneLinkDTO.setId(nnz.getId());
        if (sourceId != null) {
            notNumberedZoneLinkDTO.setFromBlockingReason(sourceId);
            notNumberedZoneLinkDTO.setFromStatus(LinkSeatStatus.PROMOTOR_LOCKED);
        } else {
            notNumberedZoneLinkDTO.setFromStatus(LinkSeatStatus.FREE);
        }
        if (targetId != null) {
            notNumberedZoneLinkDTO.setToBlockingReason(targetId);
            notNumberedZoneLinkDTO.setToStatus(LinkSeatStatus.PROMOTOR_LOCKED);
        } else {
            notNumberedZoneLinkDTO.setToStatus(LinkSeatStatus.FREE);
        }
        notNumberedZoneLinkDTO.setCapacity(nnz.getCount());
        return notNumberedZoneLinkDTO;
    }

    public static TierQuotaAvailabilityDTO toSaleGroupTierAvailability(
            Tier tier, Long saleGroupId, String saleGroupName, Integer saleGroupLimit, Long priceTypeCapacity) {
        TierQuotaAvailabilityDTO res = new TierQuotaAvailabilityDTO();

        PriceTypeTierDTO priceType = new PriceTypeTierDTO();
        priceType.setId(tier.getPriceTypeId());
        priceType.setName(tier.getPriceTypeName());
        priceType.setCapacity(priceTypeCapacity);
        res.setPriceType(priceType);

        TierInfoDTO tierInfo = new TierInfoDTO();
        tierInfo.setId(tier.getId());
        tierInfo.setName(tier.getName());
        tierInfo.setActive(tier.getActive());
        tierInfo.setLimit(CommonUtils.ifNotNull(tier.getLimit(), Long::intValue));
        res.setTier(tierInfo);

        QuotaTierInfoDTO saleGroup = new QuotaTierInfoDTO();
        saleGroup.setId(saleGroupId);
        saleGroup.setName(saleGroupName);
        saleGroup.setLimit(saleGroupLimit);
        res.setQuota(saleGroup);
        return res;
    }

    public static PriceTypeDTO fromMsEvent(PriceType priceType, VenueTemplate venueTemplate) {
        if (priceType == null) {
            return null;
        }
        PriceTypeDTO priceTypeDTO = new PriceTypeDTO();
        priceTypeDTO.setId(priceType.getId());
        priceTypeDTO.setName(priceType.getName());
        priceTypeDTO.setAdditionalConfig(new PriceTypeAdditionalConfigDTO());
        priceTypeDTO.getAdditionalConfig().setRestrictiveAccess(priceType.getAdditionalConfig().getRestrictiveAccess());
        if (VenueTemplateUtils.isVisitOrThemePark(venueTemplate.getTemplateType())) {
            priceTypeDTO.getAdditionalConfig().setGateId(priceType.getAdditionalConfig().getGateId());
        }
        return priceTypeDTO;
    }

    public static PriceType toMsEvent(PriceTypeRequestDTO priceTypeRequestDTO) {
        if (priceTypeRequestDTO == null) {
            return null;
        }
        PriceType priceType = new PriceType();
        if (priceTypeRequestDTO.getAdditionalConfig() != null) {
            priceType.setAdditionalConfig(new PriceTypeAdditionalConfig());
            priceType.getAdditionalConfig().setGateId(priceTypeRequestDTO.getAdditionalConfig().getGateId());
        }
        return priceType;
    }

    private static void fillSessionRelationship(Session session, BaseSessionDTO target) {
        if (session.getSessionType() != null) {
            if (SessionType.SESSION.getType().equals(session.getSessionType().getType()) &&
                    CollectionUtils.isNotEmpty(session.getSeasonIds())) {
                target.setSessionIds(session.getSeasonIds());
            } else if (CollectionUtils.isNotEmpty(session.getSessionIds())) {
                target.setSessionIds(session.getSessionIds());
            }
        }
    }

    private static void fillVenueTemplate(Session session, BaseSessionDTO target) {
        if (session.getVenueConfigId() != null || session.getCapacity() != null) {
            target.setVenueTemplate(new VenueTemplateDTO());
            target.getVenueTemplate().setId(session.getVenueConfigId());
            target.getVenueTemplate().setName(session.getVenueConfigName());
            target.getVenueTemplate().setCapacity(session.getCapacity());
            target.getVenueTemplate().setGraphic(session.isVenueConfigGraphic());
            target.getVenueTemplate().setType(VenueTemplateTypeDTO.fromId(session.getVenueConfigTemplateType()));
            if (session.getVenueConfigSpaceId() != null) {
                target.getVenueTemplate().setSpace(new IdNameDTO(
                        session.getVenueConfigSpaceId(), session.getVenueConfigSpaceName()));
            }

        }
        if (session.getVenueId() != null || StringUtils.isNotBlank(session.getVenueName())) {
            if (target.getVenueTemplate() == null) {
                target.setVenueTemplate(new VenueTemplateDTO());
            }
            VenueDTO venue = new VenueDTO();
            venue.setId(session.getVenueId());
            venue.setName(session.getVenueName());
            venue.setCity(session.getCity());
            venue.setTimezone(session.getTimeZone() == null ? null : session.getTimeZone().getOlsonId());
            target.getVenueTemplate().setVenue(venue);
        }
    }

    private static SessionSettingsDTO getSessionSettingsDTO(Session session) {
        SessionSettingsDTO settings = new SessionSettingsDTO();

        if (!CommonUtils.isEmpty(session.getRates())) {
            settings.setRates(new ArrayList<>());
            for (Rate rate : session.getRates()) {
                settings.getRates().add(
                        new es.onebox.mgmt.sessions.dto.RateDTO(rate.getId(), rate.getName(), rate.isDefaultRate()));
            }
        }

        settings.setTaxes(fillTaxes(session));

        settings.setRelease(new SetttingsReleaseDTO());
        settings.getRelease().setEnable(session.getEnableChannels());
        settings.getRelease().setDate(session.getDate().getChannelPublication());

        settings.setBooking(new SettingsBookingsDTO());
        settings.getBooking().setEnable(session.getEnableBookings());
        settings.getBooking().setStartDate(session.getDate().getBookingsStart());
        settings.getBooking().setEndDate(session.getDate().getBookingsEnd());

        settings.setSale(new SettingsSalesDTO());
        settings.getSale().setEnable(session.getEnableSales());
        settings.getSale().setStartDate(session.getDate().getSalesStart());
        settings.getSale().setEndDate(session.getDate().getSalesEnd());

        settings.setSessionChannelSettings(new SessionChannelSettingsDTO());
        settings.getSessionChannelSettings().setEnableShowDateInChannels(session.getEnableShowDateInChannels());
        settings.getSessionChannelSettings().setEnableShowTimeInChannels(session.getEnableShowTimeInChannels());
        settings.getSessionChannelSettings().setEnableShowUnconfirmedDateInChannels(session.getEnableShowUnconfirmedDateInChannels());

        settings.setEnableCaptcha(session.getEnableCaptcha());
        settings.setEnableOrphanSeats(session.getEnableOrphanSeats());
        settings.setUseVenueTemplateCapacityConfig(session.getUseVenueConfigCapacity());
        settings.setUseVenueTemplateAccess(session.getUseTemplateAccess());

        if (session.getAccessScheduleType() != null) {
            settings.setAccessControl(new SettingsAccessControlDTO());
            settings.getAccessControl().setDates(new SettingsAccessControlDatesDTO());
            settings.getAccessControl().getDates().setOverride(AccessScheduleType.SPECIFIC.equals(session.getAccessScheduleType()));
            if (session.getDate() != null && settings.getAccessControl().getDates().getOverride()) {
                settings.getAccessControl().getDates().setStart(session.getDate().getAdmissionStart());
                settings.getAccessControl().getDates().setEnd(session.getDate().getAdmissionEnd());
            }
            settings.getAccessControl().setSpace(new SettingsAccessControlSpaceDTO());
            settings.getAccessControl().getSpace().setOverride(session.getSpace() != null);
            if (settings.getAccessControl().getSpace().getOverride()) {
                settings.getAccessControl().getSpace().setId(session.getSpace().getId());
            }
        }

        if (session.getStreaming() != null) {
            SessionStreamingDTO streaming = session.getStreaming();
            settings.setLiveStreaming(new SettingsLiveStreamingDTO());
            settings.getLiveStreaming().setEnable(streaming.getEnabled());
            settings.getLiveStreaming().setValue(streaming.getValue());
            if (streaming.getVendor() != null) {
                settings.getLiveStreaming().setVendor(StreamingVendor.valueOf(streaming.getVendor().name()));
            }
        }
        if (session.getSaleType() != null) {
            settings.setActivitySaleType(SessionSaleType.byId(session.getSaleType()));
        }
        if (session.getSessionType() != null && !session.getSessionType().equals(es.onebox.mgmt.datasources.ms.event.dto.session.SessionType.SESSION)) {
            settings.setSessionPackSettings(fillSessionPackSettings(session));
        }

        if (session.getQueueAlias() != null || session.getEnableQueue() != null) {
            SessionVirtualQueueDTO virtualQueueDTO = new SessionVirtualQueueDTO();
            virtualQueueDTO.setAlias(session.getQueueAlias());
            virtualQueueDTO.setEnable(session.getEnableQueue());
            virtualQueueDTO.setSkipQueueToken(session.getSkipQueueToken());
            virtualQueueDTO.setVersion(SessionVirtualQueueVersion.getByName(session.getQueueVersion().getName()));
            settings.setSessionVirtualQueue(virtualQueueDTO);
        }

        if (session.getEnableSubscriptionList() != null || session.getSubscriptionListId() != null) {
            SessionSubscriptionListDTO subscriptionListDTO = new SessionSubscriptionListDTO();
            if (BooleanUtils.isTrue(session.getEnableSubscriptionList())) {
                subscriptionListDTO.setScope(SubscriptionListType.SESSION);
            } else {
                subscriptionListDTO.setScope(SubscriptionListType.EVENT);
            }
            subscriptionListDTO.setId(session.getSubscriptionListId());
            settings.setSubscriptionList(subscriptionListDTO);
        }

        if (session.getEnableCountryFilter() != null || session.getCountries() != null) {
            SessionCountryFilterDTO sessionCountryFilter = new SessionCountryFilterDTO();
            sessionCountryFilter.setCountries(session.getCountries());
            sessionCountryFilter.setEnable(session.getEnableCountryFilter());
            settings.setSessionCountryFilter(sessionCountryFilter);
        }

        if (session.getEnableSecondaryMarket() != null && session.getDate() != null) {
            SettingsSecondaryMarketDTO secondaryMarket = new SettingsSecondaryMarketDTO();
            secondaryMarket.setEnable(session.getEnableSecondaryMarket());
            secondaryMarket.setStartDate(session.getDate().getSecondaryMarketStart());
            secondaryMarket.setEndDate(session.getDate().getSecondaryMarketEnd());
            settings.setSecondaryMarket(secondaryMarket);
        }

        SessionSmartBookingDTO smartBooking = getSessionSmartBookingDTO(session);
        settings.setSmartBooking(smartBooking);

        settings.setLimits(fillLimitSettings(session));

        settings.setEnablePresale(session.getPresaleEnabled());

        if (session.getPresalesRedirectionPolicy() != null) {
            PresalesRedirectionPolicyDTO presalesRedirectionPolicyDTO = new PresalesRedirectionPolicyDTO();
            presalesRedirectionPolicyDTO.setValue(session.getPresalesRedirectionPolicy().getValue());
            if (session.getPresalesRedirectionPolicy().getMode() != null) {
                presalesRedirectionPolicyDTO.setMode(PresalesLinkDestinationMode.valueOf(session.getPresalesRedirectionPolicy().getMode().name()));
            }

            settings.setPresalesRedirectionPolicy(presalesRedirectionPolicyDTO);
        }

        settings.setHighDemand(session.getHighDemand());

        if (session.getSessionExternalConfig() != null) {
            SessionExternalConfigDTO sessionExternalConfig = new SessionExternalConfigDTO();
            sessionExternalConfig.setDigitalTicketMode(session.getSessionExternalConfig().getDigitalTicketMode());
            settings.setSessionExternalConfig(sessionExternalConfig);
        }

        settings.setUseDynamicPrices(session.getUseDynamicPrices());

        return settings;
    }

    private static SessionSmartBookingDTO getSessionSmartBookingDTO(Session session) {
        if (session.getIsSmartBooking() != null) {
            SessionSmartBookingDTO smartBooking = new SessionSmartBookingDTO();
            smartBooking.setType(BooleanUtils.isTrue(session.getIsSmartBooking()) ? SessionSmartBookingType.SMART_BOOKING : SessionSmartBookingType.SEAT_SELECTION);
            smartBooking.setRelatedSessionId(session.getSmartBookingRelatedId());
            return smartBooking;
        }
        return null;
    }

    private static TaxesDTO fillTaxes(Session session) {
        TaxesDTO taxes = new TaxesDTO();
        taxes.setTicket(session.getTicketTax());
        taxes.setTicketTaxes(session.getTicketTaxes());
        taxes.setCharges(session.getChargesTax());
        taxes.setChargesTaxes(session.getChargesTaxes());
        taxes.setData(new TaxesDataDTO());
        taxes.getData().setType(Boolean.TRUE.equals(session.getEnableProducerTaxData()) ?
                TaxesDataTypeDTO.PRODUCER : TaxesDataTypeDTO.EVENT);
        if (Boolean.TRUE.equals(session.getEnableProducerTaxData())) {
            taxes.getData().setProducerId(session.getProducerId());
            taxes.getData().setInvoicePrefixId(session.getInvoicePrefixId());
        }
        return taxes;
    }

    private static SessionSettingsLimitsDTO fillLimitSettings(Session session) {
        SessionSettingsLimitsDTO limits = new SessionSettingsLimitsDTO();

        limits.setTicketsLimit(new SessionSettingsLimitsTicketsDTO());
        limits.getTicketsLimit().setEnableSessionTicketLimit(session.getEnableSessionTicketLimit());
        limits.getTicketsLimit().setSessionTicketLimit(session.getSessionTicketLimit());

        limits.setMembersLoginsLimit(new SessionSettingsLimitsMembersLoginsDTO());
        limits.getMembersLoginsLimit().setEnableMembersLoginsLimit(session.getEnableMembersLoginsLimit());
        limits.getMembersLoginsLimit().setMembersLoginsLimit(session.getMembersLoginsLimit());

        return limits;
    }

    private static SessionPackSettingsDTO fillSessionPackSettings(Session session) {
        if (!es.onebox.mgmt.datasources.ms.event.dto.session.SessionType.SESSION.equals(session.getSessionType())) {
            SessionPackSettingsDTO sessionPackSettings = new SessionPackSettingsDTO();
            sessionPackSettings.setColor(session.getColor());
            sessionPackSettings.setAllowPartialRefund(session.getAllowPartialRefund());
            return sessionPackSettings;
        }
        return null;
    }

    private static void fillSessionSettings(Session target, UpdateSessionSettingsDTO settings) {
        if (settings.getRates() != null) {
            target.setRates(new ArrayList<>());
            for (RateDTO rate : settings.getRates()) {
                target.getRates().add(new Rate(rate.getId(), CommonUtils.isTrue(rate.getDefaultRate())));
            }
        }
        if (settings.getTaxes() != null) {
            target.setTicketTax(settings.getTaxes().getTicket());
            target.setTicketTaxes(settings.getTaxes().getTicketTaxes());
            target.setChargesTax(settings.getTaxes().getCharges());
            target.setChargesTaxes(settings.getTaxes().getChargesTaxes());
            if (settings.getTaxes().getData() != null) {
                if (settings.getTaxes().getData().getType() != null &&
                        settings.getTaxes().getData().getType().equals(TaxesDataTypeDTO.EVENT)) {
                    target.setEnableProducerTaxData(Boolean.FALSE);
                } else if (settings.getTaxes().getData().getType().equals(TaxesDataTypeDTO.PRODUCER)) {
                    target.setEnableProducerTaxData(Boolean.TRUE);
                    target.setInvoicePrefixId(settings.getTaxes().getData().getInvoicePrefixId());
                }
                target.setProducerId(settings.getTaxes().getData().getProducerId());
            }
        }
        if (settings.getRelease() != null) {
            target.setEnableChannels(settings.getRelease().getEnable());
            target.getDate().setChannelPublication(settings.getRelease().getDate());
        }
        if (settings.getBooking() != null) {
            target.setEnableBookings(settings.getBooking().getEnable());
            target.getDate().setBookingsStart(settings.getBooking().getStartDate());
            target.getDate().setBookingsEnd(settings.getBooking().getEndDate());
        }
        if (settings.getSale() != null) {
            target.setEnableSales(settings.getSale().getEnable());
            target.getDate().setSalesStart(settings.getSale().getStartDate());
            target.getDate().setSalesEnd(settings.getSale().getEndDate());
        }
        target.setUseTemplateAccess(settings.getUseVenueTemplateAccess());
        target.setEnableCaptcha(settings.getEnableCaptcha());
        target.setEnableOrphanSeats(settings.getEnableOrphanSeats());
        if (settings.getActivitySaleType() != null) {
            target.setSaleType(settings.getActivitySaleType().getId());
        }
        if (settings.getSessionPackSettings() != null) {
            target.setColor(settings.getSessionPackSettings().getColor());
        }
        if (settings.getLimits() != null) {
            if (settings.getLimits().getTicketsLimit() != null) {
                target.setEnableSessionTicketLimit(settings.getLimits().getTicketsLimit().getEnableSessionTicketLimit());
                target.setSessionTicketLimit(settings.getLimits().getTicketsLimit().getSessionTicketLimit());
            }
            if (settings.getLimits().getMembersLoginsLimit() != null) {
                target.setEnableMembersLoginsLimit(settings.getLimits().getMembersLoginsLimit().getEnableMembersLoginsLimit());
                target.setMembersLoginsLimit(settings.getLimits().getMembersLoginsLimit().getMembersLoginsLimit());
            }
        }
        if (settings.getSessionCountryFilter() != null) {
            target.setEnableCountryFilter(settings.getSessionCountryFilter().getEnable());
            target.setCountries(settings.getSessionCountryFilter().getCountries());
        }

        if (settings.getSessionChannelSettings() != null) {
            target.setEnableShowDateInChannels(settings.getSessionChannelSettings().getEnableShowDateInChannels());
            target.setEnableShowTimeInChannels(settings.getSessionChannelSettings().getEnableShowTimeInChannels());
            target.setEnableShowUnconfirmedDateInChannels(settings.getSessionChannelSettings().getEnableShowUnconfirmedDateInChannels());
        }

        if (settings.getSessionVirtualQueue() != null) {
            target.setQueueAlias(settings.getSessionVirtualQueue().getAlias());
            target.setEnableQueue(settings.getSessionVirtualQueue().getEnable());
            if (settings.getSessionVirtualQueue().getVersion() != null) {
                target.setQueueVersion(es.onebox.mgmt.datasources.ms.event.dto.session.SessionVirtualQueueVersion.getByName(settings.getSessionVirtualQueue().getVersion().getName()));
            }
        }
        if (settings.getSubscriptionList() != null) {
            if (settings.getSubscriptionList().getScope() != null) {
                if (SubscriptionListType.SESSION.equals(settings.getSubscriptionList().getScope())) {
                    target.setEnableSubscriptionList(Boolean.TRUE);
                } else {
                    target.setEnableSubscriptionList(Boolean.FALSE);
                }
            }
            target.setSubscriptionListId(settings.getSubscriptionList().getId());
        }
        if (settings.getSecondaryMarket() != null) {
            target.setEnableSecondaryMarket(settings.getSecondaryMarket().getEnable());
            target.getDate().setSecondaryMarketStart(settings.getSecondaryMarket().getStartDate());
            target.getDate().setSecondaryMarketEnd(settings.getSecondaryMarket().getEndDate());
        }

        if (settings.getPresalesRedirectionPolicy() != null) {
            PresalesRedirectionPolicy presalesRedirectionPolicy = new PresalesRedirectionPolicy();

            presalesRedirectionPolicy.setValue(settings.getPresalesRedirectionPolicy().getValue());
            if (settings.getPresalesRedirectionPolicy().getMode() != null) {
                presalesRedirectionPolicy.setMode(PresalesLinkMode.valueOf(settings.getPresalesRedirectionPolicy().getMode().name()));
            }

            target.setPresalesRedirectionPolicy(presalesRedirectionPolicy);
        }

        target.setHighDemand(settings.getHighDemand());
        target.setUseVenueConfigCapacity(settings.getUseVenueTemplateCapacityConfig());
        fillSessionSettingsAccessControl(target, settings);
        fillSessionSettingsLiveStreaming(target, settings);
        if (settings.getSessionExternalConfig() != null) {
            SessionExternalConfig sessionExternalConfig = new SessionExternalConfig();
            sessionExternalConfig.setDigitalTicketMode(settings.getSessionExternalConfig().getDigitalTicketMode());
            target.setSessionExternalConfig(sessionExternalConfig);
        }
        target.setUseDynamicPrices(settings.getUseDynamicPrices());
    }

    private static void fillSessionSettingsAccessControl(Session target, UpdateSessionSettingsDTO settings) {
        SettingsAccessControlDTO accessControl = settings.getAccessControl();
        if (accessControl != null) {
            if (accessControl.getDates() != null) {
                if (CommonUtils.isTrue(accessControl.getDates().getOverride())) {
                    target.setAccessScheduleType(AccessScheduleType.SPECIFIC);
                    target.getDate().setAdmissionStart(accessControl.getDates().getStart());
                    target.getDate().setAdmissionEnd(accessControl.getDates().getEnd());
                } else {
                    target.setAccessScheduleType(AccessScheduleType.DEFAULT);
                }
            }
            if (accessControl.getSpace() != null) {
                if (CommonUtils.isTrue(accessControl.getSpace().getOverride())) {
                    target.setSpace(new IdNameDTO(accessControl.getSpace().getId()));
                } else {
                    target.setSpace(new IdNameDTO(null));
                }
            }
        }
    }

    private static void fillSessionSettingsLiveStreaming(Session target, UpdateSessionSettingsDTO settings) {
        if (settings.getLiveStreaming() != null) {
            SessionStreamingDTO streaming = new SessionStreamingDTO();
            streaming.setEnabled(settings.getLiveStreaming().getEnable());
            streaming.setValue(settings.getLiveStreaming().getValue());
            if (settings.getLiveStreaming().getVendor() != null) {
                streaming.setVendor(es.onebox.mgmt.datasources.common.enums.StreamingVendor.valueOf(
                        settings.getLiveStreaming().getVendor().name()));
            }
            target.setStreaming(streaming);
        }
    }

    public static SessionGroupDTO fromMsGroupConfig(SessionGroupConfig source, VenueTemplate venueTemplateSource) {
        SessionGroupDTO target = new SessionGroupDTO();
        target.setAttendees(new GroupAttendeeDTO());
        target.setCompanions(new GroupCompanionDTO());
        target.setVenueTemplateName(venueTemplateSource.getName());
        if (source == null || source.getId() == null) {
            target.setUseVenueTemplateGroupConfig(true);
            target.setLimit(new LimitlessValueDTO(venueTemplateSource.getMaxGroups()));
            target.getAttendees().setMin(venueTemplateSource.getMinAttendees());
            target.getAttendees().setMax(new LimitlessValueDTO(venueTemplateSource.getMaxAttendees()));
            target.getCompanions().setMin(venueTemplateSource.getMinCompanions());
            target.getCompanions().setMax(new LimitlessValueDTO(venueTemplateSource.getMaxCompanions()));
            target.getCompanions().setOccupyCapacity(venueTemplateSource.getCompanionsOccupyCapacity());
        } else {
            target.setUseVenueTemplateGroupConfig(false);
            target.setLimit(new LimitlessValueDTO(source.getMaxGroups()));
            target.getAttendees().setMin(source.getMinAttendees());
            target.getAttendees().setMax(new LimitlessValueDTO(source.getMaxAttendees()));
            target.getCompanions().setMin(source.getMinCompanions());
            target.getCompanions().setMax(new LimitlessValueDTO(source.getMaxCompanions()));
            target.getCompanions().setOccupyCapacity(source.getCompanionsOccupyCapacity());
        }
        return target;
    }

    public static SessionGroupConfig toMsGroupConfig(SessionGroupDTO source) {
        if (source == null) {
            return null;
        }
        SessionGroupConfig target = new SessionGroupConfig();
        target.setMaxGroups(ConverterUtils.getIntLimitlessValue(source.getLimit()));
        if (source.getAttendees() != null) {
            target.setMinAttendees(source.getAttendees().getMin());
            target.setMaxAttendees(ConverterUtils.getIntLimitlessValue(source.getAttendees().getMax()));
        }
        if (source.getCompanions() != null) {
            target.setMinCompanions(source.getCompanions().getMin());
            target.setMaxCompanions(ConverterUtils.getIntLimitlessValue(source.getCompanions().getMax()));
            target.setCompanionsOccupyCapacity(source.getCompanions().getOccupyCapacity());
        }
        return target;
    }

    public static LinkedSessionDTO fromMsLinkedSession(LinkedSession source) {
        LinkedSessionDTO target = new LinkedSessionDTO();
        if (source != null) {
            target.setId(source.getId());
            target.setName(source.getName());
            target.setColor(source.getColor());
        }
        return target;
    }

    public static SessionsGroupsDTO toDTO(SessionsGroups source) {
        return source.stream().map(SessionConverter::toDTO)
                .collect(Collectors.toCollection(SessionsGroupsDTO::new));
    }

    private static SessionsGroupDTO toDTO(SessionsGroup source) {
        SessionsGroupDTO target = new SessionsGroupDTO();
        target.setEndDate(source.getEndDate());
        target.setStartDate(source.getStartDate());
        target.setTotal(source.getTotal());
        return target;
    }

    public static VenueTagSeatDTO[] fromVenueTagRequest(SessionVenueTagSeatRequestDTO[] values) {
        VenueTagSeatDTO[] response = VenueTagConverter.fromVenueTagRequest(values);
        for (int i = 0; i < response.length; i++) {
            response[i].setGateUpdateType(values[i].getGateUpdateType());
        }
        return response;
    }

    public static VenueTagNotNumberedZoneDTO[] fromVenueTagRequest(SessionVenueTagNotNumberedZoneRequestDTO[] values) {
        VenueTagNotNumberedZoneDTO[] response = VenueTagConverter.fromVenueTagRequest(values);
        for (int i = 0; i < response.length; i++) {
            response[i].setGateUpdateType(values[i].getGateUpdateType());
        }
        return response;
    }

    public static List<RateDTO> toDTO(List<Rate> source) {
        return source.stream().map(SessionConverter::toDTO).collect(Collectors.toList());
    }

    private static RateDTO toDTO(Rate source) {
        RateDTO target = new RateDTO();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setDefaultRate(source.isDefaultRate());
        return target;
    }

    public static void fillSettingsSearchSessions(SearchSessionsDTO sessionTarget, Session sessionSource, SessionSearchFilter filter) {
        SettingsSessionsSearchDTO settings = new SettingsSessionsSearchDTO();
        boolean isSettingsEmpty = true;
        if (CollectionUtils.isNotEmpty(filter.getFields())
                && sessionSource.getIsSmartBooking() != null
                && filter.getFields().stream()
                .anyMatch(field -> SessionField.SMART_BOOKING_SETTING_RELATED_SESSION.getName().equals(field)
                        || SessionField.SMART_BOOKING_SETTING_STATUS.getName().equals(field))
        ) {
            settings.setSmartBookingDTO(getSessionSmartBookingDTO(sessionSource));
            isSettingsEmpty = false;
        }

        if (CollectionUtils.isNotEmpty(filter.getFields())
                && sessionSource.getEnableOrphanSeats() != null
                && filter.getFields().stream()
                .anyMatch(field -> SessionField.SETTINGS_ENABLE_ORPHAN_SEATS.getName().equals(field))
        ) {
            settings.setEnableOrphanSeats(sessionSource.getEnableOrphanSeats());
            isSettingsEmpty = false;
        }
        sessionTarget.setSettings(isSettingsEmpty ? null : settings);
    }

    private static LoyaltyPointsConfig toMs(CreateLoyaltyPointsConfigDTO in) {
        LoyaltyPointsConfig out = new LoyaltyPointsConfig();
        if (in.getPointGain() != null) {
            out.setPointGain(toMs(in.getPointGain()));
        }
        return out;
    }

    private static PointGain toMs(CreatePointGainDTO in) {
        PointGain out = new PointGain();
        out.setAmount(in.getAmount());
        out.setType(toMs(in.getType()));
        return out;
    }

    private static CreateSessionSettings toMs(CreateSessionSettingsDTO in) {
        CreateSessionSettings out = new CreateSessionSettings();
        out.setEnableOrphanSeats(in.getEnableOrphanSeats());
        return out;
    }

    private static SessionPointsType toMs(CreateSessionPointsTypeDTO in) {
        if (in == null) {
            return null;
        }
        return SessionPointsType.valueOf(in.name());
    }

    private static ZonedDateTime determineEndDate(CreateSessionDates dates, Entity entity) {
        if (dates.getEndDate() != null) {
            return dates.getEndDate();
        }
        if (dates.getStartDate() != null) {
            return endDateCalculator(dates.getStartDate(), entity);
        }
        return null;
    }

    private static ZonedDateTime endDateCalculator(ZonedDateTime startDate, Entity entity) {
        if (entity != null && entity.getSessionDuration() != null) {
            return startDate.plus(entity.getSessionDuration());
        } else {
            return startDate.plus(Duration.ofHours(2));
        }
    }

    public static CapacityRelocationRequest toMs(CapacityRelocationRequestDTO capacityRelocationRequest) {
        CapacityRelocationRequest result = new CapacityRelocationRequest();
        result.setSeats(toMs(capacityRelocationRequest.getSeats()));
        result.setUserId(SecurityUtils.getUserId());
        return result;
    }

    private static List<SeatRelocation> toMs(List<SeatRelocationDTO> seats) {
        return seats.stream().map(relocation -> {
            SeatRelocation result = new SeatRelocation();
            result.setSourceId(relocation.getSourceId());
            result.setDestinationId(relocation.getDestinationId());
            return result;
        }).collect(Collectors.toList());
    }
}
