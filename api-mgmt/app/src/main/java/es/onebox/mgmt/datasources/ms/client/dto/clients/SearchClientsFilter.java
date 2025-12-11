package es.onebox.mgmt.datasources.ms.client.dto.clients;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class SearchClientsFilter implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer entityId;
    private String keyword;
    private Integer clientTypeId;
    private Boolean active;
    private SortableElement sortBy;
    private Boolean countAllElements;
    private Boolean sortAsc;
    private Boolean invertEntityIdFilter;
    private String taxId;

    private List<Long> clientsId;
    private Integer from;
    private Integer amount;

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getClientTypeId() {
        return clientTypeId;
    }

    public void setClientTypeId(Integer clientTypeId) {
        this.clientTypeId = clientTypeId;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public SortableElement getSortBy() {
        return sortBy;
    }

    public void setSortBy(SortableElement sortBy) {
        this.sortBy = sortBy;
    }

    public Boolean getCountAllElements() {
        return countAllElements;
    }

    public void setCountAllElements(Boolean countAllElements) {
        this.countAllElements = countAllElements;
    }

    public Boolean getSortAsc() {
        return sortAsc;
    }

    public void setSortAsc(Boolean sortAsc) {
        this.sortAsc = sortAsc;
    }

    public Boolean getInvertEntityIdFilter() {
        return invertEntityIdFilter;
    }

    public void setInvertEntityIdFilter(Boolean invertEntityIdFilter) {
        this.invertEntityIdFilter = invertEntityIdFilter;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public List<Long> getClientsId() {
        return clientsId;
    }

    public void setClientsId(List<Long> clientsId) {
        this.clientsId = clientsId;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
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
