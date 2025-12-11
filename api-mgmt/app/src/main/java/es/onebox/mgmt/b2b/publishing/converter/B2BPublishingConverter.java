package es.onebox.mgmt.b2b.publishing.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.b2b.publishing.dto.BaseSeatPublishingDTO;
import es.onebox.mgmt.b2b.publishing.dto.HistoricDTO;
import es.onebox.mgmt.b2b.publishing.dto.PublisherDataDTO;
import es.onebox.mgmt.b2b.publishing.dto.SeatDataDTO;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingDTO;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingsResponseDTO;
import es.onebox.mgmt.b2b.publishing.dto.SeatPublishingsSearchRequest;
import es.onebox.mgmt.b2b.publishing.dto.SessionDataDTO;
import es.onebox.mgmt.b2b.publishing.enums.PublishingFields;
import es.onebox.mgmt.b2b.publishing.enums.PublishingUserType;
import es.onebox.mgmt.b2b.publishing.enums.TicketStatus;
import es.onebox.mgmt.b2b.publishing.enums.TransactionType;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.BaseSeatPublishing;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.Historic;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishing;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishingsFilter;
import es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.SeatPublishingsResponse;
import es.onebox.mgmt.datasources.ms.client.dto.clients.Client;
import es.onebox.mgmt.datasources.ms.client.dto.clients.ClientUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.LongFunction;

public class B2BPublishingConverter {
    private B2BPublishingConverter(){
        throw new UnsupportedOperationException();
    }

    public static SeatPublishingsFilter toMsFilter(SeatPublishingsSearchRequest request) {
        SeatPublishingsFilter filter = new SeatPublishingsFilter();

        if (!CommonUtils.isEmpty(request.getEntityIds())) {
            filter.setEntityIds(request.getEntityIds());
        }
        if (!CommonUtils.isEmpty(request.getChannelIds())) {
            filter.setChannelIds(request.getChannelIds());
        }
        if (!CommonUtils.isEmpty(request.getClientEntityIds())) {
            filter.setClientEntityIds(request.getClientEntityIds());
        }
        if (!CommonUtils.isEmpty(request.getClientIds())) {
            filter.setClientIds(request.getClientIds());
        }
        if (!CommonUtils.isEmpty(request.getEventIds())) {
            filter.setEventIds(request.getEventIds());
        }
        if (!CommonUtils.isEmpty(request.getSessionIds())) {
            filter.setSessionIds(request.getSessionIds());
        }
        if (!CommonUtils.isEmpty(request.getTypes())) {
            filter.setTypes(request.getTypes().stream().map(B2BPublishingConverter::toMs).toList());
        }
        if (request.getDateFrom() != null) {
            filter.setDateFrom(request.getDateFrom());
        }
        if (request.getDateTo() != null) {
            filter.setDateTo(request.getDateTo());
        }
        if (request.getQ() != null) {
            filter.setQ(request.getQ());
        }
        if (request.getLimit() != null) {
            filter.setLimit(request.getLimit());
        }
        if (request.getOffset() != null) {
            filter.setOffset(request.getOffset());
        }
        if (request.getSort() != null){
            filter.setSort(ConverterUtils.checkSortFields(request.getSort(), PublishingFields::byName));
        }
        /*if (!CommonUtils.isEmpty(request.getStatus())) {
            filter.setStatus(request.getStatus().stream().map(B2BPublishingConverter::toMs).toList());
        }TODO BreakPoint Allow filtering when webhook or conciliation is made*/

        return filter;
    }

    public static SeatPublishingsResponseDTO toDtoList(SeatPublishingsResponse seatPublishingsResponse) {
        SeatPublishingsResponseDTO response = new SeatPublishingsResponseDTO();
        response.setMetadata(seatPublishingsResponse.getMetadata());
        response.setData(seatPublishingsResponse.getData().stream()
                .map(item -> B2BPublishingConverter.toBaseDto(new BaseSeatPublishingDTO(), item))
                .toList());
        return response;
    }

    private static <T extends BaseSeatPublishingDTO> T toBaseDto(T target, BaseSeatPublishing source) {
        target.setId(source.getId());
        target.setEvent(toIdNameDto(new IdNameDTO(), source.getEventId(), source.getAdditionalData().getEventName()));
        target.setChannel(toIdNameDto(new IdNameDTO(), source.getChannelId(), source.getAdditionalData().getChannelName()));
        target.setSession(toSessionDto(source));
        target.setSeat(toSeatDto(source));
        target.setPublisher(toPublisherDto(source));
        target.setPrice(source.getAdditionalData().getPrice());
        target.setType(fromMs(source.getType()));
        target.setDate(source.getDate());
        target.getSeat().setSeatStatus(fromMs(source.getAdditionalData().getStatus()));
        return target;
    }

    private static <T extends IdNameDTO> T toIdNameDto(T target, Integer id, String name) {
        target.setId(id == null ? null : id.longValue());
        target.setName(name);
        return target;
    }

    private static SessionDataDTO toSessionDto(BaseSeatPublishing source) {
        SessionDataDTO target = toIdNameDto(new SessionDataDTO(), source.getSessionId(), source.getAdditionalData().getSessionName());
        target.setDate(source.getAdditionalData().getSessionDate());
        return target;
    }

    private static SeatDataDTO toSeatDto(BaseSeatPublishing source) {
        SeatDataDTO target = new SeatDataDTO();
        target.setVenueName(source.getAdditionalData().getVenueName());
        target.setSectorName(source.getAdditionalData().getSectorName());
        target.setRowName(source.getAdditionalData().getRowName());
        target.setSeatName(source.getAdditionalData().getSeatName());
        target.setSeatId(source.getSeatId());
        target.setNotNumberedAreaId(source.getNotNumberedAreaId());
        target.setNotNumberedAreaName(source.getAdditionalData().getNotNumberedAreaName());
        return target;
    }


    public static SeatPublishingDTO toDto(SeatPublishing source,
                                          BiFunction<Long, Long, Client> clientBiFunction,
                                          LongFunction<ClientUser> userFunction) {
        SeatPublishingDTO target = toBaseDto(new SeatPublishingDTO(), source);
        target.setEntityId(source.getEntityId());
        target.setSourceQuotaId(source.getSourceQuotaId());
        target.setTargetQuotaId(source.getTargetQuotaId());
        target.setSourcePriceTypeId(source.getSourcePriceTypeId());
        target.setTargetPriceTypeId(source.getTargetPriceTypeId());
        target.setDate(source.getDate());
        target.setPublisher(toPublisherDto(source));
        target.setHistoric(toHistoricDto(source.getHistoric(), source.getEntityId(), clientBiFunction, userFunction));
        return target;
    }

    private static PublisherDataDTO toPublisherDto(SeatPublishing source) {
        PublisherDataDTO target = new PublisherDataDTO();
        target.setUserType(fromMs(source.getPublishingUserType()));
        target.setClientEntityId(source.getClientEntityId());
        target.setClientId(source.getClientId());
        target.setClientName(source.getAdditionalData().getClientName());
        target.setUserId(source.getUserId());
        target.setUsername(source.getAdditionalData().getUsername());
        return target;
    }

    private static PublisherDataDTO toPublisherDto(BaseSeatPublishing source) {
        PublisherDataDTO target = new PublisherDataDTO();
        target.setUserType(fromMs(source.getPublishingUserType()));
        target.setClientEntityId(source.getClientEntityId());
        target.setClientId(source.getClientId());
        target.setClientName(source.getAdditionalData().getClientName());
        target.setUserId(source.getUserId());
        target.setUsername(source.getAdditionalData().getUsername());
        return target;
    }

    private static List<HistoricDTO> toHistoricDto(List<Historic> source,
                                                   Integer entityId,
                                                   BiFunction<Long, Long, Client> clientBiFunction,
                                                   LongFunction<ClientUser> userFunction) {
        return  source == null ? new ArrayList<>()
                : source.stream()
                        .map(historic -> B2BPublishingConverter.toHistoricDto(historic, entityId, clientBiFunction, userFunction))
                        .sorted(Comparator.comparing(HistoricDTO::getDate).reversed())
                        .toList();
    }

    private static HistoricDTO toHistoricDto(Historic source,
                                             Integer entityId,
                                             BiFunction<Long, Long, Client> clientBiFunction,
                                             LongFunction<ClientUser> userFunction) {
        HistoricDTO dto = new HistoricDTO();
        dto.setPublisher(toPublisherDto(source, entityId, clientBiFunction, userFunction));
        dto.setSourceQuotaId(source.getSourceQuotaId());
        dto.setTargetQuotaId(source.getTargetQuotaId());
        dto.setSourcePriceTypeId(source.getSourcePriceTypeId());
        dto.setTargetPriceTypeId(source.getTargetPriceTypeId());
        dto.setDate(source.getDate());
        dto.setType(fromMs(source.getType()));
        dto.setSeatStatus(toDTO(source.getStatus()));
        dto.setPrice(source.getPrice());
        return dto;
    }

    private static PublisherDataDTO toPublisherDto(Historic source,
                                                   Integer entityId,
                                                   BiFunction<Long, Long, Client> clientBiFunction,
                                                   LongFunction<ClientUser> userFunction) {
        PublisherDataDTO target = new PublisherDataDTO();
        target.setUserType(fromMs(source.getUserType()));
        target.setClientEntityId(source.getClientEntityId());
        target.setClientId(source.getClientId());
        target.setUserId(source.getUserId());
        target.setClientName(clientBiFunction.apply(source.getClientId().longValue(), entityId.longValue()).getName());
        if (PublishingUserType.B2B.equals(target.getUserType())) {
            target.setUsername(userFunction.apply(source.getUserId()).getUsername());
        } else {
            //TODO next iteration, bring down the function to fetch a CPanel username from entitiesRepository
        }
        return target;
    }

    private static es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.TransactionType toMs(TransactionType type) {
        return type == null ? null
                            : Arrays.stream(es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.TransactionType.values())
                              .filter(msType -> msType.name().equals(type.name()))
                              .findFirst().orElse(null);
    }

    private static TransactionType fromMs(es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.TransactionType msType) {
        return msType == null ? null
                              : Arrays.stream(TransactionType.values())
                                .filter(type -> type.name().equals(msType.name()))
                                .findFirst().orElse(null);
    }

    private static es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.PublishingUserType toMs(PublishingUserType userType) {
        return userType == null ? null
                                : Arrays.stream(es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.PublishingUserType.values())
                                .filter(msType -> msType.name().equals(userType.name()))
                                .findFirst().orElse(null);
    }

    private static PublishingUserType fromMs(es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.PublishingUserType msType) {
        return msType == null ? null
                              : Arrays.stream(PublishingUserType.values())
                                .filter(userType -> userType.name().equals(msType.name()))
                                .findFirst().orElse(null);
    }

    private static es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.TicketStatus toMs(TicketStatus status) {
        return status == null ? null
                              : Arrays.stream(es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.TicketStatus.values())
                                .filter(msType -> msType.name().equals(status.name()))
                                .findFirst().orElse(null);
    }

    private static TicketStatus toDTO(es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.TicketStatus status) {
        return status == null ? null
                : Arrays.stream(TicketStatus.values())
                .filter(msType -> msType.name().equals(status.name()))
                .findFirst().orElse(null);
    }

    private static TicketStatus fromMs(es.onebox.mgmt.datasources.ms.client.dto.b2bpublishing.TicketStatus msType) {
        return msType == null ? null
                              : Arrays.stream(TicketStatus.values())
                                .filter(status -> status.name().equals(msType.name()))
                                .findFirst().orElse(null);
    }
}
