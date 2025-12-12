package es.onebox.internal.automaticrenewals.renewals.provider.csvpayments;

import com.opencsv.bean.CsvBindByName;

import java.io.Serial;
import java.io.Serializable;

public class CSVPaymentDataRow implements Serializable {

    @Serial
    private static final long serialVersionUID = -5177232671029772923L;

    @CsvBindByName(column = "renewal_id")
    private String renewalId;
    @CsvBindByName(column = "reference")
    private String reference;

    public CSVPaymentDataRow() {}

    public CSVPaymentDataRow(String renewalId, String reference) {
        this.renewalId = renewalId;
        this.reference = reference;
    }

    public String getRenewalId() {
        return renewalId;
    }

    public void setRenewalId(String renewalId) {
        this.renewalId = renewalId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
