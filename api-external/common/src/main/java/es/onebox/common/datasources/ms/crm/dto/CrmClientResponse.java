package es.onebox.common.datasources.ms.crm.dto;

import java.util.List;

public class CrmClientResponse extends CrmResponse {

    private CrmParams parameters;
    private List<CrmClientDocResponse> buyers;

    public CrmParams getParameters() {
        return parameters;
    }

    public void setParameters(CrmParams parameters) {
        this.parameters = parameters;
    }

    public List<CrmClientDocResponse> getBuyers() {
        return buyers;
    }

    public void setBuyers(List<CrmClientDocResponse> buyers) {
        this.buyers = buyers;
    }

}
