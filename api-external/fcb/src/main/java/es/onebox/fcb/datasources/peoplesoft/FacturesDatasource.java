package es.onebox.fcb.datasources.peoplesoft;

import es.onebox.audit.cxf.interceptor.CxfClientAuditTracingStartInterceptor;
import es.onebox.audit.cxf.interceptor.CxfClientAuditTracingStopInterceptor;
import es.onebox.fcb.datasources.config.FcbPeopleSoftProperties;
import es.onebox.fcb.datasources.peoplesoft.utils.ServiceUtils;
import es.onebox.fcb.datasources.peoplesoft.wsdl.factures.FacturesFault;
import es.onebox.fcb.datasources.peoplesoft.wsdl.factures.FacturesPortType;
import es.onebox.fcb.datasources.peoplesoft.wsdl.factures.PeticioFacturar;
import es.onebox.fcb.datasources.peoplesoft.wsdl.factures.RespostaFacturar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class FacturesDatasource {

    private static final Logger LOGGER = LoggerFactory.getLogger(FacturesDatasource.class);

    private String RESPONSE_OK = "00";

    private FacturesPortType facturesClient;

    public FacturesDatasource(FcbPeopleSoftProperties fcbPeopleSoftProperties,
                              CxfClientAuditTracingStartInterceptor cxfClientAuditTracingStartInterceptor,
                              CxfClientAuditTracingStopInterceptor cxfClientAuditTracingStopInterceptor) {
        this.facturesClient = ServiceUtils.getFacturesClient(
                fcbPeopleSoftProperties.getFactures().getUrl(),
                fcbPeopleSoftProperties.getChannel(),
                fcbPeopleSoftProperties.getPassword(),
                cxfClientAuditTracingStartInterceptor,
                cxfClientAuditTracingStopInterceptor);
    }

    public void registerOperation(PeticioFacturar peticioFacturar) {
        try {
            RespostaFacturar facturar = facturesClient.facturar(peticioFacturar);
            if (!RESPONSE_OK.equals(facturar.getEstat())) {
                LOGGER.error("[FCB WEBHOOK] Error wsdl factures: {} - {}", facturar.getEstat(), facturar.getDescripcioEstat());
            }
        } catch (FacturesFault e) {
            LOGGER.error("[FCB WEBHOOK] Error wsdl factures: {} - {} - {} - {}", e.getFaultInfo().getCodiError(), e.getFaultInfo().getDescripcioError(), e.getFaultInfo().getCausa(), e.getFaultInfo().getSistema());
        }
    }

}
