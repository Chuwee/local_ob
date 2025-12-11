package es.onebox.mgmt.datasources.ms.client.dto.clients;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;


public class ClientUsers implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ClientUser> clientUserList;

    private Integer totalElements;

    private Integer amount;

    private Integer from;

    public List<ClientUser> getClientUserList() {
        return clientUserList;
    }

    public void setClientUserList(List<ClientUser> clientUserList) {
        this.clientUserList = clientUserList;
    }

    public Integer getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
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
