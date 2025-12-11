package es.onebox.mgmt.queueit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.queue_it.connector.integrationconfig.CustomerIntegration;
import es.onebox.mgmt.datasources.queueit.repository.QueueItRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class QueueItService {

    private final QueueItRepository queueItRepository;
    private final ObjectMapper jacksonMapper;

    @Autowired
    public QueueItService(QueueItRepository queueItRepository, ObjectMapper jacksonMapper) {
        this.queueItRepository = queueItRepository;
        this.jacksonMapper = jacksonMapper;
    }

    public CustomerIntegration getCustomerIntegrationConfiguration(Long entityId) {
        CustomerIntegration customerIntegration = null;
        try {
            String integrationConfigJSON = queueItRepository.getCustomerIntegrationConfiguration(entityId);
            customerIntegration = buildResponse(integrationConfigJSON, CustomerIntegration.class, jacksonMapper);
        } catch (Exception ex) {

        }
        return customerIntegration;
    }

    public static <T> T buildResponse(String responseString, Class<T> returnType, ObjectMapper jacksonMapper) throws IOException {
        return !responseString.isEmpty() ? jacksonMapper.readValue(responseString, returnType) : null;
    }
}
