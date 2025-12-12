package es.onebox.eci.ticketsales.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class SaleOperation implements Serializable {

    @Serial
    private static final long serialVersionUID = 1896730348984234338L;

    private String identifier;
    @JsonProperty("sale_type")
    private String saleType;
    @JsonProperty("related_sale_operation")
    private RelatedSaleOperation relatedSaleOperation;

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getSaleType() {
        return saleType;
    }

    public void setSaleType(String saleType) {
        this.saleType = saleType;
    }

    public RelatedSaleOperation getRelatedSaleOperation() {
        return relatedSaleOperation;
    }

    public void setRelatedSaleOperation(RelatedSaleOperation relatedSaleOperation) {
        this.relatedSaleOperation = relatedSaleOperation;
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
