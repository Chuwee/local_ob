package es.onebox.internal.automaticsales.report.enums;

import es.onebox.internal.automaticsales.report.provider.AutomaticSalesReportProvider;
import es.onebox.core.file.exporter.generator.model.ExportType;
import es.onebox.core.file.exporter.generator.provider.ExportProvider;

public enum ApiExternalExportType implements ExportType {

    AUTOMATIC_SALES(AutomaticSalesReportProvider.class, "exportAutomaticSales");

    private final Class<? extends ExportProvider<?, ?>> provider;
    private final String mailTemplateKey;

    <T extends ExportProvider<?, ?>> ApiExternalExportType(Class<T> provider, String mailTemplateKey) {
        this.provider = provider;
        this.mailTemplateKey = mailTemplateKey;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ExportProvider<?, ?>> Class<T> getProvider() {
        return (Class<T>) provider;
    }

    @Override
    public String getName() {
        return this.name();
    }

    public String getMailTemplateKey() {
        return mailTemplateKey;
    }
}
