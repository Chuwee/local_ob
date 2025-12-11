package es.onebox.event.catalog.elasticsearch.builder;

import es.onebox.event.catalog.elasticsearch.dto.channelsession.ChannelSession;
import es.onebox.event.catalog.elasticsearch.pricematrix.PriceMatrix;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionOccupationVenueContainer;
import es.onebox.event.datasources.ms.ticket.dto.occupation.SessionPriceZoneOccupationDTO;

import java.util.List;

public class ChannelSessionOccupationBuilder {

    private final ChannelSession data;

    protected Boolean soldOut;
    protected List<Long> promotions;
    protected PriceMatrix priceMatrix;
    protected List<SessionPriceZoneOccupationDTO> priceZoneOccupations;
    protected List<SessionOccupationVenueContainer> containerOccupations;

    private ChannelSessionOccupationBuilder(ChannelSession data) {
        super();
        this.data = data;
    }

    public static ChannelSessionOccupationBuilder builder(ChannelSession data) {
        return new ChannelSessionOccupationBuilder(data);
    }

    public ChannelSessionOccupationBuilder soldOut(Boolean soldOut) {
        this.soldOut = soldOut;
        return this;
    }

    public ChannelSessionOccupationBuilder promotions(List<Long> promotions) {
        this.promotions = promotions;
        return this;
    }

    public ChannelSessionOccupationBuilder priceMatrix(PriceMatrix priceMatrix) {
        this.priceMatrix = priceMatrix;
        return this;
    }

    public ChannelSessionOccupationBuilder containerOccupation(List<SessionOccupationVenueContainer> containerOccupations) {
        this.containerOccupations = containerOccupations;
        return this;
    }

    public ChannelSessionOccupationBuilder priceZoneOccupations(List<SessionPriceZoneOccupationDTO> priceZoneOccupations) {
        this.priceZoneOccupations = priceZoneOccupations;
        return this;
    }


    public void buildOccupation() {
        data.setSoldOut(soldOut);
        data.setPromotions(promotions);
        data.setPrices(priceMatrix);
        data.setPriceZoneOccupations(priceZoneOccupations);
        data.setContainerOccupations(containerOccupations);
    }

}
