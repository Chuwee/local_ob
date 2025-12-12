package es.onebox.fcb.datasources.peoplesoft;

import es.onebox.audit.cxf.interceptor.CxfClientAuditTracingStartInterceptor;
import es.onebox.audit.cxf.interceptor.CxfClientAuditTracingStopInterceptor;
import es.onebox.fcb.datasources.config.FcbPeopleSoftProperties;
import es.onebox.fcb.datasources.peoplesoft.utils.ServiceUtils;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.ClientsFault;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.ClientsPortType;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.PeticioAltaClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.PeticioCercaClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.PeticioModificacioClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.PeticioValidacioNif;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.RespostaAltaClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.RespostaCercaClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.RespostaModificacioClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.RespostaValidacioNif;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class ClientsDatasource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClientsDatasource.class);

    private String RESPONSE_OK = "00";
    private String RESPONSE_OK_02 = "02";

    private ClientsPortType clientsPortType;

    public ClientsDatasource(FcbPeopleSoftProperties fcbPeopleSoftProperties,
                             CxfClientAuditTracingStartInterceptor cxfClientAuditTracingStartInterceptor,
                             CxfClientAuditTracingStopInterceptor cxfClientAuditTracingStopInterceptor) {
        this.clientsPortType = ServiceUtils.getClientsClient(
                fcbPeopleSoftProperties.getClients().getUrl(),
                fcbPeopleSoftProperties.getChannel(),
                fcbPeopleSoftProperties.getPassword(),
                cxfClientAuditTracingStartInterceptor,
                cxfClientAuditTracingStopInterceptor);
    }

    public RespostaAltaClient altaClient(PeticioAltaClient peticioAltaClient) {
        RespostaAltaClient client = null;
        try {
            client = clientsPortType.altaClient(peticioAltaClient);
            if (!RESPONSE_OK.equals(client.getEstat())) {
                LOGGER.error("[FCB WEBHOOK] altaClient: {} - {}", client.getEstat(), client.getDescripcioEstat());
                client = null;
            }
        } catch (ClientsFault e) {
            LOGGER.error("[FCB WEBHOOK] altaClient: {} - {} - {} - {}", e.getFaultInfo().getCodiError(), e.getFaultInfo().getDescripcioError(), e.getFaultInfo().getCausa(), e.getFaultInfo().getSistema());
        }
        return client;
    }

    public RespostaCercaClient cercarClient(PeticioCercaClient peticioCercaClient) {
        RespostaCercaClient client = null;
        try {
            client = clientsPortType.cercarClient(peticioCercaClient);
            if (!RESPONSE_OK.equals(client.getEstat()) && !RESPONSE_OK_02.equals(client.getEstat())) {
                LOGGER.info("[FCB WEBHOOK] cercarClient: {} - {}", client.getEstat(), client.getDescripcioEstat());
                client = null;
            }
        } catch (ClientsFault e) {
            LOGGER.error("[FCB WEBHOOK] cercarClient: {} - {} - {} - {}", e.getFaultInfo().getCodiError(), e.getFaultInfo().getDescripcioError(), e.getFaultInfo().getCausa(), e.getFaultInfo().getSistema());
        }
        return client;
    }

    public RespostaValidacioNif validarNif(PeticioValidacioNif peticioValidacioNif) {
        RespostaValidacioNif respostaValidacioNif = null;
        try {
            respostaValidacioNif = clientsPortType.validarNif(peticioValidacioNif);
            if (!RESPONSE_OK.equals(respostaValidacioNif.getEstat())) {
                LOGGER.info("[FCB WEBHOOK] validarNif: {} - {}", respostaValidacioNif.getEstat(), respostaValidacioNif.getDescripcioEstat());
                respostaValidacioNif = null;
            }
        } catch (ClientsFault e) {
            LOGGER.error("[FCB WEBHOOK] validarNif: {} - {} - {} - {}", e.getFaultInfo().getCodiError(), e.getFaultInfo().getDescripcioError(), e.getFaultInfo().getCausa(), e.getFaultInfo().getSistema());
        }
        return respostaValidacioNif;
    }

    public RespostaModificacioClient modificarClient(PeticioModificacioClient peticioModificacioClient) {
        RespostaModificacioClient client = null;
        try {
            client = clientsPortType.modificarClient(peticioModificacioClient);
            if (!RESPONSE_OK.equals(client.getEstat())) {
                LOGGER.error("[FCB WEBHOOK] modificarClient: {} - {}", client.getEstat(), client.getDescripcioEstat());
                client = null;
            }
        } catch (ClientsFault e) {
            LOGGER.error("[FCB WEBHOOK] modificarClient: {} - {} - {} - {}", e.getFaultInfo().getCodiError(), e.getFaultInfo().getDescripcioError(), e.getFaultInfo().getCausa(), e.getFaultInfo().getSistema());
        }
        return client;
    }
}
