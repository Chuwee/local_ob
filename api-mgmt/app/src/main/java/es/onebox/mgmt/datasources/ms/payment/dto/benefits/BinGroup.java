package es.onebox.mgmt.datasources.ms.payment.dto.benefits;

import java.io.Serial;
import java.util.List;

public class BinGroup extends BenefitGroupConfig {

    @Serial
    private static final long serialVersionUID = 1696882893787606551L;

    private List<String> bins;

    public List<String> getBins() {
        return bins;
    }

    public void setBins(List<String> bins) {
        this.bins = bins;
    }
}
