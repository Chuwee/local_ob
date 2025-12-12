package es.onebox.fever.service;

import es.onebox.common.datasources.ms.channel.MsChannelDatasource;
import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.ms.channel.enums.WhitelabelType;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.event.dto.EventChannelsDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.promotion.dto.CommunicationElementDTO;
import es.onebox.common.datasources.ms.promotion.dto.EventPromotionPriceTypesDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionChannelsDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionDetailDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionEventSessionsDTO;
import es.onebox.common.datasources.ms.promotion.dto.PromotionRatesDTO;
import es.onebox.common.datasources.ms.promotion.enums.PromotionTargetType;
import es.onebox.common.datasources.ms.promotion.repository.PromotionChannelRepository;
import es.onebox.common.datasources.webhook.dto.fever.EventUpdate;
import es.onebox.common.datasources.webhook.dto.fever.WebhookFeverDTO;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.fever.converter.CommonConverter;
import es.onebox.fever.converter.PromotionConverter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PromotionWebhookService {

  private final PromotionChannelRepository promotionChannelRepository;
  private final MsChannelDatasource msChannelDatasource;
  private final MsEventRepository msEventRepository;
  private final ChannelRepository channelRepository;

  @Autowired
  public PromotionWebhookService(PromotionChannelRepository promotionChannelRepository,
      MsChannelDatasource msChannelDatasource, MsEventRepository msEventRepository,
      ChannelRepository channelRepository) {
    this.promotionChannelRepository = promotionChannelRepository;
    this.msChannelDatasource = msChannelDatasource;
    this.msEventRepository = msEventRepository;
    this.channelRepository = channelRepository;
  }

  public WebhookFeverDTO sendPromotionDetail(
      WebhookFeverDTO webhookFeverDTO) {

    validateAllowedEntitiesWithEventChannel(webhookFeverDTO);
    if (!webhookFeverDTO.getAllowSend()){
      return webhookFeverDTO;
    }

    PromotionDetailDTO promotionDetails = promotionChannelRepository.getEventPromotion(Long.valueOf(webhookFeverDTO.getNotificationMessage()
        .getId()), webhookFeverDTO.getNotificationMessage().getPromotionId());

    EventUpdate eventUpdate = new EventUpdate();
    eventUpdate.setPromotionDetails(PromotionConverter.convert(promotionDetails));

    webhookFeverDTO.setFeverMessage(CommonConverter.convert(webhookFeverDTO.getNotificationMessage()));
    webhookFeverDTO.getFeverMessage().setEventUpdate(eventUpdate);

    return webhookFeverDTO;
  }

  public WebhookFeverDTO sendPromotionChannel(WebhookFeverDTO webhookFeverDTO) {

    PromotionChannelsDTO promotionChannels = promotionChannelRepository.getEventPromotionChannel(Long.valueOf(webhookFeverDTO.getNotificationMessage()
        .getId()), webhookFeverDTO.getNotificationMessage().getPromotionId());

    List<ChannelDTO> channelsAllowed = validateAllowedEntities(promotionChannels, webhookFeverDTO);
    List<Long> channelAllowedIds = PromotionConverter.convertToId(channelsAllowed);

    if (!webhookFeverDTO.getAllowSend()){
      return webhookFeverDTO;
    }

    if (!channelAllowedIds.isEmpty()) {
      promotionChannels.setChannels(promotionChannels.getChannels().stream().filter(channel -> channelAllowedIds.contains(channel.getId())).collect(
          Collectors.toSet()));
    }

    EventUpdate eventUpdate = new EventUpdate();
    eventUpdate.setPromotionChannels(PromotionConverter.convert(promotionChannels));

    webhookFeverDTO.setFeverMessage(CommonConverter.convert(webhookFeverDTO.getNotificationMessage()));
    webhookFeverDTO.getFeverMessage().setEventUpdate(eventUpdate);

    return webhookFeverDTO;
  }

  public WebhookFeverDTO sendPromotionSession(WebhookFeverDTO webhookFeverDTO) {

    validateAllowedEntitiesWithEventChannel(webhookFeverDTO);

    if (!webhookFeverDTO.getAllowSend()){
      return webhookFeverDTO;
    }

    PromotionEventSessionsDTO promotionSessions = promotionChannelRepository.getEventPromotionSession(Long.valueOf(webhookFeverDTO.getNotificationMessage()
        .getId()), webhookFeverDTO.getNotificationMessage().getPromotionId());


    EventUpdate eventUpdate = new EventUpdate();
    eventUpdate.setPromotionSessions(PromotionConverter.convert(promotionSessions));

    webhookFeverDTO.setFeverMessage(CommonConverter.convert(webhookFeverDTO.getNotificationMessage()));
    webhookFeverDTO.getFeverMessage().setEventUpdate(eventUpdate);

    return webhookFeverDTO;
  }

  public WebhookFeverDTO sendPromotionPriceTypes(WebhookFeverDTO webhookFeverDTO){

    validateAllowedEntitiesWithEventChannel(webhookFeverDTO);

    if (!webhookFeverDTO.getAllowSend()){
      return webhookFeverDTO;
    }

    EventPromotionPriceTypesDTO promotionPriceTypes = promotionChannelRepository.getEventPromotionPriceTypes(
        Long.valueOf(webhookFeverDTO.getNotificationMessage().getId()),
        webhookFeverDTO.getNotificationMessage().getPromotionId());


    EventUpdate eventUpdate = new EventUpdate();
    eventUpdate.setPromotionPriceTypes(PromotionConverter.convert(promotionPriceTypes));

    webhookFeverDTO.setFeverMessage(CommonConverter.convert(webhookFeverDTO.getNotificationMessage()));
    webhookFeverDTO.getFeverMessage().setEventUpdate(eventUpdate);

    return webhookFeverDTO;
  }

  public WebhookFeverDTO sendPromotionRates(WebhookFeverDTO webhookFeverDTO){

    validateAllowedEntitiesWithEventChannel(webhookFeverDTO);

    if (!webhookFeverDTO.getAllowSend()){
      return webhookFeverDTO;
    }

    PromotionRatesDTO promotionRates = promotionChannelRepository.getEventPromotionRates(
        Long.valueOf(webhookFeverDTO.getNotificationMessage().getId()),
        webhookFeverDTO.getNotificationMessage().getPromotionId());


    EventUpdate eventUpdate = new EventUpdate();
    eventUpdate.setPromotionRates(PromotionConverter.convert(promotionRates));

    webhookFeverDTO.setFeverMessage(CommonConverter.convert(webhookFeverDTO.getNotificationMessage()));
    webhookFeverDTO.getFeverMessage().setEventUpdate(eventUpdate);

    return webhookFeverDTO;
  }

  public WebhookFeverDTO sendPromotionChannelDetail(WebhookFeverDTO webhookFeverDTO){

    validateAllowedEntitiesWithEventChannel(webhookFeverDTO);

    if (!webhookFeverDTO.getAllowSend()){
      return webhookFeverDTO;
    }

    List<CommunicationElementDTO> promotionChannelDetails = promotionChannelRepository.getEventCommunicationElements(
        Long.valueOf(webhookFeverDTO.getNotificationMessage().getId()),
        webhookFeverDTO.getNotificationMessage().getPromotionId());

    EventUpdate eventUpdate = new EventUpdate();
    eventUpdate.setPromotionChannelDetails(PromotionConverter.convert(promotionChannelDetails));

    webhookFeverDTO.setFeverMessage(CommonConverter.convert(webhookFeverDTO.getNotificationMessage()));
    webhookFeverDTO.getFeverMessage().setEventUpdate(eventUpdate);

    return webhookFeverDTO;
  }

  public WebhookFeverDTO sendPromotionLimits(WebhookFeverDTO webhookFeverDTO) {
    validateAllowedEntitiesWithEventChannel(webhookFeverDTO);

    if (!webhookFeverDTO.getAllowSend()){
      return webhookFeverDTO;
    }

    webhookFeverDTO.setFeverMessage(CommonConverter.convert(webhookFeverDTO.getNotificationMessage()));
    return webhookFeverDTO;
  }

  private void validateAllowedEntitiesWithEventChannel(WebhookFeverDTO webhookFever){

    Long eventId = webhookFever.getNotificationMessage().getEventId()!= null ? webhookFever.getNotificationMessage().getEventId()
            : Long.valueOf(webhookFever.getNotificationMessage().getId());
    Long promotionId = webhookFever.getNotificationMessage().getPromotionId();

    PromotionChannelsDTO promotionChannelsDTO = Optional.ofNullable(
            promotionChannelRepository.getEventPromotionChannel(eventId, promotionId))
        .orElseThrow(() -> ExceptionBuilder.build(
            ApiExternalErrorCode.PROMOTION_CHANNEL_NOT_FOUND));

    if (!PromotionTargetType.ALL.equals(promotionChannelsDTO.getType())){
      webhookFever.setAllowSend(Optional.ofNullable(
              promotionChannelRepository.getEventPromotionChannel(eventId, promotionId))
          .orElseThrow(() -> ExceptionBuilder.build(
              ApiExternalErrorCode.PROMOTION_CHANNEL_NOT_FOUND)).getChannels().stream()
          .anyMatch(channel -> {
            ChannelDTO channelFromMS = channelRepository.getChannel(channel.getId());
            return WhitelabelType.EXTERNAL
                .equals(channelFromMS.getWhitelabelType())
                || channelFromMS.getEntityId().equals(
                webhookFever.getAllowedEntitiesFileData().getEntityId());
          }));
    }
  }

  private List<ChannelDTO> validateAllowedEntities(PromotionChannelsDTO promotionChannels, WebhookFeverDTO webhookFever){

    List<ChannelDTO> channelList = new ArrayList<>();

    if (promotionChannels!= null && promotionChannels.getChannels() != null
        && !PromotionTargetType.ALL.equals(promotionChannels.getType())){
      promotionChannels.getChannels().forEach(channel -> channelList.add(channelRepository.getChannel(channel.getId())));

      List<ChannelDTO> channelAllowedList = channelList.stream().filter(channelDTO ->
          (webhookFever.getAllowedEntitiesFileData().getAllowedEntities().contains(channelDTO.getEntityId())
              && WhitelabelType.EXTERNAL.equals(channelDTO.getWhitelabelType())
              || webhookFever.getAllowedEntitiesFileData().getEntityId().equals(channelDTO.getEntityId()))).toList();

      webhookFever.setAllowSend(!channelAllowedList.isEmpty());

      return channelAllowedList;
    } else if (promotionChannels != null && PromotionTargetType.ALL.equals(promotionChannels.getType())){


      EventChannelsDTO eventChannels = msEventRepository.getEventChannels(
          Long.valueOf(webhookFever.getNotificationMessage().getId()));

      if (eventChannels == null) {
        webhookFever.setAllowSend(false);
        return channelList;
      }

      Set<IdNameDTO> channels = new HashSet<>();

      eventChannels.getData().forEach(eventChannel -> {
        ChannelDTO channel = msChannelDatasource.getChannel(eventChannel.getChannel().getId());
        if ((webhookFever.getAllowedEntitiesFileData().getAllowedEntities().contains(
            channel.getEntityId()) && WhitelabelType.EXTERNAL.equals(channel.getWhitelabelType()))
            || channel.getEntityId().equals(webhookFever.getAllowedEntitiesFileData().getEntityId())) {
          IdNameDTO idNameDTO = new IdNameDTO();
          idNameDTO.setId(channel.getId());
          idNameDTO.setName(channel.getName());
          channels.add(idNameDTO);
        }
      });

      promotionChannels.setChannels(channels);

      webhookFever.setAllowSend(!promotionChannels.getChannels().isEmpty());
      return channelList;

    } else {
      webhookFever.setAllowSend(false);
      return channelList;
    }
  }
}
