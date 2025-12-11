package es.onebox.event.catalog.elasticsearch.dto.seasonticket;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;

import java.io.Serial;
import java.io.Serializable;

@CouchDocument
public class SeasonTicket implements Serializable {

    @Serial
    private static final long serialVersionUID = -4288821101310378722L;

    @Id
    private Long seasonTicketId;
    private Long sessionId;

    private Boolean registerMandatory;
    private Integer customerMaxSeats;

    private RenewalConfig renewalConfig;
    private SeatReallocationConfig seatReallocationConfig;
    private TransferConfig transferConfig;
    private ReleaseConfig releaseConfig;

    public Long getSeasonTicketId() {
        return seasonTicketId;
    }

    public void setSeasonTicketId(Long seasonTicketId) {
        this.seasonTicketId = seasonTicketId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Boolean getRegisterMandatory() {
        return registerMandatory;
    }

    public void setRegisterMandatory(Boolean registerMandatory) {
        this.registerMandatory = registerMandatory;
    }

    public Integer getCustomerMaxSeats() {
        return customerMaxSeats;
    }

    public void setCustomerMaxSeats(Integer customerMaxSeats) {
        this.customerMaxSeats = customerMaxSeats;
    }

    public RenewalConfig getRenewalConfig() {
        return renewalConfig;
    }

    public void setRenewalConfig(RenewalConfig renewalConfig) {
        this.renewalConfig = renewalConfig;
    }

    public SeatReallocationConfig getSeatReallocationConfig() {
        return seatReallocationConfig;
    }

    public void setSeatReallocationConfig(SeatReallocationConfig seatReallocationConfig) {
        this.seatReallocationConfig = seatReallocationConfig;
    }

    public TransferConfig getTransferConfig() {
        return transferConfig;
    }

    public void setTransferConfig(TransferConfig transferConfig) {
        this.transferConfig = transferConfig;
    }

    public ReleaseConfig getReleaseConfig() {
        return releaseConfig;
    }

    public void setReleaseConfig(ReleaseConfig releaseConfig) {
        this.releaseConfig = releaseConfig;
    }
}