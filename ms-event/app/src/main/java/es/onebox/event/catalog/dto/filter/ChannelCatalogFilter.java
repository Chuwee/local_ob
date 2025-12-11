package es.onebox.event.catalog.dto.filter;

import es.onebox.core.serializer.dto.request.FilterWithOperator;
import es.onebox.event.catalog.dto.ChannelCatalogField;
import es.onebox.event.common.Pageable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public abstract class ChannelCatalogFilter extends ChannelCatalogTypeFilter implements Pageable {

    @Serial
    private static final long serialVersionUID = -7543684169532369514L;

    public static final long DEFAULT_MAX_LIMIT = 100L;

    @Min(1L)
    @Max(DEFAULT_MAX_LIMIT)
    private Long limit;

    @Min(0L)
    private Long offset;

    private Long agencyId;
    private Boolean forSale;
    private Boolean soldOut;
    private Boolean onCatalog;
    private Boolean onCarousel;
    private List<FilterWithOperator<ZonedDateTime>> publishDate;
    private List<FilterWithOperator<ZonedDateTime>> startDate;
    private List<FilterWithOperator<ZonedDateTime>> endDate;
    private List<FilterWithOperator<ZonedDateTime>> startLocalDate;
    private List<FilterWithOperator<ZonedDateTime>> startSaleDate;
    private List<FilterWithOperator<ZonedDateTime>> endSaleDate;
    private List<ChannelCatalogField> field;

    public Long getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Long agencyId) {
        this.agencyId = agencyId;
    }

    @Override
    public Long getLimit() {
        return limit;
    }

    @Override
    public void setLimit(Long limit) {
        this.limit = limit;
    }

    @Override
    public Long getOffset() {
        return offset;
    }

    @Override
    public void setOffset(Long offset) {
        this.offset = offset;
    }


    public Boolean getForSale() {
        return forSale;
    }

    public void setForSale(Boolean forSale) {
        this.forSale = forSale;
    }

    public Boolean getSoldOut() {
        return soldOut;
    }

    public void setSoldOut(Boolean soldOut) {
        this.soldOut = soldOut;
    }

    public Boolean getOnCatalog() {
        return onCatalog;
    }

    public void setOnCatalog(Boolean onCatalog) {
        this.onCatalog = onCatalog;
    }

    public Boolean getOnCarousel() {
        return onCarousel;
    }

    public void setOnCarousel(Boolean onCarousel) {
        this.onCarousel = onCarousel;
    }

    public List<FilterWithOperator<ZonedDateTime>> getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(List<FilterWithOperator<ZonedDateTime>> publishDate) {
        this.publishDate = publishDate;
    }

    public List<FilterWithOperator<ZonedDateTime>> getStartDate() {
        return startDate;
    }

    public void setStartDate(List<FilterWithOperator<ZonedDateTime>> startDate) {
        this.startDate = startDate;
    }

    public List<FilterWithOperator<ZonedDateTime>> getEndDate() {
        return endDate;
    }

    public void setEndDate(List<FilterWithOperator<ZonedDateTime>> endDate) {
        this.endDate = endDate;
    }

    public List<FilterWithOperator<ZonedDateTime>> getStartSaleDate() {
        return startSaleDate;
    }

    public List<FilterWithOperator<ZonedDateTime>> getStartLocalDate() {
        return startLocalDate;
    }

    public void setStartLocalDate(List<FilterWithOperator<ZonedDateTime>> startLocalDate) {
        this.startLocalDate = startLocalDate;
    }

    public void setStartSaleDate(List<FilterWithOperator<ZonedDateTime>> startSaleDate) {
        this.startSaleDate = startSaleDate;
    }

    public List<FilterWithOperator<ZonedDateTime>> getEndSaleDate() {
        return endSaleDate;
    }

    public void setEndSaleDate(List<FilterWithOperator<ZonedDateTime>> endSaleDate) {
        this.endSaleDate = endSaleDate;
    }

    public List<ChannelCatalogField> getField() {
        return field;
    }

    public void setField(List<ChannelCatalogField> field) {
        this.field = field;
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
