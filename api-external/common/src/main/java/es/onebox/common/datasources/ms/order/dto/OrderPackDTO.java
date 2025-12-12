package es.onebox.common.datasources.ms.order.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * Created by abosch on 20/06/2024.
 */
public class OrderPackDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String code;
    private Boolean mainItem; //Only applies for automatic pack type

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
