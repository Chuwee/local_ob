package es.onebox.event.common.services;

import es.onebox.event.common.utils.UrlUtils;

public class S3URLResolver {

    private static final String PATH_EVENT = "evento";
    private static final String PATH_PRODUCT = "product";
    private static final String PATH_PASSBOOK = "passbook";
    private static final String PATH_SESSION = "sesion";
    private static final String PATH_TOUR = "gira";
    private static final String PATH_ITEM = "itemIdioma";
    private static final String PATH_ENTITY_LOGO = "logo";
    private static final String PATH_VENUE = "recinto";
    private static final String PATH_VENUE_LOGO = "logo";
    private static final String PATH_PRODUCT_TICKET = "product-ticket-image";
    private static final String PATH_PACK = "pack";

    private final String url;
    private final Number operatorId;
    private final Number entityId;
    private final Number productId;
    private final Number eventId;
    private final Number sessionId;
    private final Number tourId;
    private final Number languageId;
    private final Number itemId;
    private final Number venueId;
    private final Number packId;
    private final S3ImageType imageType;

    private S3URLResolver(Builder builder) {
        this.url = builder.url;
        this.operatorId = builder.operatorId;
        this.entityId = builder.entityId;
        this.eventId = builder.eventId;
        this.productId = builder.productId;
        this.sessionId = builder.sessionId;
        this.tourId = builder.tourId;
        this.imageType = builder.imageType;
        this.languageId = builder.languageId;
        this.itemId = builder.itemId;
        this.venueId = builder.venueId;
        this.packId = builder.packId;
    }

    public String buildPath(String fileName) {
        return switch (imageType) {
            case EVENT_IMAGE -> buildEventPath(fileName);
            case EVENT_TICKET_IMAGE -> buildEventTicketPath(fileName);
            case SESSION_IMAGE -> buildSessionPath(fileName);
            case TOUR_IMAGE -> buildTourPath(fileName);
            case ITEM_IMAGE -> buildItemPath(fileName);
            case CHANNEL_NOTIFICATION -> buildChannelNotificationPath(fileName);
            case ENTITY_IMAGE -> buildEntityLogoPath(fileName);
            case EVENT_PASSBOOK_IMAGE -> buildEventPassbookPath(fileName);
            case SESSION_PASSBOOK_IMAGE -> buildSessionPassbookPath(fileName);
            case VENUE_IMAGE -> buildVenuePath(fileName);
            case PRODUCT_IMAGE -> buildProductPath(fileName);
            case PRODUCT_TICKET_IMAGE -> buildProductTicketPath(fileName);
            case PACK_IMAGE -> buildPackPath(fileName);
        };
    }

    public String buildRelativePath(String fileName) {
        return switch (imageType) {
            case EVENT_IMAGE -> buildRelativeEventPath(fileName);
            case EVENT_TICKET_IMAGE -> buildRelativeEventTicketPath(fileName);
            case SESSION_IMAGE -> buildRelativeSessionPath(fileName);
            case TOUR_IMAGE -> buildRelativeTourPath(fileName);
            case ITEM_IMAGE -> buildRelativeItemPath(fileName);
            case CHANNEL_NOTIFICATION -> buildRelativeChannelNotificationPath(fileName);
            case ENTITY_IMAGE -> buildRelativeEntityLogoPath(fileName);
            case EVENT_PASSBOOK_IMAGE -> buildRelativeEventPassbookPath(fileName);
            case SESSION_PASSBOOK_IMAGE -> buildRelativeSessionPassbookPath(fileName);
            case PRODUCT_IMAGE -> buildRelativeProductPath(fileName);
            case PRODUCT_TICKET_IMAGE -> buildRelativeProductTicketPath(fileName);
            case PACK_IMAGE -> buildRelativePackPath(fileName);
            default -> null;
        };
    }

    private String buildChannelNotificationPath(String fileName) {
        return UrlUtils.composeAbsoluteUrl(this.url, fileName);
    }

    private String buildEventPath(final String fileName) {
        return UrlUtils.composeAbsoluteUrl(this.url, this.operatorId, this.entityId, PATH_EVENT, this.eventId, fileName);
    }

    private String buildEventTicketPath(final String fileName) {
        return UrlUtils.composeAbsoluteUrl(this.url, this.operatorId, this.operatorId, PATH_ITEM, this.itemId, fileName);
    }

    private String buildEventPassbookPath(final String fileName) {
        return UrlUtils.composeAbsoluteUrl(this.url, this.operatorId, this.entityId, PATH_EVENT, this.eventId, PATH_PASSBOOK, fileName);
    }

    private String buildSessionPassbookPath(String fileName) {
        return UrlUtils.composeAbsoluteUrl(this.url, this.operatorId, this.entityId, PATH_EVENT, this.eventId, PATH_SESSION, this.sessionId, PATH_PASSBOOK, fileName);
    }

    private String buildProductPath(String fileName) {
        return UrlUtils.composeAbsoluteUrl(this.url, this.operatorId, this.entityId, PATH_PRODUCT, this.productId, fileName);
    }

    private String buildProductTicketPath(String fileName) {
        return UrlUtils.composeAbsoluteUrl(this.url, this.operatorId, this.entityId, PATH_PRODUCT_TICKET, this.productId, fileName);
    }

    private String buildVenuePath(String fileName) {
        return UrlUtils.composeAbsoluteUrl(this.url, this.operatorId, this.entityId, PATH_VENUE, this.venueId, PATH_VENUE_LOGO, fileName);
    }

    private String buildSessionPath(final String fileName) {
        return UrlUtils.composeAbsoluteUrl(this.url, this.operatorId, this.entityId, PATH_EVENT, this.eventId, PATH_SESSION, this.sessionId, fileName);
    }

    private String buildTourPath(final String fileName) {
        return UrlUtils.composeAbsoluteUrl(this.url, this.operatorId, this.entityId, PATH_TOUR, this.tourId, fileName);
    }

    private String buildEntityLogoPath(final String fileName) {
        return UrlUtils.composeAbsoluteUrl(this.url, this.operatorId, this.entityId, PATH_ENTITY_LOGO, fileName);
    }

    private String buildItemPath(final String fileName) {
        return UrlUtils.composeAbsoluteUrl(this.url, this.operatorId, this.operatorId, PATH_ITEM, this.itemId, fileName);
    }

    private String buildPackPath(final String fileName) {
        return UrlUtils.composeAbsoluteUrl(this.url, this.operatorId, this.entityId, PATH_PACK, this.packId, fileName);
    }

    private String buildRelativeChannelNotificationPath(String fileName) {
        return UrlUtils.composeRelativePathNullable(this.url, fileName);
    }

    private String buildRelativeProductTicketPath(final String fileName) {
        return UrlUtils.composeRelativePathNullable(this.operatorId, this.entityId, PATH_PRODUCT_TICKET, this.productId, fileName);
    }

    public String buildRelativeEventPath(final String fileName) {
        return UrlUtils.composeRelativePathNullable(this.operatorId, this.entityId, PATH_EVENT, this.eventId, fileName);
    }

    private String buildRelativeEventTicketPath(final String fileName) {
        return UrlUtils.composeRelativePathNullable(this.operatorId, this.operatorId, PATH_ITEM, this.itemId, fileName);
    }

    private String buildRelativeEventPassbookPath(final String fileName) {
        return UrlUtils.composeRelativePathNullable(this.operatorId, this.entityId, PATH_EVENT, this.eventId, PATH_PASSBOOK, fileName);
    }

    private String buildRelativeSessionPassbookPath(String fileName) {
        return UrlUtils.composeRelativePathNullable(this.operatorId, this.entityId, PATH_EVENT, this.eventId, PATH_SESSION, this.sessionId, PATH_PASSBOOK, fileName);
    }

    private String buildRelativeProductPath(String fileName) {
        return UrlUtils.composeRelativePathNullable(this.operatorId, this.entityId, PATH_PRODUCT, this.productId, fileName);
    }

    public String buildRelativeSessionPath(final String fileName) {
        return UrlUtils.composeRelativePathNullable(this.operatorId, this.entityId, PATH_EVENT, this.eventId, PATH_SESSION, this.sessionId, fileName);
    }

    public String buildRelativeTourPath(final String fileName) {
        return UrlUtils.composeRelativePathNullable(this.operatorId, this.entityId, PATH_TOUR, this.eventId, PATH_TOUR, this.tourId, fileName);
    }

    private String buildRelativeEntityLogoPath(final String fileName) {
        return UrlUtils.composeRelativePathNullable(this.url, this.operatorId, this.entityId, PATH_ENTITY_LOGO, fileName);
    }

    private String buildRelativeItemPath(final String fileName) {
        return UrlUtils.composeRelativePathNullable(this.operatorId, this.operatorId, PATH_ITEM, this.itemId, fileName);
    }

    public String buildRelativePackPath(final String fileName) {
        return UrlUtils.composeRelativePathNullable(this.operatorId, this.entityId, PATH_PACK, this.packId, fileName);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String url;
        private Number operatorId;
        private Number entityId;
        private Number eventId;
        private Number productId;
        private Number sessionId;
        private Number tourId;
        private Number languageId;
        private Number itemId;
        private Number venueId;
        private Number packId;
        private S3ImageType imageType;

        private Builder() {
        }

        public Builder withUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder withOperatorId(Number operatorId) {
            this.operatorId = operatorId;
            return this;
        }

        public Builder withEntityId(Number entityId) {
            this.entityId = entityId;
            return this;
        }

        public Builder withEventId(Number eventId) {
            this.eventId = eventId;
            return this;
        }

        public Builder withProductId(Number productId) {
            this.productId = productId;
            return this;
        }

        public Builder withSessionId(Number sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public Builder withTourId(Number tourId) {
            this.tourId = tourId;
            return this;
        }

        public Builder withType(S3ImageType type) {
            this.imageType = type;
            return this;
        }

        public Builder withLanguageId(Number languageId) {
            this.languageId = languageId;
            return this;
        }

        public Builder withItemId(Number itemId) {
            this.itemId = itemId;
            return this;
        }

        public Builder withVenueId(Number venueId) {
            this.venueId = venueId;
            return this;
        }

        public Builder withPackId(Number packId) {
            this.packId = packId;
            return this;
        }

        public S3URLResolver build() {
            if (this.imageType == null) {
                throw new IllegalStateException("Image type doesn't specified.");
            }
            return new S3URLResolver(this);
        }
    }

    public enum S3ImageType {
        EVENT_IMAGE,
        EVENT_TICKET_IMAGE,
        SESSION_IMAGE,
        TOUR_IMAGE,
        ENTITY_IMAGE,
        CHANNEL_NOTIFICATION,
        ITEM_IMAGE,
        EVENT_PASSBOOK_IMAGE,
        SESSION_PASSBOOK_IMAGE,
        VENUE_IMAGE,
        PRODUCT_IMAGE,
        PRODUCT_TICKET_IMAGE,
        PACK_IMAGE
    }
}
