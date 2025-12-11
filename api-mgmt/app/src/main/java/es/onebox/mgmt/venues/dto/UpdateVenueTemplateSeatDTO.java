package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.venues.enums.UpdateSeatStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public class UpdateVenueTemplateSeatDTO extends BaseVenueTagDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("status")
    private UpdateSeatStatus status;

    @JsonProperty("blocking_reason")
    private Long blockingReason;

    @JsonProperty("quota")
    private Long quota;

    @JsonProperty("row_id")
    private Integer rowId;

    @JsonProperty("name")
    private String name;

    @JsonProperty("view_id")
    private Integer viewId;

    @JsonProperty("position_x")
    private Integer positionX;

    @JsonProperty("position_y")
    private Integer positionY;

    @JsonProperty("weight")
    private Integer weight;

    @JsonProperty("sort")
    private Integer sort;

    @JsonProperty("row_block")
    private String rowBlock;

    @JsonProperty("external_id")
    private Integer externalId;

    public UpdateSeatStatus getStatus() {
        return status;
    }

    public void setStatus(UpdateSeatStatus status) {
        this.status = status;
    }

    public Long getBlockingReason() {
        return blockingReason;
    }

    public void setBlockingReason(Long blockingReason) {
        this.blockingReason = blockingReason;
    }

    public Long getQuota() {
        return quota;
    }

    public void setQuota(Long quota) {
        this.quota = quota;
    }

    public Integer getRowId() {
        return rowId;
    }

    public void setRowId(Integer rowId) {
        this.rowId = rowId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getViewId() {
        return viewId;
    }

    public void setViewId(Integer viewId) {
        this.viewId = viewId;
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

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getRowBlock() {
        return rowBlock;
    }

    public void setRowBlock(String rowBlock) {
        this.rowBlock = rowBlock;
    }

    public Integer getExternalId() {
        return externalId;
    }

    public void setExternalId(Integer externalId) {
        this.externalId = externalId;
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
