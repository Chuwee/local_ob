package es.onebox.atm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("atm.entity")
public class ATMEntityConfiguration {

    private List<Long> allowedEntities;
    private Long entityId;

    public List<Long> getAllowedEntities() {
        return allowedEntities;
    }

    public void setAllowedEntities(List<Long> allowedEntities) {
        this.allowedEntities = allowedEntities;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }
}
