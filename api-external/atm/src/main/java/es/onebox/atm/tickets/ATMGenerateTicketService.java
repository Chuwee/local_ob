package es.onebox.atm.tickets;

import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.common.datasources.ms.entity.dto.Language;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.entity.repository.MasterDataRepository;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.order.dto.EventType;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.enums.OrderState;
import es.onebox.common.datasources.ms.order.enums.ProductType;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.ms.venue.dto.PriceTypeTicketCommunicationElement;
import es.onebox.common.datasources.ms.venue.repository.VenueTemplateRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.tickets.AbstractGenerateTicketService;
import es.onebox.common.tickets.TicketData;
import es.onebox.common.tickets.TicketGenerationSupport;
import es.onebox.common.tickets.dto.SessionData;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.EncryptionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ATMGenerateTicketService extends AbstractGenerateTicketService {

    private static final String LOG_TAG = "ATM TICKET";
    private static final String ATM_REPORT_FILE_NAME = "TicketAvetVerticalMobile";
    private static final String ATM_ACTIVITY_REPORT_FILE_NAME = "TicketVerticalActividad";
    private static final String INVITATION_TICKET_TYPE = "INVITATION";


    @Autowired
    public ATMGenerateTicketService(TicketGenerationSupport ticketGenerationSupport, MsEventRepository msEventRepository,
                                    MsOrderRepository msOrderRepository, EntitiesRepository entitiesRepository,
                                    VenueTemplateRepository venueTemplateRepository,
                                    @Qualifier("s3TicketsExternalRepository") S3BinaryRepository s3TicketsExternalRepository,
                                    MasterDataRepository masterDataRepository, EncryptionUtils encryptionUtils) {

        super(ticketGenerationSupport, msEventRepository, msOrderRepository, entitiesRepository, venueTemplateRepository,
                s3TicketsExternalRepository, masterDataRepository, encryptionUtils);
    }

    @Override
    protected String getReportFileName(OrderProductDTO orderProductDTO) {
        if (EventType.AVET.equals(orderProductDTO.getEventType())) {
            return ATM_REPORT_FILE_NAME;
        } else {
            return ATM_ACTIVITY_REPORT_FILE_NAME;
        }
    }

    @Override
    protected boolean containExternalTicket(OrderDTO orderDTO) {
        return true;
    }

    @Override
    protected List<OrderProductDTO> getItemsForPDFTicket(OrderDTO orderDTO) {
        if (!orderDTO.getStatus().getState().equals(OrderState.PAID)) {
            return null;
        }
        return (orderDTO.getProducts().stream()
                .filter(i -> !ProductType.PRODUCT.equals(i.getType()))
                .collect(Collectors.toList()));
    }

    @Override
    protected String getLogsTag() {
        return LOG_TAG;
    }

    @Override
    protected Language getLanguage(OrderDTO orderDTO) {
        List<Language> languages = masterDataRepository.findLanguages(orderDTO.getOrderData().getLanguage());
        if (languages == null || languages.isEmpty()) {
            throw new OneboxRestException(ApiExternalErrorCode.GENERIC_ERROR);
        }
        return languages.stream().findAny().get();
    }

    @Override
    protected TicketData prepareTicketData(OrderDTO orderDTO, OrderProductDTO orderProductDTO, SessionData sessionData, List<PriceTypeTicketCommunicationElement> priceTypeCommElement, boolean containExternalTicket, Language language) {
        TicketData ticketData = super.prepareTicketData(orderDTO, orderProductDTO, sessionData, priceTypeCommElement, containExternalTicket, language);
        composeDates(sessionData, ticketData);
        return ticketData;
    }

    private void composeDates(SessionData sessionData, TicketData ticketData) {
        if (INVITATION_TICKET_TYPE.equals(ticketData.getTicketType())) {
            ticketData.setComposedDate(ticketData.getVenueAddress());
        } else {
            if (BooleanUtils.isTrue(sessionData.getShowDatetime())) {
                ticketData.setComposedDate(ticketData.getDiaSemanaTexto() + " " + ticketData.getDiaMes() + " - " +
                        ticketData.getHoraTexto());
            } else if (BooleanUtils.isTrue(sessionData.getShowDate())) {
                ticketData.setComposedDate(ticketData.getDiaSemanaTexto() + " " + ticketData.getDiaMes());
            } else {
                ticketData.setComposedDate("");
            }
        }
    }
}