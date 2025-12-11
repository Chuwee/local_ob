package es.onebox.event.packs.enums;

import es.onebox.jooq.cpanel.tables.records.CpanelPackItemSubsetRecord;

import java.util.stream.Stream;

public enum PackItemSubsetType {
    SESSION((byte) 1);

    private final byte id;

    PackItemSubsetType(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }

    public static PackItemSubsetType getById(final byte id) {
        return Stream.of(values())
                .filter(field -> field.getId() == id)
                .findFirst()
                .orElse(null);
    }


    public static PackItemSubsetType from(CpanelPackItemSubsetRecord pack) {
        return PackItemSubsetType.getById(pack.getType());
    }

    public static boolean isSession(CpanelPackItemSubsetRecord pack) {
        return SESSION.equals(PackItemSubsetType.from(pack));
    }
}
