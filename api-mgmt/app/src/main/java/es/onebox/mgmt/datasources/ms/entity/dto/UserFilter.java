package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.core.security.Roles;
import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.core.serializer.dto.request.SortOperator;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class UserFilter extends BaseRequestFilter {

    private static final long serialVersionUID = 1L;

    private List<Long> id;
    private String username;
    private String email;
    private String apiKey;
    private String password;
    private Long operatorId;
    private String operator;
    private Long entityId;
    private Long entityAdminId;
    private List<Integer> status;
    private String freeSearch;
    private SortOperator<String> sort;
    private Roles role;
    private List<String> roles;
    private List<String> permissions;

    private UserFilter(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.email = builder.email;
        this.apiKey = builder.apiKey;
        this.password = builder.password;
        this.operatorId = builder.operatorId;
        this.operator = builder.operator;
        this.entityId = builder.entityId;
        this.entityAdminId = builder.entityAdminId;
        this.status = builder.status;
        this.freeSearch = builder.freeSearch;
        this.sort = builder.sort;
        this.role = builder.role;
        this.roles = builder.roles;
        this.permissions = builder.permissions;
        this.setLimit(builder.limit);
        this.setOffset(builder.offset);
    }

    public UserFilter() {
    }

    public List<Long> getId() {
        return id;
    }

    public void setId(List<Long> id) {
        this.id = id;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getEntityAdminId() { return entityAdminId; }

    public void setEntityAdminId(Long entityAdminId) { this.entityAdminId = entityAdminId; }

    public List<Integer> getStatus() {
        return status;
    }

    public void setStatus(List<Integer> status) {
        this.status = status;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
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

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builderFrom(UserFilter userFilter) {
        return new Builder(userFilter);
    }

    public static final class Builder {
        private List<Long> id;
        private String username;
        private String email;
        private String apiKey;
        private String password;
        private Long operatorId;
        private String operator;
        private Long entityId;
        private Long entityAdminId;
        private List<Integer> status;
        private String freeSearch;
        private SortOperator<String> sort;
        private Roles role;
        private List<String> roles;
        private List<String> permissions;
        private Long limit;
        private Long offset;

        private Builder() {
        }

        private Builder(UserFilter userFilter) {
            this.id = userFilter.id;
            this.username = userFilter.username;
            this.email = userFilter.email;
            this.apiKey = userFilter.apiKey;
            this.password = userFilter.password;
            this.operatorId = userFilter.operatorId;
            this.operator = userFilter.operator;
            this.entityId = userFilter.entityId;
            this.status = userFilter.status;
            this.freeSearch = userFilter.freeSearch;
            this.sort = userFilter.sort;
            this.role = userFilter.role;
            this.roles = userFilter.roles;
            this.permissions = userFilter.permissions;
            this.limit = userFilter.getLimit();
            this.offset = userFilter.getOffset();
        }

        public Builder id(List<Long> id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }
        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder operatorId(Long operatorId) {
            this.operatorId = operatorId;
            return this;
        }

        public Builder operator(String operator) {
            this.operator = operator;
            return this;
        }

        public Builder entityId(Long entityId) {
            this.entityId = entityId;
            return this;
        }
        public Builder entityAdminId(Long entityAdminId) {
            this.entityAdminId = entityAdminId;
            return this;
        }
        public Builder status(List<Integer> status) {
            this.status = status;
            return this;
        }

        public Builder freeSearch(String freeSearch) {
            this.freeSearch = freeSearch;
            return this;
        }

        public Builder sort(SortOperator<String> sort) {
            this.sort = sort;
            return this;
        }

        public Builder role(Roles role) {
            this.role = role;
            return this;
        }

        public Builder roles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public Builder permissions(List<String> permissions) {
            this.permissions = permissions;
            return this;
        }

        public Builder limit(Long limit) {
            this.limit = limit;
            return this;
        }

        public Builder offset(Long offset) {
            this.offset = offset;
            return this;
        }
        public UserFilter build() {
            return new UserFilter(this);
        }
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
