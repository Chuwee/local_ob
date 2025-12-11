package es.onebox.mgmt.insurance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class InsuranceTermsConditionsDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 2640522696488134846L;

    private Integer id;
    @JsonProperty("insurance_policy_id")
    private Integer insurancePolicyId;
    private String lang;
    @JsonProperty("privacy_policy_text")
    private String privacyPolicyText;
    @JsonProperty("agreement_text")
    private String agreementText;
    @JsonProperty("subject_mail_template")
    private String subjectMailTemplate;
    @JsonProperty("mail_template")
    private String mailTemplate;
    private String file;
    @JsonProperty("is_default")
    private Boolean isDefault;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInsurancePolicyId() {
        return insurancePolicyId;
    }

    public void setInsurancePolicyId(Integer insurancePolicyId) {
        this.insurancePolicyId = insurancePolicyId;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getPrivacyPolicyText() {
        return privacyPolicyText;
    }

    public void setPrivacyPolicyText(String privacyPolicyText) {
        this.privacyPolicyText = privacyPolicyText;
    }

    public String getAgreementText() {
        return agreementText;
    }

    public void setAgreementText(String agreementText) {
        this.agreementText = agreementText;
    }

    public String getSubjectMailTemplate() {
        return subjectMailTemplate;
    }

    public void setSubjectMailTemplate(String subjectMailTemplate) {
        this.subjectMailTemplate = subjectMailTemplate;
    }

    public String getMailTemplate() {
        return mailTemplate;
    }

    public void setMailTemplate(String mailTemplate) {
        this.mailTemplate = mailTemplate;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}
