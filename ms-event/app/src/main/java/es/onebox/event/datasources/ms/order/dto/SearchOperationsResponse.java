package es.onebox.event.datasources.ms.order.dto;

import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.dal.dto.couch.order.OrderDTO;

import java.io.Serializable;
import java.util.List;

public class SearchOperationsResponse implements Serializable {

    private List<OrderDTO> data;
    private Metadata metadata;

    public List<OrderDTO> getData() {
        return data;
    }

    public void setData(List<OrderDTO> data) {
        this.data = data;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }


}
