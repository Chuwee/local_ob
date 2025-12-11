package es.onebox.event.priceengine.simulation.record;

import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;

import java.io.Serial;

public class ProductRecord extends CpanelProductRecord {
    @Serial
    private static final long serialVersionUID = -5787408743188817261L;

    private String entityName;
    private String producerName;
    private String taxName;
    private String surchargeTaxName;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getProducerName() {
        return producerName;
    }

    public void setProducerName(String producerName) {
        this.producerName = producerName;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public String getSurchargeTaxName() {
        return surchargeTaxName;
    }

    public void setSurchargeTaxName(String surchargeTaxName) {
        this.surchargeTaxName = surchargeTaxName;
    }
}
