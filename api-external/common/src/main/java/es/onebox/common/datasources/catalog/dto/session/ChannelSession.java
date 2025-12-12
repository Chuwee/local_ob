package es.onebox.common.datasources.catalog.dto.session;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.catalog.dto.common.Dates;
import es.onebox.common.datasources.catalog.dto.common.Prices;
import es.onebox.common.datasources.catalog.dto.common.Promotion;
import es.onebox.common.datasources.catalog.dto.common.Venue;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;
import java.util.List;

public class ChannelSession implements Serializable {

    private static final long serialVersionUID = -8328092166076078771L;

    protected Long id;
    protected String name;
    protected String reference;
    @JsonProperty("for_sale")
    private Boolean forSale;
    @JsonProperty("sold_out")
    private Boolean soldOut;
    private Venue venue;
    private Dates date;
    private ChannelSessionTexts texts;
    private Prices price;
    private IdNameDTO event;
    private List<Promotion> promotions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Boolean getForSale() {
        return forSale;
    }

    public void setForSale(Boolean forSale) {
        this.forSale = forSale;
    }

    public Boolean getSoldOut() {
        return soldOut;
    }

    public void setSoldOut(Boolean soldOut) {
        this.soldOut = soldOut;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public Dates getDate() {
        return date;
    }

    public void setDate(Dates date) {
        this.date = date;
    }

    public ChannelSessionTexts getTexts() {
        return texts;
    }

    public void setTexts(ChannelSessionTexts texts) {
        this.texts = texts;
    }

    public Prices getPrice() {
        return price;
    }

    public void setPrice(Prices price) {
        this.price = price;
    }

    public IdNameDTO getEvent() {
        return event;
    }

    public void setEvent(IdNameDTO event) {
        this.event = event;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
