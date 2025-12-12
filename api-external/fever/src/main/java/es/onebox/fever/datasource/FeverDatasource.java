package es.onebox.fever.datasource;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.fever.dto.city.FeverCityData;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FeverDatasource {

    @Value("${fever.country.url}")
    private String cityPath;

    private static final int TIMEOUT = 60000;

    private final HttpClient httpClient;

    public FeverDatasource(ObjectMapper jacksonMapper,
                               TracingInterceptor tracingInterceptor) {
        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl("")
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .readTimeout(TIMEOUT)
                .build();
    }

    public FeverCityData getCity(String cityName, String countryCode) {
        QueryParameters params = new QueryParameters.Builder()
                .addQueryParameter("city_name", cityName)
                .addQueryParameter("country_iso_code", countryCode).build();

        return httpClient.buildRequest(HttpMethod.GET, cityPath)
                .params(params)
                .execute(FeverCityData.class);
    }
}
