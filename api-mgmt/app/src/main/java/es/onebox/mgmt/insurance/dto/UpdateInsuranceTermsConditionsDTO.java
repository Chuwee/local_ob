package es.onebox.mgmt.insurance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class UpdateInsuranceTermsConditionsDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -7531232879947498335L;

    @JsonProperty("privacy_policy_text")
    private String privacyPolicyText;
    @JsonProperty("agreement_text")
    private String agreementText;
    @JsonProperty("subject_mail_template")
    private String subjectMailTemplate;
    @JsonProperty("mail_template")
    private String mailTemplate;
    @JsonProperty("is_default")
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
