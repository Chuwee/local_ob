package es.onebox.mgmt.b2b.publishing.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.b2b.publishing.enums.TicketStatus;
import es.onebox.mgmt.b2b.publishing.enums.TransactionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class BaseSeatPublishingDTO implements Serializable {
    @Serial private static final long serialVersionUID = 1L;

    private Long id;
    private IdNameDTO event;
    private IdNameDTO channel;
    private SessionDataDTO session;
    private SeatDataDTO seat;
    private PublisherDataDTO publisher;
    private BigDecimal price;
    private TransactionType type;
    private ZonedDateTime date;
    @JsonProperty("seat_status")
    private TicketStatus seatStatus;


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public IdNameDTO getEvent() {
        return event;
    }
    public void setEvent(IdNameDTO event) {
        this.event = event;
    }

    public IdNameDTO getChannel() {
        return channel;
    }
    public void setChannel(IdNameDTO channel) {
        this.channel = channel;
    }

    public SessionDataDTO getSession() {
        return session;
    }
    public void setSession(SessionDataDTO session) {
        this.session = session;
    }

    public SeatDataDTO getSeat() {
        return seat;
    }
    public void setSeat(SeatDataDTO seat) {
        this.seat = seat;
    }

    public PublisherDataDTO getPublisher() {
        return publisher;
    }
    public void setPublisher(PublisherDataDTO publisher) {
        this.publisher = publisher;
    }

    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public TransactionType getType() {
        return type;
    }
    public void setType(TransactionType type) {
        this.type = type;
    }

    public ZonedDateTime getDate() {
        return date;
    }
    public void setDate(ZonedDateTime date) {
        this.date = date;
    }

    public TicketStatus getSeatStatus() {
        return seatStatus;
    }
    public void setSeatStatus(TicketStatus seatStatus) {
        this.seatStatus = seatStatus;
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
