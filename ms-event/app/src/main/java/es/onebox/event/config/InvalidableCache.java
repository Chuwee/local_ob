package es.onebox.event.config;

public class InvalidableCache {
    
    private InvalidableCache() {
    }

    public static final String EVENT_PROMOTIONS = "IC.eventPromotions";

    public static final String EVENT_SURCHARGES = "evSurcharges";

    public static final String EVENT_ENTITY_SURCHARGES = "evEntSurcharges";

    public static final String CHANNEL_EVENT_SURCHARGES = "ceMainSurcharges";

    public static final String EVENT_PROMOTIONS_SURCHARGES = "evPromotionSurcharges";

    public static final String CHANNEL_EVENT_PROMOTIONS_SURCHARGES = "cePromotionSurcharges";

    public static final String EVENT_INVITATIONS_SURCHARGES = "evInvSurcharges";

    public static final String EVENT_SECONDARY_MARKET_SURCHARGES = "evSMSurcharges";

    public static final String EVENT_ENTITY_SECONDARY_MARKET_SURCHARGES = "evEntSMSurcharges";

    public static final String EVENT_CHANNEL_SURCHARGES = "ecMainSurcharges";

    public static final String EVENT_CHANNEL_PROMOTIONS_SURCHARGES = "ecPromotionSurcharges";

    public static final String CHANNEL_PROMOTIONS_SURCHARGES = "cPromotionSurcharges";

    public static final String CHANNEL_INVITATIONS_SURCHARGES = "cInvitationSurcharges";

    public static final String CHANNEL_SECONDARY_MARKET_SURCHARGES = "cSMSurcharges";

    public static final int TTL = 8 * 60 * 60;
}
