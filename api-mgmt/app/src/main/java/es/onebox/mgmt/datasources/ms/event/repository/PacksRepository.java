package es.onebox.mgmt.datasources.ms.event.repository;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.PackItemPriceTypesRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.PackItemPriceTypesResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.ticketcontent.ChannelTicketContent;
import es.onebox.mgmt.datasources.ms.event.MsEventDatasource;
import es.onebox.mgmt.datasources.ms.event.dto.packs.CreatePack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.CreatePackItems;
import es.onebox.mgmt.datasources.ms.event.dto.packs.CreatePackRate;
import es.onebox.mgmt.datasources.ms.event.dto.packs.Pack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackDetail;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackItem;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackItemSubItemsResponse;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackPrice;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PackRate;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PacksFilterRequest;
import es.onebox.mgmt.datasources.ms.event.dto.packs.PacksResponse;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePack;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePackItem;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePackPrice;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePackRate;
import es.onebox.mgmt.datasources.ms.event.dto.packs.channel.PackChannel;
import es.onebox.mgmt.datasources.ms.event.dto.packs.channel.PackChannels;
import es.onebox.mgmt.datasources.ms.event.dto.packs.channel.UpdatePackChannel;
import es.onebox.mgmt.events.enums.TicketCommunicationElementCategory;
import es.onebox.mgmt.datasources.ms.event.dto.packs.UpdatePackItemSubitemsRequest;
import es.onebox.mgmt.packs.dto.PackItemSubitemFilterDTO;
import es.onebox.mgmt.packs.dto.comelements.PackCommunicationElement;
import es.onebox.mgmt.packs.enums.PackTagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.Predicate;

@Repository
public class PacksRepository {

    private final MsEventDatasource msEventDatasource;

    @Autowired
    public PacksRepository(MsEventDatasource msEventDatasource) {
        this.msEventDatasource = msEventDatasource;
    }

    public PacksResponse getPacks(PacksFilterRequest request) {
        return msEventDatasource.getPacks(request);
    }

    public PackDetail getPack(Long packId) {
        return msEventDatasource.getPack(packId);
    }

    public Pack createPack(CreatePack createPack) {
        return msEventDatasource.createPack(createPack);
    }

    public void updatePack(Long packId, UpdatePack updatePack) {
        msEventDatasource.updatePack(packId, updatePack);
    }

    public void deletePack(Long packId) {
        msEventDatasource.deletePack(packId);
    }

    public List<PackItem> getPackItems(Long packId) {
        return msEventDatasource.getPackItems(packId);
    }

    public void createPackItems(Long packId, CreatePackItems createPackItems) {
        msEventDatasource.createPackItems(packId, createPackItems);
    }

    public void updatePackItem(Long packId, Long packItemId, UpdatePackItem updatePackItem) {
        msEventDatasource.updatePackItem(packId, packItemId, updatePackItem);
    }

    public void deletePackItem(Long packId, Long packItemId) {
        msEventDatasource.deletePackItem(packId, packItemId);
    }

    public List<PackRate> getPackRates(Long packId) {
        return msEventDatasource.getPackRates(packId);
    }

    public IdDTO createPackRates(Long packId, CreatePackRate createPackRate) {
        return msEventDatasource.createPackRates(packId, createPackRate);
    }

    public void refreshPackRates(Long packId) {
        msEventDatasource.refreshPackRates(packId);
    }

    public void updatePackRate(Long packId, Long rateId, UpdatePackRate updatePackRate) {
        msEventDatasource.updatePackRate(packId, rateId, updatePackRate);
    }

    public void deletePackRate(Long packId, Long rateId) {
        msEventDatasource.deletePackRate(packId, rateId);
    }

    public List<PackPrice> getPackPrices(Long packId) {
        return msEventDatasource.getPackPrices(packId);
    }

    public void updatePackPrices(Long packId, List<UpdatePackPrice> updatePackPrices) {
        msEventDatasource.updatePackPrices(packId, updatePackPrices);
    }

    public PackItemPriceTypesResponse getPackItemPriceTypes(Long packId, Long packItemId) {
        return msEventDatasource.getPackItemPriceTypes(packId, packItemId);
    }

    public void updatePackItemPriceTypes(Long packId, Long packItemId, PackItemPriceTypesRequest priceTyepsRequest) {
        msEventDatasource.updatePackItemPriceTypes(packId, packItemId, priceTyepsRequest);
    }

    public PackItemSubItemsResponse getPackItemSubitems(Long packId, Long packItemId, PackItemSubitemFilterDTO packItemSubitemFilterDTO){
        return msEventDatasource.getPackItemSubitems(packId, packItemId, packItemSubitemFilterDTO);
    }

    public void updatePackItemSubitems(Long packId, Long packItemId, UpdatePackItemSubitemsRequest request) {
        msEventDatasource.updatePackItemSubitems(packId, packItemId, request);
    }

    public List<PackCommunicationElement> getPackCommunicationElements(Long packId, CommunicationElementFilter<PackTagType> filter, Predicate<PackTagType> tagType) {
        ChannelContentsUtils.addPackTagsToFilter(filter, tagType);
        return msEventDatasource.getPackCommunicationElements(packId, filter);
    }

    public void updatePackCommunicationElements(Long packId, List<PackCommunicationElement> elements) {
        msEventDatasource.updatePackCommunicationElements(packId, elements);
    }

    public List<ChannelTicketContent> getPackTicketContent(Long packId, String language, String type, TicketCommunicationElementCategory category) {
        return msEventDatasource.getPackTicketContent(packId, language, type, category);
    }

    public void updatePackTicketContent(Long packId, List<ChannelTicketContent> body, TicketCommunicationElementCategory category) {
        msEventDatasource.updatePackTicketContent(packId, body, category);
    }

    public void deletePackTicketContent(Long packId, String language, String type, TicketCommunicationElementCategory category) {
        msEventDatasource.deletePackTicketContent(packId, language, type, category);
    }

    public PackChannels getPackChannels(Long packId) {
        return msEventDatasource.getPackChannels(packId);
    }

    public PackChannel getPackChannel(Long packId, Long channelId) {
        return msEventDatasource.getPackChannel(packId, channelId);
    }

    public void createPackChannels(Long packId, List<Long> channelId) {
        msEventDatasource.createPackChannel(packId, channelId);
    }

    public void updatePackChannel(Long packId, Long channelId, UpdatePackChannel request) {
        msEventDatasource.updatePackChannel(packId, channelId, request);
    }

    public void deletePackChannel(Long packId, Long channelId) {
        msEventDatasource.deletePackChannel(packId, channelId);
    }

    public void requestChannelApproval(Long packId, Long channelId, Long userId) {
        msEventDatasource.requestPackChannelApproval(packId, channelId, userId);
    }
}
