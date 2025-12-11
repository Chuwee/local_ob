package es.onebox.mgmt.datasources.ms.insurance.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateInsuranceTermsConditions implements Serializable {
    @Serial
    private static final long serialVersionUID = 964246067044482882L;

    private String privacyPolicyText;
    private String agreementText;
    private String subjectMailTemplate;
    private String mailTemplate;
    private Boolean isDefault;

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

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean aDefault) {
        isDefault = aDefault;
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
