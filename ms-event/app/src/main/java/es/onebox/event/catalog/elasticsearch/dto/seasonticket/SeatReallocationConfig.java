package es.onebox.event.catalog.elasticsearch.dto.seasonticket;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class SeatReallocationConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = -8740630340597281163L;

    private Boolean enabled;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private Integer releasedSeatQuotaId;
    private Double fixedSurcharge;
    private Integer maxChanges;
    private List<SeatReallocationPrice> prices;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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

    public Integer getReleasedSeatQuotaId() {
        return releasedSeatQuotaId;
    }

    public void setReleasedSeatQuotaId(Integer releasedSeatQuotaId) {
        this.releasedSeatQuotaId = releasedSeatQuotaId;
    }

    public Double getFixedSurcharge() {
        return fixedSurcharge;
    }

    public void setFixedSurcharge(Double fixedSurcharge) {
        this.fixedSurcharge = fixedSurcharge;
    }

    public Integer getMaxChanges() {
        return maxChanges;
    }

    public void setMaxChanges(Integer maxChanges) {
        this.maxChanges = maxChanges;
    }

    public List<SeatReallocationPrice> getPrices() {
        return prices;
    }

    public void setPrices(List<SeatReallocationPrice> prices) {
        this.prices = prices;
    }
}
