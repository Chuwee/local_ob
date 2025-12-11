package es.onebox.mgmt.datasources.api.accounting.dto;

import es.onebox.core.serializer.param.ZonedDateTimeParam;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class SearchTransactionsFilter implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long clientId;
    private ZonedDateTimeParam dateFrom;
    private ZonedDateTimeParam dateTo;
    private MovementType movementType;
    private String freeText;
    private Integer fromElement;
    private Integer numberOfResults;
    private Long providerId;
    private String currencyCode;

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public ZonedDateTimeParam getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(ZonedDateTimeParam dateFrom) {
        this.dateFrom = dateFrom;
    }

    public ZonedDateTimeParam getDateTo() {
        return dateTo;
    }

    public void setDateTo(ZonedDateTimeParam dateTo) {
        this.dateTo = dateTo;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public String getFreeText() {
        return freeText;
    }

    public void setFreeText(String freeText) {
        this.freeText = freeText;
    }

    public Integer getFromElement() {
        return fromElement;
    }

    public void setFromElement(Integer fromElement) {
        this.fromElement = fromElement;
    }

    public Integer getNumberOfResults() {
        return numberOfResults;
    }

    public void setNumberOfResults(Integer numberOfResults) {
        this.numberOfResults = numberOfResults;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
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
