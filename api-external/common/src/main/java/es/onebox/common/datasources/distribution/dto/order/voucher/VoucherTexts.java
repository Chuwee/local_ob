package es.onebox.common.datasources.distribution.dto.order.voucher;

import java.io.Serial;
import java.io.Serializable;

public class VoucherTexts implements Serializable {

    @Serial
    private static final long serialVersionUID = -324792589977487954L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
