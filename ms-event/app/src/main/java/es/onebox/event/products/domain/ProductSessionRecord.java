package es.onebox.event.products.domain;

import es.onebox.jooq.cpanel.tables.records.CpanelProductSessionRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelSesionRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductSessionRecord extends CpanelProductSessionRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private CpanelSesionRecord session;

    public CpanelSesionRecord getSession() {
        return session;
    }

    public void setSession(CpanelSesionRecord session) {
        this.session = session;
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
