package es.onebox.common.datasources.distribution.dto.order.items;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.distribution.dto.allocation.session.Session;
import es.onebox.common.datasources.distribution.dto.order.items.allocation.Event;
import es.onebox.common.datasources.distribution.dto.order.items.allocation.ItemAccessibility;
import es.onebox.common.datasources.distribution.dto.order.items.allocation.ItemVisibility;
import es.onebox.common.datasources.distribution.dto.order.items.allocation.PriceType;
import es.onebox.common.datasources.distribution.dto.order.items.allocation.Row;
import es.onebox.common.datasources.distribution.dto.order.items.allocation.SeasonTicketSession;
import es.onebox.common.datasources.distribution.dto.order.items.allocation.Seat;
import es.onebox.common.datasources.distribution.dto.order.items.allocation.Sector;
import es.onebox.core.serializer.dto.common.IdNameCodeDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ItemSeatAllocation implements Serializable {

    @Serial
    private static final long serialVersionUID = -3124573551016975422L;

    @JsonProperty("type")
    private ItemAllocationType type;
    @JsonProperty("event")
    private Event event;
    @JsonProperty("session")
    private Session session;
    @JsonProperty("venue")
    private VenueDTO venue;
    @JsonProperty("sector")
    private Sector sector;
    @JsonProperty("row")
    private Row row;
    @JsonProperty("seat")
    private Seat seat;
    @JsonProperty("price_type")
    private PriceType priceType;
    @JsonProperty("nnz")
    private IdNameDTO nnz;
    @JsonProperty("view")
    private IdNameCodeDTO view;
    @JsonProperty("accessibility")
    private ItemAccessibility accessibility;
    @JsonProperty("visibility")
    private ItemVisibility visibility;
    @JsonProperty("season_ticket_sessions")
    private List<SeasonTicketSession> seasonTicketSessions;

    public ItemAllocationType getType() {
        return type;
    }

    public void setType(ItemAllocationType type) {
        this.type = type;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public VenueDTO getVenue() {
        return venue;
    }

    public void setVenue(VenueDTO venue) {
        this.venue = venue;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public Row getRow() {
        return row;
    }

    public void setRow(Row row) {
        this.row = row;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public PriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }

    public IdNameDTO getNnz() {
        return nnz;
    }

    public void setNnz(IdNameDTO nnz) {
        this.nnz = nnz;
    }

    public IdNameCodeDTO getView() {
        return view;
    }

    public void setView(IdNameCodeDTO view) {
        this.view = view;
    }

    public ItemAccessibility getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(ItemAccessibility accessibility) {
        this.accessibility = accessibility;
    }

    public ItemVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(ItemVisibility visibility) {
        this.visibility = visibility;
    }

    public List<SeasonTicketSession> getSeasonTicketSessions() {
        return seasonTicketSessions;
    }

    public void setSeasonTicketSessions(List<SeasonTicketSession> seasonTicketSessions) {
        this.seasonTicketSessions = seasonTicketSessions;
    }
}