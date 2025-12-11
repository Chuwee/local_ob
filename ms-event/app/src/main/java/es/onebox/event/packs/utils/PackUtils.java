package es.onebox.event.packs.utils;

import es.onebox.event.packs.enums.PackItemType;
import es.onebox.jooq.cpanel.tables.records.CpanelPackItemRecord;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;

public class PackUtils {

    public static final String PACK_CATALOG_REFRESH = "[PACK CATALOG REFRESH]";

    public static boolean isMain(CpanelPackItemRecord cpanelPackItemRecord) {
        return BooleanUtils.isTrue(cpanelPackItemRecord.getPrincipal());
    }

    public static PackItemType getType(CpanelPackItemRecord record) {
        return PackItemType.getById(record.getTipoitem());
    }

    public static boolean isSession(CpanelPackItemRecord record) {
        return PackItemType.SESSION.equals(getType(record));
    }

    public static boolean isProduct(CpanelPackItemRecord record) {
        return PackItemType.PRODUCT.equals(getType(record));
    }

    public static boolean isEvent(CpanelPackItemRecord record) {
        return PackItemType.EVENT.equals(getType(record));
    }

    public static boolean isEventOrSession(CpanelPackItemRecord packItemRecord) {
        return isEvent(packItemRecord) || isSession(packItemRecord);
    }

    public static CpanelPackItemRecord getMainPackItemRecord(List<CpanelPackItemRecord> packItemRecords) {
        return packItemRecords.stream().filter(PackUtils::isMain).findFirst().orElse(null);
    }

    public static List<CpanelPackItemRecord> getSessionPackItemRecords(List<CpanelPackItemRecord> packItemRecords) {
        return packItemRecords.stream().filter(PackUtils::isSession).toList();
    }

    public static List<CpanelPackItemRecord> getProductPackItemRecords(List<CpanelPackItemRecord> packItemRecords) {
        return packItemRecords.stream().filter(PackUtils::isProduct).toList();
    }

    public static List<Long> getItemIds(List<CpanelPackItemRecord> packItemRecords) {
        return packItemRecords.stream().map(r -> r.getIditem().longValue()).toList();
    }

    public static List<CpanelPackItemRecord> getSessionPackItemNotMainWithPriceTypeConfig(List<CpanelPackItemRecord> packItemRecords) {
        return getSessionPackItemRecords(packItemRecords).stream()
                .filter(r -> !isMain(r) && r.getIdzonaprecio() != null)
                .toList();
    }

}
