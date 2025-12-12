package es.onebox.internal.automaticrenewals.renewals.provider;

import es.onebox.internal.automaticrenewals.renewals.enums.AutomaticRenewalsProviderType;
import es.onebox.internal.automaticrenewals.renewals.provider.csvpayments.CSVPaymentsProvider;
import es.onebox.internal.automaticrenewals.renewals.provider.xmlsepa.XMLSepaProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
public class AutomaticRenewalsProviderFactory {

    private final Map<AutomaticRenewalsProviderType, AutomaticRenewalsProvider<?, ?>> providers;

    @Autowired
    public AutomaticRenewalsProviderFactory(ApplicationContext applicationContext) {
        providers = new EnumMap<>(AutomaticRenewalsProviderType.class);
        providers.put(AutomaticRenewalsProviderType.CSV_IMPORT, applicationContext.getBean(CSVPaymentsProvider.class));
        providers.put(AutomaticRenewalsProviderType.XML_SEPA, applicationContext.getBean(XMLSepaProvider.class));
    }

    public AutomaticRenewalsProvider<?, ?> get(AutomaticRenewalsProviderType provider) {
        return providers.get(provider);
    }
}