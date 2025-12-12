package es.onebox.ms.notification.config;

public class ApiConfig {

    public static final String API_VERSION = "v1";
    public static final String API_CONTEXT = "/ms-notification-api";
    public static final String BASE_URL = API_CONTEXT + "/" + API_VERSION;

    private ApiConfig() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }
}
