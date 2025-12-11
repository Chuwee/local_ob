package es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelWhitelabelSettings implements Serializable {

    @Serial
    private static final long serialVersionUID = 8281583738128139764L;

    private ChannelWhitelabelPromotions promotions;
    private ChannelWhitelabelVenuemap venueMap;
    private ChannelWhitelabelCart cart;
    private ChannelWhitelabelThankYouPage thankYouPage;
    private ChannelWhitelabelReviews reviews;
    private ChannelWhitelabelCheckout checkout;
    private ChannelWhitelabelResendTickets resendTickets;

    public ChannelWhitelabelPromotions getPromotions() {
        return promotions;
    }

    public void setPromotions(ChannelWhitelabelPromotions promotions) {
        this.promotions = promotions;
    }

    public ChannelWhitelabelVenuemap getVenueMap() {
        return venueMap;
    }

    public void setVenueMap(ChannelWhitelabelVenuemap venueMap) {
        this.venueMap = venueMap;
    }

    public ChannelWhitelabelCart getCart() {
        return cart;
    }

    public void setCart(ChannelWhitelabelCart cart) {
        this.cart = cart;
    }

    public ChannelWhitelabelThankYouPage getThankYouPage() {
        return thankYouPage;
    }

    public void setThankYouPage(ChannelWhitelabelThankYouPage thankYouPage) {
        this.thankYouPage = thankYouPage;
    }

    public ChannelWhitelabelReviews getReviews() {
        return reviews;
    }

    public void setReviews(ChannelWhitelabelReviews reviews) {
        this.reviews = reviews;
    }

    public ChannelWhitelabelCheckout getCheckout() {
        return checkout;
    }

    public void setCheckout(ChannelWhitelabelCheckout checkout) {
        this.checkout = checkout;
    }

    public ChannelWhitelabelResendTickets getResendTickets() {
        return resendTickets;
    }

    public void setResendTickets(ChannelWhitelabelResendTickets resendTickets) {
        this.resendTickets = resendTickets;
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
