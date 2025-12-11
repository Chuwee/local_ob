package es.onebox.event.report.enums;

import es.onebox.core.file.exporter.generator.model.ExportType;
import es.onebox.core.file.exporter.generator.provider.ExportProvider;
import es.onebox.event.common.amqp.eventsreport.PriceSimulationReportProvider;
import es.onebox.event.common.amqp.eventsreport.SeasonTicketRenewalsReportProvider;

public enum MsEventReportType implements ExportType {

        SEASON_TICKETS_RENEWALS(SeasonTicketRenewalsReportProvider.class, "exportSeasonTicketsRenewals"),
        PRICE_SIMULATION(PriceSimulationReportProvider.class, "priceSimulations");
        private final Class<? extends ExportProvider<?, ?>> provider;
        private final String mailTemplateKey;

        <T extends ExportProvider<?, ?>> MsEventReportType(Class<T> provider, String mailTemplateKey) {
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