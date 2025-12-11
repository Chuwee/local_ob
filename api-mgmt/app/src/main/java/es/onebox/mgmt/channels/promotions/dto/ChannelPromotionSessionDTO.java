package es.onebox.mgmt.channels.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.sessions.enums.SessionType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ChannelPromotionSessionDTO implements Serializable {

    private static final long serialVersionUID = -8061105847538611953L;

    private Long id;
    private String name;
    private SessionDateDTO dates;
    private SessionType type;

    @JsonProperty("catalog_sale_request_id")
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

    public SessionDateDTO getDates() {
        return dates;
    }

    public void setDates(SessionDateDTO dates) {
        this.dates = dates;
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
