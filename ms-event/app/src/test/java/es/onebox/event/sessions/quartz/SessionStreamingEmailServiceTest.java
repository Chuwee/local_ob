package es.onebox.event.sessions.quartz;

import es.onebox.utils.ObjectRandomizer;
import es.onebox.core.serializer.dto.response.Metadata;
import es.onebox.dal.dto.couch.order.OrderDTO;
import es.onebox.dal.dto.couch.order.OrderProductDTO;
import es.onebox.event.catalog.elasticsearch.indexer.StaticDataContainer;
import es.onebox.event.datasources.ms.entity.dto.EntityDTO;
import es.onebox.event.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.event.datasources.ms.order.dto.SearchOperationsResponse;
import es.onebox.event.datasources.ms.order.repository.OrdersRepository;
import es.onebox.event.events.amqp.requestchannelnotification.HandlebarComposer;
import es.onebox.event.events.amqp.sendemail.SendEmailService;
import es.onebox.event.events.dao.ChannelDao;
import es.onebox.event.events.dao.EventCommunicationElementDao;
import es.onebox.event.sessions.dao.SessionConfigCouchDao;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.domain.sessionconfig.SessionConfig;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelElementosComEventoRecord;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;

public class SessionStreamingEmailServiceTest {

    @Mock
    private SendEmailService sendEmailService;
    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private SessionConfigCouchDao sessionConfigCouchDao;
    @Mock
    private SessionDao sessionDao;
    @Mock
    private ChannelDao channelDao;
    @Mock
    private HandlebarComposer sessionStreamingNotification;
    @Mock
    private EventCommunicationElementDao eventCommunicationElementDao;
    @Mock
    private StaticDataContainer staticDataContainer;
    @Mock
    private EntitiesRepository entitiesRepository;
    @Mock
    private ApplicationContext ctx;

    @InjectMocks
    private SessionStreamingEmailDataService sessionStreamingEmailDataService;

    @InjectMocks
    private SessionStreamingEmailService sessionStreamingEmailService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        ReflectionTestUtils.setField(sessionStreamingEmailService, "domain", "http://domain.com");
        ReflectionTestUtils.setField(sessionStreamingEmailService, "fileBasePath", "/path/");
        ReflectionTestUtils.setField(sessionStreamingEmailService, "liveEndpoint", "www.google.com/%s/%s/live?barcode=%s");
    }

    @Test
    public void sendEmailsOK() throws IOException {

        Long sessionId = 1l;
        String email = "test@test.com";
        String sessionName = "session test";

        SearchOperationsResponse response = new SearchOperationsResponse();
        response.setData(ObjectRandomizer.randomListOf(OrderDTO.class, 10));
        response.getData().forEach(o -> {
            o.getCustomer().setEmail(email);
            o.getOrderData().setLanguage("es_ES");
            o.getDate().setTimeZone("UTC");
            o.getProducts().forEach(p -> {
                p.setSessionId(sessionId.intValue());
                p.setRelatedProductState(null);
            });
        });
        Metadata metadata = new Metadata();
        metadata.setTotal((long) response.getData().size());
        response.setMetadata(metadata);
        Mockito.when(ordersRepository.searchOperations(any())).thenReturn(response);
        SessionConfig randomSC = ObjectRandomizer.random(SessionConfig.class);
        randomSC.getStreamingVendorConfig().setSendEmail(true);
        Mockito.when(sessionConfigCouchDao.get(anyString())).thenReturn(randomSC);
        Mockito.when(entitiesRepository.getEntity(anyInt())).thenReturn(ObjectRandomizer.random(EntityDTO.class));
        Mockito.when(sessionStreamingNotification.getPropertiesMessage(any(), anyString(), any())).thenReturn("");
        Mockito.when(sessionStreamingNotification.composeFromProperties(any(), any(), any())).thenReturn("");
        List<CpanelElementosComEventoRecord> communication = new ArrayList<>();
        Mockito.when(eventCommunicationElementDao.findCommunicationElements(any(), isNull(), isNull(), any())).thenReturn(communication);

        Mockito.when(sessionDao.findSession(any())).thenReturn(initSession(sessionName));

        CpanelCanalRecord cpanelCanalRecord = new CpanelCanalRecord();
        cpanelCanalRecord.setUrlintegracion("urltest");
        Mockito.when(channelDao.getById(any())).thenReturn(cpanelCanalRecord);

        Mockito.when(ctx.getBean(SessionStreamingEmailDataService.class)).thenReturn(sessionStreamingEmailDataService);

        Mockito.doNothing().when(sendEmailService).sendEmail(anyString(), anyString(), anyString(), anyInt(), any(), any());

        sessionStreamingEmailService.sendEmails(sessionId, null);

        Mockito.verify(ordersRepository, Mockito.times(1)).searchOperations(any());
        Mockito.verify(sendEmailService, Mockito.times(metadata.getTotal().intValue())).
                sendEmail(anyString(), anyString(), anyString(), anyInt(), any(), any());
        Mockito.doAnswer(a -> {
            String sendEmail = (String) a.getArguments()[0];
            Assertions.assertEquals(email, sendEmail);
            return Void.class;
        }).when(sendEmailService).sendEmail(anyString(), anyString(), anyString(), anyInt(), any(), any());


    }

    SessionRecord initSession(String sessionName) {
        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setNombre(sessionName);
        sessionRecord.setVenueTZ("Europe/Berlin");
        return sessionRecord;
    }

    @Test
    public void sendEmailsWithInvalidProducts() throws IOException {
        Long sessionId = 1l;
        String sessionName = "session test";

        SearchOperationsResponse response = new SearchOperationsResponse();
        response.setData(ObjectRandomizer.randomListOf(OrderDTO.class, 3));
        //Session has 3 orders, 1 valid, 1 of other session, 1 with product refunded
        for (int i = 0; i < response.getData().size(); i++) {
            OrderDTO o = response.getData().get(i);
            o.getOrderData().setLanguage("es_ES");
            o.getDate().setTimeZone("UTC");
            for (OrderProductDTO p : o.getProducts()) {
                if (i == 1) {
                    p.setSessionId(2);
                } else {
                    p.setSessionId(sessionId.intValue());
                }
                if (i == 2) {
                    p.setRelatedProductState(null);
                }
            }
        }
        Metadata metadata = new Metadata();
        metadata.setTotal((long) response.getData().size());
        response.setMetadata(metadata);
        Mockito.when(ordersRepository.searchOperations(any())).thenReturn(response);
        SessionConfig randomSC = ObjectRandomizer.random(SessionConfig.class);
        randomSC.getStreamingVendorConfig().setSendEmail(true);
        Mockito.when(sessionConfigCouchDao.get(anyString())).thenReturn(randomSC);
        Mockito.when(entitiesRepository.getEntity(anyInt())).thenReturn(ObjectRandomizer.random(EntityDTO.class));
        Mockito.when(sessionStreamingNotification.getPropertiesMessage(any(), anyString(), any())).thenReturn("");
        Mockito.when(sessionStreamingNotification.composeFromProperties(any(), any(), any())).thenReturn("");

        List<CpanelElementosComEventoRecord> communication = new ArrayList<>();
        Mockito.when(eventCommunicationElementDao.findCommunicationElements(any(), any(), any(), any())).thenReturn(communication);

        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setNombre(sessionName);
        Mockito.when(sessionDao.findSession(any())).thenReturn(initSession(sessionName));

        CpanelCanalRecord cpanelCanalRecord = new CpanelCanalRecord();
        cpanelCanalRecord.setUrlintegracion("urltest");
        Mockito.when(channelDao.getById(any())).thenReturn(cpanelCanalRecord);

        Mockito.when(ctx.getBean(SessionStreamingEmailDataService.class)).thenReturn(sessionStreamingEmailDataService);

        sessionStreamingEmailService.sendEmails(sessionId, null);

        Mockito.verify(ordersRepository, Mockito.times(1)).searchOperations(any());

        //Only send 1 of 3 products
        Mockito.verify(sendEmailService, Mockito.times(1)).
                sendEmail(anyString(), anyString(), anyString(), anyInt(), any(), any());

    }

    @Test
    public void sendEmailsWithErrorOnSending() throws IOException {
        Long sessionId = 1l;
        String sessionName = "session test";

        SearchOperationsResponse response = new SearchOperationsResponse();
        response.setData(ObjectRandomizer.randomListOf(OrderDTO.class, 1));
        for (int i = 0; i < response.getData().size(); i++) {
            OrderDTO o = response.getData().get(i);
            o.getOrderData().setLanguage("es_ES");
            o.getDate().setTimeZone("UTC");
            for (OrderProductDTO p : o.getProducts()) {
                p.setSessionId(sessionId.intValue());
                p.setRelatedProductState(null);
            }
        }
        Metadata metadata = new Metadata();
        metadata.setTotal((long) response.getData().size());
        response.setMetadata(metadata);
        Mockito.when(ordersRepository.searchOperations(any())).thenReturn(response);
        SessionConfig randomSC = ObjectRandomizer.random(SessionConfig.class);
        randomSC.getStreamingVendorConfig().setSendEmail(true);
        Mockito.when(sessionConfigCouchDao.get(anyString())).thenReturn(randomSC);
        Mockito.when(entitiesRepository.getEntity(anyInt())).thenReturn(ObjectRandomizer.random(EntityDTO.class));
        Mockito.when(sessionStreamingNotification.getPropertiesMessage(any(), anyString(), any())).thenReturn("");

        List<CpanelElementosComEventoRecord> communication = new ArrayList<>();
        Mockito.when(eventCommunicationElementDao.findCommunicationElements(any(), any(), any(), any())).thenReturn(communication);

        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setNombre(sessionName);
        Mockito.when(sessionDao.findSession(any())).thenReturn(initSession(sessionName));

        CpanelCanalRecord cpanelCanalRecord = new CpanelCanalRecord();
        cpanelCanalRecord.setUrlintegracion("urltest");
        Mockito.when(channelDao.getById(any())).thenReturn(cpanelCanalRecord);

        Mockito.when(ctx.getBean(SessionStreamingEmailDataService.class)).thenReturn(sessionStreamingEmailDataService);

        Mockito.doThrow(new IOException("error composing email")).when(sessionStreamingNotification).composeFromProperties(any(), any(), any());

        sessionStreamingEmailService.sendEmails(sessionId, null);

        Mockito.verify(ordersRepository, Mockito.times(1)).searchOperations(any());
        Mockito.verify(sendEmailService, Mockito.times(0)).
                sendEmail(anyString(), anyString(), anyString(), anyInt(), any(), any());

    }

    @Test
    public void sendEmailsWithDisabledEmail() throws IOException {
        Long sessionId = 1l;
        String sessionName = "session test";

        SearchOperationsResponse response = new SearchOperationsResponse();
        response.setData(ObjectRandomizer.randomListOf(OrderDTO.class, 1));
        for (int i = 0; i < response.getData().size(); i++) {
            OrderDTO o = response.getData().get(i);
            o.getOrderData().setLanguage("es_ES");
            o.getDate().setTimeZone("UTC");
            for (OrderProductDTO p : o.getProducts()) {
                p.setSessionId(sessionId.intValue());
                p.setRelatedProductState(null);
            }
        }
        Metadata metadata = new Metadata();
        metadata.setTotal((long) response.getData().size());
        response.setMetadata(metadata);
        Mockito.when(ordersRepository.searchOperations(any())).thenReturn(response);
        SessionConfig randomSC = ObjectRandomizer.random(SessionConfig.class);
        randomSC.getStreamingVendorConfig().setSendEmail(false);
        Mockito.when(sessionConfigCouchDao.get(anyString())).thenReturn(randomSC);
        Mockito.when(entitiesRepository.getEntity(anyInt())).thenReturn(ObjectRandomizer.random(EntityDTO.class));
        Mockito.when(sessionStreamingNotification.getPropertiesMessage(any(), anyString(), any())).thenReturn("");

        List<CpanelElementosComEventoRecord> communication = new ArrayList<>();
        Mockito.when(eventCommunicationElementDao.findCommunicationElements(any(), any(), any(), any())).thenReturn(communication);

        SessionRecord sessionRecord = new SessionRecord();
        sessionRecord.setNombre(sessionName);
        Mockito.when(sessionDao.findSession(any())).thenReturn(initSession(sessionName));

        CpanelCanalRecord cpanelCanalRecord = new CpanelCanalRecord();
        cpanelCanalRecord.setUrlintegracion("urltest");
        Mockito.when(channelDao.getById(any())).thenReturn(cpanelCanalRecord);

        Mockito.when(ctx.getBean(SessionStreamingEmailDataService.class)).thenReturn(sessionStreamingEmailDataService);

        sessionStreamingEmailService.sendEmails(sessionId, null);

        Mockito.verify(ordersRepository, Mockito.times(1)).searchOperations(any());
        Mockito.verify(sendEmailService, Mockito.times(0)).
                sendEmail(anyString(), anyString(), anyString(), anyInt(), any(), any());

    }
}
