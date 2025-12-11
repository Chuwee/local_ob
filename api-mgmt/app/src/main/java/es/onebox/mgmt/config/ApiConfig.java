package es.onebox.mgmt.config;

public class ApiConfig {

    public static final String API_VERSION = "v1";
    public static final String API_CONTEXT = "/mgmt-api";
    public static final String BASE_URL = API_CONTEXT + "/" + API_VERSION;
    public static final String AUTH_SCOPE = "api-mgmt-all";
    public static final String AUTH_RESOURCE_ID = "api-mgmt";
    public static final String ONEBOX_CLIENT_ID = "onebox-client";

    private ApiConfig() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }
}
