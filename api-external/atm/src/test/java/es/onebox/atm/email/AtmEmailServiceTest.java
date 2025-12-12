package es.onebox.atm.email;

import es.onebox.atm.access.ATMAccessService;
import es.onebox.atm.email.service.AtmEmailService;
import es.onebox.atm.security.AtmSecurityChecker;
import es.onebox.cache.repository.CacheRepository;
import es.onebox.common.auth.service.AuthenticationService;
import es.onebox.common.datasources.common.dto.Channel;
import es.onebox.common.datasources.common.enums.OrderType;
import es.onebox.common.datasources.ms.channel.dto.ChannelDTO;
import es.onebox.common.datasources.ms.channel.dto.ChannelDeliveryMethodDTO;
import es.onebox.common.datasources.ms.channel.dto.ChannelDeliveryMethodsDTO;
import es.onebox.common.datasources.ms.channel.dto.EmailServerDTO;
import es.onebox.common.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.common.datasources.ms.channel.enums.DeliveryMethodStatus;
import es.onebox.common.datasources.ms.channel.enums.EmailMode;
import es.onebox.common.datasources.ms.channel.enums.EmailServerType;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.event.enums.ChannelType;
import es.onebox.common.datasources.orders.dto.OrderDetail;
import es.onebox.common.datasources.orders.dto.OrderDetailItem;
import es.onebox.common.datasources.orders.enums.OrderDetailsItemState;
import es.onebox.common.datasources.orders.repository.OrdersRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.common.utils.AuthenticationUtils;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.oauth2.resource.context.AuthContextUtils;
import es.onebox.utils.ObjectRandomizer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;


class AtmEmailServiceTest {

    @InjectMocks
    private AtmEmailService atmEmailService;

    @Mock
    private ATMAccessService accessService;
    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private AtmSecurityChecker atmSecurityChecker;
    @Mock
    private ChannelRepository channelRepository;
    @Mock
    private CacheRepository hazelcastCacheRepository;

    private static MockedStatic<AuthenticationUtils> authenticationUtils;
    private static MockedStatic<AuthContextUtils> authContextUtils;
    private static MockedStatic<AuthenticationService> authenticationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeAll
    public static void beforeAll() {
        authenticationUtils = Mockito.mockStatic(AuthenticationUtils.class);
        authenticationService = Mockito.mockStatic(AuthenticationService.class);
        authContextUtils = Mockito.mockStatic(AuthContextUtils.class);
    }

    @AfterAll
    public static void afterAll() {
        authenticationUtils.close();
        authContextUtils.close();
        authenticationService.close();
    }

    @Test
    void sendEmail() throws Exception {
        Mockito.when(AuthContextUtils.getToken()).thenReturn("entityId");
        Mockito.when(AuthenticationService.getEntityId()).thenReturn(25L);

        Mockito.when(accessService.getAccessToken(Mockito.anyLong(), Mockito.anyString(), Mockito.anyString())).thenReturn(ObjectRandomizer.randomString());

        Mockito.when(atmSecurityChecker.validateEntity()).thenReturn(true);

        Mockito.when(hazelcastCacheRepository.get(Mockito.any(), Mockito.any())).thenReturn(null);

        OrderDetail orderDetail = ObjectRandomizer.random(OrderDetail.class);
        OrderDetailItem orderDetailItem = orderDetail.getItems().get(0);
        orderDetail.setItems(new ArrayList<>());
        orderDetail.getItems().add(orderDetailItem);
        orderDetail.getItems().get(0).getTicket().getAllocation().getSession().getDate().setStart(ZonedDateTime.now().minusDays(4));
        Mockito.when(ordersRepository.getById(Mockito.anyString(), Mockito.anyString())).thenReturn(orderDetail);
        try {
            atmEmailService.sendExternalEmail(ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.INVALID_ORDER_DATE.getErrorCode());
        }

        orderDetail.getItems().get(0).getTicket().getAllocation().getSession().getDate().setStart(ZonedDateTime.now().plusDays(4));
        orderDetail.getItems().get(0).setState(OrderDetailsItemState.EXPIRED);
        Mockito.when(ordersRepository.getById(Mockito.anyString(), Mockito.anyString())).thenReturn(orderDetail);
        try {
            atmEmailService.sendExternalEmail(ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.INVALID_ORDER_STATE.getErrorCode());
        }


        orderDetail.setType(OrderType.BOOKING);
        orderDetail.getItems().get(0).setState(OrderDetailsItemState.PURCHASED);
        Mockito.when(ordersRepository.getById(Mockito.anyString(), Mockito.anyString())).thenReturn(orderDetail);
        try {
            atmEmailService.sendExternalEmail(ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.INVALID_ORDER_TYPE.getErrorCode());
        }

        orderDetail.setType(OrderType.PURCHASE);
        orderDetail.getItems().get(0).setState(OrderDetailsItemState.PURCHASED);
        orderDetail.setChannel(null);
        Mockito.when(ordersRepository.getById(Mockito.anyString(), Mockito.anyString())).thenReturn(orderDetail);
        try {
            atmEmailService.sendExternalEmail(ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.CHANNEL_ID_NOT_FOUND.getErrorCode());
        }

        orderDetail.setChannel(ObjectRandomizer.random(Channel.class));
        Mockito.when(ordersRepository.getById(Mockito.anyString(), Mockito.anyString())).thenReturn(orderDetail);

        Mockito.when(channelRepository.getChannel(Mockito.anyLong())).thenReturn(null);
        try {
            atmEmailService.sendExternalEmail(ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.CHANNEL_NOT_FOUND.getErrorCode());
        }

        ChannelDTO channelDTO = ObjectRandomizer.random(ChannelDTO.class);
        channelDTO.setEntityId(null);
        Mockito.when(channelRepository.getChannel(Mockito.anyLong())).thenReturn(channelDTO);
        try {
            atmEmailService.sendExternalEmail(ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.CHANNEL_ENTITY_NOT_FOUND.getErrorCode());
        }

        channelDTO.setEntityId(ObjectRandomizer.randomLong());
        channelDTO.setStatus(ChannelStatus.PENDING);
        Mockito.when(channelRepository.getChannel(Mockito.anyLong())).thenReturn(channelDTO);
        try {
            atmEmailService.sendExternalEmail(ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.WRONG_CHANNEL_STATUS.getErrorCode());
        }

        channelDTO.setStatus(ChannelStatus.ACTIVE);
        channelDTO.setType(ChannelType.MEMBER);
        Mockito.when(channelRepository.getChannel(Mockito.anyLong())).thenReturn(channelDTO);
        try {
            atmEmailService.sendExternalEmail(ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.WRONG_CHANNEL_TYPE.getErrorCode());
        }

        channelDTO.setType(ChannelType.OB_PORTAL);
        Mockito.when(channelRepository.getChannel(Mockito.anyLong())).thenReturn(channelDTO);

        Mockito.when(channelRepository.getChannelConfig(Mockito.anyLong())).thenReturn(null);
        try {
            atmEmailService.sendExternalEmail(ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.CHANNEL_DELIVERY_METHODS_NOT_FOUND.getErrorCode());
        }

        Mockito.when(channelRepository.getChannelDeliveryMethods(Mockito.anyLong())).thenReturn(null);
        try {
            atmEmailService.sendExternalEmail(ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.CHANNEL_DELIVERY_METHODS_NOT_FOUND.getErrorCode());
        }

        ChannelDeliveryMethodsDTO channelDeliveryMethodsDTO = ObjectRandomizer.random(ChannelDeliveryMethodsDTO.class);
        channelDeliveryMethodsDTO.setEmailMode(null);
        Mockito.when(channelRepository.getChannelDeliveryMethods(Mockito.anyLong())).thenReturn(channelDeliveryMethodsDTO);
        try {
            atmEmailService.sendExternalEmail(ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.CHANNEL_DELIVERY_METHODS_NOT_FOUND.getErrorCode());
        }

        channelDeliveryMethodsDTO.setEmailMode(EmailMode.NONE);
        Mockito.when(channelRepository.getChannelDeliveryMethods(Mockito.anyLong())).thenReturn(channelDeliveryMethodsDTO);
        try {
            atmEmailService.sendExternalEmail(ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.CHANNEL_DELIVERY_METHODS_NOT_FOUND.getErrorCode());
        }

        channelDeliveryMethodsDTO.setEmailMode(EmailMode.ONLY_TICKET);
        channelDeliveryMethodsDTO.setDeliveryMethods(new ArrayList<>());
        List<ChannelDeliveryMethodDTO> deliveryMethods = new ArrayList();
        ChannelDeliveryMethodDTO channelDeliveryMethodDTO = ObjectRandomizer.random(ChannelDeliveryMethodDTO.class);
        channelDeliveryMethodDTO.setStatus(DeliveryMethodStatus.INACTIVE);
        deliveryMethods.add(channelDeliveryMethodDTO);
        channelDeliveryMethodsDTO.setDeliveryMethods(deliveryMethods);
        Mockito.when(channelRepository.getChannelDeliveryMethods(Mockito.anyLong())).thenReturn(channelDeliveryMethodsDTO);
        try {
            atmEmailService.sendExternalEmail(ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.CHANNEL_DELIVERY_METHODS_NOT_FOUND.getErrorCode());
        }

        channelDeliveryMethodDTO.setStatus(DeliveryMethodStatus.ACTIVE);
        deliveryMethods = new ArrayList<>();
        deliveryMethods.add(channelDeliveryMethodDTO);
        channelDeliveryMethodsDTO.setDeliveryMethods(deliveryMethods);
        Mockito.when(channelRepository.getChannelDeliveryMethods(Mockito.anyLong())).thenReturn(channelDeliveryMethodsDTO);

        Mockito.when(channelRepository.getChannelEmailServerConfiguration(Mockito.anyLong())).thenReturn(null);
        try {
            atmEmailService.sendExternalEmail(ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.WRONG_SERVER_TYPE.getErrorCode());
        }

        EmailServerDTO emailServerDTO = ObjectRandomizer.random(EmailServerDTO.class);
        emailServerDTO.setType(EmailServerType.ONEBOX);
        Mockito.when(channelRepository.getChannelEmailServerConfiguration(Mockito.anyLong())).thenReturn(null);
        try {
            atmEmailService.sendExternalEmail(ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.WRONG_SERVER_TYPE.getErrorCode());
        }

        emailServerDTO.setType(EmailServerType.OTHER);
        emailServerDTO.getConfiguration().setServer(null);
        Mockito.when(channelRepository.getChannelEmailServerConfiguration(Mockito.anyLong())).thenReturn(emailServerDTO);
        try {
            atmEmailService.sendExternalEmail(ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.EMAIL_SERVER_CONFIG_INCOMPLETE.getErrorCode());
        }

        emailServerDTO.getConfiguration().setServer(ObjectRandomizer.randomString());
        emailServerDTO.getConfiguration().setPort(null);
        Mockito.when(channelRepository.getChannelEmailServerConfiguration(Mockito.anyLong())).thenReturn(emailServerDTO);
        try {
            atmEmailService.sendExternalEmail(ObjectRandomizer.randomString());
        } catch (OneboxRestException e) {
            Assertions.assertEquals(e.getErrorCode(), ApiExternalErrorCode.EMAIL_SERVER_CONFIG_INCOMPLETE.getErrorCode());
        }

        emailServerDTO.getConfiguration().setPort(ObjectRandomizer.randomInteger());
        Mockito.when(channelRepository.getChannelEmailServerConfiguration(Mockito.anyLong())).thenReturn(emailServerDTO);

        atmEmailService.sendExternalEmail(ObjectRandomizer.randomString());
    }

}
