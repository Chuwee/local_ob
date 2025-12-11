package es.onebox.mgmt.venues.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class CreateVenueTemplateSeatDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "row_id must not be null")
    @Min(value = 1, message = "row_id must be above 0")
    @JsonProperty("row_id")
    private Integer rowId;

    @NotBlank(message = "name must not be blank")
    @JsonProperty("name")
    private String name;

    @NotNull(message = "view_id must not be null")
    @Min(value = 1L, message = "view_id must be above 0")
    @JsonProperty("view_id")
    private Long viewId;

    @NotNull(message = "position_x must not be null")
    @JsonProperty("position_x")
    private Integer positionX;

    @NotNull(message = "position_y must not be null")
    @JsonProperty("position_y")
    private Integer positionY;

    @NotNull(message = "weight must not be null")
    @JsonProperty("weight")
    private Integer weight;

    @NotNull(message = "sort must not be null")
    @JsonProperty("sort")
    private Integer sort;

    @NotNull(message = "row_block must not be null")
    @JsonProperty("row_block")
    private String rowBlock;

    @JsonProperty("external_id")
    private Long externalId;

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

    public Long getViewId() {
        return viewId;
    }

    public void setViewId(Long viewId) {
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

    public Long getExternalId() {
        return externalId;
    }

    public void setExternalId(Long externalId) {
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
