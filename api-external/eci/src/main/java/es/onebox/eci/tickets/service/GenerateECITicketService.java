package es.onebox.eci.tickets.service;

import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.common.datasources.ms.entity.dto.Language;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.entity.repository.MasterDataRepository;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.order.enums.ProductType;
import es.onebox.eci.config.ATMEntityECIConfiguration;
import es.onebox.common.datasources.ms.order.dto.EventType;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.ms.venue.repository.VenueTemplateRepository;
import es.onebox.common.tickets.AbstractGenerateTicketService;
import es.onebox.common.tickets.TicketGenerationSupport;
import es.onebox.core.utils.common.EncryptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenerateECITicketService extends AbstractGenerateTicketService {

    private static final String FILE_NAME = "EciTicket";
    private static final String EXTERNAL_FILE_NAME = "EciExternalTicket";

    private static final String EXTERNAL_FILE_NAME_VERTICAL = "EciExternalTicketVertical";
    private static final String LOG_TAG = "ECI TICKET";
    private static final long ES_LANG_ID = 1L;
    private final ATMEntityECIConfiguration atmEntityECIConfiguration;

    @Autowired
    public GenerateECITicketService(TicketGenerationSupport ticketGenerationSupport,
                                    MsEventRepository msEventRepository, MsOrderRepository msOrderRepository,
                                    EntitiesRepository entitiesRepository, VenueTemplateRepository venueTemplateRepository,
                                    @Qualifier("s3TicketsExternalRepository") S3BinaryRepository s3TicketsExternalRepository,
                                    MasterDataRepository masterDataRepository, EncryptionUtils encryptionUtils, ATMEntityECIConfiguration atmEntityECIConfiguration) {
        super(ticketGenerationSupport, msEventRepository, msOrderRepository, entitiesRepository, venueTemplateRepository,
                s3TicketsExternalRepository, masterDataRepository, encryptionUtils);
        this.atmEntityECIConfiguration = atmEntityECIConfiguration;
    }

    @Override
    protected String getReportFileName(OrderProductDTO orderProductDTO) {
        if(orderProductDTO.getEventType().equals(EventType.AVET)) {
            if (orderProductDTO.getEventEntityId().longValue() == atmEntityECIConfiguration.getEntityId()) {
                return EXTERNAL_FILE_NAME_VERTICAL;
            }
            return EXTERNAL_FILE_NAME;

        } else {
            return FILE_NAME;
        }
    }

    @Override
    protected boolean containExternalTicket(OrderDTO orderDTO) {
        return orderDTO.getProducts().stream().filter(pr -> !pr.getType().equals(ProductType.PRODUCT))
                .anyMatch(i -> es.onebox.common.datasources.ms.order.dto.EventType.AVET.equals(i.getEventType()));
    }

    @Override
    protected List<OrderProductDTO> getItemsForPDFTicket(OrderDTO orderDTO) {
        return orderDTO.getProducts().stream()
                .filter(i -> !i.getType().equals(ProductType.PRODUCT) && i.getRelatedRefundCode() == null)
                .collect(Collectors.toList());
    }

    @Override
    protected Language getLanguage(OrderDTO orderDTO) {
        return masterDataRepository.getLanguage(ES_LANG_ID);
    }

    @Override
    protected String getLogsTag() {
        return LOG_TAG;
    }

}
