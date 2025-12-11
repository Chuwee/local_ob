package es.onebox.mgmt.datasources.ms.channel.repositories;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.channels.externaltools.dto.ChannelExternalToolsNamesDTO;
import es.onebox.mgmt.channels.suggestions.dto.CreateSuggestionTargetRequestDTO;
import es.onebox.mgmt.common.surcharges.dto.SurchargeTypeDTO;
import es.onebox.mgmt.customdomains.common.dto.DomainSettings;
import es.onebox.mgmt.datasources.common.dto.AuthConfig;
import es.onebox.mgmt.datasources.common.dto.Surcharge;
import es.onebox.mgmt.datasources.ms.channel.MsChannelDatasource;
import es.onebox.mgmt.datasources.ms.channel.dto.BookingSettings;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelAcceptRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelConfig;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelCorsSettings;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelEventSaleRestrictionResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelEventsSaleRestrictions;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelLoyaltyPoints;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelUpdateRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelVouchers;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelsResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.CreateChannel;
import es.onebox.mgmt.datasources.ms.channel.dto.PackItemPriceTypesRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.PackItemPriceTypesResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.SaleRequestChannelCandidatesResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.UpdateFavoriteChannel;
import es.onebox.mgmt.datasources.ms.channel.dto.authvendor.ChannelAuthVendor;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklist;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistStatus;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistType;
import es.onebox.mgmt.datasources.ms.channel.dto.blacklists.ChannelBlacklistsResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEvent;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEventMsFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEventUpdate;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEvents;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelEventsUpdate;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelSessions;
import es.onebox.mgmt.datasources.ms.channel.dto.catalog.ChannelSessionsMsFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.CustomResourcesMsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.UpdateCustomResourcesMsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.assets.CreateCustomResourceAssetsMsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.assets.CustomResourceAssetsFilterMs;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.assets.CustomResourceAssetsMsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.deliverymethod.ChannelDeliveryMethods;
import es.onebox.mgmt.datasources.ms.channel.dto.externaltools.ChannelExternalTool;
import es.onebox.mgmt.datasources.ms.channel.dto.externaltools.ChannelExternalTools;
import es.onebox.mgmt.datasources.ms.channel.dto.notifications.ChannelEmailServer;
import es.onebox.mgmt.datasources.ms.channel.dto.notifications.ChannelEmailTemplates;
import es.onebox.mgmt.datasources.ms.channel.dto.suggestions.ChannelSuggestionMsFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.suggestions.ChannelSuggestions;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.ResetVoucherRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.whitelabelsettings.ChannelWhitelabelSettings;
import es.onebox.mgmt.datasources.ms.channel.enums.SuggestionType;
import es.onebox.mgmt.datasources.ms.event.dto.event.EventSaleRequestChannelFilter;
import es.onebox.mgmt.datasources.ms.event.dto.packs.CreatePack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.CreatePackItems;
import es.onebox.mgmt.datasources.ms.event.dto.packs.CreatePackRate;
import es.onebox.mgmt.datasources.ms.event.dto.packs.Pack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackDetail;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackItem;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackPrice;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackRate;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePackItem;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePackPrice;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePackRate;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSaleRequestDetail;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductSaleRequests;
import es.onebox.mgmt.datasources.ms.event.dto.products.SearchProductSaleRequestFilter;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductSaleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChannelsRepository {

    private final MsChannelDatasource msChannelDatasource;

    @Autowired
    public ChannelsRepository(MsChannelDatasource msChannelDatasource) {
        this.msChannelDatasource = msChannelDatasource;
    }

    public ChannelsResponse getChannels(Long userOperatorId, ChannelFilter filter) {
        return msChannelDatasource.getChannels(userOperatorId, filter);
    }

    public ChannelResponse getChannel(Long channelId) {
        return msChannelDatasource.getChannel(channelId, null);
    }

    public ChannelResponse getChannel(Long channelId, Boolean includeDeleted) {
        return msChannelDatasource.getChannel(channelId, includeDeleted);
    }

    public IdDTO create(CreateChannel channelDTO) {
        return msChannelDatasource.create(channelDTO);
    }

    public void deleteChannel(Long channelId) {
        msChannelDatasource.deleteChannel(channelId);
    }

    public void updateChannel(Long channelId, ChannelUpdateRequest channelDTO) {
        msChannelDatasource.updateChannel(channelId, channelDTO);
    }

    public ChannelDeliveryMethods getChannelDeliveryMethods(Long channelId) {
        return msChannelDatasource.getChannelDeliveryMethods(channelId);
    }

    public void updateChannelDeliveryMethods(Long channelId, ChannelDeliveryMethods msRequest) {
        msChannelDatasource.updateChannelDeliveryMethods(channelId, msRequest);
    }


    public List<Surcharge> getChannelRanges(Long channelId, List<SurchargeTypeDTO> types) {
        return msChannelDatasource.getChannelRanges(channelId, types, null);
    }

    public void requestChannelEventApproval(Long eventId, Long channelId) {
        msChannelDatasource.requestChannelEventApproval(eventId, channelId);
    }

    public void requestChannelProductApproval(Long productId, Long channelId, Long userId) {
        msChannelDatasource.requestChannelProductApproval(productId, channelId, userId);
    }

    public ProductSaleRequestDetail getProductSaleRequestDetail(Long saleRequestId) {
        return msChannelDatasource.getSaleRequestDetail(saleRequestId);
    }

    public void deleteProductSaleRequest(Long saleRequestId) {
        msChannelDatasource.deleteSaleRequest(saleRequestId);
    }

    public void deleteProductSaleRequestByProductAndChannel(Long productId, Long channelId) {
        msChannelDatasource.deleteProductSaleRequestByProductAndChannel(productId, channelId);
    }

    public void updateProductSaleRequest(Long saleRequestId, UpdateProductSaleRequest updateProductSaleRequest) {
        msChannelDatasource.updateSaleRequest(saleRequestId, updateProductSaleRequest);
    }

    public ProductSaleRequests searchProductSaleRequests(SearchProductSaleRequestFilter filter) {
        return msChannelDatasource.searchProductSaleRequests(filter);
    }

    public void acceptEventRequest(Long eventId, Long channelId, ChannelAcceptRequest channelAcceptRequestDTO) {
        msChannelDatasource.acceptEventRequest(eventId, channelId, channelAcceptRequestDTO);
    }

    public void updateChannelBookingSettings(Long channelId, BookingSettings bookingSettings) {
        ChannelConfig cc = new ChannelConfig();
        cc.setBookingSettings(bookingSettings);
        msChannelDatasource.updateChannelConfig(channelId, cc);
    }

    public ChannelConfig getChannelConfig(Long channelId) {
        return msChannelDatasource.getChannelConfig(channelId);
    }

    public void updateChannelConfig(Long channelId, ChannelConfig channelConfig) {
        msChannelDatasource.updateChannelConfig(channelId, channelConfig);
    }

    public BookingSettings getChannelBookingSettings(Long channelId) {
        ChannelConfig cc = msChannelDatasource.getChannelConfig(channelId);
        if (cc == null) {
            return null;
        }
        return cc.getBookingSettings();
    }

    public List<IdNameDTO> getEntityFavoriteChannel(Long entityId) {
        return msChannelDatasource.getEntityFavoriteChannel(entityId);
    }

    public void updateEntityFavoriteChannel(Long entityId, Long channelId, UpdateFavoriteChannel body) {
        msChannelDatasource.updateEntityFavoriteChannel(entityId, channelId, body);
    }

    public ChannelEmailTemplates getChannelEmailTemplates(Long channelId) {
        return msChannelDatasource.getChannelEmailTemplates(channelId);
    }

    public void updateChannelEmailTemplates(Long channelId, ChannelEmailTemplates channelEmailTemplates) {
        msChannelDatasource.updateChannelEmailTemplates(channelId, channelEmailTemplates);
    }

    public ChannelExternalTools getChannelExternalTools(Long channelId) {
        return msChannelDatasource.getChannelExternalTools(channelId);
    }

    public void updateChannelExternalTools(Long channelId, ChannelExternalTool msRequest) {
        msChannelDatasource.updateChannelExternalTools(channelId, msRequest);
    }

    public ChannelEmailServer getChannelEmailServer(Long channelId) {
        return msChannelDatasource.getChannelEmailServer(channelId);
    }

    public void updateChannelEmailServer(final Long channelId, final ChannelEmailServer payload) {
        msChannelDatasource.updateChannelEmailServer(channelId, payload);
    }

    public ChannelEvents getChannelEvents(Long channelId, ChannelEventMsFilter request) {
        return msChannelDatasource.getChannelEvents(channelId, request);
    }

    public void updateChannelEvents(Long channelId, ChannelEventsUpdate body) {
        msChannelDatasource.updateChannelEvents(channelId, body);
    }

    public ChannelEvent getChannelEvent(Long channelId, Long eventId) {
        return msChannelDatasource.getChannelEvent(channelId, eventId);
    }

    public void putChannelEvent(Long channelId, Long eventId, ChannelEventUpdate channelEventUpdate) {
        msChannelDatasource.putChannelEvent(channelId, eventId, channelEventUpdate);
    }

    public ChannelSessions getChannelEventSessions(Long channelId, Long eventId, ChannelSessionsMsFilter request) {
        return msChannelDatasource.getChannelEventSessions(channelId, eventId, request);
    }

    public ChannelBlacklistStatus getChannelBlacklistStatus(Long channelId, ChannelBlacklistType type) {
        return msChannelDatasource.getChannelBlacklistStatus(channelId, type);
    }

    public void updateChannelBlacklistStatus(Long channelId, ChannelBlacklistType type, ChannelBlacklistStatus msRequest) {
        msChannelDatasource.updateChannelBlacklistStatus(channelId, type, msRequest);
    }

    public ChannelBlacklistsResponse getChannelBlacklist(Long channelId, ChannelBlacklistType type, ChannelBlacklistFilter filter) {
        return msChannelDatasource.getChannelBlacklists(channelId, type, filter);
    }

    public ChannelBlacklist getChannelBlacklistItem(Long channelId, ChannelBlacklistType type, String value) {
        return msChannelDatasource.getChannelBlacklistItem(channelId, type, value);
    }

    public void createChannelBlacklists(Long channelId, ChannelBlacklistType type, List<ChannelBlacklist> body) {
        msChannelDatasource.createChannelBlacklists(channelId, type, body);
    }

    public void deleteChannelBlacklists(Long channelId, ChannelBlacklistType type, ChannelBlacklistFilter filter) {
        msChannelDatasource.deleteChannelBlacklists(channelId, type, filter);
    }

    public void deleteChannelBlacklistItem(Long channelId, ChannelBlacklistType type, String value) {
        msChannelDatasource.deleteChannelBlacklistItem(channelId, type, value);
    }

    public ChannelVouchers getChannelVouchersConfig(Long channelId) {
        return msChannelDatasource.getChannelVouchersConfig(channelId);
    }

    public ChannelLoyaltyPoints getLoyaltyPoints(Long channelId) {
        return msChannelDatasource.getChannelLoyaltyPoints(channelId);
    }

    public void updateChannelLoyaltyPoints(Long channelId, ChannelLoyaltyPoints body) {
        msChannelDatasource.updateChannelLoyaltyPoints(channelId, body);
    }

    public void updateChannelVouchersConfig(Long channelId, ChannelVouchers body) {
        msChannelDatasource.updateChannelVouchersConfig(channelId, body);
    }

    public ChannelAuthVendor getChannelAuthVendor(Long channelId) {
        return msChannelDatasource.getChannelAuthVendors(channelId);
    }

    public void updateChannelAuthVendor(Long channelId, ChannelAuthVendor body) {
        msChannelDatasource.updateChannelAuthVendors(channelId, body);
    }

    public ChannelEventSaleRestrictionResponse getEventSaleRestrictions(Long channelId) {
        return msChannelDatasource.getEventSaleRestrictions(channelId);
    }

    public void updateEventSaleRestrictions(Long channelId, ChannelEventsSaleRestrictions body) {
        msChannelDatasource.updateEventSaleRestrictions(channelId, body);
    }

    public void resetExternalTool(Long channelId, ChannelExternalToolsNamesDTO toolName) {
        msChannelDatasource.resetExternalTool(channelId, toolName);
    }

    public ChannelSuggestions getSuggestions(Long channelId, ChannelSuggestionMsFilter filter) {
        return msChannelDatasource.getSuggestions(channelId, filter);
    }

    public void addChannelSuggestion(Long channelId, SuggestionType sourceType, Long sourceId, CreateSuggestionTargetRequestDTO createSuggestionTargetRequestDTO) {
        msChannelDatasource.addChannelSuggestion(channelId, sourceType, sourceId, createSuggestionTargetRequestDTO);
    }

    public void deleteSuggestion(Long channelId, SuggestionType sourceType, Long sourceId, SuggestionType targetType, Long targetId) {
        msChannelDatasource.deleteSuggestion(channelId, sourceType, sourceId, targetType, targetId);
    }

    public void deleteSuggestions(Long channelId, SuggestionType sourceType, Long sourceId) {
        msChannelDatasource.deleteSuggestions(channelId, sourceType, sourceId);
    }

    public ChannelWhitelabelSettings getChannelWhitelabelSettings(Long channelId) {
        return msChannelDatasource.getChannelWhitelabelSettings(channelId);
    }

    public void updateChannelWhitelabelSettings(Long channelId, ChannelWhitelabelSettings request) {
        msChannelDatasource.updateChannelWhitelabelSettings(channelId, request);
    }

    public CustomResourcesMsDTO getCustomResources(Integer channelId) {
        return msChannelDatasource.getCustomResources(channelId);
    }

    public void createOrUpdateCustomResources(Integer channelId, UpdateCustomResourcesMsDTO updateCustomResourcesMsDTO) {
        msChannelDatasource.createOrUpdateCustomResource(channelId, updateCustomResourcesMsDTO);
    }

    public List<Pack> getPacks(Long channelId) {
        return msChannelDatasource.getPacks(channelId);
    }

    public PackDetail getPack(Long channelId, Long packId) {
        return msChannelDatasource.getPack(channelId, packId);
    }

    public Pack createPack(Long channelId, CreatePack createPack) {
        return msChannelDatasource.createPack(channelId, createPack);
    }

    public void updatePack(Long channelId, Long packId, UpdatePack updatePack) {
        msChannelDatasource.updatePack(channelId, packId, updatePack);
    }

    public void deletePack(Long channelId, Long packId) {
        msChannelDatasource.deletePack(channelId, packId);
    }

    public List<PackItem> getPackItems(Long channelId, Long packId) {
        return msChannelDatasource.getPackItems(channelId, packId);
    }

    public void createPackItems(Long channelId, Long packId, CreatePackItems createPackItems) {
        msChannelDatasource.createPackItems(channelId, packId, createPackItems);
    }

    public void updatePackItem(Long channelId, Long packId, Long packItemId, UpdatePackItem updatePackItem) {
        msChannelDatasource.updatePackItem(channelId, packId, packItemId, updatePackItem);
    }

    public void deletePackItem(Long channelId, Long packId, Long packItemId) {
        msChannelDatasource.deletePackItem(channelId, packId, packItemId);
    }

    public List<PackRate> getPackRates(Long channelId, Long packId) {
        return msChannelDatasource.getPackRates(channelId, packId);
    }

    public IdDTO createPackRates(Long channelId, Long packId, CreatePackRate createPackRate) {
        return msChannelDatasource.createPackRates(channelId, packId, createPackRate);
    }

    public void refreshPackRates(Long channelId, Long packId) {
        msChannelDatasource.refreshPackRates(channelId, packId);
    }

    public void updatePackRate(Long channelId, Long packId, Long rateId, UpdatePackRate updatePackRate) {
        msChannelDatasource.updatePackRate(channelId, packId, rateId, updatePackRate);
    }

    public void deletePackRate(Long channelId, Long packId, Long rateId) {
        msChannelDatasource.deletePackRate(channelId, packId, rateId);
    }

    public List<PackPrice> getPackPrices(Long channelId, Long packId) {
        return msChannelDatasource.getPackPrices(channelId, packId);
    }

    public void updatePackPrices(Long channelId, Long packId, List<UpdatePackPrice> updatePackPrices) {
        msChannelDatasource.updatePackPrices(channelId, packId, updatePackPrices);
    }

    public CustomResourceAssetsMsDTO getCustomResourceAssets(Integer channelId, CustomResourceAssetsFilterMs filter) {
        return msChannelDatasource.getCustomResourceAssets(channelId, filter);
    }

    public void addCustomResourceAssets(Integer channelId, CreateCustomResourceAssetsMsDTO createCustomResourceAssetsMsDTO) {
        msChannelDatasource.addCustomResourceAssets(channelId, createCustomResourceAssetsMsDTO);
    }

    public void deleteCustomResourceAsset(Integer channelId, String filename) {
        msChannelDatasource.deleteCustomResourceAsset(channelId, filename);
    }

    public AuthConfig getAuthConfig(Long channelId) {
        return msChannelDatasource.getAuthConfig(channelId);
    }

    public void updateAuthConfig(Long channelId, AuthConfig authConfig) {
        msChannelDatasource.updateAuthConfig(channelId, authConfig);
    }

    public void resetVoucher(Long voucherGroupId, ResetVoucherRequest requestBody) {
        msChannelDatasource.resetVoucher(voucherGroupId, requestBody);
    }

    public DomainSettings getChannelDomainSettings(Long channelId) {
        return msChannelDatasource.getChannelDomainSettings(channelId);
    }

    public void upsertChannelDomainSettings(Long channelId, DomainSettings channelDomainSettings) {
        msChannelDatasource.upsertChannelDomainSettings(channelId, channelDomainSettings);
    }

    public void disableChannelDomainSettings(Long channelId) {
        msChannelDatasource.disableChannelDomainSettings(channelId);
    }

    public ChannelCorsSettings getChannelCorsSettings(Long channelId) {
        return msChannelDatasource.getChannelCorsSettings(channelId);
    }

    public void upsertChannelCorsSettings(Long channelId, ChannelCorsSettings channelCorsSettings) {
        msChannelDatasource.upsertChannelCorsSettings(channelId, channelCorsSettings);
    }

    public void disableChannelCorsSettings(Long channelId) {
        msChannelDatasource.disableChannelCorsSettings(channelId);
    }

    public SaleRequestChannelCandidatesResponse getEventSaleRequestChannelsCandidates(EventSaleRequestChannelFilter filter) {
        return msChannelDatasource.getEventSaleRequestChannelsCandidates(filter);
    }

    public PackItemPriceTypesResponse getPackItemPriceTypes(Long channelId, Long packId, Long packItemId) {
        return msChannelDatasource.getPackItemPriceTypes(channelId, packId, packItemId);
    }

    public void updatePackItemPriceTypes(Long channelId, Long packId, Long packItemId, PackItemPriceTypesRequest priceTyepsRequest) {
        msChannelDatasource.updatePackItemPriceTypes(channelId, packId, packItemId, priceTyepsRequest);
    }

    public void acceptPackRequest(Long packId, Long channelId, ChannelAcceptRequest request) {
        msChannelDatasource.acceptPackRequest(packId, channelId);
    }

    public void createPackSaleRequest(Long packId, Long channelId) {
        msChannelDatasource.createPackSaleRequest(packId, channelId);
    }

}
