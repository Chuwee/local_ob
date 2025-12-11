package es.onebox.event.datasources.integration.dispatcher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.event.datasources.integration.dispatcher.enums.ConnectorRelationType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ConnectorRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private ConnectorRelationType relationType;

    private Integer apiId;

    @JsonProperty("connectorID")
    private Integer connectorId;

    @JsonProperty("relationID")
    private Integer relationId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ConnectorRelationType getRelationType() {
        return relationType;
    }

    public void setRelationType(ConnectorRelationType relationType) {
        this.relationType = relationType;
    }

    public Integer getApiId() {
        return apiId;
    }

    public void setApiId(Integer apiId) {
        this.apiId = apiId;
    }

    public Integer getConnectorId() {
        return connectorId;
    }

    public void setConnectorId(Integer connectorId) {
        this.connectorId = connectorId;
    }

    public Integer getRelationId() {
        return relationId;
    }

    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
