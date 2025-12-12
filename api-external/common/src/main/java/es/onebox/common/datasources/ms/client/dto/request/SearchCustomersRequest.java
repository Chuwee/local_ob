package es.onebox.common.datasources.ms.client.dto.request;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SearchCustomersRequest  implements Serializable {

    @Serial
    private static final long serialVersionUID = 2166076440799189427L;

    private Integer entityId;
    private String originId;
    private String email;
    private Integer limit;
    private Integer offset;
    private List<String> externalCustomerIds;

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getExternalCustomerIds() {
        return externalCustomerIds;
    }

    public void setExternalCustomerIds(List<String> externalCustomerIds) {
        this.externalCustomerIds = externalCustomerIds;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
