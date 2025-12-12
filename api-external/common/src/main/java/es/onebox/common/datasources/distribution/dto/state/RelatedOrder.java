package es.onebox.common.datasources.distribution.dto.state;

import java.io.Serial;
import java.io.Serializable;

public class RelatedOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = -1368712466666589723L;

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
