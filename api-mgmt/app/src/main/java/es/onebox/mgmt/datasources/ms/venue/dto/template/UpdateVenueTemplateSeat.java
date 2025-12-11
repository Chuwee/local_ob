package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.io.Serial;

public class UpdateVenueTemplateSeat extends VenueTagDTO {

    @Serial
    private static final long serialVersionUID = 1L;

    private UpdateSeatStatus status;
    private Long blockingReason;
    private Long quota;

    private Integer rowId;
    private String name;
    private Integer viewId;
    private Integer positionX;
    private Integer positionY;
    private Integer weight;
    private Integer sort;
    private String rowBlock;
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
}
