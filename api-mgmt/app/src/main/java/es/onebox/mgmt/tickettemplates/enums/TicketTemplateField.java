package es.onebox.mgmt.tickettemplates.enums;

import es.onebox.mgmt.common.FiltrableField;

import java.util.stream.Stream;

public enum TicketTemplateField implements FiltrableField {

    NAME("name");

    String dtoName;

    TicketTemplateField(String dtoName) {
        this.dtoName = dtoName;
    }

    public String getDtoName() {
        return dtoName;
    }

    public static TicketTemplateField byName(String name) {
        return Stream.of(TicketTemplateField.values())
                .filter(v -> v.dtoName.equals(name))
                .findFirst()
                .orElse(null);
    }

}
