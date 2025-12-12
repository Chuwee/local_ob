package es.onebox.common.datasources.ms.order.dto.invoice;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class InvoiceEventDataDTO implements Serializable {

    private static final long serialVersionUID = -925606235621097078L;

    private Integer eventId;
    private String eventName;
    private Integer groupId;
    private String groupName;
    private List<InvoiceSessionDataDTO> invoiceSessionsData = new LinkedList();

    public Integer getEventId() {
        return eventId;
    }

    public void setEventId(Integer eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<InvoiceSessionDataDTO> getInvoiceSessionsData() {
        return invoiceSessionsData;
    }

    public void setInvoiceSessionsData(List<InvoiceSessionDataDTO> invoiceSessionsData) {
        this.invoiceSessionsData = invoiceSessionsData;
    }
}
