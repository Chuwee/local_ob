package es.onebox.event.config;

public class LocalCache {

    private LocalCache() {}

    public static final String EVENT_TYPE_KEY = "eventType";
    public static final String ENTITY_KEY = "entity";
    public static final String ENTITYINFO_KEY = "entityInfo";
    public static final String COUNTRY_SUB_KEY = "countrySub";
    public static final String PRODUCER_KEY = "producer";
    public static final String USERNAME_KEY = "userName";
    public static final String TOUR_KEY = "tour";
    public static final String EVENT_CHANNEL_BANNER_KEY = "ecBanner";
    public static final String SESSION_CHANNEL_BANNER_KEY = "ecBannerSession";
    public static final String CLIENT_CONDITIONS_KEY = "clientConditions";
    public static final String CHANNELS_KEY = "channels";
    public static final String CHANNEL_BANNER_KEY = "cBanner";
    public static final String TAXONOMY_KEY = "customTaxonomy";
    public static final String TEMPLATE_CONTAINERS = "templateContainers";
    public static final String TEMPLATE_SECTORS = "templateSectors";
    public static final String TEMPLATE_NNZS = "templateNNZs";
    public static final String TEMPLATE_LINKS = "templateLinks";

    public static final int EVENT_TYPE_TTL = 8 * 60 * 60;
    public static final int ENTITY_TTL = 3 * 60;
    public static final int CHANNELS_TTL = 1 * 60;
    public static final int CHANNELS_TTL_EXTENDED = 5 * 60;
    public static final int ENTITYINFO_TTL = 10 * 60;
    public static final int COUNTRY_SUB_TTL = 30 * 60;
    public static final int PRODUCER_TTL = 5 * 60;
    public static final int USERNAME_TTL = 5 * 60;
    public static final int TOUR_TTL = 10 * 60;
    public static final int EVENT_CHANNEL_COM_TTL = 2 * 60;
    public static final int SESSION_CHANNEL_COM_TTL = 2 * 60;
    public static final int CHANNEL_COM_TTL = 5 * 60;
    public static final int TAXONOMY_TTL = 30 * 60;
    public static final int TEMPLATE_TTL = 5 * 60;
}
