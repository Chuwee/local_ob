package es.onebox.common.tickets;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.oneboxtds.datasource.s3.ObjectPolicy;
import com.oneboxtds.datasource.s3.S3BinaryRepository;
import es.onebox.common.amt.AMTCustomTag;
import es.onebox.common.datasources.ms.entity.dto.Language;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.entity.repository.MasterDataRepository;
import es.onebox.common.datasources.ms.event.dto.CommunicationElement;
import es.onebox.common.datasources.ms.event.dto.EventDTO;
import es.onebox.common.datasources.ms.event.dto.EventsDTO;
import es.onebox.common.datasources.ms.event.dto.RateDTO;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.dto.SessionsDTO;
import es.onebox.common.datasources.ms.event.dto.TicketTemplateLiteral;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.order.dto.EventType;
import es.onebox.common.datasources.ms.order.dto.OrderDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductDTO;
import es.onebox.common.datasources.ms.order.dto.OrderProductRequest;
import es.onebox.common.datasources.ms.order.dto.TicketType;
import es.onebox.common.datasources.ms.order.enums.OrderActionTypeSupport;
import es.onebox.common.datasources.ms.order.enums.TicketFormat;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.ms.venue.dto.PriceTypeCommunicationElementFilter;
import es.onebox.common.datasources.ms.venue.dto.PriceTypeTicketCommunicationElement;
import es.onebox.common.datasources.ms.venue.dto.VenueDTO;
import es.onebox.common.datasources.ms.venue.repository.VenueTemplateRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.tickets.converter.TicketDataConverter;
import es.onebox.common.tickets.converter.TicketParamMapConverter;
import es.onebox.common.tickets.dto.SeasonDate;
import es.onebox.common.tickets.dto.SessionData;
import es.onebox.common.tickets.dto.SessionDate;
import es.onebox.common.url.ExternalTicketParams;
import es.onebox.common.url.ExternalTicketUrlBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.EncryptionUtils;
import es.onebox.tracer.core.AMT;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;


public abstract class AbstractGenerateTicketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractGenerateTicketService.class);
    public static final String PDF = "PDF";
    public static final String INVITATION = "INVITATION_PDF";


    protected final TicketGenerationSupport ticketGenerationSupport;
    protected final MsEventRepository msEventRepository;
    protected final MsOrderRepository msOrderRepository;
    protected final EntitiesRepository entitiesRepository;
    protected final VenueTemplateRepository venueTemplateRepository;
    protected final S3BinaryRepository s3TicketsExternalRepository;
    protected final MasterDataRepository masterDataRepository;
    protected final EncryptionUtils encryptionUtils;

    protected AbstractGenerateTicketService(TicketGenerationSupport ticketGenerationSupport,
                                            MsEventRepository msEventRepository, MsOrderRepository msOrderRepository,
                                            EntitiesRepository entitiesRepository, VenueTemplateRepository venueTemplateRepository,
                                            S3BinaryRepository s3TicketsExternalRepository,
                                            MasterDataRepository masterDataRepository, EncryptionUtils encryptionUtils) {
        this.ticketGenerationSupport = ticketGenerationSupport;
        this.msEventRepository = msEventRepository;
        this.msOrderRepository = msOrderRepository;
        this.entitiesRepository = entitiesRepository;
        this.venueTemplateRepository = venueTemplateRepository;
        this.s3TicketsExternalRepository = s3TicketsExternalRepository;
        this.masterDataRepository = masterDataRepository;
        this.encryptionUtils = encryptionUtils;
    }

    public void generateAndJoinTicketsPDF(String orderCode, Long userId) {
        String logTag = getLogsTag();
        if (ticketGenerationSupport.processReady(orderCode)) {
            LOGGER.info("[{}][{}] Ticket generation in process", logTag, orderCode);
            return;
        }
        AMT.addTracingAndAuditProperty(AMTCustomTag.ORDER_CODE.value(), orderCode);

        OrderDTO orderDTO = msOrderRepository.getOrderByCode(orderCode);
        Language language = getLanguage(orderDTO);
        ticketGenerationSupport.createExternalPrintGenerationMap(orderCode);
        LOGGER.info("[{}][{}] Start ticket generation process", logTag, orderCode);

        boolean containExternalTicket = containExternalTicket(orderDTO);

        List<OrderProductDTO> orderProductsList = getItemsForPDFTicket(orderDTO);

        Map<Long, SessionData> sessionsData = getSessionsData(orderProductsList, language);

        Map<Long, List<PriceTypeTicketCommunicationElement>> priceTypeCommElement = getPriceTypeCommElement(orderProductsList, language);

        List<JasperPrint> itemsPdf = orderProductsList.parallelStream()
                .map(orderProductDTO -> {
                    String reportFileName = getReportFileName(orderProductDTO);
                    JasperReport report = ticketGenerationSupport.loadReportFile(orderCode, reportFileName);
                    LOGGER.info("[{}][{}] Individual ticket generation for product {}", logTag, orderCode, orderProductDTO.getId());
                    return generateTicketsPDF(orderDTO, orderProductDTO, sessionsData, priceTypeCommElement, report, containExternalTicket, language);
                }).collect(Collectors.toList());

        ticketGenerationSupport.joinTicketsPDF(orderDTO, null, itemsPdf);

        if(userId != null) {
            setActionGenerated(userId, orderProductsList, orderDTO);
        }

        ticketGenerationSupport.removeSemaphore(orderDTO.getCode());
        LOGGER.info("[{}][{}] Finish ticket generation process", logTag, orderCode);
    }

    private void setActionGenerated(Long userId, List<OrderProductDTO> orderProductsList, OrderDTO orderDTO) {
        List<Long> items = orderProductsList.stream()
                .filter(item -> item.getRelatedRefundCode() == null)
                .map(OrderProductDTO::getId)
                .toList();
        OrderProductRequest orderProductRequest = new OrderProductRequest();
        orderProductRequest.setUserId(userId);
        orderProductRequest.setType(OrderActionTypeSupport.GENERATED);
        orderProductRequest.setFormat(TicketFormat.HARD_TICKET);
        orderProductRequest.setProductsId(items);
        msOrderRepository.upsertOrderAction(orderDTO.getCode(), orderProductRequest);
    }


    private static boolean isSeasonTicket(OrderProductDTO product) {
        return EventType.SEASON_TICKET.equals(product.getEventType());
    }


    private JasperPrint generateTicketsPDF(OrderDTO orderDTO, OrderProductDTO orderProductDTO,
                                           Map<Long, SessionData> sessionsData,
                                           Map<Long, List<PriceTypeTicketCommunicationElement>> priceTypeCommElements,
                                           JasperReport report, boolean containExternalTicket, Language language) {
        Long priceTypeId = orderProductDTO.getTicketData().getPriceZoneId().longValue();
        SessionData sessionData = sessionsData.get(orderProductDTO.getId());
        List<PriceTypeTicketCommunicationElement> priceTypeCommElement = priceTypeCommElements.get(priceTypeId);
        TicketData ticketData = prepareTicketData(orderDTO, orderProductDTO, sessionData, priceTypeCommElement,
                containExternalTicket, language);
        JasperPrint pdfData = getTicketPdfData(report, ticketData);
        ticketGenerationSupport.uploadTicketToS3(orderDTO.getCode(), pdfData, orderProductDTO);
        return pdfData;
    }

    protected TicketData prepareTicketData(OrderDTO orderDTO, OrderProductDTO orderProductDTO,
                                         SessionData sessionData,
                                         List<PriceTypeTicketCommunicationElement> priceTypeCommElement,
                                         boolean containExternalTicket, Language language) {
        TicketData ticketData = new TicketData();
        ticketData.setOrderCode(orderDTO.getCode());
        TicketDataConverter.fillDates(orderProductDTO, sessionData, ticketData, language);
        TicketDataConverter.fillAllocationData(orderProductDTO, ticketData, sessionData, encryptionUtils);
        TicketDataConverter.fillPricesData(orderProductDTO, ticketData, language, orderDTO.getPrice().getCurrency());
        TicketDataConverter.fillPromoterData(sessionData, ticketData);
        TicketDataConverter.fillAttendantInfo(orderProductDTO, ticketData);
        TicketDataConverter.fillTicketCommunicationElements(sessionData, priceTypeCommElement, ticketData);
        if (nonNull(sessionData.getRate())) {
            ticketData.setRate(sessionData.getRate().getName());
        }
        if (nonNull(orderProductDTO.getTicketData().getTicketType())) {
            ticketData.setTicketType(orderProductDTO.getTicketData().getTicketType().name());
        }
        if (containExternalTicket) {
            fillExternalTicketData(orderDTO, orderProductDTO, ticketData);
        }
        ticketData.setTicketLiteralsByCode(sessionData.getTicketTemplateLiteralsByCode());
        ticketData.setTicketCommElementsByTagType(sessionData.getTicketCommElementsByTagType());

        return ticketData;
    }

    private void fillExternalTicketData(OrderDTO orderDTO, OrderProductDTO orderProductDTO, TicketData ticketData) {
        String logTag = getLogsTag();
        String entityId = String.valueOf(orderProductDTO.getEventEntityId());
        Long sessionId = orderProductDTO.getSessionId().longValue();
        String orderId = orderDTO.getCode();
        Long productId = orderProductDTO.getId();
        ExternalTicketParams params = new ExternalTicketParams(entityId, sessionId, orderId, productId);
        String path = ExternalTicketUrlBuilder.of(params).build();
        try {
            if (Boolean.TRUE.equals(s3TicketsExternalRepository.existObject(path))) {
                Date expiration = DateUtils.addMinutes(new Date(), TicketGenerationSupport.EXPIRATION_MINUTES);
                String externalTicketUrl = s3TicketsExternalRepository.getPublicSignedUrl(path, ObjectPolicy.builder().expiration(expiration).build());
                ticketData.setExternalTicket(externalTicketUrl);
            } else {
                LOGGER.error("[{}][{}] External ticket image not found for path {}", logTag, orderDTO.getCode(), path);
            }
        } catch (AmazonS3Exception e) {
            LOGGER.error("[{}][{}] External ticket image not found for product {}", logTag, orderDTO.getCode(), orderProductDTO.getId(), e);
        }
    }


    private JasperPrint getTicketPdfData(JasperReport report, TicketData ticketData) {
        Map<String, Object> params = TicketParamMapConverter.fillReportParams(ticketData);
        String orderCode = params.get("LOCALIZADOR").toString();
        String logTag = getLogsTag();

        JasperPrint printer;
        try {
            printer = JasperFillManager.fillReport(report, params, new JREmptyDataSource());
            if (printer.getPages().size() > 1) {
                printer.removePage(1);
            }
        } catch (Exception e) {
            LOGGER.error("[{}][{}] Error filling the data in the in the jasper print", logTag, orderCode, e);
            ticketGenerationSupport.removeSemaphore(orderCode);
            throw new OneboxRestException(ApiExternalErrorCode.GENERIC_ERROR, "An error occurred filling the data in the in the jasper print", null);
        }
        return printer;
    }

    private Map<Long, SessionData> getSessionsData(List<OrderProductDTO> itemsList, Language language) {
        Map<Long, SessionData> result = new HashMap<>();
        for (OrderProductDTO item : itemsList) {
            Long sessionId = item.getSessionId().longValue();
            Long eventId = item.getEventId().longValue();

            SessionDTO session = msEventRepository.getSession(eventId, sessionId);

            SessionData sessionData = new SessionData();

            if (session.getDate() != null) {
                SessionDate date = new SessionDate();
                date.setStart(session.getDate().getStart());
                date.setEnd(session.getDate().getEnd());
                sessionData.setSessionDate(date);
            }

            if (nonNull(session) && !CollectionUtils.isEmpty(session.getSessionIds()) && !isSeasonTicket(item)) {
                SessionsDTO sessions = msEventRepository.getSessions(eventId, session.getSessionIds());
                if (!CollectionUtils.isEmpty(sessions.getData())) {
                    sessions.getData().sort(Comparator.comparing(o -> o.getDate().getStart()));
                    sessionData.setSeasonsDate(new SeasonDate(
                            CollectionUtils.firstElement(sessions.getData()).getDate().getStart(),
                            CollectionUtils.lastElement(sessions.getData()).getDate().getStart()));
                }
            }
            sessionData.setSessionName(session.getName());
            sessionData.setSessionType(session.getSessionType());
            sessionData.setShowDate(session.getShowDate());
            sessionData.setShowDatetime(session.getShowDatetime());

            RateDTO rateDTO = msEventRepository.getRate(eventId, item.getTicketData().getRateId().longValue());
            sessionData.setRate(rateDTO);

            VenueDTO venueDTO = venueTemplateRepository.getVenue(item.getVenueId().longValue());
            sessionData.setVenue(venueDTO);

            EventsDTO eventsDTO = msEventRepository.getCachedEventWithSeasonTickets(item.getEventId().longValue());
            EventDTO eventDTO = eventsDTO != null && !eventsDTO.getData().isEmpty() ? eventsDTO.getData().get(0) : null;

            if (eventDTO == null) {
                throw new OneboxRestException(ApiExternalErrorCode.EVENT_NOT_FOUND);
            }

            Long promoterId;
            if (nonNull(session.getProducerId())) {
                promoterId = session.getProducerId().longValue();
            } else {
                promoterId = eventDTO.getProducer() != null ? eventDTO.getProducer().getId() : null;
            }

            if (isSeasonTicket(item)) {
                sessionData.setEventCommElement(
                        msEventRepository.getSeasonTicketCommunicationElements(eventId, language.getId(), PDF));
            } else {
                String type = PDF;
                if(BooleanUtils.isTrue(TicketType.INVITATION.equals(item.getTicketData().getTicketType()))){
                   type = INVITATION;
                }
                sessionData.setEventCommElement(
                        msEventRepository.getEventTicketCommunicationElements(eventId, language.getId(), type));
                sessionData.setSessionCommElement(
                        msEventRepository.getSessionTicketCommunicationElements(eventId, sessionId, language.getId(), type));
            }

            if (promoterId != null) {
                sessionData.setPromoter(entitiesRepository.getProducerById(promoterId));
            }

            List<TicketTemplateLiteral> ticketTemplateLiterals = msEventRepository.getTicketTemplateLiterals(
                    eventDTO.getTicketTemplates().getIndividualTicketPdfTemplateId(), language.getId());

            Map<String, String> templatesLiteralsByCode = ticketTemplateLiterals.stream()
                    .collect(Collectors.toMap(TicketTemplateLiteral::getCode, TicketTemplateLiteral::getValue));
            sessionData.setTicketTemplateLiteralsByCode(templatesLiteralsByCode);

            List<CommunicationElement> ticketTemplateComElements = msEventRepository.getTicketTemplateCommElements(
                    eventDTO.getTicketTemplates().getIndividualTicketPdfTemplateId(), language.getId());

            Map<String, String> templatesCommElementsByTagType = ticketTemplateComElements.stream()
                    .collect(Collectors.toMap(CommunicationElement::getTagType, CommunicationElement::getValue));
            sessionData.setTicketCommElementsByTagType(templatesCommElementsByTagType);
            result.put(item.getId(), sessionData);
        }
        return result;
    }

    private Map<Long, List<PriceTypeTicketCommunicationElement>> getPriceTypeCommElement(List<OrderProductDTO> itemsList,
                                                                                         Language language) {
        Map<Long, List<PriceTypeTicketCommunicationElement>> result = new HashMap<>();
        for (OrderProductDTO item : itemsList) {
            Long sessionId = item.getSessionId().longValue();
            Long eventId = item.getEventId().longValue();


            SessionDTO session = msEventRepository.getSession(eventId, sessionId);

            Long venueTemplateId = session.getVenueConfigId();
            Long priceTypeId = item.getTicketData().getPriceZoneId().longValue();
            PriceTypeCommunicationElementFilter priceTypeCommunicationElementFilter = new PriceTypeCommunicationElementFilter();
            priceTypeCommunicationElementFilter.setLanguage(language.getCode());
            List<PriceTypeTicketCommunicationElement> priceTypeComElements =
                    venueTemplateRepository.getPriceTypeTicketCommunicationElements(venueTemplateId, priceTypeId,
                            priceTypeCommunicationElementFilter);
            result.put(priceTypeId, priceTypeComElements);
        }
        return result;
    }

    protected abstract String getReportFileName(OrderProductDTO orderProductDTO);

    protected abstract boolean containExternalTicket(OrderDTO orderDTO);

    protected abstract List<OrderProductDTO> getItemsForPDFTicket(OrderDTO orderDTO);

    protected abstract Language getLanguage(OrderDTO orderDTO);

    protected abstract String getLogsTag();

}
