package es.onebox.mgmt.collectives.collectivecodes.dto.request;

import jakarta.validation.constraints.NotNull;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class UpdateCollectiveCodesBulkUnifiedRequest implements Serializable {

    private static final long serialVersionUID = -5315644685016573041L;

    private List<String> codes;
    @NotNull
    private UpdateCollectiveCodesBulkUnifiedData data;

    public List<String> getCodes() {
        return codes;
    }

    public void setCodes(List<String> codes) {
        this.codes = codes;
    }

    public UpdateCollectiveCodesBulkUnifiedData getData() {
        return data;
    }

    public void setData(UpdateCollectiveCodesBulkUnifiedData data) {
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
