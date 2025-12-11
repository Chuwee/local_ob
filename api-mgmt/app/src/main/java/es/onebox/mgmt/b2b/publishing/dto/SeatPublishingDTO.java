package es.onebox.mgmt.b2b.publishing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class SeatPublishingDTO extends BaseSeatPublishingDTO implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    @JsonProperty("entity_id")
    private Integer entityId;
    @JsonProperty("source_quota_id")
    private Integer sourceQuotaId;
    @JsonProperty("target_quota_id")
    private Integer targetQuotaId;
    @JsonProperty("source_price_type_id")
    private Integer sourcePriceTypeId;
    @JsonProperty("target_price_type_id")
    private Integer targetPriceTypeId;
    private ZonedDateTime date;
    private PublisherDataDTO publisher;
    private List<HistoricDTO> historic;

    public Integer getEntityId() {
        return entityId;
    }
    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public Integer getSourceQuotaId() {
        return sourceQuotaId;
    }
    public void setSourceQuotaId(Integer sourceQuotaId) {
        this.sourceQuotaId = sourceQuotaId;
    }

    public Integer getTargetQuotaId() {
        return targetQuotaId;
    }
    public void setTargetQuotaId(Integer targetQuotaId) {
        this.targetQuotaId = targetQuotaId;
    }

    public Integer getSourcePriceTypeId() {
        return sourcePriceTypeId;
    }
    public void setSourcePriceTypeId(Integer sourcePriceTypeId) {
        this.sourcePriceTypeId = sourcePriceTypeId;
    }

    public Integer getTargetPriceTypeId() {
        return targetPriceTypeId;
    }
    public void setTargetPriceTypeId(Integer targetPriceTypeId) {
        this.targetPriceTypeId = targetPriceTypeId;
    }

    public ZonedDateTime getDate() {
        return date;
    }
    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public PublisherDataDTO getPublisher() {
        return publisher;
    }
    public void setPublisher(PublisherDataDTO publisher) {
        this.publisher = publisher;
    }

    public List<HistoricDTO> getHistoric() {
        return historic;
    }
    public void setHistoric(List<HistoricDTO> historic) {
        this.historic = historic;
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
