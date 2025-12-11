package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.core.serializer.dto.common.CodeDTO;

import java.io.Serializable;

public class Permission extends CodeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    public Permission() {
    }

    public Permission(String code) {
        super(code);
    }

}
