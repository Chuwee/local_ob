package es.onebox.mgmt.datasources.ms.collective.dto.request;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class MsCreateCollectiveCodeDTO implements Serializable {

    private static final long serialVersionUID = 3911538575537303587L;

    @NotNull
    private String code;
    private String password;
    private Integer usageLimit;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
}
