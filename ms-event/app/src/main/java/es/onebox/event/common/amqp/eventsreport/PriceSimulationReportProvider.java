package es.onebox.event.common.amqp.eventsreport;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.file.exporter.generator.provider.ExportProvider;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.pricesengine.PriceSimulationService;
import es.onebox.event.report.converter.PriceSimulationReportConverter;
import es.onebox.event.report.model.filter.PriceSimulationReportRequest;
import es.onebox.event.report.model.report.PriceSimulationReportDTO;
import es.onebox.event.seasontickets.dto.pricesimulation.PriceSimulationResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PriceSimulationReportProvider extends
    ExportProvider<PriceSimulationReportDTO, PriceSimulationReportRequest> {

    private final int maxReportSize;
    private final PriceSimulationService service;

    public PriceSimulationReportProvider(
        @Value("${exports.price-simulation.max-size:100000}") int maxReportSize,
        PriceSimulationService service) {
        this.service = service;
        this.maxReportSize = maxReportSize;
    }

    @Override
    public List<PriceSimulationReportDTO> fetchAll(
        PriceSimulationReportRequest request) {
        PriceSimulationResponse response = this.fetchPriceSimulation(request);
        List<PriceSimulationReportDTO> priceSimulationReportDTOs = new ArrayList<>();
        if (response.getData() != null) {
            response.getData().forEach(priceSimulation ->

                priceSimulationReportDTOs.addAll(PriceSimulationReportConverter.toReport(
                    priceSimulation,
                    request.getTranslations())
                )
            );
        }

        return priceSimulationReportDTOs;
    }

    private PriceSimulationResponse fetchPriceSimulation(PriceSimulationReportRequest filter) {
        return this.service.getPriceSimulation(filter.getSaleRequestId());
    }


    @Override
    public void validate(PriceSimulationReportRequest message) {
        PriceSimulationResponse response = fetchPriceSimulation(message);
        long total = response.getData() != null ? response.getData().size() : 0;
        if (total > maxReportSize) {
            throw ExceptionBuilder.build(MsEventErrorCode.EXPORT_WITH_TOO_MANY_RECORDS,
                maxReportSize);
        }
    }
}
