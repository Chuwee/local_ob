package es.onebox.event.products.service;

import static es.onebox.core.utils.common.CommonUtils.isNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.core.serializer.dto.response.ListWithMetadata;
import es.onebox.event.common.amqp.refreshdata.RefreshDataService;
import es.onebox.event.common.amqp.webhook.WebhookService;
import es.onebox.event.common.amqp.webhook.dto.enums.NotificationSubtype;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dao.ChannelDao;
import es.onebox.event.events.dao.bean.ChannelInfo;
import es.onebox.event.events.enums.ChannelSubtype;
import es.onebox.event.exception.MsEventErrorCode;
import es.onebox.event.products.converter.ProductChannelsConverter;
import es.onebox.event.products.converter.ProductSessionConverter;
import es.onebox.event.products.dao.ProductChannelDao;
import es.onebox.event.products.dao.ProductEventDao;
import es.onebox.event.products.dao.ProductSessionDao;
import es.onebox.event.products.domain.ProductChannelRecord;
import es.onebox.event.products.domain.ProductEventRecord;
import es.onebox.event.products.domain.ProductSessionRecord;
import es.onebox.event.products.dto.CreateProductChannelDTO;
import es.onebox.event.products.dto.CreateProductChannelResponseDTO;
import es.onebox.event.products.dto.CreateProductChannelsResponseDTO;
import es.onebox.event.products.dto.ProductChannelDTO;
import es.onebox.event.products.dto.ProductChannelSessionDTO;
import es.onebox.event.products.dto.ProductChannelSessionsFilter;
import es.onebox.event.products.dto.ProductChannelsDTO;
import es.onebox.event.products.dto.UpdateProductChannelDTO;
import es.onebox.event.products.enums.SelectionType;
import es.onebox.event.sessions.dao.SessionDao;
import es.onebox.event.sessions.dao.record.SessionRecord;
import es.onebox.event.sessions.dto.SessionsDTO;
import es.onebox.event.sessions.request.SessionSearchFilter;
import es.onebox.event.sessions.service.SessionService;
import es.onebox.jooq.annotation.MySQLRead;
import es.onebox.jooq.annotation.MySQLWrite;
import es.onebox.jooq.cpanel.tables.records.CpanelCanalRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductChannelRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductRecord;
import es.onebox.jooq.cpanel.tables.records.CpanelProductSessionRecord;

@Service
public class ProductChannelService {

    private final ChannelDao channelDao;
    private final ProductChannelDao productChannelDao;
    private final ProductSessionDao productSessionDao;
    private final ProductEventDao productEventDao;
    private final SessionDao sessionDao;

    private final ProductService productService;
    private final SessionService sessionService;
    private final RefreshDataService refreshDataService;
    private final WebhookService webhookService;

    @Value("${onebox.repository.S3SecureUrl}")
    private String s3domain;
    @Value("${onebox.repository.fileBasePath}")
    private String fileBasePath;

    @Autowired
    public ProductChannelService(ChannelDao channelDao, ProductChannelDao productChannelDao,
            ProductSessionDao productSessionDao, ProductService productService,
            RefreshDataService refreshDataService,
            ProductEventDao productEventDao,
            SessionService sessionService, SessionDao sessionDao, WebhookService webhookService) {
        this.channelDao = channelDao;
        this.productChannelDao = productChannelDao;
        this.productSessionDao = productSessionDao;
        this.productService = productService;
        this.refreshDataService = refreshDataService;
        this.productEventDao = productEventDao;
        this.sessionService = sessionService;
        this.sessionDao = sessionDao;
        this.webhookService = webhookService;
    }

    @MySQLRead
    public ProductChannelsDTO getProductChannels(Long productId) {
        productService.getAndCheckProductRecord(productId);
        List<ProductChannelRecord> productChannelRecords = productChannelDao.findByProductId(productId);
        if (productChannelRecords == null) {
            return new ProductChannelsDTO();
        }
        return ProductChannelsConverter.toEntity(productChannelRecords, getS3Repository());
    }

    @MySQLRead
    public ProductChannelDTO getProductChannel(Long productId, Long channelId) {
        productService.getAndCheckProductRecord(productId);
        ProductChannelRecord productChannelRecord = productChannelDao.findByProductIdAndChannelId(productId, channelId);
        if (productChannelRecord == null) {
            return null;
        }
        return ProductChannelsConverter.toEntity(productChannelRecord, getS3Repository());
    }

    @MySQLWrite
    public void deleteProductChannel(Long productId, Long channelId) {
        productService.getAndCheckProductRecord(productId);
        checkChannel(channelId);

        CpanelProductChannelRecord productChannelRecord = productChannelDao.findByProductIdAndChannelId(productId,
                channelId);
        if (productChannelRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_CHANNEL_NOT_FOUND);
        }
        productChannelDao.delete(productChannelRecord);

        postUpdateProduct(productId, List.of(channelId.intValue()));
        webhookService.sendProductChannelDeleteNotification(productId, channelId, NotificationSubtype.PRODUCT_CHANNEL_DELETED);
    }

    @MySQLWrite
    public CreateProductChannelsResponseDTO createProductChannels(Long productId,
            CreateProductChannelDTO createProductChannelDTO) {
        // check product exists
        CpanelProductRecord productRecord = productService.getAndCheckProductRecord(productId);

        // check channels exist
        List<ChannelInfo> channelRecords = channelDao.getByIdsNotDeleted(createProductChannelDTO.getChannelIds());
        if (channelRecords == null || channelRecords.isEmpty()
                || channelRecords.size() != createProductChannelDTO.getChannelIds().size()) {
            throw new OneboxRestException(MsEventErrorCode.CHANNEL_NOT_FOUND);
        }
        for (ChannelInfo channel : channelRecords) {
            if (ChannelSubtype.WEB.getIdSubtipo() == channel.getSubtypeId()) {
                throw new OneboxRestException(MsEventErrorCode.PRODUCT_CHANNEL_INVALID_TYPE);
            }
        }
        // check same product-channels from the request don't exist
        if (productChannelDao.countByProductIdAndChannelIds(productId, createProductChannelDTO.getChannelIds()) > 0) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_CHANNEL_ALREADY_EXISTS);
        }

        CreateProductChannelsResponseDTO result = new CreateProductChannelsResponseDTO();

        for (ChannelInfo channelInfo : channelRecords) {
            CpanelProductChannelRecord cpanelProductChannelRecord = new CpanelProductChannelRecord();
            cpanelProductChannelRecord.setProductid(productId.intValue());
            cpanelProductChannelRecord.setChannelid(channelInfo.getId().intValue());
            cpanelProductChannelRecord.setStandaloneenabled((byte) 1);
            cpanelProductChannelRecord.setCheckoutsuggestionenabled((byte) 1);
            productChannelDao.insert(cpanelProductChannelRecord);

            CreateProductChannelResponseDTO productChannelDTO = new CreateProductChannelResponseDTO();
            productChannelDTO.setChannel(new IdNameDTO(channelInfo.getId(), channelInfo.getName()));
            productChannelDTO.setProduct(new IdNameDTO(productId, productRecord.getName()));
            result.add(productChannelDTO);
        }
        postUpdateProduct(productId, createProductChannelDTO.getChannelIds());
        webhookService.sendProductNotification(productId, NotificationSubtype.PRODUCT_CHANNEL_ADDED);
        return result;
    }

    @MySQLWrite
    public void updateProductChannel(Long productId, Long channelId, UpdateProductChannelDTO updateProductChannelDTO) {
        productService.getAndCheckProductRecord(productId);
        checkChannel(channelId);

        CpanelProductChannelRecord productChannelRecord = productChannelDao.findByProductIdAndChannelId(productId,
                channelId);
        if (productChannelRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_CHANNEL_NOT_FOUND);
        }

        productChannelRecord.setStandaloneenabled(
                ConverterUtils.isTrueAsByte(BooleanUtils.isTrue(updateProductChannelDTO.getStandaloneEnabled())));
        productChannelRecord.setCheckoutsuggestionenabled(
                ConverterUtils
                        .isTrueAsByte(BooleanUtils.isTrue(updateProductChannelDTO.getCheckoutSuggestionEnabled())));

        productChannelDao.update(productChannelRecord);
        postUpdateProduct(productId, List.of(channelId.intValue()));
        webhookService.sendProductChannelNotification(productId, channelId, NotificationSubtype.PRODUCT_CHANNEL_SALE_TYPE);
    }

    @MySQLRead
    public ListWithMetadata<ProductChannelSessionDTO> getProductChannelSessions(Long productId, Long channelId,
            ProductChannelSessionsFilter filter) {
        productService.getAndCheckProductRecord(productId);
        CpanelProductChannelRecord productChannelRecord = productChannelDao.findByProductIdAndChannelId(productId,
                channelId);
        if (productChannelRecord == null) {
            throw new OneboxRestException(MsEventErrorCode.PRODUCT_CHANNEL_NOT_FOUND);
        }
        if (!ConverterUtils.isByteAsATrue(productChannelRecord.getStandaloneenabled())) {
            return new ListWithMetadata<>();
        } else {
            List<ProductEventRecord> productEventRecords = productEventDao.findByProductId(productId.intValue(), false);
            SessionSearchFilter sessionFilter = prepareSessionSearchFilter(productId, productEventRecords, filter);
            SessionsDTO sessions = sessionService.searchSessions(sessionFilter);
            return ProductSessionConverter.toProductSessionResponse(sessions);
        }
    }

    private void postUpdateProduct(Long productId, List<Integer> changedChannels) {
        // update products catalog
        refreshDataService.refreshProduct(productId);

        // update channel-session published products
        Set<Integer> events = productSessionDao.findRelatedEvents(productId, null, changedChannels);
        for (Integer eventId : events) {
            refreshDataService.refreshEvent(eventId.longValue(), "productChannelService.postUpdateProduct");
        }
        Map<Long, List<Long>> publishedSessions = productSessionDao.findPublishedSessions(productId, null, null,
                changedChannels);
        if (MapUtils.isNotEmpty(publishedSessions)) {
            for (Map.Entry<Long, List<Long>> sessionsByEvent : publishedSessions.entrySet()) {
                refreshDataService.refreshSessions(sessionsByEvent.getKey(), sessionsByEvent.getValue(),
                        "productChannelService.postUpdateProduct");
            }
        }
    }

    private void checkChannel(Long channelId) {
        CpanelCanalRecord channel = channelDao.getById(channelId.intValue());
        if (isNull(channel) || channel.getEstado() == 0) {
            throw new OneboxRestException(MsEventErrorCode.CHANNEL_NOT_FOUND);
        }
    }

    private SessionSearchFilter prepareSessionSearchFilter(Long productId, List<ProductEventRecord> productEventRecords,
            ProductChannelSessionsFilter filter) {
        SessionSearchFilter sessionFilter = new SessionSearchFilter();
        sessionFilter.setLimit(filter.getLimit());
        sessionFilter.setOffset(filter.getOffset());
        sessionFilter.setStatus(filter.getStatus());
        sessionFilter.setSessionPack(false);
        sessionFilter.setIncludeDeleted(false);

        List<Long> allRequiredSessionIds = new ArrayList<>();

        for (ProductEventRecord productEventRecord : productEventRecords) {
            if (SelectionType.RESTRICTED.equals(SelectionType.get(productEventRecord.getSessionsselectiontype()))) {
                List<ProductSessionRecord> productSessions = productSessionDao
                        .findProductSessionsByProductId(productId.intValue(), productEventRecord.getEventid());
                allRequiredSessionIds.addAll(
                        Optional.ofNullable(productSessions).orElse(Collections.emptyList())
                                .stream()
                                .map(CpanelProductSessionRecord::getSessionid)
                                .map(Integer::longValue)
                                .toList());
            } else {
                List<SessionRecord> allEventSessions = sessionDao
                        .findSessionsByEventId(productEventRecord.getEventid());
                allRequiredSessionIds.addAll(
                        Optional.ofNullable(allEventSessions).orElse(Collections.emptyList())
                                .stream()
                                .map(session -> session.getIdsesion().longValue())
                                .toList());
            }
        }
        sessionFilter.setIds(allRequiredSessionIds);
        return sessionFilter;
    }

    private String getS3Repository() {
        return this.s3domain + this.fileBasePath;
    }
}
