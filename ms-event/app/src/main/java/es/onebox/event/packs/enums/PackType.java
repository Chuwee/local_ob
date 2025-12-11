package es.onebox.event.packs.enums;

import es.onebox.jooq.cpanel.tables.records.CpanelPackRecord;

import java.util.stream.Stream;

public enum PackType {
    MANUAL(0),
    AUTOMATIC(1);

    private final Integer id;

    PackType(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    public static PackType getById(final Integer id) {
        return Stream.of(values()).filter(field -> field.getId().equals(id)).findFirst().orElse(null);
    }

    public static PackType from(CpanelPackRecord pack) {
        return PackType.getById(pack.getSubtipo());
    }

    public static boolean isManual(CpanelPackRecord pack) {
        return MANUAL.equals(PackType.from(pack));
    }

    public static boolean isAutomatic(CpanelPackRecord pack) {
        return AUTOMATIC.equals(PackType.from(pack));
    }


}
