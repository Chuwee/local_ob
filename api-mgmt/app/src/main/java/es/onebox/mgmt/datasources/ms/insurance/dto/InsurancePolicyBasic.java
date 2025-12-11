package es.onebox.mgmt.datasources.ms.insurance.dto;

import es.onebox.mgmt.insurance.enums.PolicyState;

import java.io.Serial;
import java.io.Serializable;

public class InsurancePolicyBasic implements Serializable {

    @Serial
    private static final long serialVersionUID = 2670068789104199602L;

    private Integer id;
    private Integer insurerId;
    private String name;
    private String policyNumber;
    private PolicyState state;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInsurerId() {
        return insurerId;
    }

    public void setInsurerId(Integer insurerId) {
        this.insurerId = insurerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    public PolicyState getState() {
        return state;
    }

    public void setState(PolicyState state) {
        this.state = state;
    }
}
