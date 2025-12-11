package es.onebox.mgmt.datasources.queueit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class QueueItDatasource {

    private static final String QUEUE_IT_CONF_URL = "https://%s.%s/status/integrationconfig/secure/%s";

    public String getEventQueueItConfig(String customerId, String apiKey, String host) throws IOException {
        String url = String.format(QUEUE_IT_CONF_URL, customerId, host, customerId);
        Response response = getResponse(apiKey, host, url);
        String responseString = "";
        if (response.body() != null) {
            responseString = response.body().string();
        }
        return responseString;
    }

    private Response getResponse(String apiKey, String host, String url) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(2000, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false)
                .build();

        Request build = new Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("api-key", apiKey)
                .addHeader("Host", host)
                .build();
        return client.newCall(build).execute();
    }
}
