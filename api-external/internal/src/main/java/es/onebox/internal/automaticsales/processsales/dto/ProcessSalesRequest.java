package es.onebox.internal.automaticsales.processsales.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.internal.automaticsales.filemanagement.dto.SaleRequestListDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProcessSalesRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -1310784601034919323L;

    @JsonProperty("config")
    private ProcessSalesConfigurationRequest processSalesConfigurationRequest;
    @JsonProperty("sales")
    private SaleRequestListDTO saleRequestListDTO;

    public ProcessSalesConfigurationRequest getProcessSalesConfigurationRequest() {
        return processSalesConfigurationRequest;
    }

    public void setProcessSalesConfigurationRequest(ProcessSalesConfigurationRequest processSalesConfigurationRequest) {
        this.processSalesConfigurationRequest = processSalesConfigurationRequest;
    }

    public SaleRequestListDTO getSaleRequestListDTO() {
        return saleRequestListDTO;
    }

    public void setSaleRequestListDTO(SaleRequestListDTO saleRequestListDTO) {
        this.saleRequestListDTO = saleRequestListDTO;
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
        return ToStringBuilder.reflectionToString(this);
    }
}
