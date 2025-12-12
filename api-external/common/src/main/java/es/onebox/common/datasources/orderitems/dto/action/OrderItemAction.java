package es.onebox.common.datasources.orderitems.dto.action;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.io.Serializable;

public class OrderItemAction implements Serializable {

    @Serial
    private static final long serialVersionUID = -1880892179702744738L;

    @JsonProperty("transfer")
    private boolean transfer;
    @JsonProperty("transfer_recover")
    private boolean transferRecover;

    public boolean isTransfer() {
        return transfer;
    }

    public void setTransfer(boolean transfer) {
        this.transfer = transfer;
    }

    public boolean isTransferRecover() {
        return transferRecover;
    }

    public void setTransferRecover(boolean transferRecover) {
        this.transferRecover = transferRecover;
    }
}
