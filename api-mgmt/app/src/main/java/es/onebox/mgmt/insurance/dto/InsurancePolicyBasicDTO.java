package es.onebox.mgmt.insurance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.insurance.enums.PolicyState;

import java.io.Serial;
import java.io.Serializable;

public class InsurancePolicyBasicDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -679482445768701908L;

    private Integer id;
    @JsonProperty("insurer_id")
    private Integer insurerId;
    private String name;
    @JsonProperty("policy_number")
    private String policyNumber;
    @JsonProperty("policy_state")
    private PolicyState policyState;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInsurerId() {
        return insurerId;
    }

    public void setInsurerId(Integer insurer) {
        this.insurerId = insurer;
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

    public PolicyState getPolicyState() {
        return policyState;
    }

    public void setPolicyState(PolicyState policyState) {
        this.policyState = policyState;
    }
}
