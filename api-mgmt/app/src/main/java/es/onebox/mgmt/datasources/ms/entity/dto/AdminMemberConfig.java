package es.onebox.mgmt.datasources.ms.entity.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class AdminMemberConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 596215558890688058L;

    private String adminUsers;
    private Long allowFreeSeatTill;
    private Long allowRecoverSeatTill;
    private String captchaSecretKey;
    private String emailBody;
    private String emailSubject;
    private String email;
    private List<Integer> blockedMatches;
    private Integer maxAdditionalMembers;
    private Boolean pricesBatchEnabled;

    public AdminMemberConfig() {
    }

    public String getAdminUsers() {
        return adminUsers;
    }

    public void setAdminUsers(String adminUsers) {
        this.adminUsers = adminUsers;
    }

    public Long getAllowFreeSeatTill() {
        return allowFreeSeatTill;
    }

    public void setAllowFreeSeatTill(Long allowFreeSeatTill) {
        this.allowFreeSeatTill = allowFreeSeatTill;
    }

    public Long getAllowRecoverSeatTill() {
        return allowRecoverSeatTill;
    }

    public void setAllowRecoverSeatTill(Long allowRecoverSeatTill) {
        this.allowRecoverSeatTill = allowRecoverSeatTill;
    }

    public String getCaptchaSecretKey() {
        return captchaSecretKey;
    }

    public void setCaptchaSecretKey(String captchaSecretKey) {
        this.captchaSecretKey = captchaSecretKey;
    }

    public String getEmailBody() {
        return emailBody;
    }

    public void setEmailBody(String emailBody) {
        this.emailBody = emailBody;
    }

    public String getEmailSubject() {
        return emailSubject;
    }

    public void setEmailSubject(String emailSubject) {
        this.emailSubject = emailSubject;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Integer> getBlockedMatches() {
        return blockedMatches;
    }

    public void setBlockedMatches(List<Integer> blockedMatches) {
        this.blockedMatches = blockedMatches;
    }

    public Integer getMaxAdditionalMembers() {
        return maxAdditionalMembers;
    }

    public void setMaxAdditionalMembers(Integer maxAdditionalMembers) {
        this.maxAdditionalMembers = maxAdditionalMembers;
    }

    public Boolean getPricesBatchEnabled() {
        return pricesBatchEnabled;
    }

    public void setPricesBatchEnabled(Boolean pricesBatchEnabled) {
        this.pricesBatchEnabled = pricesBatchEnabled;
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
