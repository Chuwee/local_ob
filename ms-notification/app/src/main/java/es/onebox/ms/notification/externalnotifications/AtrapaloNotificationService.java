package es.onebox.ms.notification.externalnotifications;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import es.onebox.ms.notification.externalnotifications.event.EventCriteria;
import es.onebox.ms.notification.externalnotifications.event.ExternalEventConsumeNotificationMessage;
import es.onebox.ms.notification.externalnotifications.factory.ExternalNotificationService;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.core.MultivaluedMap;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.CharEncoding.UTF_8;

@Service
public class AtrapaloNotificationService implements ExternalNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AtrapaloNotificationService.class);

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    @Autowired(required = false)
    @Qualifier("atrapaloNotificationHttpClient")
    private Client client;

    @Autowired
    private ExternalNotifications externalNotifications;

    @Override
    public void notificationEvent(ExternalEventConsumeNotificationMessage message) throws Exception {
        ClientResponse clientResponse = null;
        ExternalNotification externalNotification =
                this.externalNotifications.getExternalNotificationByChannel(message.getChannelId());
        try {
            if (message.getForceNotification() ||
                    (
                            (message.getOldEvent() != null && message.getOldEvent().getState().equals(EventCriteria.READY_EVENT)) ||
                                    (message.getNewEvent() != null && message.getNewEvent().getState().equals(EventCriteria.READY_EVENT))
                    )) {

                LOGGER.info("ATRAPALO notification for eventId: {}", message.getEventId());
                List<NameValuePair> nameValuePairs = new ArrayList(1);
                nameValuePairs.add(new BasicNameValuePair("idEvent", String.valueOf(message.getEventId())));

                clientResponse =
                        this.doPost(nameValuePairs, externalNotification.getPassword(), externalNotification.getNotificationUrl());

                if (clientResponse.getStatus() < 200 || clientResponse.getStatus() >= 300) {
                    LOGGER.error("ERROR (code != 200) Atrapalo notification for event id " + message.getEventId());
                    throw new IllegalArgumentException("ERROR (code != 200) Atrapalo notification for event id " + message.getEventId() + ")" +
                            " con codigo respuesta: " + clientResponse.getStatus());
                }
                LOGGER.info("OK Atrapalo notification for event id {} with body: {}", message.getEventId(), clientResponse.getEntity(String.class));
            }

        } catch (ClientHandlerException exception) {
            if (exception.getCause() instanceof SocketTimeoutException) {
                LOGGER.error("ERROR Se ha producido un timeout esperando la respuesta");
                throw new IllegalArgumentException("Error notificando cambios de evento (" + message.getEventId() + ") " +
                        "por timeout");
            } else if (exception.getCause() instanceof ConnectException) {
                LOGGER.error("ERROR Se ha producido un error de conexión");
                throw new IllegalArgumentException("Error notificando cambios de evento (" + message.getEventId() + ") " +
                        "por conexión");
            }
        } catch (Exception e) {
            LOGGER.error("ERROR" + e.getMessage());
            throw e;
        } finally {
            if (clientResponse != null) {
                clientResponse.close();
            }
        }
    }

    private ClientResponse doPost(List<NameValuePair> nameValuePairs, String password, String url)
            throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {

        // add provider parameter
        nameValuePairs.add(new BasicNameValuePair("provider", "onebox"));

        addSignature(nameValuePairs, password);

        // Llamada nueva
        WebResource resource = client.resource(url);
        MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        for (NameValuePair nameValuePair : nameValuePairs) {
            formData.add(nameValuePair.getName(), nameValuePair.getValue());
        }
        return resource.post(ClientResponse.class, formData);
    }

    protected void addSignature(List<NameValuePair> nameValuePairs, String privateKey) throws NoSuchAlgorithmException,
            InvalidKeyException, UnsupportedEncodingException {

        Collections.sort(nameValuePairs, (o1, o2) -> {
            int nameCmp = o1.getName().compareTo(o2.getName());
            if (nameCmp != 0) {
                return nameCmp;
            } else {
                return o1.getValue().compareTo(o2.getValue());
            }
        });
        StringBuilder sb = new StringBuilder("EventSesionChangesNotification");
        for (NameValuePair nameValuePair : nameValuePairs) {
            sb.append("|").append(nameValuePair.toString());
        }
        String signature = calculateRFC2104HMAC(sb.toString(), privateKey);
        nameValuePairs.add(new BasicNameValuePair("ob_signature", signature));
        LOGGER.info("Atrapalo Notification signature: " + sb.toString() + " - " + signature + " - " + privateKey);
    }

    private static String calculateRFC2104HMAC(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        String result;


        // get an hmac_sha1 key from the raw key bytes
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(UTF_8), HMAC_SHA1_ALGORITHM);

        // get an hmac_sha1 Mac instance and initialize with the signing key
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);

        // compute the hmac on input data bytes
        byte[] rawHmac = mac.doFinal(data.getBytes(UTF_8));
        byte[] base64 = org.apache.commons.codec.binary.Base64.encodeBase64(rawHmac);
        // base64-encode the hmac
        result = new String(base64);


        return result;
    }
}
