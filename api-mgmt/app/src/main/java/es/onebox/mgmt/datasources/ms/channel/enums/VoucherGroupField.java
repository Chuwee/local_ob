package es.onebox.mgmt.datasources.ms.channel.enums;

import es.onebox.mgmt.common.FiltrableField;

import java.util.stream.Stream;

public enum VoucherGroupField implements FiltrableField {

    NAME("name");

    String dtoName;

    VoucherGroupField(String dtoName) {
        this.dtoName = dtoName;
    }

    public String getDtoName() {
        return dtoName;
    }

    public static VoucherGroupField byName(String name) {
        return Stream.of(VoucherGroupField.values())
                .filter(v -> v.dtoName.equals(name))
                .findFirst()
                .orElse(null);
    }

}
