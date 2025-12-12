package es.onebox.flc.invoices.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.ms.order.dto.invoice.InvoiceSessionDataDTO;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class InvoiceEventDTO implements Serializable {

    private static final long serialVersionUID = -6489716804722332473L;
    @JsonProperty("event_id")
    private Integer eventId;
    @JsonProperty("event_name")
    private String eventName;
    @JsonProperty("group_id")
    private Integer groupId;
    @JsonProperty("group_name")
    private String groupName;
    @JsonProperty("invoice_sessions_data")
    private List<InvoiceSessionDTO> invoiceSessionsData = new LinkedList<>();

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

    public List<InvoiceSessionDTO> getInvoiceSessionsData() {
        return invoiceSessionsData;
    }

    public void setInvoiceSessionsData(List<InvoiceSessionDTO> invoiceSessionsData) {
        this.invoiceSessionsData = invoiceSessionsData;
    }
}
