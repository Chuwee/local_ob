package es.onebox.common.datasources.orderitems.dto.transfer;

import java.io.Serial;
import java.io.Serializable;

public class OrderItemTransfer implements Serializable {

    @Serial
    private static final long serialVersionUID = 2407068160377365452L;

    private String status;
    private TransferReceiver receiver;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TransferReceiver getReceiver() {
        return receiver;
    }

    public void setReceiver(TransferReceiver receiver) {
        this.receiver = receiver;
    }
}
