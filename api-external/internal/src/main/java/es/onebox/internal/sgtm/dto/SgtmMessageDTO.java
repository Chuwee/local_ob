package es.onebox.internal.sgtm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.utils.GeneratorUtils;
import es.onebox.internal.sgtm.domains.FacebookExternalToolParams;
import es.onebox.internal.sgtm.domains.GoogleExternalToolParams;

import java.util.List;

public class SgtmMessageDTO {

    @JsonProperty("code")
    private String code;

    @JsonProperty("action")
    private String action;

    @JsonProperty("event")
    private String event;

    @JsonProperty("orderDetail")
    private Object orderDetail;

    @JsonProperty("deliveryId")
    private String deliveryId;

    @JsonProperty("hookId")
    private String hookId;

    private List<FacebookExternalToolParams> sgtmFacebookCredentials;

    private List<GoogleExternalToolParams> sgtmGoogleCredentials;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Object getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(Object orderDetail) {
        this.orderDetail = orderDetail;
    }

    public String getSignature() {
        return GeneratorUtils.getHashSHA256(this.code);
    }

    public String getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        this.deliveryId = deliveryId;
    }

    public String getHookId() {
        return hookId;
    }

    public void setHookId(String hookId) {
        this.hookId = hookId;
    }

    public List<FacebookExternalToolParams> getSgtmFacebookCredentials() {
        return sgtmFacebookCredentials;
    }

    public void setSgtmFacebookCredentials(List<FacebookExternalToolParams> sgtmFacebookCredentials) {
        this.sgtmFacebookCredentials = sgtmFacebookCredentials;
    }

    public List<GoogleExternalToolParams> getSgtmGoogleCredentials() {
        return sgtmGoogleCredentials;
    }

    public void setSgtmGoogleCredentials(List<GoogleExternalToolParams> sgtmGoogleCredentials) {
        this.sgtmGoogleCredentials = sgtmGoogleCredentials;
    }
}