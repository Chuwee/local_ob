package es.onebox.mgmt.datasources.ms.event.dto.packs;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.packs.enums.PackStatus;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PacksFilterRequest extends BaseRequestFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 875022906583831695L;

    private Long entityId;
    @NotNull
    private Long operatorId;
    private String name;
    private Integer eventId;
    private List<PackStatus> status;
    private SortOperator<String> sort;

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
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
