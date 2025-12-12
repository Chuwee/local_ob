package es.onebox.exchange.dto;

import java.io.Serial;
import java.io.Serializable;

public class ExchangeError implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

     private Integer code;
     private String info;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
