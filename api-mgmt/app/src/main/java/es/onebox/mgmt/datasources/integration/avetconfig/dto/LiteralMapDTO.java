package es.onebox.mgmt.datasources.integration.avetconfig.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class LiteralMapDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4313056753440581540L;

    private Map<String, Object> listLiteralMap = new HashMap<String, Object>();

    public Map<String, Object> getListLiteralMap() {
        return listLiteralMap;
    }

    public void setListLiteralMap(Map<String, Object> listLiteralMap) {
        this.listLiteralMap = listLiteralMap;
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
