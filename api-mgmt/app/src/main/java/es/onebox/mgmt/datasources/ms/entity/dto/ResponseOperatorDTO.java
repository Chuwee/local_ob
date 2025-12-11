package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals.BaseTerminal;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

public class ResponseOperatorDTO extends Entity {

    private static final long serialVersionUID = 1L;

    private List<String> gateways = new ArrayList<>();
    private BaseTerminal terminal;

    public List<String> getGateways() {
        return gateways;
    }

    public void setGateways(List<String> gateways) {
        this.gateways = gateways;
    }

    public BaseTerminal getTerminal() {
        return terminal;
    }

    public void setTerminal(BaseTerminal terminal) {
        this.terminal = terminal;
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
