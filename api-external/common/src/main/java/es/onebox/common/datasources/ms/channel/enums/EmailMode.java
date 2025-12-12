package es.onebox.common.datasources.ms.channel.enums;

import java.util.stream.Stream;

public enum EmailMode {

    TICKET_AND_RECEIPT(1),
    ONLY_TICKET (2),
    ONLY_RECEIPT(3),
    NONE(4),
    UNIFIED_TICKET_AND_RECEIPT(5),
    RECEIPT_AND_PASSBOOK(6);

    private int id;

    EmailMode(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static EmailMode getById(Integer id){
        return Stream.of(values())
                .filter(type -> type.id == id)
                .findFirst()
                .orElse(null);
    }
}
