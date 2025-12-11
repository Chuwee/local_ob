package es.onebox.mgmt.datasources.ms.collective.dto;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class MsCollectiveCodeDTO  implements Serializable {

    private static final long serialVersionUID = 3361521140252957068L;

    private Integer collectiveId;
    private String code;
    private CollectiveValidationMethod type;
    private String password;
    private Integer usageLimit;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private Integer usages;

    public Integer getCollectiveId() {
        return collectiveId;
    }

    public void setCollectiveId(Integer collectiveId) {
        this.collectiveId = collectiveId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CollectiveValidationMethod getType() {
        return type;
    }

    public void setType(CollectiveValidationMethod type) {
        this.type = type;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getUsages() {
        return usages;
    }

    public void setUsages(Integer usages) {
        this.usages = usages;
    }
}
