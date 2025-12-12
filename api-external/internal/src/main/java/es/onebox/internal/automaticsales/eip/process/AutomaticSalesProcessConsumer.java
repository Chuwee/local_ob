package es.onebox.internal.automaticsales.eip.process;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.internal.automaticsales.processsales.dto.ProcessSalesConfigurationRequest;
import es.onebox.internal.automaticsales.processsales.enums.AutomaticSalesExecutionStatus;
import es.onebox.internal.automaticsales.processsales.service.ProcessSalesService;
import es.onebox.message.broker.eip.processor.DefaultProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AutomaticSalesProcessConsumer extends DefaultProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(AutomaticSalesProcessConsumer.class);
    private final ObjectMapper jacksonMapper;
    private final ProcessSalesService processSalesService;

    @Autowired
    public AutomaticSalesProcessConsumer(ObjectMapper jacksonMapper, ProcessSalesService processSalesService) {
        this.jacksonMapper = jacksonMapper;
        this.processSalesService = processSalesService;
    }

    @Override
    public void execute(Exchange exchange)  {
        Message messageRaw = exchange.getIn();
        Object body = messageRaw.getBody();
        if (body instanceof byte[]) {
            try {
                GenerateAutomaticSalesProcesssMessage message = jacksonMapper.readValue((byte[]) body, GenerateAutomaticSalesProcesssMessage.class);
                ProcessSalesConfigurationRequest config = new ProcessSalesConfigurationRequest();

                config.setFilename(message.getFilename());
                config.setChannelId(message.getChannelId());
                config.setSort(message.getSort());
                config.setAllowBreakAdjacentSeats(message.getAllowBreakAdjacentSeats());
                config.setPreviewToken(message.getPreviewToken());
                config.setUseLocators(message.getUseLocators());
                config.setDefaultPurchaseLanguage(message.getDefaultPurchaseLanguage());
                config.setForceMultiTicket(message.getForceMultiTicket());
                config.setSkipAddAttendant(message.getSkipAddAttendant());
                config.setUseOBIdsForSeatMappings(message.getUseOBIdsForSeatMappings());
                config.setUseSeatMappings(message.getUseSeatMappings());
                config.setReceiptEmail(message.getReceiptEmail());
                config.setExtraFieldValue(message.getExtraFieldValue());
                config.setAllowSkipNonAdjacentSeats(message.getAllowSkipNonAdjacentSeats());

                AutomaticSalesExecutionStatus inProgress = processSalesService.getSemaphore(message.getSessionId());

                if(inProgress != null && inProgress.equals(AutomaticSalesExecutionStatus.IN_PROGRESS)) {
                    throw new OneboxRestException(ApiExternalErrorCode.ALREADY_EXECUTING_AUTOMATIC_SALES);
                }
                processSalesService.executeProcess(message.getSessionId(), config);

            } catch (Exception e) {

                LOG.error("[PROCESS SALES] - Failed to read create AutomaticSalesProcess message from queue message",e);
                throw new OneboxRestException(ApiExternalErrorCode.AUTOMATIC_SALES_RETRY_ERROR);
            }
        } else {
            LOG.debug("[PROCESS SALES] - Invalid AutomaticSalesProcess queue message {}", body);
        }
    }
}
