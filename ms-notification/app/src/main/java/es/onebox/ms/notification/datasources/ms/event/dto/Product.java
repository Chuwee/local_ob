package es.onebox.ms.notification.datasources.ms.event.dto;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;

import java.io.Serial;

public class Product extends IdDTO {
    @Serial
    private static final long serialVersionUID = -2056760995366677643L;

    private Long productId;
    private String name;
    private IdNameDTO entity;

    public Long getProductId() {return productId;}

    public void setProductId(Long productId) {this.productId = productId;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public IdNameDTO getEntity() {return entity;}

    public void setEntity(IdNameDTO entity) {this.entity = entity;}
}
