package es.onebox.mgmt.entities.enums;

import es.onebox.mgmt.common.FiltrableField;

import java.util.stream.Stream;

public enum ProducerField implements FiltrableField {

    NAME("name"),
    STATUS("status");

    String dtoName;

    ProducerField(String dtoName) {
        this.dtoName = dtoName;
    }

    public String getDtoName() {
        return dtoName;
    }

    public static ProducerField byName(String name) {
        return Stream.of(ProducerField.values())
                .filter(v -> v.dtoName.equals(name))
                .findFirst()
                .orElse(null);
    }

}
