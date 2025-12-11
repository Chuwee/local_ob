package es.onebox.mgmt.seasontickets.dto.renewals;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.AutomaticRenewalStatus;
import es.onebox.mgmt.datasources.ms.event.dto.seasonticket.RenewalGenerationStatus;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class SeasonTicketRenewalSeatsSummaryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("origin_season_ticket_id")
    private Long originSeasonTicketId;
    @JsonProperty("origin_season_ticket_name")
    private String originSeasonTicketName;
    @JsonProperty("renewal_import_date")
    private ZonedDateTime renewalImportDate;
    @JsonProperty("mapped_imports")
    private Integer mappedImports;
    @JsonProperty("not_mapped_imports")
    private Integer notMappedImports;
    @JsonProperty("total_imports")
    private Integer totalRenewals;
    @JsonProperty("generation_status")
    private RenewalGenerationStatus generationStatus;
    @JsonProperty("automatic_renewal_status")
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