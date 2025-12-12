package es.onebox.internal.automaticsales.processsales.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.internal.automaticsales.filemanagement.dto.SaleRequestListDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AutomaticSaleRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -1310784601034919323L;

    @JsonProperty("channel_id")
    private Long channelId;

    @JsonProperty("source_code")
    private String sourceCode;

    @JsonProperty("customer_data")
    private CustomerData customerData;

    private List<AutomaticSaleItem> items;

    private Boolean invitation;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public CustomerData getCustomerData() {
        return customerData;
    }

    public void setCustomerData(CustomerData customerData) {
        this.customerData = customerData;
    }

    public List<AutomaticSaleItem> getItems() {
        return items;
    }

    public void setItems(List<AutomaticSaleItem> items) {
        this.items = items;
    }

    public Boolean getInvitation() {
        return invitation;
    }

    public void setInvitation(Boolean invitation) {
        this.invitation = invitation;
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
