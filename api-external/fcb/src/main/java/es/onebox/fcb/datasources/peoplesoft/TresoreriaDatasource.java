package es.onebox.fcb.datasources.peoplesoft;

import es.onebox.audit.cxf.interceptor.CxfClientAuditTracingStartInterceptor;
import es.onebox.audit.cxf.interceptor.CxfClientAuditTracingStopInterceptor;
import es.onebox.fcb.datasources.config.FcbPeopleSoftProperties;
import es.onebox.fcb.datasources.peoplesoft.utils.ServiceUtils;
import es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria.AltaDipositInputType;
import es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria.AltaDipositOutputType;
import es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria.ServeiTresoreriaFault;
import es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria.ServeiTresoreriaPortType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class TresoreriaDatasource {

    private static final Logger LOGGER = LoggerFactory.getLogger(TresoreriaDatasource.class);

    private String RESPONSE_OK = "00";

    private ServeiTresoreriaPortType serveiTresoreriaPortType;

    public TresoreriaDatasource(FcbPeopleSoftProperties fcbPeopleSoftProperties,
                                CxfClientAuditTracingStartInterceptor cxfClientAuditTracingStartInterceptor,
                                CxfClientAuditTracingStopInterceptor cxfClientAuditTracingStopInterceptor) {
        this.serveiTresoreriaPortType = ServiceUtils.getClientsTresoreria(
                fcbPeopleSoftProperties.getTresoreria().getUrl(),
                fcbPeopleSoftProperties.getChannel(),
                fcbPeopleSoftProperties.getPassword(),
                cxfClientAuditTracingStartInterceptor,
                cxfClientAuditTracingStopInterceptor);
    }

    public void altaDiposit(AltaDipositInputType altaDiposit) {
        try {
            AltaDipositOutputType diposit = serveiTresoreriaPortType.altaDiposit(altaDiposit);
            if (!RESPONSE_OK.equals(diposit.getResultat().getCodiResultat())) {
                LOGGER.error("[FCB WEBHOOK] Error wsdl servei tresoreria: {} - {}", diposit.getResultat().getCodiResultat(), diposit.getResultat().getDescripcioError());
            }
        } catch (ServeiTresoreriaFault e) {
            LOGGER.error("[FCB WEBHOOK] Error wsdl servei tresoreria: {} - {} - {} - {}", e.getFaultInfo().getCodiError(), e.getFaultInfo().getDescripcioError(), e.getFaultInfo().getCausa(), e.getFaultInfo().getSistema());
        }
    }

}
