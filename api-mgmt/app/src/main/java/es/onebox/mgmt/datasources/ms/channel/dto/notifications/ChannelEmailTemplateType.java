package es.onebox.mgmt.datasources.ms.channel.dto.notifications;

import java.io.Serializable;

public enum ChannelEmailTemplateType implements Serializable {
    PURCHASE_RECEIPT,
    TICKET,
    INVOICE,
    USER_ACCOUNT_MANAGEMENT
}