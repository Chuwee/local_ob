package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class ChannelPromotionEvent implements Serializable {

    private static final long serialVersionUID = -3953568466690575867L;

    private Long id;
    private String name;
    private ZonedDateTime startDate;
    private Integer catalogSaleRequestId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public Integer getCatalogSaleRequestId() {
        return catalogSaleRequestId;
    }

    public void setCatalogSaleRequestId(Integer catalogSaleRequestId) {
        this.catalogSaleRequestId = catalogSaleRequestId;
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
