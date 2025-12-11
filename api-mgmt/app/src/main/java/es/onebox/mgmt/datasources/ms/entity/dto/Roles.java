package es.onebox.mgmt.datasources.ms.entity.dto;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;

public class Roles extends HashSet<Role> implements Serializable {

    public Roles() {
    }

    public Roles(@NotNull Collection<? extends Role> c) {
        super(c);
    }
}
