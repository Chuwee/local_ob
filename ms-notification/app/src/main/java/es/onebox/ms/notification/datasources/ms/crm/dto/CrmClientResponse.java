package es.onebox.ms.notification.datasources.ms.crm.dto;


import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class CrmClientResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -8849515575350087247L;

    private List<CrmClientDocResponse> buyers;

    public List<CrmClientDocResponse> getBuyers() {
        return buyers;
    }

    public void setBuyers(List<CrmClientDocResponse> buyers) {
        this.buyers = buyers;
    }
}
