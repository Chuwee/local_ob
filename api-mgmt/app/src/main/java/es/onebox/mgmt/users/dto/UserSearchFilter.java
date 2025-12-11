package es.onebox.mgmt.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.core.serializer.validation.DefaultLimit;
import es.onebox.core.serializer.validation.MaxLimit;
import es.onebox.mgmt.common.BaseEntityRequestFilter;
import es.onebox.mgmt.users.enums.UserStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

@MaxLimit(1000)
@DefaultLimit(50)
public class UserSearchFilter extends BaseEntityRequestFilter {

    private static final long serialVersionUID = 1L;

    private List<UserStatus> status;
    @JsonProperty("q")
    private String freeSearch;
    private SortOperator<String> sort;

    private List<String> roles;
    private List<String> permissions;

    @JsonProperty("operator_id")
    private Long operatorId;

    public List<UserStatus> getStatus() {
        return status;
    }

    public void setStatus(List<UserStatus> status) {
        this.status = status;
    }

    public String getFreeSearch() {
        return freeSearch;
    }

    public void setFreeSearch(String freeSearch) {
        this.freeSearch = freeSearch;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
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
