package es.onebox.internal.automaticrenewals.renewals.enums;

import es.onebox.internal.automaticrenewals.renewals.provider.csvpayments.InputCSVPaymentData;
import es.onebox.internal.automaticrenewals.renewals.provider.xmlsepa.XMLSepaData;

public enum AutomaticRenewalsProviderType {
    XML_SEPA(XMLSepaData.class, String.class),
    CSV_IMPORT(InputCSVPaymentData.class, String.class);

    private final Class<?> preparationDataClass;
    private final Class<?> executionDataClass;

    AutomaticRenewalsProviderType(Class<?> preparationDataClass, Class<?> executionDataClass) {
        this.preparationDataClass = preparationDataClass;
        this.executionDataClass = executionDataClass;
    }

    public Class<?> getPreparationDataClass() {
        return preparationDataClass;
    }

    public Class<?> getExecutionDataClass() {
        return executionDataClass;
    }
}
