package es.onebox.mgmt.datasources.ms.accesscontrol.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class ProductResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String productCode;
    private Integer productTypeId;
    private String productTypeName;
    private String productType;
    private String productTitle;
    private String startDate;
    private String endDate;
    private String productGameNumber;
    private Boolean isLeague;
    private String productTeams;
    private String printTime;
    private String productSeasonId;
    private Integer eventTypeId;
    private String eventName;
    private String masterProductCode;

    public String getProductCode() {

        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(Integer productTypeId) {
        this.productTypeId = productTypeId;
    }

    public String getProductTypeName() {
        return productTypeName;
    }

    public void setProductTypeName(String productTypeName) {
        this.productTypeName = productTypeName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductTitle() {
        return productTitle;
    }

    public void setProductTitle(String productTitle) {
        this.productTitle = productTitle;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getProductGameNumber() {
        return productGameNumber;
    }

    public void setProductGameNumber(String productGameNumber) {
        this.productGameNumber = productGameNumber;
    }

    public Boolean getIsLeague() {
        return isLeague;
    }

    public void setIsLeague(Boolean isLeague) {
        this.isLeague = isLeague;
    }

    public String getProductTeams() {
        return productTeams;
    }

    public void setProductTeams(String productTeams) {
        this.productTeams = productTeams;
    }

    public String getPrintTime() {
        return printTime;
    }

    public void setPrintTime(String printTime) {
        this.printTime = printTime;
    }

    public String getProductSeasonId() {
        return productSeasonId;
    }

    public void setProductSeasonId(String productSeasonId) {
        this.productSeasonId = productSeasonId;
    }

    public Integer getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(Integer eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getMasterProductCode() {
        return masterProductCode;
    }

    public void setMasterProductCode(String masterProductCode) {
        this.masterProductCode = masterProductCode;
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
