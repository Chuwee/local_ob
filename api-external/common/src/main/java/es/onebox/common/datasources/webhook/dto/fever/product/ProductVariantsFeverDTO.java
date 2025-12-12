package es.onebox.common.datasources.webhook.dto.fever.product;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import es.onebox.core.serializer.dto.response.Metadata;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductVariantsFeverDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -3456789012345678901L;

    private List<ProductVariantFeverDTO> data;
    private Metadata metadata;

    public ProductVariantsFeverDTO() {
    }

    public ProductVariantsFeverDTO(List<ProductVariantFeverDTO> data, Metadata metadata) {
        this.data = data;
        this.metadata = metadata;
    }

    public List<ProductVariantFeverDTO> getData() {
        return data;
    }

    public void setData(List<ProductVariantFeverDTO> data) {
        this.data = data;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }
}
