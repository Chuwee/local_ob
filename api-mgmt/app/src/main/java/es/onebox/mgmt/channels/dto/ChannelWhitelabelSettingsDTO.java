package es.onebox.mgmt.channels.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelWhitelabelSettingsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8281583738128139764L;

    private ChannelWhitelabelPromotionsDTO promotions;

    @JsonProperty("venue_map")
    private ChannelWhitelabelVenuemapDTO venueMap;
    private ChannelWhitelabelCartDTO cart;
    @JsonProperty("thank_you_page")
    private ChannelWhitelabelThankYouPageDTO thankYouPage;
    private ChannelWhitelabelReviewsDTO reviews;
    private ChannelWhitelabelCheckoutDTO checkout;
    @JsonProperty("resend_tickets")
    private ChannelWhitelabelResendTicketsDTO resendTickets;

    public ChannelWhitelabelPromotionsDTO getPromotions() {
        return promotions;
    }

    public void setPromotions(ChannelWhitelabelPromotionsDTO promotions) {
        this.promotions = promotions;
    }

    public ChannelWhitelabelVenuemapDTO getVenueMap() {
        return venueMap;
    }

    public void setVenueMap(ChannelWhitelabelVenuemapDTO venueMap) {
        this.venueMap = venueMap;
    }

    public ChannelWhitelabelCartDTO getCart() {
        return cart;
    }

    public void setCart(ChannelWhitelabelCartDTO cart) {
        this.cart = cart;
    }

    public ChannelWhitelabelThankYouPageDTO getThankYouPage() {
        return thankYouPage;
    }

    public void setThankYouPage(ChannelWhitelabelThankYouPageDTO thankYouPage) {
        this.thankYouPage = thankYouPage;
    }

    public ChannelWhitelabelReviewsDTO getReviews() {
        return reviews;
    }

    public void setReviews(ChannelWhitelabelReviewsDTO reviews) {
        this.reviews = reviews;
    }

    public ChannelWhitelabelCheckoutDTO getCheckout() {
        return checkout;
    }

    public void setCheckout(ChannelWhitelabelCheckoutDTO checkout) {
        this.checkout = checkout;
    }

    public ChannelWhitelabelResendTicketsDTO getResendTickets() {
        return resendTickets;
    }

    public void setResendTickets(ChannelWhitelabelResendTicketsDTO resendTickets) {
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
