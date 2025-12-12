package es.onebox.fcb.datasources.peoplesoft.utils;

import es.onebox.audit.cxf.interceptor.CxfClientAuditTracingStartInterceptor;
import es.onebox.audit.cxf.interceptor.CxfClientAuditTracingStopInterceptor;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.ClientsPortType;
import es.onebox.fcb.datasources.peoplesoft.wsdl.factures.FacturesPortType;
import es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria.ServeiTresoreriaPortType;
import jakarta.xml.ws.BindingProvider;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.apache.cxf.ws.policy.PolicyEngine;
import org.apache.cxf.ws.policy.PolicyEngineImpl;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.handler.WSHandlerConstants;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

public class ServiceUtils {

    private static final long CONNECTION_TIMEOUT = 10000L;
    private static final long RECEIVE_TIMEOUT = 20000L;

    public static FacturesPortType getFacturesClient(String urlFactures, String user, String password,
                                                     CxfClientAuditTracingStartInterceptor cxfClientAuditTracingStartInterceptor,
                                                     CxfClientAuditTracingStopInterceptor cxfClientAuditTracingStopInterceptor) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(FacturesPortType.class);
        factory.setAddress(urlFactures);
        FacturesPortType port = (FacturesPortType) factory.create();

        configureClient(user, password, cxfClientAuditTracingStartInterceptor, cxfClientAuditTracingStopInterceptor, (BindingProvider) port);

        return port;
    }

    public static ClientsPortType getClientsClient(String urlClients, String user, String password,
                                                   CxfClientAuditTracingStartInterceptor cxfClientAuditTracingStartInterceptor,
                                                   CxfClientAuditTracingStopInterceptor cxfClientAuditTracingStopInterceptor) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(ClientsPortType.class);
        factory.setAddress(urlClients);
        ClientsPortType port = (ClientsPortType) factory.create();

        configureClient(user, password, cxfClientAuditTracingStartInterceptor, cxfClientAuditTracingStopInterceptor, (BindingProvider) port);

        return port;
    }

    public static ServeiTresoreriaPortType getClientsTresoreria(String urlTresoreria, String user, String password,
                                                                CxfClientAuditTracingStartInterceptor cxfClientAuditTracingStartInterceptor,
                                                                CxfClientAuditTracingStopInterceptor cxfClientAuditTracingStopInterceptor) {
        JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
        factory.setServiceClass(ServeiTresoreriaPortType.class);
        factory.setAddress(urlTresoreria);
        ServeiTresoreriaPortType port = (ServeiTresoreriaPortType) factory.create();

        configureClient(user, password, cxfClientAuditTracingStartInterceptor, cxfClientAuditTracingStopInterceptor, (BindingProvider) port);

        return port;
    }

    private static void configureClient(String user, String password, CxfClientAuditTracingStartInterceptor cxfClientAuditTracingStartInterceptor, CxfClientAuditTracingStopInterceptor cxfClientAuditTracingStopInterceptor, BindingProvider port) {
        Client client = ClientProxy.getClient(port);
        addTimeOut(client);
        addInterceptors(client, cxfClientAuditTracingStartInterceptor, cxfClientAuditTracingStopInterceptor);
        addCredentials(client, user, password);
        attachSSLContext(client);
    }

    private static void attachSSLContext(Client client) {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            TLSClientParameters tlsParams = new TLSClientParameters();
            tlsParams.setSSLSocketFactory(sslContext.getSocketFactory());
            tlsParams.setDisableCNCheck(true);
            tlsParams.setTrustManagers(trustAllCerts);

            HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
            httpConduit.setTlsClientParameters(tlsParams);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void addCredentials(Client client, String username, String password) {
        Map<String, Object> outProps = new HashMap<>();
        outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
        outProps.put(WSHandlerConstants.USER, username);
        outProps.put(WSHandlerConstants.PASSWORD_TYPE, WSConstants.PW_TEXT);
        outProps.put(WSHandlerConstants.PW_CALLBACK_REF, new PasswordCallback(username, password));
        WSS4JOutInterceptor outInterceptor = new WSS4JOutInterceptor(outProps);
        client.getOutInterceptors().add(outInterceptor);
    }

    private static void addTimeOut(Client client){
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setConnectionTimeout(CONNECTION_TIMEOUT);
        httpClientPolicy.setReceiveTimeout(RECEIVE_TIMEOUT);
        httpConduit.setClient(httpClientPolicy);
    }

    public static void addInterceptors(Client client, CxfClientAuditTracingStartInterceptor cxfClientAuditTracingStartInterceptor,
                                       CxfClientAuditTracingStopInterceptor cxfClientAuditTracingStopInterceptor) {
        if (cxfClientAuditTracingStartInterceptor != null) {
            client.getOutInterceptors().add(cxfClientAuditTracingStartInterceptor);
        }
        if (cxfClientAuditTracingStopInterceptor != null) {
            client.getInInterceptors().add(cxfClientAuditTracingStopInterceptor);
        }
    }

    private static void disablePolicyEngineConfig() {
        SpringBus springBus = new SpringBus();
        PolicyEngine policyEngine = new PolicyEngineImpl();
        policyEngine.setEnabled(false);
        springBus.setExtension(policyEngine, PolicyEngine.class);
    }
}