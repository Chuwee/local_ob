package es.onebox.event.events.utils;

import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.converter.TierConverter;
import es.onebox.event.events.dto.TierCondition;
import es.onebox.event.events.dto.TierDTO;
import es.onebox.jooq.cpanel.tables.records.CpanelTierRecord;

import jakarta.validation.constraints.NotNull;
import java.time.Instant;

public class EvaluableTierWrapper {

    private final boolean isRecord;

    private CpanelTierRecord tierRecord;
    private TierDTO tierDTO;
    private boolean active;
    private Long stock;

    public EvaluableTierWrapper(@NotNull CpanelTierRecord tier) {
        this.tierRecord = tier;
        this.isRecord = true;
    }

    public EvaluableTierWrapper(@NotNull TierDTO tier) {
        this.tierDTO = tier;
        this.isRecord = false;
    }

    public Integer getId() {
        if (isRecord) {
            return tierRecord.getIdtier();
        }
        return ConverterUtils.longToInt(tierDTO.getId());
    }

    public Integer getPriceTypeId() {
        if (isRecord) {
            return tierRecord.getIdzona();
        }
        return ConverterUtils.longToInt(tierDTO.getPriceTypeId());
    }

    public Instant getStartDate() {
        if (isRecord) {
            return tierRecord.getFechaInicio().toInstant();
        }
        return tierDTO.getStartDate().toInstant();
    }

    public TierCondition getCondition() {
        if (isRecord) {
            return TierCondition.getById(tierRecord.getCondicion());
        }
        return tierDTO.getCondition();
    }

    public TierDTO getTierDTO() {
        if (isRecord) {
            return TierConverter.convert(tierRecord);
        }
        return tierDTO;
    }

    public CpanelTierRecord getTierRecord() {
        if (isRecord) {
            return tierRecord;
        }
        return TierConverter.convert(tierDTO);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        if (!isRecord) {
            this.tierDTO.setActive(active);
            this.active = active;
        }
        this.active = active;
    }

    public Long getStock() {
        return stock;
    }

    public void setStock(Long stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "EvaluableTierWrapper{" +
                "isRecord=" + isRecord +
                ", tierRecord=" + (tierRecord != null ? tierRecord.getIdtier() : "null") +
                ", tierDTO=" + tierDTO +
                ", active=" + active +
                ", stock=" + stock +
                '}';
    }
}
