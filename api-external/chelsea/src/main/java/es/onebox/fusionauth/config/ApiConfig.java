package es.onebox.fusionauth.config;

public class ApiConfig {
    public static final String API_VERSION = "v1";
    public static final String API_CONTEXT = "/fusion-auth-api";
    public static final String BASE_URL = API_CONTEXT + "/" + API_VERSION;

    private ApiConfig() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

}