package es.onebox.common.datasources.ms.order.dto;

import es.onebox.common.datasources.common.dto.Metadata;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class OrderSearchResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Metadata metadata;
    private List<OrderDTO> data;

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public List<OrderDTO> getData() {
        return data;
    }

    public void setData(List<OrderDTO> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderSearchResponse that = (OrderSearchResponse) o;
        return Objects.equals(getMetadata(), that.getMetadata()) && Objects.equals(getData(), that.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMetadata(), getData());
    }
}
