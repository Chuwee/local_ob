package es.onebox.mgmt.packs.helper;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.ms.channel.enums.PackItemType;
import es.onebox.mgmt.datasources.ms.channel.enums.PackRangeType;
import es.onebox.mgmt.datasources.ms.channel.enums.PackSubtype;
import es.onebox.mgmt.datasources.ms.channel.enums.PackType;
import es.onebox.mgmt.datasources.ms.channel.repositories.ChannelsRepository;
import es.onebox.mgmt.datasources.ms.event.dto.event.Event;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventStatus;
import es.onebox.mgmt.datasources.ms.event.dto.event.Product;
import es.onebox.mgmt.datasources.ms.event.dto.packs.Pack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackDetail;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackItem;
import es.onebox.mgmt.datasources.ms.event.dto.products.DeliveryPoint;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductVariant;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductVariants;
import es.onebox.mgmt.datasources.ms.event.dto.session.PriceTypes;
import es.onebox.mgmt.datasources.ms.event.dto.session.Session;
import es.onebox.mgmt.datasources.ms.event.dto.session.SessionStatus;
import es.onebox.mgmt.datasources.ms.event.dto.session.Sessions;
import es.onebox.mgmt.datasources.ms.event.repository.EventsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.PacksRepository;
import es.onebox.mgmt.datasources.ms.event.repository.ProductsRepository;
import es.onebox.mgmt.datasources.ms.event.repository.SessionsRepository;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplate;
import es.onebox.mgmt.datasources.ms.venue.repository.VenuesRepository;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import es.onebox.mgmt.packs.dto.CreatePackDTO;
import es.onebox.mgmt.packs.dto.CreatePackItemDTO;
import es.onebox.mgmt.packs.dto.CreatePackItemsDTO;
import es.onebox.mgmt.security.SecurityUtils;
import es.onebox.mgmt.sessions.dto.SessionSaleFlagStatus;
import es.onebox.mgmt.sessions.dto.SessionSearchFilter;
import es.onebox.mgmt.validation.ValidationService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PacksHelper {

    private final ChannelsRepository channelsRepository;
    private final EventsRepository eventsRepository;
    private final SessionsRepository sessionsRepository;
    private final ProductsRepository productsRepository;
    private final ValidationService validationService;
    private final PacksRepository packsRepository;
    private final VenuesRepository venuesRepository;

    public PacksHelper(ChannelsRepository channelsRepository,
                       EventsRepository eventsRepository,
                       SessionsRepository sessionsRepository,
                       ProductsRepository productsRepository,
                       ValidationService validationService, PacksRepository packsRepository, VenuesRepository venuesRepository) {
        this.channelsRepository = channelsRepository;
        this.eventsRepository = eventsRepository;
        this.sessionsRepository = sessionsRepository;
        this.productsRepository = productsRepository;
        this.validationService = validationService;
        this.packsRepository = packsRepository;
        this.venuesRepository = venuesRepository;
    }

    public PackItem getAndCheckPackItem(Long channelId, Long packId, Long packItemId) {
        List<PackItem> items = channelsRepository.getPackItems(channelId, packId);
        return validateItems(packItemId, items);
    }

    public PackItem getAndCheckPackItem(Long packId, Long packItemId) {
        List<PackItem> items = packsRepository.getPackItems(packId);
        return validateItems(packItemId, items);
    }

    private static PackItem validateItems(Long packItemId, List<PackItem> items) {
        if (CollectionUtils.isEmpty(items)) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_NOT_FOUND);
        }
        return items.stream()
                .filter(item -> packItemId.equals(item.getPackItemId()))
                .findFirst()
                .orElseThrow(() -> new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_ITEM_NOT_FOUND));
    }

    public Sessions getAndCheckPackSessions(List<Long> ids, Long entityId) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        SessionSearchFilter sessionSearchFilter = new SessionSearchFilter();
        sessionSearchFilter.setId(ids);
        Sessions sessions = eventsRepository.getSessionsByEventIds(SecurityUtils.getUserOperatorId(), null, sessionSearchFilter);
        if (sessions.getData() == null || ids.size() != sessions.getData().size()
                || sessions.getData().stream().anyMatch(session -> !session.getEntityId().equals(entityId))) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.SESSION_NOT_FOUND_IN_CHANNEL_EVENTS);
        }
        return sessions;
    }

    public Session getAndCheckMainPackSessions(CreatePackDTO createPackDTO, Long entityId) {
        if (PackType.MANUAL.equals(PackType.getByName(createPackDTO.getType().name()))) {
            return null;
        }
        return getAndCheckPackSessions(List.of(createPackDTO.getMainItem().getItemId()), entityId).getData().get(0);
    }

    public PackItem getMainItem(List<PackItem> items) {
        return items.stream()
                .filter(PackItem::getMain)
                .findFirst()
                .orElse(null);
    }


    public PackItem getMainEventItem(List<PackItem> items) {
        return items.stream()
                .filter(item -> PackItemType.EVENT.equals(item.getType()))
                .findFirst()
                .orElse(null);
    }

    public Event getMainEventPackItemEvent(PackItem mainEventPackItem) {
        if (mainEventPackItem == null) {
            return null;
        }
        return validationService.getAndCheckEvent(mainEventPackItem.getItemId());
    }

    public VenueTemplate getMainEvenPackItemVenueTemplate(PackItem mainEventPackItem) {
        if (mainEventPackItem == null) {
            return null;
        }
        return validationService.getAndCheckVenueTemplate(mainEventPackItem.getVenueTemplateId().longValue());
    }

    public Map<Long, Session> getPackItemSessionMap(List<PackItem> sessionPackItems) {
        if (CollectionUtils.isEmpty(sessionPackItems)) {
            return new HashMap<>();
        }
        SessionSearchFilter sessionSearchFilter = new SessionSearchFilter();
        List<Long> sessionIds = sessionPackItems.stream().map(PackItem::getItemId).toList();
        sessionSearchFilter.setId(sessionIds);
        List<Session> sessions = eventsRepository.getSessionsByEventIds(SecurityUtils.getUserOperatorId(), null, sessionSearchFilter).getData();
        return sessionPackItems.stream()
                .collect(Collectors.toMap(
                        PackItem::getPackItemId,
                        item -> sessions.stream()
                                .filter(session -> session.getId().equals(item.getItemId()))
                                .findFirst()
                                .orElseThrow(() -> new OneboxRestException(ApiMgmtChannelsErrorCode.SESSION_NOT_FOUND))
                ));
    }

    public Map<Long, List<IdNameDTO>> getPackItemPriceTypesMap(PackType packType, List<PackItem> sessionPackItems, PackItem mainEventPackItem, Map<Long, Session> sessionMap) {
        if (MapUtils.isEmpty(sessionMap) || PackType.MANUAL.equals(packType)) {
            return new HashMap<>();
        }
        Map<Long, List<IdNameDTO>> response = sessionPackItems.stream().collect(Collectors.toMap(PackItem::getPackItemId,
                item -> {
                    Session session = sessionMap.get(item.getPackItemId());
                    PriceTypes priceTypes = sessionsRepository.getPriceTypes(session.getEventId(), session.getId());
                    return priceTypes.getData().stream().map(p -> new IdNameDTO(p.getId(), p.getName())).toList();
                }
        ));
        if (mainEventPackItem != null && PackItemType.EVENT.equals(mainEventPackItem.getType())) {
            List<es.onebox.mgmt.datasources.ms.venue.dto.template.PriceType> priceTypes =
                    venuesRepository.getPriceTypes(mainEventPackItem.getVenueTemplateId().longValue());
            response.put(mainEventPackItem.getPackItemId(), priceTypes.stream().map(p -> new IdNameDTO(p.getId(), p.getName())).toList());
        }
        return response;
    }

    public Map<Long, Product> getPackItemProductMap(List<PackItem> productPackItems) {
        return productPackItems.stream()
                .collect(Collectors.toMap(
                        PackItem::getPackItemId,
                        item -> productsRepository.getProduct(item.getItemId())
                ));
    }

    public Map<Long, ProductVariant> getPackItemProductVariantMap(PackType packType, List<PackItem> productPackItems) {
        if (PackType.MANUAL.equals(packType)) {
            return new HashMap<>();
        }
        return productPackItems.stream()
                .filter(item -> item.getVariantId() != null)
                .collect(Collectors.toMap(
                        PackItem::getPackItemId,
                        item -> productsRepository.getProductVariant(item.getItemId(), item.getVariantId().longValue())
                ));
    }

    public Map<Long, DeliveryPoint> getPackItemDeliveryPointMap(PackType packType, List<PackItem> productPackItems) {
        if (PackType.MANUAL.equals(packType)) {
            return new HashMap<>();
        }
        return productPackItems.stream()
                .filter(item -> item.getDeliveryPointId() != null)
                .collect(Collectors.toMap(
                        PackItem::getPackItemId,
                        item -> productsRepository.getDeliveryPoint(item.getDeliveryPointId().longValue())
                ));
    }

    public List<PackItem> getSessionPackItems(List<PackItem> items) {
        return items.stream().filter(item -> PackItemType.SESSION.equals(item.getType())).toList();
    }

    public List<PackItem> getProductPackItems(List<PackItem> items) {
        return items.stream().filter(item -> PackItemType.PRODUCT.equals(item.getType())).toList();
    }

    public Boolean isDateConfigured(Pack pack) {
        return pack.getPackRangeType() != null && (PackRangeType.AUTOMATIC.equals(pack.getPackRangeType())
                || pack.getCustomStartSaleDate() != null && pack.getCustomEndSaleDate() != null);
    }

    public boolean hasNoSessionsOrProductItems(List<PackItem> items) {
        return items.stream().noneMatch(item -> PackItemType.SESSION.equals(item.getType()) || PackItemType.EVENT.equals(item.getType()));
    }

    public Long getMainItemVenueTemplateId(PackItem mainItem) {
        return switch (mainItem.getType()) {
            case EVENT -> mainItem.getVenueTemplateId().longValue();
            case SESSION -> sessionsRepository.getSession(mainItem.getItemId()).getVenueConfigId();
            default -> null;
        };
    }

    public List<CreatePackItemDTO> getCreateSessionPackItems(CreatePackItemsDTO createPackItemDTO) {
        return createPackItemDTO.stream().filter(item -> PackItemType.SESSION.equals(item.getType())).toList();
    }

    public List<CreatePackItemDTO> getCreateProductPackItems(CreatePackItemsDTO createPackItemDTO) {
        return createPackItemDTO.stream().filter(item -> PackItemType.PRODUCT.equals(item.getType())).toList();
    }


    public List<Session> getSessionsFromCreateSessionPackItems(List<CreatePackItemDTO> sessionPackItems) {
        SessionSearchFilter sessionSearchFilter = new SessionSearchFilter();
        List<Long> sessionIds = sessionPackItems.stream().map(CreatePackItemDTO::getItemId).toList();
        sessionSearchFilter.setId(sessionIds);
        return eventsRepository.getSessionsByEventIds(SecurityUtils.getUserOperatorId(), null, sessionSearchFilter).getData();
    }

    public Session getSessionFromCreateSessionPackItem(CreatePackItemDTO item, List<Session> sessions) {
        return sessions.stream()
                .filter(session -> session.getId().equals(item.getItemId()))
                .findFirst()
                .orElseThrow(() -> new OneboxRestException(ApiMgmtChannelsErrorCode.SESSION_NOT_FOUND));
    }

    public boolean sessionHasMainTemplate(Session session, PackItem mainItem) {
        return session.getVenueConfigId().equals(getMainItemVenueTemplateId(mainItem));
    }

    public void validateSessionStatus(Session session) {
        EnumSet<SessionStatus> sessionValidStatus = EnumSet.of(
                SessionStatus.READY,
                SessionStatus.IN_PROGRESS,
                SessionStatus.PLANNED,
                SessionStatus.SCHEDULED
        );
        EnumSet<SessionSaleFlagStatus> sessionValidSaleStatus = EnumSet.of(
                SessionSaleFlagStatus.SALE,
                SessionSaleFlagStatus.SALE_PENDING,
                SessionSaleFlagStatus.IN_PROGRAMMING,
                SessionSaleFlagStatus.PLANNED
        );
        if (!sessionValidStatus.contains(session.getStatus()) || !sessionValidSaleStatus.contains(session.getSale())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_SESSION_ITEM_INVALID_STATUS);
        }
    }

    public void validateEventStatus(Event event) {
        EnumSet<EventStatus> eventValidStatus = EnumSet.of(
                EventStatus.PLANNED,
                EventStatus.IN_PROGRAMMING,
                EventStatus.READY
        );
        if (!eventValidStatus.contains(event.getStatus())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.CHANNEL_PACK_EVENT_ITEM_INVALID_STATUS);
        }
    }

    public PackDetail getAndCheckPack(Long channelId, Long packId) {
        PackDetail pack = channelsRepository.getPack(channelId, packId);
        if (pack == null) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.PACK_NOT_FOUND);
        }
        return pack;
    }

    public PackDetail getAndCheckPack(Long packId) {
        PackDetail pack = packsRepository.getPack(packId);
        if (pack == null || !PackSubtype.PROMOTER.getId().equals(pack.getSubtype().getId())) {
            throw new OneboxRestException(ApiMgmtChannelsErrorCode.PACK_NOT_FOUND);
        }
        return pack;
    }

    public void addVariantsToSimpleProducts(PackType packType, CreatePackItemsDTO createPackItemDTO) {
        if (PackType.MANUAL.equals(packType)) {
            return;
        }
        createPackItemDTO.stream()
                .filter(item -> PackItemType.PRODUCT.equals(item.getType()))
                .filter(productItem -> productItem.getVariantId() == null)
                .forEach(productItem -> {
                    ProductVariants productVariants = productsRepository.searchProductVariants(productItem.getItemId(), null);
                    productItem.setVariantId(productVariants.getData().get(0).getId().intValue());
                });
    }

}
