package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.datasources.ms.entity.enums.terminals.terminals.BaseTerminal;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

public class Operator extends Entity {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<String> gateways = new ArrayList<>();
    private BaseTerminal terminal;

    private Boolean useMultiCurrency;
    private List<WalletConfigDTO> wallets;

    public List<String> getGateways() {
        return gateways;
    }

    public void setGateways(List<String> gateways) {
        this.gateways = gateways;
    }

    public BaseTerminal getTerminal() {
        return terminal;
    }

    public void setTerminal(BaseTerminal terminal) {
        this.terminal = terminal;
    }

    public Boolean getUseMultiCurrency() {
        return useMultiCurrency;
    }

    public void setUseMultiCurrency(Boolean useMultiCurrency) {
        this.useMultiCurrency = useMultiCurrency;
    }

    public List<WalletConfigDTO> getWallets() {
        return wallets;
    }

    public void setWallets(List<WalletConfigDTO> wallets) {
        this.wallets = wallets;
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
