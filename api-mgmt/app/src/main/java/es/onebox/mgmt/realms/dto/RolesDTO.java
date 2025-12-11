package es.onebox.mgmt.realms.dto;

import jakarta.validation.Valid;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

public class RolesDTO extends HashSet<@Valid RoleDTO> implements Serializable {

    private static final long serialVersionUID = 1L;

    public RolesDTO() {
    }

    public RolesDTO(@NotNull Collection<? extends RoleDTO> c) {
        super(c);
    }

}
