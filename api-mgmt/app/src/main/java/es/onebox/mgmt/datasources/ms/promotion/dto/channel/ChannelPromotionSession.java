package es.onebox.mgmt.datasources.ms.promotion.dto.channel;

import es.onebox.mgmt.sessions.enums.SessionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelPromotionSession implements Serializable {

    private static final long serialVersionUID = -2123212067966748495L;

    private Long id;
    private String name;
    private SessionDate date;
    private Integer catalogSaleRequestId;
    private SessionType type;

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

    public SessionDate getDate() {
        return date;
    }

    public void setDate(SessionDate date) {
        this.date = date;
    }

    public Integer getCatalogSaleRequestId() {
        return catalogSaleRequestId;
    }

    public void setCatalogSaleRequestId(Integer catalogSaleRequestId) {
        this.catalogSaleRequestId = catalogSaleRequestId;
    }

    public SessionType getType() {
        return type;
    }

    public void setType(SessionType type) {
        this.type = type;
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
