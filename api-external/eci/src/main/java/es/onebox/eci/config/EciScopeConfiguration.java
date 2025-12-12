package es.onebox.eci.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties("eci")
public class EciScopeConfiguration {

    private List<Long> entities;
    private Long operatorId;

    public List<Long> getEntities() {
        return entities;
    }

    public void setEntities(List<Long> entities) {
        this.entities = entities;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }
}
