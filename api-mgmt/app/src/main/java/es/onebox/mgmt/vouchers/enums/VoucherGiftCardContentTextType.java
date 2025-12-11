package es.onebox.mgmt.vouchers.enums;


import java.io.Serializable;
import java.util.Arrays;

public enum VoucherGiftCardContentTextType implements Serializable {

    NAME("PURCHASE_NAME"),
    DESCRIPTION("PURCHASE_DESCRIPTION"),
    EMAIL_SUBJECT("PURCHASE_EMAIL_SUBJECT"),
    EMAIL_BODY("PURCHASE_EMAIL_BODY"),
    EMAIL_COPYRIGHT("PURCHASE_EMAIL_COPYRIGHT");

    private String internalName;

    VoucherGiftCardContentTextType(String internalName) {
        this.internalName = internalName;
    }

    public String getInternalName() {
        return internalName;
    }

    public static VoucherGiftCardContentTextType getByInternalName(String n) {
        return Arrays.stream(VoucherGiftCardContentTextType.values()).filter(v -> v.getInternalName().equals(n)).findFirst().orElse(null);
    }

    private static final long serialVersionUID = 1L;

}
