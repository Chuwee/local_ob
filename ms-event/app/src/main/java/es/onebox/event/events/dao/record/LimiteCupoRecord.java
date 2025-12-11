package es.onebox.event.events.dao.record;

import es.onebox.jooq.cpanel.tables.records.CpanelCuposConfigRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class LimiteCupoRecord extends CpanelCuposConfigRecord {

    private Integer limite;

    public Integer getLimite() {
        return limite;
    }

    public void setLimite(Integer limite) {
        this.limite = limite;
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
        return "LimiteCupoRecord{" +
                "limite=" + limite +
                "} " + super.toString();
    }
}
