package es.onebox.bepass.datasources.bepass;

import es.onebox.bepass.auth.BepassAuthContext;
import es.onebox.datasource.http.RequestHeaders;

public abstract class BepassDatasource {

    protected static final long CONNECTION_TIMEOUT = 10000L;
    protected static final long READ_TIMEOUT = 10000L;
    protected static final String HEADER_TENANT_ID = "x-tenant-id";
    protected static final String HEADER_API_KEY = "x-api-key";

    protected BepassDatasource() {
    }

    protected RequestHeaders prepareTenantHeader() {
        return new RequestHeaders.Builder()
                .addHeader(HEADER_TENANT_ID, BepassAuthContext.get().tenantId())
                .build();
    }

    protected RequestHeaders prepareHeaders(String token) {
        return new RequestHeaders.Builder()
                .addHeader(HEADER_API_KEY, token)
                .addHeader(HEADER_TENANT_ID, BepassAuthContext.get().tenantId())
                .build();
    }

}
