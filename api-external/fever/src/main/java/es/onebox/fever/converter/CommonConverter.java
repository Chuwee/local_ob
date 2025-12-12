package es.onebox.fever.converter;

import es.onebox.common.datasources.webhook.dto.fever.FeverMessageDTO;
import es.onebox.common.datasources.webhook.dto.fever.NotificationMessageDTO;

public class CommonConverter {

  public static FeverMessageDTO convert(NotificationMessageDTO message){

    if (message == null) {
      return null;
    }

    FeverMessageDTO feverMessage = new FeverMessageDTO();

    feverMessage.setCode(message.getCode());
    feverMessage.setPromotionActive(message.getPromotionActive());
    feverMessage.setId(message.getId());
    feverMessage.setEmail(message.getEmail());
    feverMessage.setPromotionId(message.getPromotionId());
    feverMessage.setName(message.getName());
    feverMessage.setAllowCommercialMailing(message.getAllowCommercialMailing());
    feverMessage.setSessionId(message.getSessionId());
    feverMessage.setEventId(message.getEventId());
    feverMessage.setPrevOrderCode(message.getPrevOrderCode());
    feverMessage.setRateId(message.getRateId());
    feverMessage.setChannelId(message.getChannelId());
    feverMessage.setSurname(message.getSurname());
    feverMessage.setTemplateId(message.getTemplateId());
    feverMessage.setReimbursement(message.getReimbursement());
    feverMessage.setUrl(message.getUrl());
    feverMessage.setSessionIds(message.getSessionIds());

    return feverMessage;
  }
}
