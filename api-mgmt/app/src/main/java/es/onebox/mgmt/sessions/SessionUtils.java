package es.onebox.mgmt.sessions;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventType;
import es.onebox.mgmt.datasources.ms.event.dto.event.Provider;
import es.onebox.mgmt.datasources.ms.event.dto.session.Rate;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.BlockingReason;
import es.onebox.mgmt.datasources.ms.venue.dto.template.PriceType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Quota;
import es.onebox.mgmt.datasources.ms.venue.dto.template.TagWithGroup;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateType;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.entities.dto.EntityDTO;
import es.onebox.mgmt.events.enums.AttendantTicketsChannelScopeTypeDTO;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.exception.ApiMgmtSessionErrorCode;
import es.onebox.mgmt.sessions.dto.AttendantTicketsSessionStatusDTO;
import es.onebox.mgmt.sessions.dto.CreateSessionRequestDTO;
import es.onebox.mgmt.sessions.dto.RateDTO;
import es.onebox.mgmt.sessions.dto.SessionAttendantTicketsDTO;
import es.onebox.mgmt.sessions.dto.SessionSaleType;
import es.onebox.mgmt.sessions.dto.UpdateSessionPreSaleDTO;
import es.onebox.mgmt.sessions.enums.SessionStatus;
import es.onebox.mgmt.venues.dto.BaseVenueTagDTO;
import es.onebox.mgmt.venues.dto.VenueTagBlockingReasonCounterDTO;
import es.onebox.mgmt.venues.dto.VenueTagNotNumberedZoneRequestDTO;
import es.onebox.mgmt.venues.dto.VenueTagQuotaCounterDTO;
import es.onebox.mgmt.venues.dto.VenueTagSeatRequestDTO;
import es.onebox.mgmt.venues.dto.VenueTagStatusCounterDTO;
import es.onebox.mgmt.venues.enums.SeatStatus;
import es.onebox.mgmt.venues.enums.VenueTagStatus;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static es.onebox.mgmt.exception.ApiMgmtErrorCode.BAD_REQUEST_PARAMETER;
import static es.onebox.mgmt.venues.enums.VenueTagStatus.PROMOTOR_LOCKED;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class SessionUtils {

    public static final String GATE_GROUP_CODE = "ACCESO";

    private SessionUtils() {
    }

    public static void validateVenueTags(BaseVenueTagDTO[] capacities) {
        Set<Long> capacityIds = Arrays.stream(capacities).map(BaseVenueTagDTO::getId).collect(Collectors.toSet());
        if (capacityIds.size() != capacities.length) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "ids cant be repeated", null);
        }
        if (capacityIds.stream().anyMatch(Objects::isNull)) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER, "id is mandatory", null);
        }
    }

    public static void validateVenueTagIds(Long venueTemplateId, VenueTagSeatRequestDTO[] venueTags, VenuesRepository venuesRepository) {
        List<Long> priceTypes = null;
        List<Long> quotas = null;
        List<Long> blockingReasons = null;
        List<TagWithGroup> gatesAndTags = null;
        for (VenueTagSeatRequestDTO venueTag : venueTags) {
            priceTypes = validatePriceType(venueTag, venueTemplateId, priceTypes, venuesRepository);
            quotas = validateQuota(venueTag, venueTemplateId, quotas, venuesRepository);
            blockingReasons = validateSeatBlockingReason(venueTag, venueTemplateId, blockingReasons, venuesRepository);
            gatesAndTags = validateGateAndDynamicTags(venueTag, venueTemplateId, gatesAndTags, venuesRepository);
        }
    }

    public static void validateVenueTagIds(Long venueTemplateId, VenueTagNotNumberedZoneRequestDTO[] capacities,
                                           VenuesRepository venuesRepository) {
        List<Long> priceTypes = null;
        List<Long> quotas = null;
        List<Long> blockingReasons = null;
        List<TagWithGroup> gatesAndTags = null;
        for (VenueTagNotNumberedZoneRequestDTO capacity : capacities) {
            priceTypes = validatePriceType(capacity, venueTemplateId, priceTypes, venuesRepository);
            quotas = validateNNZQuotas(capacity, venueTemplateId, quotas, venuesRepository);
            blockingReasons = validateNNZBlockingReason(capacity, venueTemplateId, blockingReasons, venuesRepository);
            blockingReasons = validateNNZStatus(capacity, venueTemplateId, blockingReasons, venuesRepository);
            gatesAndTags = validateGateAndDynamicTags(capacity, venueTemplateId, gatesAndTags, venuesRepository);
        }
    }

    //Check target for session pack child session to update pack linked seats to new status/blocking_reason
    public static Long validateSessionPackSeatTarget(Session session, String target, VenuesRepository venuesRepository) {
        Long targetId = null;
        if (SessionType.SESSION.equals(session.getSessionType()) && !CommonUtils.isEmpty(session.getSeasonIds())) {
            if (target == null) {
                throw new OneboxRestException(BAD_REQUEST_PARAMETER,
                        "Target is required for season pack session clone", null);
            }
            targetId = validateSessionPackChange(session, target, null, venuesRepository);
        }
        return targetId;
    }

    public static Long validateSessionPackChange(Session session, String seatChange, Long quotaId, VenuesRepository venuesRepository) {
        Long targetId = null;
        if (StringUtils.isNumeric(seatChange)) {
            targetId = Long.parseLong(seatChange);
            List<Long> blockingReasons = venuesRepository.getBlockingReasons(session.getVenueConfigId()).stream().
                    map(BlockingReason::getId).collect(Collectors.toList());
            if (!blockingReasons.contains(targetId)) {
                throw new OneboxRestException(BAD_REQUEST_PARAMETER,
                        "Invalid target blocking_reason for session with id: " + session.getId() + ", blockingReasonId: " + targetId, null);
            }
        } else if ((seatChange != null && !seatChange.equals(SeatStatus.FREE.name())) ||
                (seatChange == null && session.getSessionType() == SessionType.SEASON_RESTRICTIVE)) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "Invalid value " +
                    "for session pack change. FREE or number values accepted", null);
        }
        if (quotaId != null) {
            List<Quota> quotas = venuesRepository.getQuotas(session.getVenueConfigId());
            if (quotas.stream().noneMatch(q -> q.getId().equals(quotaId))) {
                throw new OneboxRestException(BAD_REQUEST_PARAMETER,
                        "Invalid target quotaId for session with id: " + session.getId() + ", quotaId: " + quotaId, null);
            }
        }
        return targetId;
    }

    public static boolean isSessionPack(SessionType type) {
        return SessionType.SEASON_RESTRICTIVE.equals(type) || SessionType.SEASON_FREE.equals(type);
    }

    public static boolean isActivitySession(Session session) {
        return ((EventType.ACTIVITY.equals(session.getEventType()) || EventType.THEME_PARK.equals(session.getEventType())) &&
                session.getSaleType() != null) || VenueTemplateType.ACTIVITY.getId().equals(session.getVenueConfigTemplateType());
    }

    public static boolean isActivitySessionWithGroups(Session session) {
        return session.getSaleType() != null && (SessionSaleType.byId(session.getSaleType()).equals(SessionSaleType.GROUP) ||
                SessionSaleType.byId(session.getSaleType()).equals(SessionSaleType.MIXED));
    }

    public static void checkRates(List<RateDTO> rates) {
        long defaultRates = rates.stream().filter(r -> CommonUtils.isTrue(r.getDefaultRate())).count();
        if (CommonUtils.isEmpty(rates)) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_RATE, "At least 1 rate must be selected", null);
        }
        if (defaultRates != 1) {
            throw new OneboxRestException(ApiMgmtErrorCode.INVALID_RATE, "One of the selected rates must be marked as default", null);
        }
    }

    public static boolean isUpdatingDate(ZonedDateTime originalValue, ZonedDateTime newValue) {
        if (newValue == null) {
            return false;
        }
        if (originalValue == null) {
            return true;
        }
        return OffsetDateTime.of(originalValue.toLocalDateTime(), originalValue.getOffset())
                .compareTo(OffsetDateTime.of(newValue.toLocalDateTime(), newValue.getOffset())) != 0;
    }

    public static boolean isUpdatingAVETRatesValid(Collection<Rate> originalRates, Collection<RateDTO> newRates) {
        if (newRates == null) {
            return false;
        }
        if (originalRates == null) {
            return true;
        }
        Rate defaultOriginalRate = originalRates.stream().filter(Rate::isDefaultRate).findFirst().orElse(null);
        if(defaultOriginalRate == null){
            return true;
        }
        RateDTO defaultNewRate = newRates.stream().filter(RateDTO::getDefaultRate).findFirst().orElse(null);
        if(defaultNewRate == null){
            return true;
        }
        boolean defaultRatesAreEquals = defaultOriginalRate.getId().equals(defaultNewRate.getId());

        boolean originalRateNotDefault = originalRates
                .stream()
                .filter(rate -> !rate.getId().equals(defaultOriginalRate.getId()))
                .noneMatch(Rate::isDefaultRate);

        boolean newRateNotDefault = newRates
                .stream()
                .filter(rate -> !rate.getId().equals(defaultNewRate.getId()))
                .noneMatch(rate -> rate.getDefaultRate() != null && rate.getDefaultRate()) ;

        return !(defaultRatesAreEquals && originalRateNotDefault && newRateNotDefault);
    }

    public static void validateSessionPreSaleConfig(Session session, EntityDTO entity, UpdateSessionPreSaleDTO request) {
        if (isNull(request)) {
            throw new OneboxRestException(ApiMgmtErrorCode.BAD_REQUEST_PARAMETER);
        }

        if (session.getEventType().equals(EventType.AVET)) {
            if (nonNull(request.getMemberTicketsLimit())) {
                throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_PRESALE_MEMBER_TICKETS_LIMIT_NOT_UPDATABLE,
                        "member_tickets_limit cannot be updated on an AVET event.", null);
            }
            if (nonNull(request.getGeneralTicketsLimit())) {
                throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_PRESALE_GENERAL_TICKETS_LIMIT_NOT_UPDATABLE,
                        "general_tickets_limit cannot be updated on an AVET event.", null);
            }
        }

        if (nonNull(request.getLoyaltyProgram())) {
            if (nonNull(entity.getSettings()) && BooleanUtils.isNotTrue(entity.getSettings().getAllowLoyaltyPoints())) {
                throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_PRESALE_LOYALTY_PROGRAM_NOT_ALLOWED);
            }

            if (BooleanUtils.isTrue(request.getLoyaltyProgram().getEnabled()) && request.getLoyaltyProgram().getPoints() == null) {
                throw new OneboxRestException(BAD_REQUEST_PARAMETER, "points can not be null", null);
            }
        }

        if (request.getPresaleSettingsDTO() != null
            && request.getPresaleSettingsDTO().getMultiplePurchase() != null
            && BooleanUtils.isFalse(session.getIsSmartBooking())) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.SESSION_PRESALE_ALLOW_MULTI_PURCHASE_NOT_UPDATABLE);
        }

    }

    public static boolean isAttendantTicketInvalid(SessionAttendantTicketsDTO attendantTickets) {
        return AttendantTicketsSessionStatusDTO.EVENT_CONFIG.equals(attendantTickets.getStatus())
                && (attendantTickets.getAutofill() != null || attendantTickets.getChannelsScope() != null);
    }

    public static void validateAttendantsUpdate(SessionAttendantTicketsDTO attendant) {
        if (AttendantTicketsSessionStatusDTO.DISABLED.equals(attendant.getStatus()) && attendant.getChannelsScope() != null) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.ATTENDANCE_DATA_CONFLICT,
                    "set to disabled but channel scope information is included");
        }
        if (attendant.getChannelsScope() != null
                && AttendantTicketsChannelScopeTypeDTO.ALL.equals(attendant.getChannelsScope().getType())) {
            if (CollectionUtils.isNotEmpty(attendant.getChannelsScope().getChannels())) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.ATTENDANCE_DATA_CONFLICT, "set to all channels but contains a channel list");
            }
            if (BooleanUtils.isTrue(attendant.getChannelsScope().getAddNewEventChannelRelationships())) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.ATTENDANCE_DATA_CONFLICT, "set to all channels auto-add flag included");
            }
        }
    }

    public static boolean isAvetEvent(EventType type) {
        return EventType.AVET.equals(type);
    }

    public static boolean isSgaEvent(Provider provider) {
        return Provider.SGA.equals(provider);
    }


    public static void checkAvetMatch(CreateSessionRequestDTO sessionData) {
        if (sessionData.getAdditionalConfig() == null || sessionData.getAdditionalConfig().getAvetMatchId() == null) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "avet_match_id is mandatory for AVET events.", null);
        }
    }

    public static void checkSessionPack(Session session) {
        if (!isSessionPack(session.getSessionType())) {
            throw new OneboxRestException(ApiMgmtSessionErrorCode.INVALID_SESSION_PACK_TYPE);
        }
    }

    private static List<Long> validatePriceType(BaseVenueTagDTO seat, Long venueTemplateId, List<Long> priceTypes,
                                                VenuesRepository venuesRepository) {
        if (seat.getPriceType() != null) {
            if (priceTypes == null) {
                priceTypes = venuesRepository.getPriceTypes(venueTemplateId).stream().
                        map(PriceType::getId).collect(Collectors.toList());
            }
            if (!priceTypes.contains(seat.getPriceType())) {
                throw new OneboxRestException(BAD_REQUEST_PARAMETER,
                        "Invalid price_type (" + seat.getPriceType() + ") for seat " + seat.getId(), null);
            }
        }
        return priceTypes;
    }

    private static List<Long> validateNNZQuotas(VenueTagNotNumberedZoneRequestDTO requestDTO, Long venueTemplateId, List<Long> quotas,
                                                VenuesRepository venuesRepository) {
        if (requestDTO.getQuotaCounters() != null && !requestDTO.getQuotaCounters().isEmpty()) {
            if (quotas == null) {
                quotas = venuesRepository.getQuotas(venueTemplateId).stream().
                        map(Quota::getId).collect(Collectors.toList());
            }
            List<Long> quotasOnRequest = requestDTO.getQuotaCounters().stream()
                    .map(VenueTagQuotaCounterDTO::getQuota)
                    .toList();
            if (!quotas.containsAll(quotasOnRequest)) {
                throw new OneboxRestException(BAD_REQUEST_PARAMETER,
                        "Some invalid quotas for session with ids: " + quotasOnRequest, null);
            }
        }
        return quotas;
    }

    private static List<Long> validateQuota(VenueTagSeatRequestDTO seat, Long venueTemplateId, List<Long> quotas,
                                            VenuesRepository venuesRepository) {
        if (seat.getQuota() != null) {
            if (quotas == null) {
                quotas = venuesRepository.getQuotas(venueTemplateId).stream().
                        map(Quota::getId).collect(Collectors.toList());
            }
            if (!quotas.contains(seat.getQuota())) {
                throw new OneboxRestException(BAD_REQUEST_PARAMETER,
                        "Invalid quota for session with id: " + seat.getQuota(), null);
            }
        }
        return quotas;
    }

    private static List<TagWithGroup> validateGateAndDynamicTags(BaseVenueTagDTO seat, Long venueTemplateId, List<TagWithGroup> gateAndTags,
                                                                 VenuesRepository venuesRepository) {
        if (seat.getGate() != null) {
            if (gateAndTags == null) {
                gateAndTags = venuesRepository.getTags(venueTemplateId);
            }
            if (gateAndTags.stream().filter(t -> t.getGroupCode().equals(GATE_GROUP_CODE)).noneMatch(t -> t.getId().equals(seat.getGate()))) {
                throw new OneboxRestException(BAD_REQUEST_PARAMETER,
                        "Invalid gate for session with id: " + seat.getGate(), null);
            }
        }
        if (seat.getDynamicTag1() != null && seat.getDynamicTag1() == -1) {
            seat.setDynamicTag1(null);
        }
        if (seat.getDynamicTag2() != null && seat.getDynamicTag2() == -1) {
            seat.setDynamicTag2(null);
        }
        if (seat.getDynamicTag1() != null || seat.getDynamicTag2() != null) {

            if (gateAndTags == null) {
                gateAndTags = venuesRepository.getTags(venueTemplateId);
            }
            Map<Long, Set<Long>> dynamicTagsByGroup = gateAndTags.stream().
                    filter(t -> !t.getGroupCode().equals(GATE_GROUP_CODE)).
                    collect(Collectors.groupingBy(
                            TagWithGroup::getGroupId,
                            LinkedHashMap::new,
                            Collectors.mapping(TagWithGroup::getId, Collectors.toSet())));

            Iterator<Map.Entry<Long, Set<Long>>> tagsGroupIterator = dynamicTagsByGroup.entrySet().iterator();
            if (seat.getDynamicTag1() != null && (!tagsGroupIterator.hasNext() ||
                    !tagsGroupIterator.next().getValue().contains(seat.getDynamicTag1()))) {
                throw new OneboxRestException(BAD_REQUEST_PARAMETER,
                        "Invalid dynamicTag1 for session with id: " + seat.getDynamicTag1(), null);
            }
            if (seat.getDynamicTag2() != null) {
                if (seat.getDynamicTag1() == null && tagsGroupIterator.hasNext()) {
                    tagsGroupIterator.next();
                }
                if (!tagsGroupIterator.hasNext() || !tagsGroupIterator.next().getValue().contains(seat.getDynamicTag2())) {
                    throw new OneboxRestException(BAD_REQUEST_PARAMETER,
                            "Invalid dynamicTag2 for session with id: " + seat.getDynamicTag2(), null);
                }
            }
        }
        return gateAndTags;
    }

    private static List<Long> validateSeatBlockingReason(VenueTagSeatRequestDTO seatCapacity,
                                                         Long venueTemplateId, List<Long> blockingReasons,
                                                         VenuesRepository venuesRepository) {
        if ((seatCapacity.getStatus() != null && seatCapacity.getStatus().equals(PROMOTOR_LOCKED) && seatCapacity.getBlockingReason() == null) ||
                (seatCapacity.getStatus() != null && !seatCapacity.getStatus().equals(PROMOTOR_LOCKED) && seatCapacity.getBlockingReason() != null) ||
                (seatCapacity.getStatus() == null && seatCapacity.getBlockingReason() != null)
        ) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER,
                    "PROMOTOR_LOCKED status is required if blocking_reason is defined", null);
        }
        if (seatCapacity.getBlockingReason() != null) {
            if (blockingReasons == null) {
                blockingReasons = venuesRepository.getBlockingReasons(venueTemplateId).stream().
                        map(BlockingReason::getId).collect(Collectors.toList());
            }
            if (!blockingReasons.contains(seatCapacity.getBlockingReason())) {
                throw new OneboxRestException(BAD_REQUEST_PARAMETER,
                        "Invalid target blocking_reason for session with id: " + seatCapacity.getBlockingReason(), null);
            }
        }
        return blockingReasons;
    }

    private static List<Long> validateNNZBlockingReason(VenueTagNotNumberedZoneRequestDTO capacity, Long venueTemplateId,
                                                        List<Long> blockingReasons, VenuesRepository venuesRepository) {
        if (!CommonUtils.isEmpty(capacity.getBlockingReasonCounters())) {
            for (VenueTagBlockingReasonCounterDTO brCounter : capacity.getBlockingReasonCounters()) {
                Long sourceId = validateNNZSource(brCounter.getSource());
                if (blockingReasons == null) {
                    blockingReasons = venuesRepository.getBlockingReasons(venueTemplateId).stream().
                            map(BlockingReason::getId).collect(Collectors.toList());
                }
                if (sourceId != null && !blockingReasons.contains(sourceId)) {
                    throw new OneboxRestException(BAD_REQUEST_PARAMETER,
                            "Invalid source blocking_reason for session with value: " + brCounter.getSource(), null);
                } else if (!blockingReasons.contains(brCounter.getBlockingReason())) {
                    throw new OneboxRestException(BAD_REQUEST_PARAMETER,
                            "Invalid target blocking_reason for session with value: " + brCounter.getBlockingReason(), null);
                }
            }
        }
        return blockingReasons;
    }

    private static Long validateNNZSource(String source) {
        Long sourceId = null;
        if (source == null) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER, "Source is required for blocking_reason update", null);
        }
        if (StringUtils.isNumeric(source)) {
            sourceId = Long.parseLong(source);
        } else if (!source.equals(VenueTagStatus.FREE.name()) && !source.equals(VenueTagStatus.KILL.name())) {
            throw new OneboxRestException(BAD_REQUEST_PARAMETER,
                    "Invalid source value " + "for blocking_reason update. FREE/KILL or number values accepted", null);
        }
        return sourceId;
    }

    private static List<Long> validateNNZStatus(VenueTagNotNumberedZoneRequestDTO nnzCapacity, Long venueTemplateId, List<Long> blockingReasons, VenuesRepository venuesRepository) {
        if (!CommonUtils.isEmpty(nnzCapacity.getStatusCounters())) {
            for (VenueTagStatusCounterDTO statusCounter : nnzCapacity.getStatusCounters()) {
                Long sourceId = validateNNZSource(statusCounter.getSource());
                if (sourceId != null && blockingReasons == null) {
                    blockingReasons = venuesRepository.getBlockingReasons(venueTemplateId).stream().
                            map(BlockingReason::getId).collect(Collectors.toList());
                    if (!blockingReasons.contains(sourceId)) {
                        throw new OneboxRestException(BAD_REQUEST_PARAMETER,
                                "Invalid source blocking_reason for session with value: " + statusCounter.getSource(), null);
                    }
                }
            }
        }
        return blockingReasons;
    }

    public static List<SessionStatus> notFinalized() {
        return Stream.of(SessionStatus.values()).filter(v -> !SessionStatus.FINALIZED.equals(v)).toList();
    }

}
