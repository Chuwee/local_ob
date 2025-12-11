package es.onebox.mgmt.datasources.ms.collective.dto.request;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class MsUpdateCollectiveCodesBulkUnifiedDTO implements Serializable {

    private static final long serialVersionUID = -5315644685016573041L;

    private List<String> codes;
    private MsUpdateCollectiveCodesBulkUnifiedData data;

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    public MsUpdateCollectiveCodesBulkUnifiedData getData() {
        return data;
    }

    public void setData(MsUpdateCollectiveCodesBulkUnifiedData data) {
        this.data = data;
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
