package es.onebox.mgmt.datasources.ms.channel.salerequests.dto;

import java.io.Serializable;

public class MsSubscriptionListSalesRequestDTO implements Serializable {

    private static final long serialVersionUID = 3620736238094519791L;

    private Boolean enableSubscriptionList;
    private Integer subscriptionListId;

    public Boolean getEnableSubscriptionList() {
        return enableSubscriptionList;
    }

    public void setEnableSubscriptionList(Boolean enableSubscriptionList) {
        this.enableSubscriptionList = enableSubscriptionList;
    }

    public Integer getSubscriptionListId() {
        return subscriptionListId;
    }

    public void setSubscriptionListId(Integer subscriptionListId) {
        this.subscriptionListId = subscriptionListId;
    }
}
