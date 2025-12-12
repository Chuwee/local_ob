package es.onebox.bepass.instrumentation;

import es.onebox.audit.core.span.AuditSpan;
import es.onebox.audit.okhttp.decorator.OkHttpClientAuditSpanDecorator;
import es.onebox.bepass.auth.BepassAuthContext;
import es.onebox.bepass.common.BepassEntityConfiguration;
import es.onebox.tracer.core.AMT;
import es.onebox.tracer.core.TracingSpan;
import okhttp3.Connection;
import okhttp3.Request;
import okhttp3.Response;

public class BepassOkHttpDecorator extends OkHttpClientAuditSpanDecorator {

    @Override
    public void onRequest(Request request, TracingSpan span, AuditSpan auditSpan) {
        BepassEntityConfiguration config = BepassAuthContext.get();
        if (config != null) {
            AMT.addTracingAndAuditProperty(AMTUtils.ENTITY_ID,  config.entityId());
            AMT.addTracingAndAuditProperty(AMTUtils.BEPASS_TENANT, AMTUtils.resolveTenant(config));
        }
    }

    @Override
    public void onResponse(Connection connection, Response response, TracingSpan span, AuditSpan auditSpan) {
    }

    @Override
    public void onError(Response response, String responseBodyString, TracingSpan span, AuditSpan auditSpan) {
    }

    @Override
    public void onException(Exception e, TracingSpan span, AuditSpan auditSpan) {
    }
}
