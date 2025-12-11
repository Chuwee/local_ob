package es.onebox.mgmt.b2b.publishing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.b2b.publishing.enums.TicketStatus;
import es.onebox.mgmt.b2b.publishing.enums.TransactionType;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class HistoricDTO implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private PublisherDataDTO publisher;

    @JsonProperty("source_quota_id")
    private Integer sourceQuotaId;
    @JsonProperty("target_quota_id")
    private Integer targetQuotaId;
    @JsonProperty("source_price_type_id")
    private Integer sourcePriceTypeId;
    @JsonProperty("target_price_type_id")
    private Integer targetPriceTypeId;


    private ZonedDateTime date;
    private TransactionType type;
    @JsonProperty("seat_status")
    private TicketStatus seatStatus;
    private BigDecimal price;

    public PublisherDataDTO getPublisher() {
        return publisher;
    }
    public void setPublisher(PublisherDataDTO publisher) {
        this.publisher = publisher;
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

    public TransactionType getType() {
        return type;
    }
    public void setType(TransactionType type) {
        this.type = type;
    }


    public TicketStatus getSeatStatus() {
        return seatStatus;
    }

    public void setSeatStatus(TicketStatus seatStatus) {
        this.seatStatus = seatStatus;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
