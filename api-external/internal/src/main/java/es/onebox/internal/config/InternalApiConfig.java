package es.onebox.internal.config;

import es.onebox.common.config.ApiConfig;

import java.util.List;

public class InternalApiConfig {

    public static final String ONEBOX_CLIENT = "onebox-client";

    public static class AutomaticSales {
        public static final String SUBCONTEXT = "/automatic-sales";
        public static final String BASE_URL = ApiConfig.InternalApiConfig.BASE_URL + SUBCONTEXT;
        public static final List<String> CLIENT_ID = List.of(ONEBOX_CLIENT);
    }

    public static class AutomaticRenewals {
        public static final String SUBCONTEXT = "/automatic-renewals";
        public static final String BASE_URL = ApiConfig.InternalApiConfig.BASE_URL + SUBCONTEXT;
        public static final List<String> CLIENT_ID = List.of(ONEBOX_CLIENT);
    }

    public static class XMLSepa {
        public static final String SUBCONTEXT = "/xml-sepa";
        public static final String BASE_URL = ApiConfig.InternalApiConfig.BASE_URL + SUBCONTEXT;
        public static final List<String> CLIENT_ID = List.of(ONEBOX_CLIENT);
    }

    public static class SGTM {
        public static final String SUBCONTEXT = "/sgtm";
        public static final String BASE_URL = ApiConfig.InternalApiConfig.BASE_URL + SUBCONTEXT;
    }
}
