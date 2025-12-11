package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.venue.dto.template.AccessibilityType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VisibilityType;
import es.onebox.mgmt.venues.enums.SeatStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class VenueTemplateBaseSeatDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("status")
    private SeatStatus status;

    @JsonProperty("row_id")
    private Long rowId;

    @JsonProperty("price_zone_id")
    private Long priceZoneId;

    @JsonProperty("quota_id")
    private Long quotaId;

    @JsonProperty("view_id")
    private Long viewId;

    @JsonProperty("visibility")
    private VisibilityType visibility;

    @JsonProperty("accessibility")
    private AccessibilityType accessibility;

    @JsonProperty("row_block")
    private String rowBlock;

    @JsonProperty("sort")
    private Integer sort;

    @JsonProperty("weight")
    private Integer weight;

    @JsonProperty("external_id")
    private Long externalId;

    @JsonProperty("position_x")
    private Integer positionX;

    @JsonProperty("position_y")
    private Integer positionY;

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

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public Long getRowId() {
        return rowId;
    }

    public void setRowId(Long rowId) {
        this.rowId = rowId;
    }

    public Long getPriceZoneId() {
        return priceZoneId;
    }

    public void setPriceZoneId(Long priceZoneId) {
        this.priceZoneId = priceZoneId;
    }

    public Long getQuotaId() {
        return quotaId;
    }

    public void setQuotaId(Long quotaId) {
        this.quotaId = quotaId;
    }

    public Long getViewId() {
        return viewId;
    }

    public void setViewId(Long viewId) {
        this.viewId = viewId;
    }

    public VisibilityType getVisibility() {
        return visibility;
    }

    public void setVisibility(VisibilityType visibility) {
        this.visibility = visibility;
    }

    public AccessibilityType getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(AccessibilityType accessibility) {
        this.accessibility = accessibility;
    }

    public String getRowBlock() {
        return rowBlock;
    }

    public void setRowBlock(String rowBlock) {
        this.rowBlock = rowBlock;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
        this.externalId = externalId;
    }

    public Integer getPositionX() {
        return positionX;
    }

    public void setPositionX(Integer positionX) {
        this.positionX = positionX;
    }

    public Integer getPositionY() {
        return positionY;
    }

    public void setPositionY(Integer positionY) {
        this.positionY = positionY;
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
