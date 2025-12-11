package es.onebox.mgmt.datasources.ms.channel.dto;

import java.io.Serializable;
import java.util.List;

public class CustomPromotionalCodeValidation implements Serializable {

    private static final long serialVersionUID = 5532496869461507566L;

    private String serviceImpl;

    private List<Integer> salesId;

    public String getServiceImpl() {
        return serviceImpl;
    }

    public void setServiceImpl(String serviceImpl) {
        this.serviceImpl = serviceImpl;
    }

    public List<Integer> getSalesId() { return salesId; }

    public void setSalesId(List<Integer> salesId) { this.salesId = salesId; }
}
