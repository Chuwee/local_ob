package es.onebox.event.products.domain;

import es.onebox.jooq.cpanel.tables.records.CpanelProductSessionDeliveryPointRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

public class ProductSessionDeliveryPointRecord extends CpanelProductSessionDeliveryPointRecord implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String productName;
    private String productDeliveryPointName;
    private String sessionName;
    private Timestamp sessionStart;
    private Timestamp sessionEnd;
    private Integer sessionType;
    private Integer templateType;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDeliveryPointName() {
        return productDeliveryPointName;
    }

    public void setProductDeliveryPointName(String productDeliveryPointName) {
        this.productDeliveryPointName = productDeliveryPointName;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public Timestamp getSessionStart() {
        return sessionStart;
    }

    public void setSessionStart(Timestamp sessionStart) {
        this.sessionStart = sessionStart;
    }

    public Timestamp getSessionEnd() {
        return sessionEnd;
    }

    public void setSessionEnd(Timestamp sessionEnd) {
        this.sessionEnd = sessionEnd;
    }

    public Integer getSessionType() {
        return sessionType;
    }

    public void setSessionType(Integer sessionType) {
        this.sessionType = sessionType;
    }

    public Integer getTemplateType() {
        return templateType;
    }

    public void setTemplateType(Integer templateType) {
        this.templateType = templateType;
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
