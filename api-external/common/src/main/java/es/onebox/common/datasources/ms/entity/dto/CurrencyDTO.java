package es.onebox.common.datasources.ms.entity.dto;

import java.io.Serial;
import java.io.Serializable;

public class CurrencyDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    private Integer id;
    private String value;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
