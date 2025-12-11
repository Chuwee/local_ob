package es.onebox.mgmt.datasources.integration.dispatcher.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class InventoriesList extends ArrayList<Inventory> {

    @Serial
    private static final long serialVersionUID = -3410493485896214825L;
    private List<AforoInfo> aforo;

    public InventoriesList(List<Inventory> inventories) {
        super(inventories);
    }

    public InventoriesList() {}

    public List<AforoInfo> getAforo() {
        return aforo;
    }

    public void setAforo(List<AforoInfo> aforo) {
        this.aforo = aforo;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
