package es.onebox.fever.service;

import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.ms.channel.dto.ChannelEventDTO;
import es.onebox.common.datasources.ms.channel.dto.ChannelFormsResponse;
import es.onebox.common.datasources.ms.channel.dto.MsSaleRequestDTO;
import es.onebox.common.datasources.ms.channel.dto.MsSaleRequestsFilter;
import es.onebox.common.datasources.ms.channel.dto.MsSaleRequestsResponseDTO;
import es.onebox.common.datasources.ms.channel.enums.ChannelEventStatus;
import es.onebox.common.datasources.ms.channel.enums.EventStatus;
import es.onebox.common.datasources.ms.channel.enums.MsSaleRequestsStatus;
import es.onebox.common.datasources.ms.channel.enums.WhitelabelType;
import es.onebox.common.datasources.ms.channel.repository.ChannelEventRepository;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.webhook.dto.fever.SaleRequestStatus;
import es.onebox.common.datasources.webhook.dto.fever.SaleRequestStatusDTO;
import es.onebox.common.datasources.webhook.dto.fever.WebhookFeverDTO;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.MapUtils;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.fever.converter.ChannelConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChannelWebhookService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelWebhookService.class);

    private final ChannelRepository channelRepository;
    private final ChannelEventRepository channelEventRepository;
    private final MsEventRepository eventRepository;

    private final Integer MAX_RETRIES = 10;
    private static final long RETRY_DELAY = 15000;

    @Autowired
    public ChannelWebhookService(ChannelRepository channelRepository, ChannelEventRepository channelEventRepository,
                                 MsEventRepository eventRepository) {
        this.channelRepository = channelRepository;
        this.channelEventRepository = channelEventRepository;
        this.eventRepository = eventRepository;
    }

    public WebhookFeverDTO sendChannelFormUpdateDetail(WebhookFeverDTO webhookFever) {
        Long channelId = getChannelAndValidateAllowedEntities(webhookFever);
        if(!webhookFever.getAllowSend()){
            return webhookFever;
        }

        ChannelFormsResponse form = channelRepository.getChannelFormByType(channelId, "default");
        webhookFever.getFeverMessage().setChannelFormDetailDTO(ChannelConverter.mapResponseToDetailDTO(form));
        webhookFever.getFeverMessage().setChannelId(channelId);
        return webhookFever;
    }

    private Long getChannelAndValidateAllowedEntities(WebhookFeverDTO webhookFever) {
        Long channelId = Long.valueOf(webhookFever.getNotificationMessage().getId());
        ChannelDTO channelFromMS = channelRepository.getChannelNoCache(channelId);

        if (webhookFever.getAllowedEntitiesFileData().getEntityId().equals(channelFromMS.getEntityId())){
            webhookFever.setAllowSend(true);
            return channelId;
        }

        if (webhookFever.getAllowedEntitiesFileData().getAllowedEntities().stream()
                .anyMatch(entity -> entity.equals(channelFromMS.getEntityId()))
                && WhitelabelType.EXTERNAL.equals(channelFromMS.getWhitelabelType())) {
            webhookFever.setAllowSend(true);
            return channelId;
        }

        webhookFever.setAllowSend(false);
        return channelId;
    }

    public WebhookFeverDTO sendChannelSaleRequestUpdateDetail(WebhookFeverDTO webhookFever) {
        Long channelId = getChannelAndValidateAllowedEntities(webhookFever);
        if(!webhookFever.getAllowSend()){
            return webhookFever;
        }

        ChannelEventDTO channelEventRelationship = channelEventRepository.getChannelEventRelationship(channelId, webhookFever.getNotificationMessage().getEventId());

        boolean eventMigrated =  isEventMigrated(channelEventRelationship);

        if (Boolean.FALSE.equals(eventMigrated)) {
            throw new OneboxRestException(ApiExternalErrorCode.EVENT_UPDATE_NOT_AVAILABLE);
        }

        SaleRequestStatusDTO saleRequestStatusDTO = new SaleRequestStatusDTO();
        saleRequestStatusDTO.setChannelId(channelEventRelationship.getChannelId());
        saleRequestStatusDTO.setEventId(channelEventRelationship.getEventId());
        saleRequestStatusDTO.setStatus(SaleRequestStatus.valueOf(channelEventRelationship.getStatus().name()));

        webhookFever.getFeverMessage().setSaleRequestStatusDTO(saleRequestStatusDTO);
        return webhookFever;
    }

    public WebhookFeverDTO sendChannelRequiredEventsUpdateDetail(WebhookFeverDTO webhookFever) {
        Long channelId = getChannelAndValidateAllowedEntities(webhookFever);
        if (!webhookFever.getAllowSend()) {
            return webhookFever;
        }

        List<Long> validEventIds = getValidEventIds(channelId);

        Map<Integer, List<Integer>> filteredRequiredEvents = getFilteredRequiredEvents(channelId, validEventIds);
        Map<Integer, List<Integer>> reversedRequiredEvents = reverseRequiredEvents(validEventIds, filteredRequiredEvents);

        webhookFever.getFeverMessage().setChannelId(channelId);
        webhookFever.getFeverMessage().setRequiredEvents(reversedRequiredEvents);

        return webhookFever;
    }

    private List<Long> getValidEventIds(Long channelId) {
        final Long count = 50L;
        Long offset = 0L;
        List<Long> eventIds = new ArrayList<>();
        MsSaleRequestsFilter saleRequestsFilter = new MsSaleRequestsFilter();
        saleRequestsFilter.setChannelId(List.of(channelId));
        saleRequestsFilter.setStatus(List.of(MsSaleRequestsStatus.ACCEPTED));
        saleRequestsFilter.setEventStatus(List.of(EventStatus.IN_PROGRAMMING, EventStatus.READY));

        MsSaleRequestsResponseDTO response;
        do {
            saleRequestsFilter.setOffset(offset);
            saleRequestsFilter.setLimit(count);

            response = channelRepository.getSaleRequests(saleRequestsFilter);
            List<MsSaleRequestDTO> currentBatch = response.getData();

            if (currentBatch != null) {
                eventIds.addAll(
                        currentBatch.stream()
                                .map(saleRequest -> saleRequest.getEvent().getId())
                                .toList()
                );
            }

            offset += count;

        } while (offset < response.getMetadata().getTotal());

        return eventIds;
    }

    private Map<Integer, List<Integer>> getFilteredRequiredEvents(Long channelId, List<Long> validEventIds) {
        Map<Integer, List<Integer>> requiredEvents = channelRepository.getEventSaleRestrictions(channelId);
        return requiredEvents.entrySet().stream()
                .filter(entry -> validEventIds.contains(entry.getKey().longValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map<Integer, List<Integer>> reverseRequiredEvents(List<Long> eventIds, Map<Integer, List<Integer>> requiredEvents) {
        Map<Integer, List<Integer>> reversed = MapUtils.reverseMap(requiredEvents);
        eventIds.forEach(eventId -> reversed.putIfAbsent(eventId.intValue(), new ArrayList<>()));
        return reversed;
    }



    private Boolean isEventMigrated(ChannelEventDTO channelEventRelationship) {
        boolean eventMigrated = Boolean.FALSE;
        int counter = 0;
        long eventId = channelEventRelationship.getEventId();
        long channelId = channelEventRelationship.getChannelId();

        do {
            es.onebox.common.datasources.ms.event.dto.ChannelEventDTO channelEventDTO = eventRepository.getChannelEvent(eventId, channelId);
            if (channelEventDTO != null && channelEventRelationship.getStatus().equals(ChannelEventStatus.fromStatusId(channelEventDTO.getChannelEventStatus()))) {
                eventMigrated = Boolean.TRUE;
            } else {
                try {
                    counter ++;
                    LOGGER.info("[FEVER WEBHOOK] - SALE_REQUEST_STATUS attempt {} of {}. Waiting...", counter, MAX_RETRIES);
                    Thread.sleep(RETRY_DELAY);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } while (counter < MAX_RETRIES && Boolean.FALSE.equals(eventMigrated));

        return eventMigrated;
    }
}
