package es.onebox.mgmt.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.core.serializer.dto.request.SortOperator;
import es.onebox.mgmt.common.BaseEntityRequestFilter;
import es.onebox.mgmt.products.enums.DeliveryPointStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class SearchDeliveryPointFilterDTO extends BaseEntityRequestFilter {

    @Serial
    private static final long serialVersionUID = 1L;

    private String q;
    @JsonProperty("country")
    private String country;
    @JsonProperty("country_subdivision")
    private String countrySubdivision;
    private List<DeliveryPointStatus> status;
    private SortOperator<String> sort;

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountrySubdivision() {
        return countrySubdivision;
    }

    public void setCountrySubdivision(String countrySubdivision) {
        this.countrySubdivision = countrySubdivision;
    }

    public List<DeliveryPointStatus> getStatus() {
        return status;
    }

    public void setStatus(List<DeliveryPointStatus> status) {
        this.status = status;
    }

    public SortOperator<String> getSort() {
        return sort;
    }

    public void setSort(SortOperator<String> sort) {
        this.sort = sort;
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
