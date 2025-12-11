package es.onebox.mgmt.datasources.ms.venue.dto.template;

import java.io.Serial;
import java.io.Serializable;

public class VenueTagDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long priceType;
    private VisibilityType visibility;
    private AccessibilityType accessibility;
    private Long gate;
    private GateUpdateType gateUpdateType;
    private Long dynamicTag1;
    private Long dynamicTag2;
    private Long saveSequence;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPriceType() {
        return priceType;
    }

    public void setPriceType(Long priceType) {
        this.priceType = priceType;
    }

    public VisibilityType getVisibility() {
        return visibility;
    }

    public void setVisibility(VisibilityType visibility) {
        this.visibility = visibility;
    }

    public AccessibilityType getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(AccessibilityType accessibility) {
        this.accessibility = accessibility;
    }

    public Long getGate() {
        return gate;
    }

    public void setGate(Long gate) {
        this.gate = gate;
    }

    public GateUpdateType getGateUpdateType() {
        return gateUpdateType;
    }

    public void setGateUpdateType(GateUpdateType gateUpdateType) {
        this.gateUpdateType = gateUpdateType;
    }

    public Long getDynamicTag1() {
        return dynamicTag1;
    }

    public void setDynamicTag1(Long dynamicTag1) {
        this.dynamicTag1 = dynamicTag1;
    }

    public Long getDynamicTag2() {
        return dynamicTag2;
    }

    public void setDynamicTag2(Long dynamicTag2) {
        this.dynamicTag2 = dynamicTag2;
    }

    public Long getSaveSequence() {
        return saveSequence;
    }

    public void setSaveSequence(Long saveSequence) {
        this.saveSequence = saveSequence;
    }
}
