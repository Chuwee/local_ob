package es.onebox.event.catalog.dto.venue.container.pricetype;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VenuePriceType implements Serializable {

    @Serial
    private static final long serialVersionUID = -4999810133340991451L;

    private Long id;
    private String name;
    private String code;
    private String color;
    private Integer order;
    private List<VenuePriceTypeCommElement> commElements;
    private Map<String, Object> additionalProperties;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public List<VenuePriceTypeCommElement> getCommElements() {
        return commElements;
    }

    public void setCommElements(List<VenuePriceTypeCommElement> commElements) {
        this.commElements = commElements;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}
