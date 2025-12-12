package es.onebox.channels.catalog.eci.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import es.onebox.channels.catalog.eci.ZonedDateTimeSerialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

public class ECIEventDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private List<ECIEventDetailDTO> events;
    private ECITypeDTO type = new ECITypeDTO();
    private String url;
    private Map<String, String> description;
    private Map<String, String> image;
    @JsonProperty("multilingual_name")
    private Map<String, String> multilingualName;
    @JsonProperty("start_sale_date")
    @JsonSerialize(using = ZonedDateTimeSerialize.class)
    private ZonedDateTime startSaleDate;
    @JsonProperty("end_sale_date")
    @JsonSerialize(using = ZonedDateTimeSerialize.class)
    private ZonedDateTime endSaleDate;
    private List<ECIPromotionDTO> promotions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ECIEventDetailDTO> getEvents() {
        return events;
    }

    public void setEvents(List<ECIEventDetailDTO> events) {
        this.events = events;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getDescription() {
        return description;
    }

    public void setDescription(Map<String, String> description) {
        this.description = description;
    }

    public Map<String, String> getImage() {
        return image;
    }

    public void setImage(Map<String, String> image) {
        this.image = image;
    }

    public Map<String, String> getMultilingualName() {
        return multilingualName;
    }

    public void setMultilingualName(Map<String, String> multilingualName) {
        this.multilingualName = multilingualName;
    }

    public ZonedDateTime getStartSaleDate() {
        return startSaleDate;
    }

    public void setStartSaleDate(ZonedDateTime startSaleDate) {
        this.startSaleDate = startSaleDate;
    }

    public ZonedDateTime getEndSaleDate() {
        return endSaleDate;
    }

    public void setEndSaleDate(ZonedDateTime endSaleDate) {
        this.endSaleDate = endSaleDate;
    }

    public ECITypeDTO getType() {
        return type;
    }

    public void setType(ECITypeDTO type) {
        this.type = type;
    }

    public List<ECIPromotionDTO> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<ECIPromotionDTO> promotions) {
        this.promotions = promotions;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
