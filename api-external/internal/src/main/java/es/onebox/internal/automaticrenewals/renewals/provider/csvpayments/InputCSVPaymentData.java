package es.onebox.internal.automaticrenewals.renewals.provider.csvpayments;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serial;
import java.util.ArrayList;

public class InputCSVPaymentData extends ArrayList<InputCSVPaymentData.CSVPaymentDataItem> {

    @Serial
    private static final long serialVersionUID = -8264070059033976733L;

    public record CSVPaymentDataItem(@JsonProperty("renewal_id") String renewalId, String reference) { }
}

