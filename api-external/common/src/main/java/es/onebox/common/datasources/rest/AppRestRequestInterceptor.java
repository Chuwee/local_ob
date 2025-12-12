package es.onebox.common.datasources.rest;

import es.onebox.oauth2.resource.context.AuthContextUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

public class AppRestRequestInterceptor implements Interceptor {

    private static final String MEDIA_TYPE = "application/x-json";

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request  = chain.request();
        Request.Builder builder = request.newBuilder()
                .addHeader(HttpHeaders.AUTHORIZATION, AuthContextUtils.getRawToken())
                .addHeader(HttpHeaders.ACCEPT, MEDIA_TYPE);
        Request newRequest = builder.build();
        return chain.proceed(newRequest);
    }
}
