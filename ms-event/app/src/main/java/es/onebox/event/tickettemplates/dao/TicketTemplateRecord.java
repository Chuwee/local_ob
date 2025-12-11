package es.onebox.event.tickettemplates.dao;

import es.onebox.jooq.cpanel.tables.records.CpanelPlantillaTicketRecord;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class TicketTemplateRecord extends CpanelPlantillaTicketRecord {

    private static final long serialVersionUID = 1L;

    private String entityName;
    private Integer operatorId;
    private String modelName;
    private String modelDescription;
    private Integer modelFormat;
    private String modelPrinter;
    private String modelPaper;
    private String modelOrientation;
    private String jasperModel;
    private Integer modelType;

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelDescription() {
        return modelDescription;
    }

    public void setModelDescription(String modelDescription) {
        this.modelDescription = modelDescription;
    }

    public Integer getModelFormat() {
        return modelFormat;
    }

    public void setModelFormat(Integer modelFormat) {
        this.modelFormat = modelFormat;
    }

    public String getModelPrinter() {
        return modelPrinter;
    }

    public void setModelPrinter(String modelPrinter) {
        this.modelPrinter = modelPrinter;
    }

    public String getModelPaper() {
        return modelPaper;
    }

    public void setModelPaper(String modelPaper) {
        this.modelPaper = modelPaper;
    }

    public String getModelOrientation() {
        return modelOrientation;
    }

    public void setModelOrientation(String modelOrientation) {
        this.modelOrientation = modelOrientation;
    }

    public String getJasperModel() {
        return jasperModel;
    }

    public void setJasperModel(String jasperModel) {
        this.jasperModel = jasperModel;
    }

    public Integer getModelType() {
        return modelType;
    }

    public void setModelType(Integer modelType) {
        this.modelType = modelType;
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
