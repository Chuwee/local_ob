package es.onebox.event.products.domain;

import es.onebox.jooq.cpanel.tables.records.CpanelProductChannelRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ProductChannelRecord extends CpanelProductChannelRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String productName;
    private String channelName;
    private Integer entityId;
    private String entityName;
    private String entityLogoPath;
    private Integer channelSubtypeId;
    private Integer productSaleRequestsStatusId;
    private Integer entityOperatorId;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityLogoPath() {
        return entityLogoPath;
    }

    public void setEntityLogoPath(String entityLogoPath) {
        this.entityLogoPath = entityLogoPath;
    }

    public Integer getChannelSubtypeId() {
        return channelSubtypeId;
    }

    public void setChannelSubtypeId(Integer channelSubtypeId) {
        this.channelSubtypeId = channelSubtypeId;
    }

    public Integer getProductSaleRequestsStatusId() {
        return productSaleRequestsStatusId;
    }

    public void setProductSaleRequestsStatusId(Integer productSaleRequestsStatusId) {
        this.productSaleRequestsStatusId = productSaleRequestsStatusId;
    }

    public Integer getEntityOperatorId() {
        return entityOperatorId;
    }

    public void setEntityOperatorId(Integer entityOperatorId) {
        this.entityOperatorId = entityOperatorId;
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
