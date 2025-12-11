package es.onebox.mgmt.datasources.ms.event.dto.seasonticket;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class SeasonTicketRenewalSeatsSummary implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long originSeasonTicketId;
    private String originSeasonTicketName;
    private ZonedDateTime renewalImportDate;
    private Integer mappedImports;
    private Integer notMappedImports;
    private Integer totalRenewals;
    private RenewalGenerationStatus generationStatus;
    private AutomaticRenewalStatus automaticRenewalStatus;

    public Long getOriginSeasonTicketId() {
        return originSeasonTicketId;
    }

    public void setOriginSeasonTicketId(Long originSeasonTicketId) {
        this.originSeasonTicketId = originSeasonTicketId;
    }

    public String getOriginSeasonTicketName() {
        return originSeasonTicketName;
    }

    public void setOriginSeasonTicketName(String originSeasonTicketName) {
        this.originSeasonTicketName = originSeasonTicketName;
    }

    public ZonedDateTime getRenewalImportDate() {
        return renewalImportDate;
    }

    public void setRenewalImportDate(ZonedDateTime renewalImportDate) {
        this.renewalImportDate = renewalImportDate;
    }

    public Integer getMappedImports() {
        return mappedImports;
    }

    public void setMappedImports(Integer mappedImports) {
        this.mappedImports = mappedImports;
    }

    public Integer getNotMappedImports() {
        return notMappedImports;
    }

    public void setNotMappedImports(Integer notMappedImports) {
        this.notMappedImports = notMappedImports;
    }

    public Integer getTotalRenewals() {
        return totalRenewals;
    }

    public void setTotalRenewals(Integer totalRenewals) {
        this.totalRenewals = totalRenewals;
    }

    public RenewalGenerationStatus getGenerationStatus() {
        return generationStatus;
    }

    public void setGenerationStatus(RenewalGenerationStatus generationStatus) {
        this.generationStatus = generationStatus;
    }

    public AutomaticRenewalStatus getAutomaticRenewalStatus() {
        return automaticRenewalStatus;
    }

    public void setAutomaticRenewalStatus(AutomaticRenewalStatus automaticRenewalStatus) {
        this.automaticRenewalStatus = automaticRenewalStatus;
    }
}
