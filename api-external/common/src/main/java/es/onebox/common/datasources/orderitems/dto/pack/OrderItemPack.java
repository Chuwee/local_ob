package es.onebox.common.datasources.orderitems.dto.pack;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class OrderItemPack implements Serializable {

    @Serial
    private static final long serialVersionUID = 6339209096558880697L;

    @JsonProperty("id")
    private Long id;
    @JsonProperty("code")
    private String code;
    @JsonProperty("main_item")
    private Boolean mainItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Boolean getMainItem() {
        return mainItem;
    }

    public void setMainItem(Boolean mainItem) {
        this.mainItem = mainItem;
    }
}
