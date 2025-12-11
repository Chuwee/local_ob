package es.onebox.event.datasources.ms.entity.dto.request;

import java.io.Serial;
import java.io.Serializable;

public class ProducersRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -3519392198718954908L;

    private Integer id;
    private Boolean includeDeleted;
    private Long limit;
    private Long offset;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getIncludeDeleted() {
        return includeDeleted;
    }

    public void setIncludeDeleted(Boolean includeDeleted) {
        this.includeDeleted = includeDeleted;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }
}
