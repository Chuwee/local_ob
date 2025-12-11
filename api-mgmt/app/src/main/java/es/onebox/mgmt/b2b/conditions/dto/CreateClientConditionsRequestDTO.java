package es.onebox.mgmt.b2b.conditions.dto;

import es.onebox.core.serializer.dto.common.IdDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class CreateClientConditionsRequestDTO extends IdDTO  {

    @Serial
    private static final long serialVersionUID = 2L;

    private ConditionManager conditionManager;
    private List<CreateConditionsRequestDTO> clients;

    public CreateClientConditionsRequestDTO() {
    }

    public List<CreateConditionsRequestDTO> getClients() {
        return clients;
    }
    public void setClients(List<CreateConditionsRequestDTO> clients) {
        this.clients = clients;
    }

    public List<CreateConditionDTO<?>> getConditions() {
        return conditionManager.getConditions();
    }

    public List<CreateConditionDTO<?>> getConditionsClients() {
        return clients.get(0).getConditions();
    }

    public void setConditions(List<CreateConditionDTO<?>> conditions) {
        this.conditionManager = new ConditionManager(conditions);
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