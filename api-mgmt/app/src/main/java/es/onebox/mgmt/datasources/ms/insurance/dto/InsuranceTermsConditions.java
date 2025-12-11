package es.onebox.mgmt.datasources.ms.insurance.dto;

import java.io.Serial;
import java.io.Serializable;

public class InsuranceTermsConditions implements Serializable {
    @Serial
    private static final long serialVersionUID = -8495923969903068929L;

    private Integer id;
    private Integer insurancePolicyId;
    private String lang;
    private String privacyPolicyText;
    private String agreementText;
    private String subjectMailTemplate;
    private String mailTemplate;
    private String file;
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
