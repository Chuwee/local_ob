package es.onebox.mgmt.datasources.integration.avetsocketconnector;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.datasource.http.HttpClient;
import es.onebox.datasource.http.QueryParameters;
import es.onebox.datasource.http.configuration.HttpClientFactoryBuilder;
import es.onebox.datasource.http.method.HttpMethod;
import es.onebox.mgmt.datasources.integration.avetsocketconnector.exception.ClientHttpExceptionBuilder;
import es.onebox.tracer.okhttp.TracingInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MsAvetSocketConnectorDatasource {
    private static final int TIMEOUT = 360000;

    private static final String BASE_PATH ="/onebox-venuebridge/venuebridge/avet";

    private final HttpClient httpClient;

    @Autowired
    public MsAvetSocketConnectorDatasource(@Value("${clients.services.int-avet-socket-connector-service}") String baseUrl,
                                           ObjectMapper jacksonMapper,
                                           TracingInterceptor tracingInterceptor){

        this.httpClient = HttpClientFactoryBuilder.builder()
                .baseUrl(baseUrl + BASE_PATH)
                .jacksonMapper(jacksonMapper)
                .interceptors(tracingInterceptor)
                .exceptionBuilder(new ClientHttpExceptionBuilder())
                .readTimeout(TIMEOUT)
                .build();
    }

    public void updateMatchAvailability(Long matchId, Long sessionId){
        QueryParameters.Builder params = new QueryParameters.Builder();
        params.addQueryParameter("idPartido", matchId.toString());
        params.addQueryParameter("idSesion", sessionId.toString());
        params.addQueryParameter("forceEnqueue", "false");

        httpClient.buildRequest(HttpMethod.PUT, "/actualizaDisponibilidadPartidoId")
                .params(params.build())
                .execute();
    }
}
