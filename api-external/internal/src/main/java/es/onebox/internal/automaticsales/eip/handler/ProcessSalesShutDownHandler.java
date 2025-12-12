package es.onebox.internal.automaticsales.eip.handler;

import es.onebox.internal.automaticsales.processsales.dto.UpdateProcessSalesRequest;
import es.onebox.internal.automaticsales.processsales.enums.AutomaticSalesExecutionStatus;
import es.onebox.internal.automaticsales.processsales.service.ProcessSalesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProcessSalesShutDownHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ProcessSalesShutDownHandler.class);
    private final ProcessSalesService processSalesService;

    public ProcessSalesShutDownHandler(ProcessSalesService processSalesService) {
        this.processSalesService = processSalesService;
        Runtime.getRuntime().addShutdownHook(new Thread(this::stopProcessSales));

    }

    private void stopProcessSales () {
        List<Long> activeProcessSales = processSalesService.getActiveSessionsProcessSales();
        LOG.warn("[PROCESS SALES] [ShutDownHandler] Script stopped by signal. Stopping the following sessions process sales: {}", activeProcessSales);
        activeProcessSales.forEach(session -> processSalesService.modifyProcessSales(session, new UpdateProcessSalesRequest(AutomaticSalesExecutionStatus.BLOCKED)));
    }
}
