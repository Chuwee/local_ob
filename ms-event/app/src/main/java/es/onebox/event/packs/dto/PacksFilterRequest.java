package es.onebox.event.packs.dto;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.event.packs.enums.PackStatus;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@MaxLimit(1000)
@DefaultLimit(50)
public class PacksFilterRequest extends BaseRequestFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 5741460280472536623L;

    private Integer entityId;
    @NotNull
    private Integer operatorId;
    private List<Long> ids;
    private String name;
    private Integer eventId;
    private List<PackStatus> status;
    private SortOperator<String> sort;

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public List<PackStatus> getStatus() {
        return status;
    }

    public void setStatus(List<PackStatus> status) {
        this.status = status;
    }
}
