package es.onebox.event.datasources.ms.entity.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class OperatorCurrenciesDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private List<OperatorCurrencyDTO> selected;
    private String defaultCurrency;


    public List<OperatorCurrencyDTO> getSelected() { return selected; }

    public void setSelected(List<OperatorCurrencyDTO> selected) { this.selected = selected; }

    public String getDefaultCurrency() { return defaultCurrency; }

    public void setDefaultCurrency(String defaultCurrency) {
        this.defaultCurrency = defaultCurrency;
    }
}
