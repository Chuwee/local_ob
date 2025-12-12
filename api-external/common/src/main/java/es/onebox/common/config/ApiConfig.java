package es.onebox.common.config;

import java.util.Arrays;
import java.util.List;

public class ApiConfig {

    private ApiConfig() {
    }

    public static final String ONEBOX_CLIENT = "onebox-client";
    public static final String ECI_CLIENT = "eci-client";
    public static final String FLC_CLIENT = "flc-client";
    public static final String SELLER_CLIENT = "seller-channel-client";
    public static final String ACCESS_CONTROL_CLIENT = "access-control-client";
    public static List<String> CLIENT_IDS = List.of(ONEBOX_CLIENT, ECI_CLIENT, FLC_CLIENT, SELLER_CLIENT, ACCESS_CONTROL_CLIENT);


    public static class ATHApiConfig {
        public static final String API_VERSION = "v1";
        public static final String API_CONTEXT = "/ath-api";
        public static final String BASE_URL = API_CONTEXT + "/" + API_VERSION;
        public static final List<String> CLIENT_ID = Arrays.asList(ONEBOX_CLIENT);
    }

    public static class ATMApiConfig {
        public static final String API_VERSION = "v1";
        public static final String API_CONTEXT = "/atm-api";
        public static final String BASE_URL = API_CONTEXT + "/" + API_VERSION;
        public static final List<String> CLIENT_ID = Arrays.asList(ONEBOX_CLIENT, SELLER_CLIENT);
    }

    public static class ChannelsApiConfig {
        public static final String API_VERSION = "v1";
        public static final String API_CONTEXT = "/channels-api";
        public static final String BASE_URL = API_CONTEXT + "/" + API_VERSION;
    }

    public static class ChannelFeedsApiConfig {
        public static final String API_VERSION = "v1";
        public static final String API_CONTEXT = "/channel-feeds-api";
        public static final String BASE_URL = API_CONTEXT + "/" + API_VERSION;
    }

    public static class CircuitApiConfig {
        public static final String APP_NAME = "api-circuit";
        public static final String API_VERSION = "v1";
        public static final String API_CONTEXT = "/circuit-api";
        public static final String BASE_URL = API_CONTEXT + "/" + API_VERSION;
        public static final List<String> CLIENT_ID = Arrays.asList(ONEBOX_CLIENT);
    }

    public static class ECIApiConfig {
        public static final String APP_NAME = "api-eci";
        public static final String API_VERSION = "v1";
        public static final String API_CONTEXT = "/eci-api";
        public static final String BASE_URL = API_CONTEXT + "/" + API_VERSION;
        public static final String CLIENT_ID = ECI_CLIENT;
    }

    public static class FCBApiConfig {
        public static final String API_VERSION = "v1";
        public static final String API_CONTEXT = "/fcb-api";
        public static final String BASE_URL = API_CONTEXT + "/" + API_VERSION;
    }

    public static class FeverApiConfig {
        public static final String API_VERSION = "v1";
        public static final String API_CONTEXT = "/fever-api";
        public static final String BASE_URL = API_CONTEXT + "/" + API_VERSION;
        public static final List<String> CLIENT_ID = Arrays.asList(SELLER_CLIENT, ONEBOX_CLIENT);
    }


    public static class FLCApiConfig {
        public static final String APP_NAME = "api-flc";
        public static final String API_VERSION = "v1";
        public static final String API_CONTEXT = "/flc-api";
        public static final String BASE_URL = API_CONTEXT + "/" + API_VERSION;
        public static final List<String> CLIENT_ID = Arrays.asList(FLC_CLIENT, ONEBOX_CLIENT);
    }

    public class CurrencyExchangeApiConfig {
        public static final String API_CONTEXT = "/exchange-api";
        public static final String API_VERSION = "v1";
        public static final String BASE_URL = API_CONTEXT + "/" + API_VERSION;
    }

    public class ChelseaApiConfig {
        public static final String API_VERSION = "v1";
        public static final String API_CONTEXT = "/chelsea-api";
        public static final String BASE_URL = API_CONTEXT + "/" + API_VERSION;
    }

    public class BepassApiConfig {
        public static final String API_VERSION = "/v1";
        public static final String API_CONTEXT = "/bepass-api";
        public static final String BASE_URL = API_CONTEXT + API_VERSION;
    }

    public static class InternalApiConfig {
        public static final String API_VERSION = "v1";
        public static final String API_CONTEXT = "/internal-api";
        public static final String BASE_URL = API_CONTEXT + "/" + API_VERSION;
    }

    public static class PalisisApiConfig {
        public static final String API_VERSION = "v1";
        public static final String API_CONTEXT = "/palisis-api";
        public static final String BASE_URL = API_CONTEXT + "/" + API_VERSION;
    }

    public static class QatarApiConfig {
        public static final String API_CONTEXT = "/fifa-qatar/api";
        public static final String BASE_URL = API_CONTEXT;
    }

    public static class HayyaApiConfig {
        public static final String API_VERSION = "v1";
        public static final String API_CONTEXT = "/hayya-api";
        public static final String BASE_URL = API_CONTEXT  + "/" + API_VERSION;
        public static final List<String> CLIENT_ID = Arrays.asList(ACCESS_CONTROL_CLIENT);
    }

    public static class ZucchettiApiConfig {
        public static final String API_VERSION = "v1";
        public static final String API_CONTEXT = "/zucchetti-api";
        public static final String BASE_URL = API_CONTEXT  + "/" + API_VERSION;
        public static final List<String> CLIENT_ID = Arrays.asList(ACCESS_CONTROL_CLIENT);
    }

    public static class CustomerSyncApiConfig {
        public static final String API_VERSION = "v1";
        public static final String API_CONTEXT = "/customer-sync-api";
        public static final String BASE_URL = API_CONTEXT + "/" + API_VERSION;
    }
}
