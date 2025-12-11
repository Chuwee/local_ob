package es.onebox.mgmt.packs.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.mgmt.common.BaseEntityRequestFilter;
import es.onebox.mgmt.packs.enums.PackStatus;
import es.onebox.mgmt.packs.enums.PackTypeDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@DefaultLimit(50)
public class PacksFilterDTO extends BaseEntityRequestFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;

    @JsonProperty("operator_id")
    private Long operatorId;
    private List<PackTypeDTO> type;
    private List<PackStatus> status;
    @JsonProperty("q")
    private String name;
    private SortOperator<String> sort;
    private Integer eventId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PackStatus> getStatus() {
        return status;
    }

    public void setStatus(List<PackStatus> status) {
        this.status = status;
    }

    public List<PackTypeDTO> getType() {
        return type;
    }

    public void setType(List<PackTypeDTO> type) {
        this.type = type;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
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
