package es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals;

import es.onebox.core.serializer.dto.request.BaseRequestFilter;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.TerminalState;
import es.onebox.mgmt.datasources.ms.entity.enums.terminals.TerminalType;

import java.io.Serial;
import java.util.List;

public class TerminalSearchFilter extends BaseRequestFilter {

    @Serial
    private static final long serialVersionUID = 6812860852117229427L;

    private List<Integer> id;
    private List<String> code;
    private List<Long> entityId;
    private Long operatorId;
    private TerminalState state;
    private List<TerminalType> type;
    private Boolean licenseEnabled;
    private String freeSearch;

    public List<Integer> getId() {
        return id;
    }

    public void setId(List<Integer> id) {
        this.id = id;
    }

    public List<String> getCode() {
        return code;
    }

    public void setCode(List<String> code) {
        this.code = code;
    }

    public List<Long> getEntityId() {
        return entityId;
    }

    public void setEntityId(List<Long> entityId) {
        this.entityId = entityId;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public TerminalState getState() {
        return state;
    }

    public void setState(TerminalState state) {
        this.state = state;
    }

    public List<TerminalType> getType() {
        return type;
    }

    public void setType(List<TerminalType> type) {
        this.type = type;
    }

    public Boolean getLicenseEnabled() {
        return licenseEnabled;
    }

    public void setLicenseEnabled(Boolean licenseEnabled) {
        this.licenseEnabled = licenseEnabled;
    }

    public String getFreeSearch() {
        return freeSearch;
    }

    public void setFreeSearch(String freeSearch) {
        this.freeSearch = freeSearch;
    }
}
