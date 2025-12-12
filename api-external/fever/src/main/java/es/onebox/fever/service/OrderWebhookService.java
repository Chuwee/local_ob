package es.onebox.fever.service;

import es.onebox.common.datasources.ms.channel.enums.TicketHandlingType;
import es.onebox.common.datasources.ms.channel.repository.ChannelEventRepository;
import es.onebox.common.datasources.ms.event.dto.SessionsDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.event.request.SessionSearchFilter;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.enums.ProductType;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.ms.ticket.dto.PdfTicketDetails;
import es.onebox.common.datasources.ms.ticket.repository.MsTicketRepository;
import es.onebox.common.datasources.webhook.dto.fever.NotificationMessageDTO;
import es.onebox.common.datasources.webhook.dto.fever.OrderDetailDTO;
import es.onebox.common.datasources.webhook.dto.fever.OrderPrintDetailDTO;
import es.onebox.common.datasources.webhook.dto.fever.PdfStatus;
import es.onebox.common.datasources.webhook.dto.fever.PrintStatus;
import es.onebox.common.datasources.webhook.dto.fever.WebhookFeverDTO;
import es.onebox.fever.converter.CommonConverter;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.OneboxRestException;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderWebhookService {
    private final Integer MAX_RETRIES = 5;

    private final MsOrderRepository msOrderRepository;
    private final MsTicketRepository msTicketRepository;
    private final ChannelEventRepository channelEventRepository;
    private final MsEventRepository msEventRepository;

  @Autowired
  public OrderWebhookService(MsOrderRepository msOrderRepository, MsTicketRepository msTicketRepository,
                             ChannelEventRepository channelEventRepository, MsEventRepository msEventRepository) {
      this.msOrderRepository = msOrderRepository;
      this.msTicketRepository = msTicketRepository;
      this.channelEventRepository = channelEventRepository;
      this.msEventRepository = msEventRepository;
  }

  public WebhookFeverDTO sendOrderDetail(WebhookFeverDTO webhookFeverDTO) {


    OrderDetailDTO orderWebhookDetail = new OrderDetailDTO();
    OrderDTO order = msOrderRepository.getOrderByCode(webhookFeverDTO.getNotificationMessage().getCode());
    orderWebhookDetail.setCode(order.getCode());
    orderWebhookDetail.setPrevCode(webhookFeverDTO.getNotificationMessage().getPrevOrderCode());
    orderWebhookDetail.setReimburse(webhookFeverDTO.getNotificationMessage().getReimbursement());
    orderWebhookDetail.setUrl(webhookFeverDTO.getNotificationMessage().getUrl());
    orderWebhookDetail.setValue(order.getPrice().getFinalPrice());


    //TODO uncomment when fever implement the new data on orderDetails
//    webhookFeverDTO.getNotificationMessage().setCode(null);
//    webhookFeverDTO.getNotificationMessage().setPrevOrderCode(null);
      webhookFeverDTO.getNotificationMessage().setReimbursement(null);
//    webhookFeverDTO.getNotificationMessage().setUrl(null);

    order.getProducts().forEach(a -> orderWebhookDetail.getProducts().add(a.getId()));

    webhookFeverDTO.setFeverMessage(CommonConverter.convert(webhookFeverDTO.getNotificationMessage()));
    webhookFeverDTO.getFeverMessage().setOrderDetail(orderWebhookDetail);

    return webhookFeverDTO;
  }

    public WebhookFeverDTO sendPDFGenerationData(WebhookFeverDTO webhookFever) {

      OrderPrintDetailDTO orderPrintDetailDTO = getPrintDetails(webhookFever.getNotificationMessage());
      orderPrintDetailDTO.setCode(webhookFever.getNotificationMessage().getCode());

      webhookFever.getFeverMessage().setOrderPrintDetail(orderPrintDetailDTO);

      return webhookFever;
    }

    private OrderPrintDetailDTO getPrintDetails(NotificationMessageDTO message) {
        OrderPrintDetailDTO orderPrintDetailDTO = new OrderPrintDetailDTO();
        if (PrintStatus.ERROR.name().equals(message.getPrintStatus())) {
            orderPrintDetailDTO.setStatus(PdfStatus.ERROR);
            return orderPrintDetailDTO;
        } else if (PrintStatus.BLACKLISTED.name().equals(message.getPrintStatus())) {
            orderPrintDetailDTO.setStatus(PdfStatus.BLACKLISTED);
            return orderPrintDetailDTO;
        }
        try {
            OrderDTO order = msOrderRepository.getOrderByCodeCached(message.getCode());
            boolean avoidPrints = avoidAllPrints(order);
            if (Boolean.TRUE.equals(avoidPrints)) {
                orderPrintDetailDTO.setStatus(PdfStatus.AVOID_PRINT);
            } else if (isAllSessionSmartbooking(order.getProducts())) {
                orderPrintDetailDTO.setStatus(PdfStatus.INELIGIBLE);
            } else {
                PdfTicketDetails pdfTicketDetails = getPdfTicketDetails(message.getCode());
                orderPrintDetailDTO.setTicketUrl(pdfTicketDetails.getUrl());
                orderPrintDetailDTO.setStatus(PdfStatus.READY);
            }
            return orderPrintDetailDTO;

        } catch (OneboxRestException e) {
            if (ApiExternalErrorCode.ORDER_NOT_FOUND.getErrorCode().equals(e.getErrorCode())
                    || ApiExternalErrorCode.ORDER_TOTALLY_REFUNDED.getErrorCode().equals(e.getErrorCode())
                    || ApiExternalErrorCode.ORDER_NOT_PAID.getErrorCode().equals(e.getErrorCode())) {
                orderPrintDetailDTO.setStatus(PdfStatus.INELIGIBLE);
            } else if (ApiExternalErrorCode.S3_FILE_NOT_FOUND.getErrorCode().equals(e.getErrorCode())) {
                orderPrintDetailDTO.setStatus(PdfStatus.NOT_GENERATED);
            } else {
                orderPrintDetailDTO.setStatus(PdfStatus.ERROR);
            }
            return orderPrintDetailDTO;
        } catch (InterruptedException e) {
            orderPrintDetailDTO.setStatus(PdfStatus.ERROR);
            return orderPrintDetailDTO;
        }
    }

    private boolean isAllSessionSmartbooking(List<OrderProductDTO> products) {
        if (CollectionUtils.isNotEmpty(products)) {
            List<Long> sessionIds = new ArrayList<>();
            for (OrderProductDTO p : products) {
                if (ProductType.PRODUCT.equals(p.getType())) {
                    return false;
                } else if (!sessionIds.contains(p.getSessionId().longValue())){
                    sessionIds.add(p.getSessionId().longValue());
                }
            }
            if (!sessionIds.isEmpty()) {
                SessionSearchFilter filter = new SessionSearchFilter();
                filter.setId(sessionIds);
                SessionsDTO sessions = msEventRepository.getSessions(filter);
                return sessions.getData().stream().allMatch(s -> Boolean.TRUE.equals(s.getSmartBooking()));
            }
        }
        return false;
    }

    private PdfTicketDetails getPdfTicketDetails(String code) throws InterruptedException {
    PdfTicketDetails pdfTicketDetails = null;
    int counter = 1;
    do {
      try {
        pdfTicketDetails = msTicketRepository.getOrderMergedTickets(code);
      } catch (Exception e) {
        if (counter < MAX_RETRIES) {
          counter++;
          Thread.sleep(1000);
        } else {
          throw e;
        }
      }
    } while (counter <= MAX_RETRIES && pdfTicketDetails == null);
    return pdfTicketDetails;
  }

  private boolean avoidAllPrints(OrderDTO order) {
    Set<Integer> productEvents = order.getProducts().stream().filter(p-> !ProductType.PRODUCT.equals(p.getType())).map(OrderProductDTO::getEventId).collect(Collectors.toSet());
    Long channelId = order.getOrderData().getChannelId().longValue();
    return productEvents.stream().allMatch(eventId -> TicketHandlingType.AVOID_TICKET.equals(channelEventRepository.getChannelEventRelationship(channelId, eventId.longValue()).getTicketHandling()));
  }

}
