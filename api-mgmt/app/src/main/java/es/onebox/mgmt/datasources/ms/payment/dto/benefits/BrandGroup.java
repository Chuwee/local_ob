package es.onebox.mgmt.datasources.ms.payment.dto.benefits;

import java.io.Serial;
import java.util.List;

public class BrandGroup extends BenefitGroupConfig {

    @Serial
    private static final long serialVersionUID = -6748020711484818112L;

    private List<String> brands;

    public List<String> getBrands() {
        return brands;
    }

    public void setBrands(List<String> brands) {
        this.brands = brands;
    }
}
