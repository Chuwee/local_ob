package es.onebox.ms.notification.ie.utils;

import es.flc.ie.model.Status;
import es.onebox.ms.notification.config.ExternalManagementClient;
import es.onebox.ms.notification.datasources.integration.ExternalDatasource;
import es.onebox.ms.notification.datasources.ms.entity.dto.ExternalMgmtConfig;
import es.onebox.ms.notification.datasources.ms.entity.repository.EntitiesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("IERequestGenerator")
public class IERequestGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(IERequestGenerator.class);

    @Autowired
    private ExternalManagementClient client;
    @Autowired
    private EntitiesRepository entitiesRepository;
    @Autowired
    private ExternalDatasource externalDatasource;

    private Map<String, EndPointHolder> endPointHolderMap = new HashMap<>();

    private String getEndPointUrl(Integer entityId, Integer endPointType) {
        String hashCode = EndPointHolder.generateHashCode(entityId, endPointType);
        EndPointHolder endPointHolder = null;
        if (endPointHolderMap.containsKey(hashCode)) {
            endPointHolder = endPointHolderMap.get(hashCode);
        }

        if (endPointHolder == null) {
            List<ExternalMgmtConfig> externalMgmtConfig = entitiesRepository.getExternalMgmtConfig(entityId.longValue());
            String endpointUrl = externalMgmtConfig.stream().filter(e -> e.getEndpointType().equals(endPointType)).
                    findFirst().map(ExternalMgmtConfig::getEndpointUrl).orElse(null);
            endPointHolder = new EndPointHolder(entityId, endPointType, endpointUrl);
            endPointHolderMap.put(hashCode, endPointHolder);
        }

        return endPointHolder.getEndPointUrl();
    }

    public Status executeCall(Integer entityId, EntityExternalManagementConfigEndpointType endPointType, Object body) {

        String url = getEndPointUrl(entityId, endPointType.getId());

        LOG.info("External Management URL: {}", url);

        if (client.getUser() == null || client.getPassword() == null) {
            LOG.warn("No username and/or password set for ExternalManagementClient");
        }

        var headers = getHeaders();
        Status response = this.externalDatasource.call(url, body, headers);
        if (response != null) {
            LOG.info("Incompatibility Engine Response {} - {} ", response.getCode(), response.getMessage());
        }
        return response;

    }

    private Map<String, String> getHeaders() {
        var headers = new HashMap<String, String>();
        headers.put("j_username", client.getUser());
        headers.put("j_password", client.getPassword());
        headers.put(HttpHeaders.USER_AGENT, "CXF-UNA");
        headers.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
}
