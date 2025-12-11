/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.onebox.event.priceengine.surcharges.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author ignasi
 */
public class SurchargeRange implements Serializable {

    @Serial
    private static final long serialVersionUID = -7419207803822251249L;

    private Double from;
    private Double to;
    private Double fixedValue;
    private Double percentageValue;
    private Double maximumValue;
    private Double minimumValue;

    public SurchargeRange(Double from, Double to, Double fixedValue, Double percentageValue, Double maximumValue, Double minimumValue) {
        this.from = from;
        this.to = to;
        this.fixedValue = fixedValue;
        this.percentageValue = percentageValue;
        this.maximumValue = maximumValue;
        this.minimumValue = minimumValue;
    }

    public SurchargeRange(){}

    public Double getFrom() {
        return from;
    }

    public void setFrom(Double from) {
        this.from = from;
    }

    public Double getTo() {
        return to;
    }

    public void setTo(Double to) {
        this.to = to;
    }

    public Double getFixedValue() {
        return fixedValue;
    }

    public void setFixedValue(Double fixedValue) {
        this.fixedValue = fixedValue;
    }

    public Double getPercentageValue() {
        return percentageValue;
    }

    public void setPercentageValue(Double percentageValue) {
        this.percentageValue = percentageValue;
    }

    public Double getMaximumValue() {
        return maximumValue;
    }

    public void setMaximumValue(Double maximumValue) {
        this.maximumValue = maximumValue;
    }

    public Double getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(Double minimumValue) {
        this.minimumValue = minimumValue;
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
