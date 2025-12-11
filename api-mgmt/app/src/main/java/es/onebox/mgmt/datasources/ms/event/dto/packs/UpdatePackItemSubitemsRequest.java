package es.onebox.mgmt.datasources.ms.event.dto.packs;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class UpdatePackItemSubitemsRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<Integer> subitemIds;

    public List<Integer> getSubitemIds() {
        return subitemIds;
    }

    public void setSubitemIds(List<Integer> subitemIds) {
        this.subitemIds = subitemIds;
    }
}

