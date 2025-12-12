package es.onebox.common.datasources.webhook.dto.fever;

import jakarta.servlet.http.HttpServletRequest;

public class WebhookFeverDTO {

  private NotificationMessageDTO notificationMessage;
  private FeverMessageDTO feverMessage;
  private HttpServletRequest headers;
  private AllowedEntitiesFileData allowedEntitiesFileData;
  private Boolean allowSend;

  public WebhookFeverDTO(NotificationMessageDTO notificationMessage, HttpServletRequest headers, FeverMessageDTO feverMessage) {
    this.notificationMessage = notificationMessage;
    this.headers = headers;
    this.feverMessage = feverMessage;
  }

  public NotificationMessageDTO getNotificationMessage() {
    return notificationMessage;
  }

  public void setNotificationMessage(
      NotificationMessageDTO notificationMessage) {
    this.notificationMessage = notificationMessage;
  }

  public HttpServletRequest getHeaders() {
    return headers;
  }

  public void setHeaders(HttpServletRequest headers) {
    this.headers = headers;
  }

  public FeverMessageDTO getFeverMessage() {
    return feverMessage;
  }

  public void setFeverMessage(
      FeverMessageDTO feverMessage) {
    this.feverMessage = feverMessage;
  }

  public Boolean getAllowSend() {
    return allowSend;
  }

  public void setAllowSend(Boolean allowSend) {
    this.allowSend = allowSend;
  }

  public AllowedEntitiesFileData getAllowedEntitiesFileData() {
    return allowedEntitiesFileData;
  }

  public void setAllowedEntitiesFileData(
      AllowedEntitiesFileData allowedEntitiesFileData) {
    this.allowedEntitiesFileData = allowedEntitiesFileData;
  }
}
