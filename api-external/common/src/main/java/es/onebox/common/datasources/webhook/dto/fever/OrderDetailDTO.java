package es.onebox.common.datasources.webhook.dto.fever;

import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonNaming(SnakeCaseStrategy.class)
public class OrderDetailDTO implements Serializable {

    private String code;
    private String prevCode;
    private String url;
    private Boolean reimburse;
    private double value;
    private List<Long> idProducts = new ArrayList<>();

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrevCode() {
        return prevCode;
    }

    public void setPrevCode(String prevCode) {
        this.prevCode = prevCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getReimburse() {
        return reimburse;
    }

    public void setReimburse(Boolean reimburse) {
        this.reimburse = reimburse;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public List<Long> getProducts() {
        return idProducts;
    }

    public void setProducts(List<Long> products) {
        this.idProducts = products;
    }
}

