package es.onebox.fifaqatar.notification.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class NotificationMessageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 9093457688867364689L;

    private String code;
    @JsonProperty("previous_code")
    private String previousCode;
    @JsonProperty("item_id")
    private Long itemId;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPreviousCode() {
        return previousCode;
    }

    public void setPreviousCode(String previousCode) {
        this.previousCode = previousCode;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}
