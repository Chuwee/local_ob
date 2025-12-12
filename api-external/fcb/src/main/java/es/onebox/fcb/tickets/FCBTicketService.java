package es.onebox.fcb.tickets;

import es.onebox.alert.sender.service.SendAlertService;
import es.onebox.common.datasources.accounting.dto.TransactionAudit;
import es.onebox.common.datasources.accounting.repository.BalanceRepository;
import es.onebox.common.datasources.avetconfig.dto.CapacityDTO;
import es.onebox.common.datasources.avetconfig.dto.SessionMatch;
import es.onebox.common.datasources.avetconfig.repository.IntAvetConfigRepository;
import es.onebox.common.datasources.ms.entity.dto.CountryDTO;
import es.onebox.common.datasources.ms.event.dto.PackDTO;
import es.onebox.common.datasources.ms.channel.dto.config.ChannelConfigDTO;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.client.dto.Client;
import es.onebox.common.datasources.ms.client.dto.ClientB2BBranch;
import es.onebox.common.datasources.ms.client.repository.ClientsRepository;
import es.onebox.common.datasources.ms.collective.dto.ResponseCollectiveDTO;
import es.onebox.common.datasources.ms.collective.repository.CollectivesRepository;
import es.onebox.common.datasources.ms.crm.dto.ChannelAgreement;
import es.onebox.common.datasources.ms.crm.dto.CrmClientDocResponse;
import es.onebox.common.datasources.ms.crm.dto.CrmClientResponse;
import es.onebox.common.datasources.ms.crm.dto.CrmOrderContainer;
import es.onebox.common.datasources.ms.crm.dto.CrmOrderParams;
import es.onebox.common.datasources.ms.crm.dto.CrmOrderResponse;
import es.onebox.common.datasources.ms.crm.dto.CrmParams;
import es.onebox.common.datasources.ms.crm.dto.CrmProductDocResponse;
import es.onebox.common.datasources.ms.crm.repository.AbandonedCartRepository;
import es.onebox.common.datasources.ms.entity.dto.CountrySubdivisionDTO;
import es.onebox.common.datasources.ms.entity.dto.EntityDTO;
import es.onebox.common.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.common.datasources.ms.entity.repository.MasterDataRepository;
import es.onebox.common.datasources.ms.event.dto.EventDTO;
import es.onebox.common.datasources.ms.event.dto.ProductVariant;
import es.onebox.common.datasources.ms.event.dto.SessionDTO;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.datasources.ms.event.repository.PackRepository;
import es.onebox.common.datasources.ms.event.repository.ProductsRepository;
import es.onebox.common.datasources.ms.order.dto.*;
import es.onebox.common.datasources.ms.order.enums.OrderType;
import es.onebox.common.datasources.ms.order.enums.ProductType;
import es.onebox.common.datasources.ms.order.repository.MsOrderRepository;
import es.onebox.common.datasources.ms.venue.dto.MsPriceTypeDTO;
import es.onebox.common.datasources.ms.venue.repository.VenueTemplateRepository;
import es.onebox.common.datasources.payment.dto.ChannelGatewayConfig;
import es.onebox.common.datasources.payment.dto.PaymentOrder;
import es.onebox.common.datasources.payment.repository.PaymentRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.MemberValidationUtils;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.utils.common.DateUtils;
import es.onebox.core.utils.common.NumberUtils;
import es.onebox.dal.dto.couch.enums.PaymentType;
import es.onebox.dal.dto.couch.enums.UserType;
import es.onebox.fcb.dao.B2CPeopleSoftCounterCouchDao;
import es.onebox.fcb.dao.ChannelCouchDao;
import es.onebox.fcb.datasources.config.FcbChannelMappingsProperties;
import es.onebox.fcb.datasources.config.FcbVenueMappingsProperties;
import es.onebox.fcb.datasources.config.FcbVipMappingsProperties;
import es.onebox.fcb.datasources.peoplesoft.repository.PeopleSoftRepository;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.Adreca;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.DadesAltaClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.DadesContacte;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.DadesIVA;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.DadesModificarClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.DadesTargeta;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.OpcionsBancaries;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.PeticioAltaClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.PeticioCercaClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.PeticioModificacioClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.RespostaAltaClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.RespostaCercaClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.clients.RespostaModificacioClient;
import es.onebox.fcb.datasources.peoplesoft.wsdl.factures.ArrayOfLinia;
import es.onebox.fcb.datasources.peoplesoft.wsdl.factures.Linia;
import es.onebox.fcb.datasources.peoplesoft.wsdl.factures.PeticioFacturar;
import es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria.AltaDipositInputType;
import es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria.ClientsSistemaOrigenType;
import es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria.CobramentType;
import es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria.DadesCompteBancDipositType;
import es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria.DadesDipositType;
import es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria.DadesImportDipositType;
import es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria.ReferenciaType;
import es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria.ReferenciesType;
import es.onebox.fcb.datasources.peoplesoft.wsdl.tresoreria.SistemesExternsType;
import es.onebox.fcb.datasources.salesforce.dto.RequestAbandonedDataDTO;
import es.onebox.fcb.datasources.salesforce.dto.RequestAbandonedOrderDTO;
import es.onebox.fcb.datasources.salesforce.repository.SalesforceRepository;
import es.onebox.fcb.domain.Channel;
import es.onebox.fcb.tickets.dto.OperationIdRequest;
import es.onebox.fcb.utils.FcbDateUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
public class FCBTicketService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FCBTicketService.class);

    public static final String ACCOUNTING_PAYMENT = "oneboxAccounting";
    public static final String FCB_ONEBOX = "FCB_ONEBOX";
    private static final String FCB_ID_CANAL = "FCB_ID_CANAL";
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public static final String PROMOTION_CODE = "FCB_ONEBOX";
    private static final String REGISTRE_UNIC = "validadorFCBRegistreUnic";
    private static final String ABANDONED_STATUS = "ABANDONED";
    private static final String B2B_IDENTIFIER = "B2B_%s";
    private static final String B2B_OTA_IDENTIFIER = "B2B_OTA_%s";
    private static final String B2B_PALISIS_OTA_IDENTIFIER = "B2B_PALISIS_OTA_%s";
    private static final String B2C_IDENTIFIER = "B2C_%s";
    private static final String TYPE_PUBLIC = "PUBLIC";
    public static final String BASIC_PLUS_CODE = "BLM REGULAR PLUS";
    public static final String EXTRA_BASIC_PLUS_CODE = "EXTRAPLUS";
    public static final String COD_CALIFICACIO = "IO";
    public static final String COMPTE_ID_BANC = "2100";
    public static final String COMPTE_NUM = "AFOR";
    public static final String METODE_PAGAMENT = "TARGETA-CAIXABANK";

    private static final String LANG_ESP = "ESP";
    private static final String LANG_CAT = "CAT";
    private static final String LANG_ENG = "ENG";

    public static final String COUNTRY_ES = "ES";
    public static final String LANG_CA = "ca";

    public static final String EFECTIU = "EFECTIU";
    public static final String TRANSFERENCIA_PENDENT = "TRANSFERENCIA PEND.";
    public static final String TRANSFERENCIA_COBRADA = "TRANSFERENCIA COB.";
    public static final String TRANSFERENCIA_SALDO = "TRANSFERENCIA-SALDO";
    public static final String SALDO = "SALDO";
    public static final String CREDIT = "CREDIT";
    public static final String ANTICIPO = "ANTICIPO";
    public static final String VAL_A_CARREGAR = "VAL A CARREGAR";
    public static final String DATAFON = "DATAFON";

    public static final String NOT_DEFINED = "Not defined";
    public static final String EURO_CODE = "EUR";
    public static final String DEFAULT_SELLER_CHANNEL = "0201"; // 0201 ONEBOX-ESPORTS_B2C
    public static final String NEWSLETTER = "newsletter"; // Barca fans
    public static final String INVITATION = "invitacion";
    public static final String PROMOTION = "descuentos";
    public static final String SELLER = "ONEBOX";
    public static final String DEFAULT_CHANNEL = "0201";
    public static final String CANAL_TAQUILLA_WEB = "TAQUILLA_WEB";
    public static final String CANAL_VENDA_B2B = "OB-B2B";
    public static final String CANAL_TAQUILLES = "TAQUILLES";
    public static final String NOT_PACK = "not_pack";
    public static final int TIME_TO_SLEEP = 200;
    public static final int MAX_RETRIES = 5;
    public static final String MUSEO = "MUSE";
    public static final String TOURS = "TOURS";
    public static final String PRODUCT_AUDIOGUIA = "PACK.AUDIOGUIA";
    public static final String PRODUCT_ECOTASA = "FEE MEDIOAMBIENT";
    public static final String NO_ERP = "NOERP";
    private static final double MUSEO_PRICE = 2d;
    private static final double ECOTASA_PRICE = 0.5d;
    private static final String BIZUM = "Bizum";
    private static final String PREFIX_B2B = "B2B_";
    private static final String PREFIX_B2B_PALISIS_OTA = "B2B_PALISIS_OTA_";
    private static final String PREFIX_B2B_OTA = "B2B_OTA_";
    private static final String PREFIX_B2C = "B2C_";
    private static final String CLAU_CLIENT = "companyName"; // clua client from buyer data(companyName)

    private Map<Integer, Boolean> collectiveRegistreUnic = new HashMap<>();
    private Map<String, Boolean> validNif = new HashMap<>();
    private Map<String, String> countryCode = new HashMap<>();

    @Value("${fcb.entity.entityId}")
    private long fcbEntity;

    @Value("${fcb.channel.url}")
    private String channelUrl;

    @Value("${onebox.alert.pagerduty.services.integration}")
    private String integration;

    private final FcbChannelMappingsProperties fcbChannelMappingsProperties;
    private final FcbVenueMappingsProperties fcbVenueMappingsProperties;
    private final MsOrderRepository msOrderRepository;
    private final PeopleSoftRepository peopleSoftRepository;
    private final SalesforceRepository salesforceRepository;
    private final AbandonedCartRepository abandonedCartRepository;
    private final OperationCodeService operationCodeService;
    private final CollectivesRepository collectivesRepository;
    private final MasterDataRepository masterDataRepository;
    private final ClientsRepository clientsRepository;
    private final EntitiesRepository entitiesRepository;
    private final PaymentRepository paymentRepository;
    private final ChannelRepository channelRepository;
    private final IntAvetConfigRepository intAvetConfigRepository;
    private final MsEventRepository msEventRepository;
    private final BalanceRepository balanceRepository;
    private final ProductsRepository productsRepository;
    private final VenueTemplateRepository venueTemplateRepository;
    private final PackRepository packRepository;
    private final ChannelCouchDao channelCouchDao;
    private final B2CPeopleSoftCounterCouchDao b2cPeopleSoftCounterCouchDao;
    private final SendAlertService sendAlertService;

    @Autowired
    public FCBTicketService(FcbChannelMappingsProperties fcbChannelMappingsProperties,
                            FcbVenueMappingsProperties fcbVenueMappingsProperties,
                            MsOrderRepository msOrderRepository,
                            PeopleSoftRepository peopleSoftRepository,
                            SalesforceRepository salesforceRepository,
                            AbandonedCartRepository abandonedCartRepository,
                            OperationCodeService operationCodeService,
                            CollectivesRepository collectivesRepository,
                            MasterDataRepository masterDataRepository,
                            ClientsRepository clientsRepository,
                            EntitiesRepository entitiesRepository,
                            PaymentRepository paymentOrdersRepository,
                            ChannelRepository channelRepository,
                            IntAvetConfigRepository intAvetConfigRepository,
                            MsEventRepository msEventRepository,
                            BalanceRepository balanceRepository,
                            ProductsRepository productsRepository,
                            VenueTemplateRepository venueTemplateRepository,
                            PackRepository packRepository,
                            ChannelCouchDao channelCouchDao,
                            B2CPeopleSoftCounterCouchDao b2cPeopleSoftCounterCouchDao,
                            SendAlertService sendAlertService) {
        this.fcbChannelMappingsProperties = fcbChannelMappingsProperties;
        this.fcbVenueMappingsProperties = fcbVenueMappingsProperties;
        this.msOrderRepository = msOrderRepository;
        this.peopleSoftRepository = peopleSoftRepository;
        this.salesforceRepository = salesforceRepository;
        this.abandonedCartRepository = abandonedCartRepository;
        this.operationCodeService = operationCodeService;
        this.collectivesRepository = collectivesRepository;
        this.masterDataRepository = masterDataRepository;
        this.clientsRepository = clientsRepository;
        this.entitiesRepository = entitiesRepository;
        this.paymentRepository = paymentOrdersRepository;
        this.channelRepository = channelRepository;
        this.intAvetConfigRepository = intAvetConfigRepository;
        this.msEventRepository = msEventRepository;
        this.balanceRepository = balanceRepository;
        this.productsRepository = productsRepository;
        this.venueTemplateRepository = venueTemplateRepository;
        this.packRepository = packRepository;
        this.channelCouchDao = channelCouchDao;
        this.b2cPeopleSoftCounterCouchDao = b2cPeopleSoftCounterCouchDao;
        this.sendAlertService = sendAlertService;
    }

    public PeticioFacturar registerOperation(String code) {
        OrderDTO order = msOrderRepository.getOrderByCode(code);

        if (!isFCBTransaction(order)) {
            LOGGER.info("[FCB WEBHOOK] Notification ignored as there are no products from FCB. Code: {} ", code);
            return null;
        }
        removeNonFCBProducts(order);

        PeticioFacturar peticioFacturar = new PeticioFacturar();
        BigDecimal totalImport = BigDecimal.ZERO;

        ArrayOfLinia arrayOfLinia = new ArrayOfLinia();
        peticioFacturar.setLinies(arrayOfLinia);
        List<Linia> lines = arrayOfLinia.getLinia();

        boolean isB2b = (order.getClient() != null && order.getClient().getClientB2B() != null);
        boolean saleWithRefund = order.getProducts()
                .stream()
                .anyMatch(orderProductDTO -> StringUtils.isNotEmpty(orderProductDTO.getRelatedRefundCode()));

        if (!saleWithRefund) {
            PaymentOrder paymentOrder;
            if (OrderType.REFUND.equals(order.getStatus().getType())) {
                paymentOrder = paymentRepository.getPaymentOrder(order.getRelatedOriginalCode());
            } else {
                paymentOrder = paymentRepository.getPaymentOrder(order.getCode());
            }

            Map<String, List<OrderProductDTO>> productByPack = order.getProducts()
                    .stream()
                    .collect(Collectors.groupingBy(orderProductDTO -> orderProductDTO.getPack() != null && orderProductDTO.getPack().getCode() != null ? orderProductDTO.getPack().getCode() : NOT_PACK));

            Channel channelErpConfig = channelCouchDao.get(order.getOrderData().getChannelId().toString());
            Linia contextLine = getContextLine(order, isB2b, channelErpConfig);

            totalImport = addTicketLines(order, productByPack.get(NOT_PACK), paymentOrder, totalImport, lines, isB2b, true, null, contextLine, channelErpConfig);
            productByPack.remove(NOT_PACK);
            for (String pack : productByPack.keySet()) {
                totalImport = addPack(order, productByPack.get(pack), paymentOrder, totalImport, lines, isB2b, lines.size(), contextLine, channelErpConfig);
            }

            if (isB2b && (paymentOrder == null || ACCOUNTING_PAYMENT.equals(paymentOrder.getGatewaySid()))) {
                // Consume all import with Saldo then we send 0 in totalImport in Peoplesoft
                BigDecimal charges = getOrderTotalCharges(order);
                addB2BLine(order, lines, totalImport.add(charges));
                if (charges.compareTo(BigDecimal.ZERO) > 0) {
                    totalImport = charges.multiply(BigDecimal.valueOf(-1));
                } else {
                    totalImport = BigDecimal.ZERO;
                }
            }
            peticioFacturar.setTotalEntrades(lines.size());
            peticioFacturar.setGenerarFactura("0");
            peticioFacturar.setImportTotal(totalImport);
            peticioFacturar.setSistemaOrigen(FCB_ONEBOX);

            peticioFacturar.setMessageId(code);

            peopleSoftRepository.registerOperation(peticioFacturar);

            //registerTresoreria(order, peticioFacturar);
        }
        return peticioFacturar;
    }

    private Boolean isFCBTransaction(OrderDTO order) {
        if (order == null) {
            throw new OneboxRestException(ApiExternalErrorCode.ORDER_NOT_FOUND);
        }
        return order.getProducts().stream()
                .anyMatch(product -> product.getEventEntityId() != null && product.getEventEntityId().longValue() == fcbEntity);
    }

    private void removeNonFCBProducts (OrderDTO order) {
        List<OrderProductDTO> originalProducts = order.getProducts();
        List<OrderProductDTO> filteredProducts = originalProducts.stream()
                .filter(product -> Objects.nonNull(product.getEventEntityId()) &&
                        product.getEventEntityId().longValue() == fcbEntity)
                .toList();

        int total = originalProducts.size();
        int matched = filteredProducts.size();
        if (total != matched) {
            LOGGER.info("[FCB WEBHOOK] Order contains FCB products: {} of {}. Code: {}",
                    matched, total, order.getCode());
            order.setProducts(filteredProducts);
        }
    }

    private void registerTresoreria(OrderDTO order, PeticioFacturar peticioFacturar) {
        OrderPaymentDTO paymentDTO = order.getPayments().stream()
                .filter(payment -> StringUtils.isNotBlank(payment.getPaymentExtraCode()))
                .findAny()
                .orElse(null);
        if (paymentDTO != null && ChannelType.BOXOFFICE.equals(order.getOrderData().getChannelType())) {
            Linia linia = peticioFacturar.getLinies().getLinia().get(0);

            AltaDipositInputType diposit = new AltaDipositInputType();

            DadesCompteBancDipositType compte = new DadesCompteBancDipositType();
            compte.setIdBanc(COMPTE_ID_BANC);
            compte.setNumCompteBanc(COMPTE_NUM);
            diposit.setCompteBancDiposit(compte);

            SistemesExternsType sistemes = new SistemesExternsType();
            sistemes.setSistemaOrigen(FCB_ONEBOX);
            sistemes.setCanalVenda(linia.getCanalVenda());
            diposit.setSistemesExterns(sistemes);

            DadesDipositType dades = new DadesDipositType();
            dades.setIdDiposit(linia.getIdOperacio());
            dades.setRefConciliacio(paymentDTO.getPaymentExtraCode());
            XMLGregorianCalendar date = DateUtils.convertDateToXMLGregorianCalendar(DateUtils.getDate(paymentDTO.getPaymentDate()));
            dades.setDataContable(date);
            dades.setDataRecepcio(date);
            dades.setTotalLiniesDiposit(BigInteger.ONE);
            DadesImportDipositType importDiposit = new DadesImportDipositType();
            importDiposit.setTotalImportDiposit(BigDecimal.valueOf(paymentDTO.getValue()));
            importDiposit.setMoneda(order.getPrice().getCurrency());
            importDiposit.setCotitzacio(BigDecimal.ONE);

            dades.setDadesImportDiposit(importDiposit);
            diposit.setDiposit(dades);

            AltaDipositInputType.Cobraments cobraments = new AltaDipositInputType.Cobraments();
            CobramentType cobrament = new CobramentType();
            cobrament.setIdCobrament(linia.getIdOperacio());
            cobrament.setImportCobrament(BigDecimal.valueOf(paymentDTO.getValue()));
            cobrament.setMetodePagament(METODE_PAGAMENT);
            ClientsSistemaOrigenType clients = new ClientsSistemaOrigenType();
            clients.getIdClient().add(linia.getClau());
            cobrament.setClientsSistemaOrigen(clients);
            ReferenciesType referencies = new ReferenciesType();
            cobrament.setReferencies(referencies);
            ReferenciaType referencia = new ReferenciaType();
            referencia.setCodCalificacio(COD_CALIFICACIO);
            referencia.setOrdreCompra(linia.getIdOperacio());
            referencia.setImportUsuari(BigDecimal.valueOf(paymentDTO.getValue()));
            cobrament.getReferencies().getReferencia().add(referencia);
            cobraments.getCobrament().add(cobrament);
            diposit.setCobraments(cobraments);

            peopleSoftRepository.altaDiposit(diposit);
        }
    }

    private BigDecimal addTicketLines(OrderDTO order, List<OrderProductDTO> products, PaymentOrder paymentOrder,
                                      BigDecimal totalImport, List<Linia> lines, boolean isB2b, boolean addMuseu,
                                      Integer totalLineCount, Linia contextLine, Channel channelErpConfig) {
        if (CollectionUtils.isNotEmpty(products)) {
            if (addMuseu) {
                List<OrderProductDTO> museus = getMuseuWithAudioGuiaItem(products);
                for (OrderProductDTO product : museus) {
                    addMuseuProducts(products, product, OrderType.REFUND.equals(order.getStatus().getType()), PRODUCT_AUDIOGUIA, MUSEO_PRICE);
                    addMuseuProducts(products, product, OrderType.REFUND.equals(order.getStatus().getType()), PRODUCT_ECOTASA, ECOTASA_PRICE);
                }
            }

            for (OrderProductDTO product : products) {

                Linia line = newLine(order, paymentOrder, lines, isB2b, channelErpConfig, totalLineCount, contextLine);

                BigDecimal totalPromotion = getTotalPromotion(product);
                BigDecimal totalComission = getTotalCharges(product).abs();

                Integer collectiveId = null;
                String collectiveKey = null;
                if (product.getPromotions() != null) {
                    OrderPromotionDTO promotions = product.getPromotions();
                    if (promotions.getPromotion() != null && promotions.getPromotion().getCollectiveId() != null) {
                        collectiveId = promotions.getPromotion().getCollectiveId();
                        collectiveKey = promotions.getPromotion().getCollectiveKey();
                    } else if (promotions.getDiscount() != null && promotions.getDiscount().getCollectiveId() != null) {
                        collectiveId = promotions.getDiscount().getCollectiveId();
                        collectiveKey = promotions.getDiscount().getCollectiveKey();
                    }
                }

                if (collectiveId != null && collectiveKey != null) {
                    Boolean isRegistreUnic = collectiveRegistreUnic.get(collectiveId);
                    if (isRegistreUnic == null) {
                        ResponseCollectiveDTO collective = collectivesRepository.getCollective(collectiveId.longValue());
                        isRegistreUnic = REGISTRE_UNIC.equals(collective.getExternalValidator());
                        collectiveRegistreUnic.put(collectiveId, isRegistreUnic);
                    }
                    //SI YA TIENE CLAU (= ha pedido factura) NO SE LE METE EL SOCIO
                    if (isRegistreUnic && StringUtils.isBlank(line.getClau())) {
                        line.setClau(MemberValidationUtils.getPromotionPartnerId(collectiveKey));
                    }
                }

                if (ProductType.PRODUCT.equals(product.getType())) {
                    ProductVariant productVariant = productsRepository.getProductVariant(product.getProductData().getId(), product.getProductData().getVariantId());
                    line.setIdProducte(productVariant.getSku());
                    // If we receive sessionId in reference this a complement
                    if (product.getProductData().getDelivery().getSessionId() != null) {
                        SessionDTO sessionReference = msEventRepository.getSession(product.getProductData().getDelivery().getSessionId());
                        if (sessionReference != null) {
                            EventDTO event = msEventRepository.getEvent(sessionReference.getEventId());
                            if (event != null && es.onebox.common.datasources.ms.event.enums.EventType.AVET.equals(event.getType())) {
                                String codeMatch = getAvetCodeMatch(sessionReference.getId(), sessionReference.getEntityId());
                                line.setComplementAnalitic(codeMatch);
                            }
                        }
                    }
                } else if (product.getEventType() != null) {
                    fillIdProductoByEventType(order, product, line);
                } else {
                    line.setIdProducte(product.getProductData().getName());
                }

                BigDecimal preuVenta;
                if (product.getPrice().getBasePrice() != null) {
                    preuVenta = NumberUtils.minus(BigDecimal.valueOf(product.getPrice().getBasePrice()), totalPromotion);
                    line.setPreuVenda(BigDecimal.valueOf(product.getPrice().getBasePrice()));
                } else {
                    preuVenta = BigDecimal.valueOf(product.getPrice().getInformativePrices().getPackItem());
                    line.setPreuVenda(preuVenta);
                }

                line.setCodiPromocio(totalPromotion.doubleValue() != 0d ? PROMOTION_CODE : "");
                line.setImportPromocio(totalPromotion);
                line.setImportComisio(totalComission);
                line.setLocalitzador(order.getCode());

                if (product.getPrice().getClientConditionsPrice() != null) {
                    preuVenta = NumberUtils.minus(preuVenta, BigDecimal.valueOf(product.getPrice().getClientConditionsPrice()));
                    line.setImportPromocio(totalPromotion.add(BigDecimal.valueOf(product.getPrice().getClientConditionsPrice())));
                }
                if (product.getPrice().getClientCommission() != null) {
                    line.setImportComisio(totalComission.add(BigDecimal.valueOf(product.getPrice().getClientCommission())));
                }
                totalImport = totalImport.add(preuVenta);

                lines.add(line);
            }
        }

        return totalImport;
    }

    private Linia getContextLine(OrderDTO order, boolean isB2b, Channel channelErpConfig) {
        Linia contextLine = new Linia();
        if (channelErpConfig != null) {
            contextLine.setCanalVenda(channelErpConfig.getSalesChannel());
        } else if (ChannelType.BOXOFFICE.equals(order.getOrderData().getChannelType())) {
            //Sobreescriurem si es taquilla amb metodes de pagament transgarencia a fillBoxOfficePayment
            contextLine.setCanalVenda(CANAL_TAQUILLES);
        } else {
            contextLine.setCanalVenda(getSellerChannel(order.getOrderData().getChannelId().longValue(), order.getPayments(), isB2b));
        }

        //A BOXOFFICE clau i autofactura a false -> fillBoxOfficePayment
        contextLine.setClau("");
        boolean isB2BUser = isB2BUser(order);
        if (isB2b && !isB2BBecameWebBoxOffice(order)) {
            contextLine.setClau(getOrCreateClientId(order, true, null, null, order.getCode(), contextLine.getCanalVenda()));
        } else if (channelErpConfig != null) {
            if (channelErpConfig.getKey() != null) {
                contextLine.setClau(getOrCreateClientId(order, false, channelErpConfig, null, order.getCode(), channelErpConfig.getSalesChannel()));
            }
        } else if (isB2C(order.getOrderData().getChannelType(), isB2BUser) && b2cRequiresInvoice(order)) {
            contextLine.setClau(getOrCreateClientId(order, false, null, null, order.getCode(), contextLine.getCanalVenda()));
        }
        //Autofactura sempre a false SI HI HA CLAU
        if (StringUtils.isNotBlank(contextLine.getClau())) {
            contextLine.setAutofactura(Boolean.FALSE);
        } else {
            contextLine.setAutofactura(Boolean.TRUE);
        }
        return contextLine;
    }

    private static boolean isB2BUser(OrderDTO order) {
        return  order.getOrderData().getUserType() != null && UserType.B2B.equals(order.getOrderData().getUserType());
    }

    private boolean b2cRequiresInvoice(OrderDTO order) {
        return order.getInvoiceData().getIdentification() != null;
    }

    private boolean isB2C(ChannelType channelType, Boolean isB2BUser) {
        return ChannelType.PORTAL.equals(channelType) && !isB2BUser;
    }

    private void fillIdProductoByEventType(OrderDTO order, OrderProductDTO product, Linia line) {
        switch (product.getEventType()) {
            case AVET -> {
                String codeMatch = getAvetCodeMatch(product.getSessionId().longValue(), product.getEventEntityId().longValue());
                ChannelConfigDTO channelConfig = channelRepository.getChannelConfig(order.getOrderData().getChannelId().longValue());
                String vipCode = getFcbChannelCode(channelConfig);
                if(isVIPChannel(channelConfig) && StringUtils.isNotEmpty(vipCode)) {
                    line.setIdProducte(vipCode);
                    line.setComplementAnalitic(codeMatch);
                } else {
                    line.setIdProducte(codeMatch);
                }
            }
            case NORMAL, ACTIVITY, THEME_PARK -> {
                SessionDTO session = msEventRepository.getSession(product.getSessionId().longValue());
                MsPriceTypeDTO priceType = venueTemplateRepository.getPriceType(session.getVenueConfigId(), product.getTicketData().getPriceZoneId().longValue());
                line.setIdProducte(priceType != null ? priceType.getCode() : Long.toString(product.getId()));
                // If we receive sessionId in reference this a complement
                if (StringUtils.isNotEmpty(session.getReference()) && org.apache.commons.lang3.math.NumberUtils.isDigits(session.getReference())) {
                    try {
                        SessionDTO sessionReference = msEventRepository.getSession(Long.valueOf(session.getReference()));
                        String codeMatch = getAvetCodeMatch(sessionReference.getId(), sessionReference.getEntityId());
                        line.setComplementAnalitic(codeMatch);
                    } catch (Exception e) {
                        LOGGER.info("[FCB WEBHOOK] Session with reference: {} not found", session.getReference());
                    }
                }
            }
            default -> line.setIdProducte(Long.toString(product.getId()));
        }
    }

    private String getAvetCodeMatch(Long sessionId, Long eventEntityId) {
        SessionMatch session = intAvetConfigRepository.getSession(sessionId);
        Integer avetSeasonId = getAvetSeasonId(eventEntityId, session);
        return String.format("%d-%02d-%03d", avetSeasonId, Math.abs(session.getCapacityId()) - 1, session.getAvetMatchId());
    }

    private BigDecimal addPack(OrderDTO order, List<OrderProductDTO> products, PaymentOrder paymentOrder, BigDecimal totalImport,
                               List<Linia> lines, boolean isB2b, Integer totalLineCount, Linia contextLine, Channel channelErpConfig) {
        if (CollectionUtils.isNotEmpty(products)) {
            List<Linia> packLines = new ArrayList<>();
            Linia mainLine = null;

            OrderProductDTO mainProduct = products.stream()
                    .filter(orderProductDTO -> BooleanUtils.isTrue(orderProductDTO.getPack().getMainItem()))
                    .findAny()
                    .orElse(null);

            String basicPlusCode = products.stream()
                    .filter(product -> ProductType.PRODUCT.equals(product.getType()))
                    .map(product -> {
                        ProductVariant productVariant = productsRepository.getProductVariant(product.getProductData().getId(), product.getProductData().getVariantId());
                        return productVariant.getSku();
                    })
                    .filter(sku -> sku.startsWith(BASIC_PLUS_CODE))
                    .findAny()
                    .orElse(null);

            if (mainProduct != null) {
                List<OrderProductDTO> itemsAudioGuia = getMuseuWithAudioGuiaItem(products);

                products.remove(mainProduct);

                // Filter no ERP items
                products = products.stream().filter(product -> {
                    if (ProductType.PRODUCT.equals(product.getType())) {
                        ProductVariant productVariant = productsRepository.getProductVariant(product.getProductData().getId(), product.getProductData().getVariantId());
                        return !ProductType.PRODUCT.equals(product.getType()) || !NO_ERP.equalsIgnoreCase(productVariant.getSku());
                    } else {
                        return true;
                    }
                }).collect(Collectors.toList());

                double informativePrice = products.stream()
                        .mapToDouble(orderProductDTO -> orderProductDTO.getPrice().getInformativePrices().getPackItem())
                        .sum();
                if (basicPlusCode != null) {
                    PackDTO pack = packRepository.getPack(mainProduct.getPack().getId());

                    // Add Extra Basic Plus  (Virtual product for ERP)
                    OrderProductDTO extraBasicPlus = new OrderProductDTO();
                    extraBasicPlus.setProductData(new OrderProductDataDTO());
                    String basicPlusCodeModifier = basicPlusCode.length() > BASIC_PLUS_CODE.length() ? " " + basicPlusCode.substring(BASIC_PLUS_CODE.length()).trim() : "";
                    switch (pack.getPricingType()) {
                        case INCREMENTAL -> {
                            mainProduct.getPrice().setBasePrice(NumberUtils.minus(mainProduct.getPrice().getBasePrice(), pack.getPriceIncrement()));

                            extraBasicPlus.getProductData().setName(EXTRA_BASIC_PLUS_CODE + basicPlusCodeModifier);
                            extraBasicPlus.setPrice(new OrderPriceDTO());
                            extraBasicPlus.getPrice().setInformativePrices(new InformativePrices());
                            extraBasicPlus.getPrice().getInformativePrices().setPackItem(pack.getPriceIncrement() - informativePrice);
                            products.add(extraBasicPlus);
                        }
                        case NEW_PRICE -> {
                            extraBasicPlus.getProductData().setName(EXTRA_BASIC_PLUS_CODE + basicPlusCodeModifier);
                            extraBasicPlus.setPrice(new OrderPriceDTO());
                            extraBasicPlus.getPrice().setInformativePrices(new InformativePrices());
                            extraBasicPlus.getPrice().getInformativePrices().setPackItem(NumberUtils.minus(mainProduct.getPrice().getBasePrice(), informativePrice, mainProduct.getPrice().getInformativePrices().getPackItem()));

                            mainProduct.getPrice().setBasePrice(mainProduct.getPrice().getInformativePrices().getPackItem());

                            products.add(extraBasicPlus);
                        }
                    }
                } else {
                    // Calculating match/session price -> Total Price - promotions - all products prices
                    mainProduct.getPrice().setBasePrice(NumberUtils.minus(mainProduct.getPrice().getBasePrice(), informativePrice));

                    // Add Extra Basic Plus  (Virtual product for ERP) only when there is no basic plus code
                    if (CollectionUtils.isNotEmpty(itemsAudioGuia) && shouldAddAudioGuia(mainProduct)) {
                        addMuseuProducts(products, mainProduct, OrderType.REFUND.equals(order.getStatus().getType()), PRODUCT_AUDIOGUIA, MUSEO_PRICE);
                    }
                }


                // Add main product to erp
                totalImport = addTicketLines(order, List.of(mainProduct), paymentOrder, totalImport, packLines, isB2b, false, totalLineCount, contextLine, channelErpConfig);

                mainLine = packLines.get(0);
                lines.add(mainLine);
            }

            for (OrderProductDTO product : products) {
                Linia line = newLine(order, paymentOrder, lines, isB2b, channelErpConfig, null, contextLine);
                line.setClau(mainLine.getClau());
                line.setDataCobrament(mainLine.getDataCobrament());
                line.setIdCobrament(mainLine.getIdCobrament());
                line.setCanalVenda(mainLine.getCanalVenda());
                if (ProductType.PRODUCT.equals(product.getType())) {
                    ProductVariant productVariant = productsRepository.getProductVariant(product.getProductData().getId(), product.getProductData().getVariantId());
                    line.setIdProducte(productVariant.getSku());
                } else if (product.getEventType() != null) {
                    SessionDTO session = msEventRepository.getSession(product.getSessionId().longValue());
                    MsPriceTypeDTO priceType = venueTemplateRepository.getPriceType(session.getVenueConfigId(), product.getTicketData().getPriceZoneId().longValue());
                    line.setIdProducte(priceType != null ? priceType.getCode() : Long.toString(product.getId()));
                    // If we receive sessionId in reference this a complement
                    if (org.apache.commons.lang3.math.NumberUtils.isDigits(session.getReference()) && StringUtils.isNotEmpty(session.getReference())) {
                        SessionDTO sessionReference = msEventRepository.getSession(Long.valueOf(session.getReference()));
                        String codeMatch = getAvetCodeMatch(sessionReference.getId(), sessionReference.getEntityId());
                        line.setComplementAnalitic(codeMatch);
                    }
                } else {
                    line.setIdProducte(product.getProductData().getName());
                }
                totalImport = NumberUtils.sum(totalImport, BigDecimal.valueOf(product.getPrice().getInformativePrices().getPackItem()));
                line.setPreuVenda(BigDecimal.valueOf(product.getPrice().getInformativePrices().getPackItem()));

                line.setCodiPromocio("");
                line.setImportPromocio(BigDecimal.ZERO);
                line.setImportComisio(BigDecimal.ZERO);
                line.setAutofactura(mainLine.isAutofactura());
                line.setLocalitzador(order.getCode());

                lines.add(line);
            }
        }
        return totalImport;
    }

    private static void addMuseuProducts(List<OrderProductDTO> products, OrderProductDTO product, boolean isRefund, String itemName, double itemPrice) {
        if (product != null) {
            OrderProductDTO museo = new OrderProductDTO();
            museo.setProductData(new OrderProductDataDTO());
            museo.getProductData().setName(itemName);
            museo.setPrice(new OrderPriceDTO());
            museo.getPrice().setInformativePrices(new InformativePrices());

            // Add museo audioguia product into pack
            if(!isRefund) {
                product.getPrice().setBasePrice(NumberUtils.minus(product.getPrice().getBasePrice(), itemPrice));
                museo.getPrice().getInformativePrices().setPackItem(itemPrice);
            } else {
                product.getPrice().setBasePrice(NumberUtils.sum(product.getPrice().getBasePrice(), itemPrice));
                museo.getPrice().getInformativePrices().setPackItem(itemPrice * -1);
            }
            products.add(museo);
        }
    }

    private List<OrderProductDTO> getMuseuWithAudioGuiaItem(List<OrderProductDTO> products) {
        return products.stream()
                .filter(product -> product.getEventType() != null && product.getEventId() != null)
                .filter(product -> {
                    EventDTO event = msEventRepository.getEvent(product.getEventId().longValue());
                    return event != null && event.getCategory() != null && MUSEO.equals(event.getCategory().getCode());
                }).collect(Collectors.toList());
    }

    private Linia newLine(OrderDTO order, PaymentOrder paymentOrder, List<Linia> lines, boolean isB2b, Channel channelErpConfig,
                          Integer totalLineCount, Linia contextLine) {
        Linia line = new Linia();

        line.setNumLinia(totalLineCount != null ? lines.size() + totalLineCount + 1 : lines.size() + 1);
        line.setTipusLinia("V");
        line.setSistemaOrigen(FCB_ONEBOX);

        line.setClau(StringUtils.isNotBlank(contextLine.getClau()) ? contextLine.getClau() : "");
        line.setAutofactura(contextLine.isAutofactura());
        line.setCanalVenda(contextLine.getCanalVenda());

        line.setDataOperacio(getPurchasedDate(order));
        line.setQuantitat(1);

        line.setIdCollectiu(""); // Not used, we can put something
        boolean isB2bBecameBoxOffice = isB2BBecameWebBoxOffice(order);
        if (channelErpConfig != null) {
            line.setModeCobrament(channelErpConfig.getPaymentMethod());
        } else if (isB2b && (paymentOrder == null || ACCOUNTING_PAYMENT.equals(paymentOrder.getGatewaySid())) && !isB2bBecameBoxOffice) {
            line.setModeCobrament(SALDO);
        } else if (paymentOrder != null && paymentOrder.getPaymentMethod() != null && paymentOrder.getAcquirer() != null && !isB2bBecameBoxOffice) {
            fillBizumOrCardModeCobrament(paymentOrder, line);
        } else if (ChannelType.BOXOFFICE.equals(order.getOrderData().getChannelType()) || isB2bBecameBoxOffice) {
            fillBoxOfficePayment(order, line, paymentOrder);
        } else {
            line.setModeCobrament(CREDIT);
        }

        if (OrderType.REFUND.equals(order.getStatus().getType())) {
            String operationId = operationCodeService.getRefundOperationId(order.getRelatedOriginalCode());
            if (isB2b) {
                line.setDescripcioTipusEntrada("LOC: " + order.getCode() + ", REF: " + operationCodeService.getOperationId(order.getRelatedOriginalCode()));
            } else if (channelErpConfig != null){
                line.setDescripcioTipusEntrada(order.getCode());
            } else {
                line.setDescripcioTipusEntrada("REF: " + operationCodeService.getOperationId(order.getRelatedOriginalCode()));
            }
            line.setIdOperacio(operationId);
            operationCodeService.storeOperationId(order.getCode(), operationId);
        } else {
            String operationId = operationCodeService.getOrGenerateOperationId(new OperationIdRequest(
                    order.getCode(),
                    order.getStatus().getType().name(),
                    order.getDate().getPurchased(),
                    order.getDate().getTimeZone()));
            line.setIdOperacio(operationId);
            if (isB2b || StringUtils.isNotBlank(line.getClau())) {
                line.setDescripcioTipusEntrada("LOC: " + order.getCode());
            } else {
                line.setDescripcioTipusEntrada("");
            }
        }

        return line;
    }

    private static void fillBizumOrCardModeCobrament(PaymentOrder paymentOrder, Linia line) {
        if (BIZUM.equals(paymentOrder.getPaymentMethod())) {
            line.setModeCobrament(paymentOrder.getPaymentMethod().toUpperCase() + "-" + paymentOrder.getAcquirer().toUpperCase());
        } else {
            line.setModeCobrament(paymentOrder.getPaymentMethod() + "-" + paymentOrder.getAcquirer());
        }
    }

    private static String getCustomerName(OrderDTO order) {
        String customerName = "";
        if (order.getCustomer().getName() != null) {
            customerName = order.getCustomer().getName();
        }
        if (order.getCustomer().getSurname() != null) {
            customerName = customerName + " " + order.getCustomer().getSurname();
        }
        return customerName;
    }

    private static String getCustomerPhone(OrderDTO order) {
        String contactPhone = "";
        if (order.getCustomer().getPhone() != null) {
            contactPhone = order.getCustomer().getPhone();
        }
        if (order.getCustomer().getInternationalPhone() != null) {
            contactPhone = order.getCustomer().getInternationalPhone().getPrefix() +
                    " " +
                    order.getCustomer().getInternationalPhone().getNumber();
        }
        return contactPhone;
    }

    private void fillBoxOfficePayment(OrderDTO order, Linia line, PaymentOrder paymentOrder) {
        line.setModeCobrament(EFECTIU);

        OrderDTO currentOrder;
        if (OrderType.REFUND.equals(order.getStatus().getType())) {
            // I need to do this because there aren't all payment fields un refunded order
            currentOrder = msOrderRepository.getOrderByCode(order.getRelatedOriginalCode());
        } else {
            currentOrder = order;
        }
        OrderPaymentDTO paymentDTO = currentOrder.getPayments().stream()
                .filter(payment -> StringUtils.isNotBlank(payment.getPaymentReference()) || StringUtils.isNotBlank(payment.getPaymentExtraCode()))
                .findAny()
                .orElse(null);

        if (paymentDTO != null) {
            //CANVIAR CANAL DE VENTA A TAQUILLA WEB
            if (paymentDTO.getPaymentReference() != null) {
                if (isB2BBecameWebBoxOffice(order)) {
                    // attention:  the b2b booking checkout is overwritten as a box office operation
                    line.setClau(order.getCustomer().getAdditionalInfo().get(CLAU_CLIENT).toString());
                    if (ACCOUNTING_PAYMENT.equals(paymentDTO.getGatewaySid())) {
                        line.setModeCobrament(SALDO);
                    } else if (paymentOrder != null && paymentOrder.getPaymentMethod() != null && paymentOrder.getAcquirer() != null) {
                        fillBizumOrCardModeCobrament(paymentOrder, line);
                    }

                } else {
                    line.setClau(paymentDTO.getPaymentReference());
                }
                line.setCanalVenda(CANAL_TAQUILLA_WEB);
                line.setAutofactura(Boolean.FALSE);

                if (paymentDTO.getPaymentType() == PaymentType.BANK_TRANSFER) {
                    if (paymentDTO.getTransferDate() != null) {
                        line.setModeCobrament(TRANSFERENCIA_COBRADA);
                        if (order.getCustomer() != null) {
                            line.setIdCobrament(Objects.requireNonNullElse(order.getCustomer().getName(), "") + " " + Objects.requireNonNullElse(order.getCustomer().getSurname(), ""));
                        }
                    } else {
                        line.setModeCobrament(TRANSFERENCIA_PENDENT);
                    }
                } else if (paymentDTO.getPaymentType() == PaymentType.EXTERNAL) {
                    line.setModeCobrament(VAL_A_CARREGAR);
                }

                if (paymentDTO.getTransferDate() != null) {
                    line.setDataCobrament(DateUtils.convertZonedDateTimeToXMLGregorianCalendar(paymentDTO.getTransferDate()));
                }
            } else if (paymentDTO.getPaymentExtraCode() != null) {
                //TODO Identificar metode de pagament
                line.setModeCobrament(METODE_PAGAMENT);
            }
        }
    }

    private void addB2BLine(OrderDTO order, List<Linia> linias, BigDecimal totalImport) {
        Linia generalData = linias.get(0);

        Linia linia = new Linia();
        linia.setNumLinia(linias.size() + 1);
        linia.setTipusLinia("V");
        linia.setSistemaOrigen(FCB_ONEBOX);
        linia.setClau(generalData.getClau());
        linia.setDataOperacio(generalData.getDataOperacio());
        linia.setIdProducte(ANTICIPO);
        linia.setQuantitat(1);
        linia.setPreuVenda(totalImport.multiply(BigDecimal.valueOf(-1)));
        linia.setIdCollectiu("");
        linia.setModeCobrament(SALDO);
        linia.setIdOperacio(generalData.getIdOperacio());
        linia.setDescripcioTipusEntrada("LOC: " + order.getCode());
        linia.setCanalVenda(generalData.getCanalVenda());
        linia.setCodiPromocio("");
        linia.setImportPromocio(BigDecimal.ZERO);
        linia.setImportComisio(BigDecimal.ZERO);
        linia.setLocalitzador(order.getCode());
        linia.setAutofactura(false);
        linia.setImportPagat(BigDecimal.ZERO);
        linias.add(linia);
    }

    public void registerBalance(String movementId) {
        TransactionAudit transaction = null;
        int tries = 0;
        do {
            List<TransactionAudit> transactions = balanceRepository.getTransaction(movementId);
            if (CollectionUtils.isEmpty(transactions)) {
                try {
                    Thread.sleep(TIME_TO_SLEEP);
                } catch (InterruptedException e) {
                    LOGGER.error("[FCB WEBHOOK] Register balance failed: {}", movementId);
                    Thread.currentThread().interrupt();
                }
            } else {
                transaction = transactions.get(0);
            }
            tries++;
        } while (tries < MAX_RETRIES && transaction == null);

        if (transaction == null) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.NOT_FOUND, "Error registering balance movementId not found: {}", movementId);
        }

        switch (transaction.getMovementType()) {
            case ADD_AMOUNT, MODIFY_AMOUNT -> {
                String operationId = operationCodeService.getOrGenerateOperationId(new OperationIdRequest(
                        movementId,
                        OrderType.PURCHASE.name(),
                        ZonedDateTime.ofInstant(new Date(transaction.getTimestamp()).toInstant(), ZoneId.systemDefault()),
                        ZoneId.systemDefault().toString()));
                Client client = clientsRepository.getClient(transaction.getClientId().longValue(), transaction.getProviderId().longValue());
                ClientB2BBranch clientB2BBranch = client.getClientB2B().getClientB2BBranches().stream()
                        .filter(ClientB2BBranch::getMain)
                        .findAny()
                        .orElse(null);

                PeticioFacturar peticioFacturar = new PeticioFacturar();

                ArrayOfLinia arrayOfLinia = new ArrayOfLinia();
                peticioFacturar.setLinies(arrayOfLinia);
                List<Linia> items = arrayOfLinia.getLinia();

                XMLGregorianCalendar date = DateUtils.convertDateToXMLGregorianCalendar(new Date(transaction.getTimestamp()));
                XMLGregorianCalendar effectiveDate = DateUtils.convertDateToXMLGregorianCalendar(DateUtils.getDate(transaction.getEffectiveDate()));

                BigDecimal amount = NumberUtils.divide(BigDecimal.valueOf(transaction.getAmount()), BigDecimal.valueOf(100));

                Linia linia = new Linia();
                linia.setNumLinia(1);
                linia.setTipusLinia("V");
                linia.setSistemaOrigen(FCB_ONEBOX);

                linia.setClau(getOrCreateClientId(
                        null,
                        true,
                        null,
                        client,
                        movementId,
                        CANAL_TAQUILLA_WEB
                ));
                linia.setDataOperacio(date);
                linia.setIdProducte(ANTICIPO);
                linia.setQuantitat(1);
                linia.setPreuVenda(amount);
                linia.setIdCollectiu("");
                linia.setModeCobrament(TRANSFERENCIA_SALDO);
                linia.setIdOperacio(operationId);
                linia.setDescripcioTipusEntrada("LOC: " + movementId);
                linia.setCanalVenda(CANAL_TAQUILLA_WEB);
                linia.setCodiPromocio("");
                linia.setImportPromocio(BigDecimal.ZERO);
                linia.setImportComisio(BigDecimal.ZERO);
                linia.setAutofactura(false);
                linia.setDataCobrament(effectiveDate);
                linia.setIdCobrament(transaction.getTransactionId());
                linia.setImportPagat(BigDecimal.ZERO);
                items.add(linia);

                peticioFacturar.setTotalEntrades(1);
                peticioFacturar.setGenerarFactura("1");
                peticioFacturar.setImportTotal(amount);
                peticioFacturar.setSistemaOrigen(FCB_ONEBOX);
                peticioFacturar.setMessageId(movementId);

                peopleSoftRepository.registerOperation(peticioFacturar);
            }
            case CHANGE_MAX_CREDIT -> LOGGER.info("[FCB WEBHOOK] Do nothing CHANGE_MAX_CREDIT");
            case REFUND -> LOGGER.info("[FCB WEBHOOK] Do nothing REFUND");
            case PAYMENT -> LOGGER.info("[FCB WEBHOOK] Do nothing PAYMENT");
        }

    }

    private Integer getAvetSeasonId(long eventEntityId, SessionMatch session) {
        List<CapacityDTO> entityCapacities = intAvetConfigRepository.getEntityCapacities(eventEntityId);
        CapacityDTO capacity = entityCapacities.stream()
                .filter(capacityDTO -> capacityDTO.getCapacityCode() == session.getCapacityId())
                .findAny().orElse(null);
        return capacity != null && capacity.getSeasonId() != null ? capacity.getSeasonId().intValue() : 0;
    }

    private String  getSellerChannel(Long channelId, List<OrderPaymentDTO> payments, boolean isB2b) {
        if(isB2b) {
            return CANAL_VENDA_B2B;
        } else {
            List<ChannelGatewayConfig> channelGatewayConfigs = paymentRepository.getChannelGatewayConfigs(channelId);

            String sellerChannel = DEFAULT_SELLER_CHANNEL;
            for (OrderPaymentDTO payment : payments) {
                ChannelGatewayConfig config = channelGatewayConfigs.stream()
                        .filter(channelGatewayConfig -> channelGatewayConfig.getGatewaySid().equals(payment.getGatewaySid()))
                        .findAny().orElse(null);
                if (config != null && config.getFieldsValues() != null && config.getFieldsValues().containsKey(FCB_ID_CANAL)) {
                    sellerChannel = config.getFieldsValues().get(FCB_ID_CANAL);
                    break;
                }
            }
            return getFcbSellerChannel(sellerChannel);
        }
    }

    private String getFcbSellerChannel(String sellerChannel) {
        if (fcbChannelMappingsProperties.containsKey(sellerChannel)) {
            return fcbChannelMappingsProperties.get(sellerChannel);
        } else {
            return fcbChannelMappingsProperties.get(DEFAULT_CHANNEL);
        }
    }

    private static XMLGregorianCalendar getPurchasedDate(OrderDTO order) {
        TimeZone tz = TimeZone.getTimeZone(order.getDate().getTimeZone());
        return DateUtils.convertDateToXMLGregorianCalendar(FcbDateUtils.convertDateTimeZone(Date.from(order.getDate().getPurchased().toInstant()), tz));
    }

    private static BigDecimal getTotalCharges(OrderProductDTO product) {
        OrderPriceChargesDTO charges = product.getPrice().getCharges();
        return BigDecimal.valueOf(
                getZeroIfNull(charges.getChannel()) +
                        getZeroIfNull(charges.getPromoterChannel()) +
                        getZeroIfNull(charges.getPromoter()));
    }

    private static BigDecimal getTotalPromotion(OrderProductDTO product) {
        OrderPricePromotionsDTO promos = product.getPrice().getPromotions();
        return BigDecimal.valueOf(
                getZeroIfNull(promos.getAutomatic()) +
                        getZeroIfNull(promos.getPromotion()) +
                        getZeroIfNull(promos.getDiscount()) +
                        getZeroIfNull(promos.getChannelAutomatic()) +
                        getZeroIfNull(promos.getChannelCollective()));
    }

    private static BigDecimal getOrderTotalCharges(OrderDTO order) {
        OrderPriceChargesDTO charges = order.getPrice().getCharges();
        return BigDecimal.valueOf(
                getZeroIfNull(order.getPrice().getClientCommission()) +
                        getZeroIfNull(charges.getChannel()) +
                        getZeroIfNull(charges.getPromoterChannel()) +
                        getZeroIfNull(charges.getPromoter()));
    }

    private static BigDecimal getOrderTotalPromotion(OrderDTO order) {
        OrderPricePromotionsDTO promos = order.getPrice().getPromotions();
        return BigDecimal.valueOf(
                getZeroIfNull(order.getPrice().getClientConditionsPrice()) +
                        getZeroIfNull(promos.getAutomatic()) +
                        getZeroIfNull(promos.getPromotion()) +
                        getZeroIfNull(promos.getDiscount()) +
                        getZeroIfNull(promos.getChannelAutomatic()) +
                        getZeroIfNull(promos.getChannelCollective()));
    }

    private static double getZeroIfNull(Double price) {
        return price == null ? 0d : price;
    }

    public void storeAbandonedOrder(String code) {
        CrmOrderParams crmOrderParams = new CrmOrderParams();
        crmOrderParams.setClientId(fcbEntity);
        crmOrderParams.setStatus(ABANDONED_STATUS);
        crmOrderParams.setId(code);
        CrmOrderResponse abandonedOrder = abandonedCartRepository.getAbandonedOrder(crmOrderParams);

        for (CrmOrderContainer purchase : abandonedOrder.getPurchases()) {
            if (CollectionUtils.isNotEmpty(purchase.getProducts())) {
                CrmParams crmParams = new CrmParams();
                crmParams.setClientId(fcbEntity);
                crmParams.setStatus(ABANDONED_STATUS);
                crmParams.setId(purchase.getOrder().getUser());
                CrmClientResponse abandonedClient = abandonedCartRepository.getAbandonedClient(crmParams);
                if (CollectionUtils.isNotEmpty(abandonedClient.getBuyers())) {
                    salesforceRepository.storeAbandonedOrder(purchase.getOrder().getId(), getRequestAbandonedOrder(purchase, abandonedClient));
                } else {
                    LOGGER.info("[FCB WEBHOOK] Skip Abandoned order {} without buyer data", purchase.getOrder().getId());
                }
            } else {
                LOGGER.info("[FCB WEBHOOK] Skip Abandoned order {} without products", purchase.getOrder().getId());
            }
        }
    }

    private RequestAbandonedOrderDTO getRequestAbandonedOrder(
            CrmOrderContainer purchase,
            CrmClientResponse abandonedClient) {

        CrmClientDocResponse client = abandonedClient.getBuyers().get(0);

        CrmProductDocResponse product = null;
        if (CollectionUtils.isNotEmpty(purchase.getProducts())) {
            product = purchase.getProducts().stream()
                    .filter(item -> StringUtils.isNotEmpty(item.getTaxonomy_code()))
                    .findAny()
                    .orElse(null);
            if (product == null) {
                product = purchase.getProducts().get(0);
            }
        }

        RequestAbandonedOrderDTO requestAbandonedOrder = new RequestAbandonedOrderDTO();
        requestAbandonedOrder.setValues(new RequestAbandonedDataDTO());

        requestAbandonedOrder.getValues().setNombre(client.getName());
        requestAbandonedOrder.getValues().setApellidos(client.getSurname());
        requestAbandonedOrder.getValues().setPais(client.getCountry());

        requestAbandonedOrder.getValues().setCantidadEntradas(Integer.toString(purchase.getOrder().getProducts_number()));
        requestAbandonedOrder.getValues().setImporteTotal(String.format("%.2f", purchase.getOrder().getAmount()));

        if (product != null) {
            requestAbandonedOrder.getValues().setCategoria(product.getTaxonomy_code());
            requestAbandonedOrder.getValues().setProducto(product.getSku());
            String baseUrl = channelUrl + "/" + purchase.getOrder().getChannel_url() + "/select/";
            if (product.getPack() != null) {
                requestAbandonedOrder.getValues().setUrl(baseUrl + "pack/" + product.getPack() + '/' + product.getSession());
            } else {
                requestAbandonedOrder.getValues().setUrl(baseUrl + product.getSession());
            }
        }

        ChannelConfigDTO channelConfig = channelRepository.getChannelConfig(purchase.getOrder().getChannel_id());

        if (isVIPChannel(channelConfig)) {
            requestAbandonedOrder.getValues().setVip("true");
        } else if (isBasicPlusChannel(channelConfig)) {
            requestAbandonedOrder.getValues().setVip("BasicPlus");
        } else {
            requestAbandonedOrder.getValues().setVip("false");
        }

        requestAbandonedOrder.getValues().setIdioma(purchase.getOrder().getLanguage().replace('_', '-'));

        TimeZone tz = TimeZone.getTimeZone(purchase.getOrder().getTime_zone());
        String stringDate = DATETIME_FORMAT.format(FcbDateUtils.convertDateTimeZone(Date.from(ZonedDateTime.parse(purchase.getOrder().getOrder_date()).toInstant()), tz));
        LocalDateTime localDate = LocalDateTime.parse(stringDate, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
        requestAbandonedOrder.getValues().setFecha(stringDate);
        requestAbandonedOrder.getValues().setFechaOk(localDate);

        if (product != null && product.getCollectiveKey() != null) {
            requestAbandonedOrder.getValues().setIdCliente(product.getCollectiveKey());
        }

        requestAbandonedOrder.getValues().setEmail(purchase.getOrder().getUser());

        requestAbandonedOrder.getValues().setPoliticaPrivacidad(BooleanUtils.TRUE);
        if (CollectionUtils.isNotEmpty(purchase.getOrder().getChannelAgreements())) {
            requestAbandonedOrder.getValues().setBarcaFansGDPR(BooleanUtils.toStringTrueFalse(getAgreementAccepted(purchase, NEWSLETTER)));
            requestAbandonedOrder.getValues().setPartnersGDPR(BooleanUtils.toStringTrueFalse(getAgreementAccepted(purchase, INVITATION)));
            requestAbandonedOrder.getValues().setProductsGDPR(BooleanUtils.toStringTrueFalse(getAgreementAccepted(purchase, PROMOTION)));
        } else {
            requestAbandonedOrder.getValues().setPartnersGDPR(BooleanUtils.FALSE);
            requestAbandonedOrder.getValues().setBarcaFansGDPR(BooleanUtils.FALSE);
            requestAbandonedOrder.getValues().setProductsGDPR(BooleanUtils.FALSE);
        }

        if (product != null && EventType.AVET.equals(product.getEventType())) {
            SessionMatch session = intAvetConfigRepository.getSession(product.getSession().longValue());
            if (session != null) {
                Long entityId = msEventRepository.getEvent(product.getEvent().longValue()).getEntityId();
                Integer avetSeasonId = getAvetSeasonId(entityId, session);
                requestAbandonedOrder.getValues().setTemporadaAVET(String.valueOf(avetSeasonId));
                requestAbandonedOrder.getValues().setAforoAVET(Integer.toString(Math.abs(session.getCapacityId())));
                requestAbandonedOrder.getValues().setPartidoAVET(Integer.toString(session.getAvetMatchId()));
            }
            // TODO museo
//        } else if (product != null) {
//            requestAbandonedOrder.getValues().setTemporadaAVET(session.getSeasonId());
        }

        return requestAbandonedOrder;
    }

    private static Boolean getAgreementAccepted(CrmOrderContainer purchase, String agreementType) {
        return purchase.getOrder().getChannelAgreements().stream()
                .filter(channelAgreement -> channelAgreement.getName().trim().equalsIgnoreCase(agreementType))
                .map(ChannelAgreement::isAccepted)
                .findAny().orElse(false);
    }

    private static boolean isVIPChannel(ChannelConfigDTO channelConfig) {
        String fcbChannelCode = getFcbChannelCode(channelConfig);
        return fcbChannelCode.contains("VIP");
    }

    private static boolean isBasicPlusChannel(ChannelConfigDTO channelConfig) {
        String fcbChannelCode = getFcbChannelCode(channelConfig);
        return fcbChannelCode.contains("BASICPLUS");
    }

    private static String getFcbChannelCode(ChannelConfigDTO channelConfig) {
        String fcbChannelCode = "";
        if (channelConfig != null && MapUtils.isNotEmpty(channelConfig.getAdditionalProperties())) {
            Object fcbChannelCodeObj = channelConfig.getAdditionalProperties().get("fcbChannelCode");
            if(fcbChannelCodeObj instanceof String) {
                fcbChannelCode = (String) fcbChannelCodeObj;
            }
        }
        return fcbChannelCode;
    }

    public String getOrCreateClientId(OrderDTO order, boolean isB2b, Channel channelErpConfig, Client client, String transaction, String salesChannel) {
        PeticioCercaClient peticioCercaClient = new PeticioCercaClient();
        String nif;
        Long entityId;
        boolean isOta = channelErpConfig != null;
        boolean isPalisisOta = order != null && order.getOrderData() != null &&
                order.getOrderData().getConsumerMetadata().get("origin_ota_id") != null &&
                order.getOrderData().getConsumerMetadata().get("origin_marketplace_agent_id") != null;

        if(order != null && client == null) {
            if (isB2b) {
                nif = order.getClient().getClientB2B().getTaxId();
                entityId = order.getClient().getClientB2B().getClientId();
            } else if(isOta) {
                //origin_ota  //origin_ota_id //origin_marketplace_agent_id
                if (isPalisisOta) {
                    nif = order.getOrderData().getConsumerMetadata().get("origin_ota_id");
                    entityId = Long.valueOf(order.getOrderData().getConsumerMetadata().get("origin_marketplace_agent_id"));
                } else {
                    nif = channelErpConfig.getKey();
                    entityId = order.getOrderData().getChannelEntityId().longValue();
                }

            } else {
                nif = order.getInvoiceData().getIdentification();
                entityId = order.getOrderData().getChannelEntityId().longValue();
            }
        } else {
            // Balance case
            nif = client.getClientB2B().getTaxId();
            entityId = client.getClientB2B().getClientId().longValue();
        }

        peticioCercaClient.setIdFiscal(nif);
        RespostaCercaClient respostaCercaClient = peopleSoftRepository.searchNif(peticioCercaClient);

        String identifier = getIdentifier(nif, entityId, transaction, salesChannel, isB2b, isOta, isPalisisOta, respostaCercaClient);

        if (respostaCercaClient == null && !isPalisisOta) {
            EntityDTO entity = entitiesRepository.getByIdCached(entityId);

            String country;
            String countrySubdivision;
            String clientName;
            String businessName;
            String name;
            String email;
            String phone;
            String carrer;
            String zipCode = NOT_DEFINED;
            String city = NOT_DEFINED;
            String dadesAddicionals = NOT_DEFINED;

            if (isB2b) {
                if (client != null) {
                    ClientB2BBranch clientB2BBranch = client.getClientB2B().getClientB2BBranches().stream()
                            .filter(ClientB2BBranch::getMain)
                            .findAny()
                            .orElse(null);

                    country = client.getCountry();
                    countrySubdivision = client.getCountrySubdivision();
                    clientName = client.getName();
                    businessName = client.getClientB2B().getBusinessName();
                    name = clientB2BBranch != null ? clientB2BBranch.getContactName() : "";
                    email = clientB2BBranch != null ? clientB2BBranch.getContactEmail() : "";
                    phone = clientB2BBranch != null ? clientB2BBranch.getContactPhone() : "";
                    carrer = client.getAddress();
                } else {
                    country = order.getClient().getCountry();
                    countrySubdivision = order.getClient().getCountrySubdivision();
                    clientName = order.getClient().getName();
                    businessName = order.getClient().getClientB2B().getBusinessName();
                    name = order.getClient().getClientB2B().getClientB2BBranch() != null ? order.getClient().getClientB2B().getClientB2BBranch().getContactName() : "";
                    email = order.getClient().getClientB2B().getClientB2BBranch() != null ? order.getClient().getClientB2B().getClientB2BBranch().getContactEmail() : "";
                    phone = order.getClient().getClientB2B().getClientB2BBranch() != null ? order.getClient().getClientB2B().getClientB2BBranch().getContactPhone() : "";
                    carrer = order.getClient().getAddress();
                }

            } else if(isOta) {
                List<CountryDTO> countries = masterDataRepository.countries();
                CountryDTO countryDTO = countries.stream()
                        .filter(c -> c.getId().equals(entity.getCountryId()))
                        .findAny()
                        .orElse(null);
                if(countryDTO != null) {
                    country = countryDTO.getCode();
                    countrySubdivision = "NOT_DEF";
                } else {
                    country = "ES"; // Default country for B2C orders
                    countrySubdivision = "ES-B";
                }
                clientName = entity.getName(); // clientName
                businessName = entity.getSocialReason(); //businessName
                name = entity.getName(); // name
                email = entity.getEmail();
                phone = StringUtils.isNotBlank(entity.getPhone()) ? entity.getPhone() : "";
                carrer = StringUtils.isNotBlank(entity.getAddress()) ? entity.getAddress() : "";
                zipCode = StringUtils.isNotBlank(entity.getPostalCode()) ? entity.getPostalCode() : "";
                city = StringUtils.isNotBlank(entity.getCity()) ? entity.getCity() : "";
            } else {
                country = order.getInvoiceData().getCountry();
                countrySubdivision = order.getInvoiceData().getCountrySubdivision();
                clientName = order.getInvoiceData().getName(); // clientName
                businessName = order.getInvoiceData().getName(); //businessName
                name = getCustomerName(order); // name
                email = order.getCustomer().getEmail() != null ? order.getCustomer().getEmail() : ""; // email
                phone = getCustomerPhone(order);
                carrer = order.getInvoiceData().getAddress();
                zipCode = order.getInvoiceData().getZipCode();
                city = order.getInvoiceData().getCity();
            }

            if (StringUtils.isNotBlank(carrer) && carrer.length() > 55) {
                dadesAddicionals = StringUtils.substring(carrer, 55);
            }

            if (StringUtils.isNotBlank(carrer)){
                carrer = StringUtils.substring(carrer, 0, 55);
            }

            checkNif(nif, country);

            PeticioAltaClient peticioAltaClient = new PeticioAltaClient();
            peticioAltaClient.setDadesClient(new DadesAltaClient());
            peticioAltaClient.getDadesClient().setNom(clientName);
            peticioAltaClient.getDadesClient().setNomComercial(businessName);
            peticioAltaClient.getDadesClient().setCanalVenda(salesChannel);
            peticioAltaClient.getDadesClient().setTipus(TYPE_PUBLIC);
            peticioAltaClient.getDadesClient().setIdOrigen(identifier);
            peticioAltaClient.getDadesClient().setIdOperadorOrigen(FCB_ONEBOX);
            if (entity != null && entity.getCurrency() != null) {
                peticioAltaClient.getDadesClient().setCodiMoneda(entity.getCurrency().getValue());
            } else {
                peticioAltaClient.getDadesClient().setCodiMoneda(EURO_CODE);
            }
            peticioAltaClient.getDadesClient().setClientEnviament(true);
            peticioAltaClient.getDadesClient().setClientFacturacio(true);
            peticioAltaClient.getDadesClient().setClientVenda(true);

            peticioAltaClient.getDadesClient().setDadesContacte(new DadesContacte());
            peticioAltaClient.getDadesClient().getDadesContacte().setContacte(name);
            peticioAltaClient.getDadesClient().getDadesContacte().setEmail(email);
            peticioAltaClient.getDadesClient().getDadesContacte().setTelefon(phone);

            peticioAltaClient.getDadesClient().setDadesIVA(new DadesIVA());
            peticioAltaClient.getDadesClient().getDadesIVA().setPais(getCountryCode(country));
            peticioAltaClient.getDadesClient().getDadesIVA().setIdFiscal(nif);

            peticioAltaClient.getDadesClient().setAdreca(new Adreca());
            peticioAltaClient.getDadesClient().getAdreca().setCorrespondencia(true);
            peticioAltaClient.getDadesClient().getAdreca().setPais(getCountryCode(country));

            peticioAltaClient.getDadesClient().getAdreca().setIdioma(LANG_ESP);
            if (entity != null) {
                Locale locale = Locale.forLanguageTag(entity.getLanguage().getCode().replace('_', '-'));
                if (COUNTRY_ES.equals(locale.getCountry()) && LANG_CA.equals(locale.getLanguage())) {
                    peticioAltaClient.getDadesClient().getAdreca().setIdioma(LANG_CAT);
                } else if (!COUNTRY_ES.equals(locale.getCountry())) {
                    peticioAltaClient.getDadesClient().getAdreca().setIdioma(LANG_ENG);
                }
            }

            peticioAltaClient.getDadesClient().getAdreca().setCarrer(carrer);
            peticioAltaClient.getDadesClient().getAdreca().setDadesAddicionals(dadesAddicionals);
            peticioAltaClient.getDadesClient().getAdreca().setCiutat(city);
            peticioAltaClient.getDadesClient().getAdreca().setCodiPostal(zipCode);

            if (COUNTRY_ES.equals(country)) {
                Map<String, CountrySubdivisionDTO> subdivisions = masterDataRepository.getSubdivisionsByCountryCode();
                CountrySubdivisionDTO subdivision = subdivisions.get(countrySubdivision);
                peticioAltaClient.getDadesClient().getAdreca().setEstatProvincia(subdivision.getZipCode());
                if(NOT_DEFINED.equals(city)) {
                    peticioAltaClient.getDadesClient().getAdreca().setCiutat(subdivision.getName());
                }
            }

            peticioAltaClient.getDadesClient().setDadesTargeta(new DadesTargeta());

            OpcionsBancaries opcionsBancaries = new OpcionsBancaries();
            peticioAltaClient.getDadesClient().setOpcionsBancaries(opcionsBancaries);
            peticioAltaClient.getDadesClient().getOpcionsBancaries().setCobrador(SELLER);

            RespostaAltaClient respostaAltaClient = peopleSoftRepository.addClient(peticioAltaClient);
            if (respostaAltaClient == null) {
                throw ExceptionBuilder.build(ApiExternalErrorCode.USER_NOT_FOUND, "Error creating user with identifier: {} in fcb, Order code/movementId: {}", nif, transaction);
            }
        }
        return identifier;
    }

    private static boolean isB2BBecameWebBoxOffice(OrderDTO order) {
        // Booking converted to purchase in a B2B channel that will be informed as a web box office
        return  OrderType.PURCHASE.equals(order.getStatus().getType()) && order.getRelatedOriginalCode() != null && // relatedOriginalCode refers to the booking code
                order.getClient() != null && order.getClient().getClientB2B() != null && isB2BUser(order) &&
                order.getCustomer() != null &&
                MapUtils.isNotEmpty(order.getCustomer().getAdditionalInfo()) &&
                order.getCustomer().getAdditionalInfo().get(CLAU_CLIENT) != null;
    }

    private @NotNull String getIdentifier(String nif, Long clientId, String transaction, String canalVenda, boolean isB2b, boolean isOTA,
                                          boolean isPalisisOTA, RespostaCercaClient respostaCercaClient) {
        String identifier = "";
        if (isB2b) {
            identifier = B2B_IDENTIFIER.formatted(clientId);
        } else if (isPalisisOTA) {
            identifier = B2B_PALISIS_OTA_IDENTIFIER.formatted(clientId);
        } else if (isOTA) {
            identifier = B2B_OTA_IDENTIFIER.formatted(clientId);
        }
        if (respostaCercaClient == null || respostaCercaClient.getDadesClient() == null){
            if (!isB2b && !isOTA && !isPalisisOTA) {
                identifier = buildB2CIdentifier();
            }
            if (isPalisisOTA) {
               identifier = "";
            }
        } else {
            if (validateRespostaCercaClient(respostaCercaClient)) {
                String oldIdentifier = respostaCercaClient.getDadesClient().getIdentificadorsOrigen()
                        .getIdentificadors().getId().stream()
                        .filter(value -> value.contains(PREFIX_B2B_PALISIS_OTA) || value.contains(PREFIX_B2B_OTA) || value.contains(PREFIX_B2B) || value.contains(PREFIX_B2C))
                        .findFirst().orElse(null);

                if (StringUtils.isBlank(oldIdentifier) && StringUtils.isBlank(identifier)) {
                    identifier = buildB2CIdentifier();
                }
                if (StringUtils.isNotBlank(oldIdentifier)) {
                    identifier = oldIdentifier;
                }

                try {
                    if (StringUtils.isBlank(oldIdentifier)) {
                        var response = updateClient(respostaCercaClient, identifier, canalVenda);
                        if (response == null) {
                            throw new OneboxRestException();
                        }
                    }
                } catch (Exception e) {
                    LOGGER.error("[FCB] Error trying to update client for orderCode: {} - new identifier generated: {}", transaction, identifier);
                    sendAlertService.warning(integration, (String.format("[FCB][B2C] Error getting line identifier. - with identifier: %s in fcb, Order code/movementId: %s", nif, transaction)));
                    throw e;
                }
            }
        }

        if (StringUtils.isBlank(identifier)) {
            LOGGER.error("[FCB] Identifier cannot be blank");
            sendAlertService.critical(integration, (String.format("[FCB] Error getting line identifier. isB2B: %s - with identifier: %s in fcb, Order code/movementId: %s", isB2b, nif, transaction)));
            throw new OneboxRestException();
        }
        return identifier;
    }

    private String buildB2CIdentifier() {
        Long b2cIdentifier = b2cPeopleSoftCounterCouchDao.getAndIncrement();
        return B2C_IDENTIFIER.formatted(b2cIdentifier);
    }

    private RespostaModificacioClient updateClient(RespostaCercaClient respostaCercaClient, String identifier, String canalVenda) {
        PeticioModificacioClient modificacioClient = new PeticioModificacioClient();
        DadesModificarClient dadesModificarClient = new DadesModificarClient();
        dadesModificarClient.setCanalVenda(canalVenda);
        dadesModificarClient.setId(respostaCercaClient.getDadesClient().getId());
        dadesModificarClient.setTipus("");
        dadesModificarClient.setIdOrigen(identifier);
        dadesModificarClient.setNom(respostaCercaClient.getDadesClient().getNom());
        dadesModificarClient.setNomComercial(respostaCercaClient.getDadesClient().getNomComercial());
        dadesModificarClient.setCodiMoneda(respostaCercaClient.getDadesClient().getCodiMoneda());
        dadesModificarClient.setTipusCanviMoneda(respostaCercaClient.getDadesClient().getTipusCanviMoneda());
        dadesModificarClient.setClientEnviament(respostaCercaClient.getDadesClient().isClientEnviament());
        dadesModificarClient.setClientFacturacio(respostaCercaClient.getDadesClient().isClientFacturacio());
        dadesModificarClient.setClientVenda(respostaCercaClient.getDadesClient().isClientVenda());
        dadesModificarClient.setIdOperadorOrigen(FCB_ONEBOX);

        DadesContacte dadesContacte = new DadesContacte();
        if (respostaCercaClient.getDadesClient().getDadesContacte() != null) {
            dadesContacte.setContacte(respostaCercaClient.getDadesClient().getDadesContacte().getContacte());
            dadesContacte.setEmail(respostaCercaClient.getDadesClient().getDadesContacte().getEmail());
            dadesContacte.setTelefon(respostaCercaClient.getDadesClient().getDadesContacte().getTelefon());
        }

        dadesModificarClient.setDadesContacte(dadesContacte);

        DadesTargeta dadesTargeta = new DadesTargeta();
        if(respostaCercaClient.getDadesClient().getDadesTargeta() != null) {
            dadesTargeta.setTipus(respostaCercaClient.getDadesClient().getDadesTargeta().getTipus());
            dadesTargeta.setNumero(respostaCercaClient.getDadesClient().getDadesTargeta().getNumero());
            dadesTargeta.setNomTitular(respostaCercaClient.getDadesClient().getDadesTargeta().getNomTitular());
            dadesTargeta.setCognomTitular(respostaCercaClient.getDadesClient().getDadesTargeta().getCognomTitular());
            dadesTargeta.setMesExpiracio(respostaCercaClient.getDadesClient().getDadesTargeta().getMesExpiracio());
            dadesTargeta.setAnyExpiracio(respostaCercaClient.getDadesClient().getDadesTargeta().getAnyExpiracio());
        }
        dadesModificarClient.setDadesTargeta(dadesTargeta);

        Adreca adreca = new Adreca();
        if (respostaCercaClient.getDadesClient().getAdreca() != null) {
            adreca.setDescripcio(respostaCercaClient.getDadesClient().getAdreca().getDescripcio());
            adreca.setCorrespondencia(respostaCercaClient.getDadesClient().getAdreca().isCorrespondencia());
            adreca.setIdioma(respostaCercaClient.getDadesClient().getAdreca().getIdioma());
            adreca.setPais(respostaCercaClient.getDadesClient().getAdreca().getPais());
            adreca.setCarrer(respostaCercaClient.getDadesClient().getAdreca().getCarrer());
            adreca.setDadesAddicionals(respostaCercaClient.getDadesClient().getAdreca().getDadesAddicionals());
            adreca.setPlanta(respostaCercaClient.getDadesClient().getAdreca().getPlanta());
            adreca.setPorta(respostaCercaClient.getDadesClient().getAdreca().getPorta());
            adreca.setNumero(respostaCercaClient.getDadesClient().getAdreca().getNumero());
            adreca.setEscala(respostaCercaClient.getDadesClient().getAdreca().getEscala());
            adreca.setEstatProvincia(respostaCercaClient.getDadesClient().getAdreca().getEstatProvincia());
            adreca.setCiutat(respostaCercaClient.getDadesClient().getAdreca().getCiutat());
            adreca.setCodiPostal(respostaCercaClient.getDadesClient().getAdreca().getCodiPostal());
        }
        dadesModificarClient.setAdreca(adreca);

        OpcionsBancaries opcionsBancaries = new OpcionsBancaries();
        if (respostaCercaClient.getDadesClient().getOpcionsBancaries() != null) {
            opcionsBancaries.setCobrador(respostaCercaClient.getDadesClient().getOpcionsBancaries().getCobrador());
            opcionsBancaries.setAnalistaCredit(respostaCercaClient.getDadesClient().getOpcionsBancaries().getAnalistaCredit());
            opcionsBancaries.setEspecialistaCobrament(respostaCercaClient.getDadesClient().getOpcionsBancaries().getEspecialistaCobrament());
            opcionsBancaries.setMetodePagament(respostaCercaClient.getDadesClient().getOpcionsBancaries().getMetodePagament());
            opcionsBancaries.setCondicionsPagament(respostaCercaClient.getDadesClient().getOpcionsBancaries().getCondicionsPagament());
            opcionsBancaries.setIdBanc(respostaCercaClient.getDadesClient().getOpcionsBancaries().getIdBanc());
            opcionsBancaries.setSucursal(respostaCercaClient.getDadesClient().getOpcionsBancaries().getSucursal());
            opcionsBancaries.setCodiVerificacio(respostaCercaClient.getDadesClient().getOpcionsBancaries().getCodiVerificacio());
            opcionsBancaries.setCompte(respostaCercaClient.getDadesClient().getOpcionsBancaries().getCompte());
            opcionsBancaries.setIBAN(respostaCercaClient.getDadesClient().getOpcionsBancaries().getIBAN());
            opcionsBancaries.setBicSwift(respostaCercaClient.getDadesClient().getOpcionsBancaries().getBicSwift());
        }
        dadesModificarClient.setOpcionsBancaries(opcionsBancaries);

        modificacioClient.setDadesClient(dadesModificarClient);
        modificacioClient.setModeModificacio(1);
        return peopleSoftRepository.modificarClient(modificacioClient);
    }

    private boolean validateRespostaCercaClient(RespostaCercaClient respostaCercaClient) {
        return respostaCercaClient != null &&
                respostaCercaClient.getDadesClient() != null &&
                respostaCercaClient.getDadesClient().getIdentificadorsOrigen() != null &&
                respostaCercaClient.getDadesClient().getIdentificadorsOrigen().getIdentificadors() != null;
    }

    private void checkNif(String nif, String country) {
        Boolean isValidNif = validNif.get(nif);
        if (isValidNif == null) {
            isValidNif = peopleSoftRepository.checkNif(nif, country);
            validNif.put(nif, isValidNif);
        }

        if (!isValidNif) {
            throw ExceptionBuilder.build(ApiExternalErrorCode.BAD_REQUEST_PARAMETER, "Identifier: " + nif + " is incorrect format");
        }
    }

    private String getCountryCode(String code2alpha) {
        String code = countryCode.get(code2alpha);
        if (code == null) {
            for (Locale locale : Locale.getAvailableLocales()) {
                if (locale.getCountry().equals(code2alpha)) {
                    code = locale.getISO3Country();
                    countryCode.put(code2alpha, locale.getISO3Country());
                    break;
                }
            }
        }
        return code;
    }

    public Map<String, String> getVenueMappings() {
        return fcbVenueMappingsProperties;
    }

    public boolean shouldAddAudioGuia(OrderProductDTO mainProduct) {
        if (mainProduct.getEventType() != null && mainProduct.getEventId() != null) {
            EventDTO event = msEventRepository.getEvent(mainProduct.getEventId().longValue());
            if (event == null || event.getCustomCategory() == null) {
                return true;
            }
            return !TOURS.equals(event.getCustomCategory().getCode());
        } else {
            return true;
        }
    }
}
