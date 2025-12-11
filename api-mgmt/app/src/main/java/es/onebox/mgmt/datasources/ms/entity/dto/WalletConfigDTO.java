package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class WalletConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 63372509812780810L;

    private String wallet;
    private List<String> gateways;

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public List<String> getGateways() {
        return gateways;
    }

    public void setGateways(List<String> gateways) {
        this.gateways = gateways;
    }
}
