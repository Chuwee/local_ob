package es.onebox.mgmt.common;

import java.io.Serializable;

public interface SizeConstrained extends Serializable {

    Integer getHeight();
    Integer getWidth();
    Integer getSize();
}

