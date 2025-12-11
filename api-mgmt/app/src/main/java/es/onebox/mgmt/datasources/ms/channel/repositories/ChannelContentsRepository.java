package es.onebox.mgmt.datasources.ms.channel.repositories;

import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.channels.contents.enums.ChannelVersion;
import es.onebox.mgmt.packs.dto.comelements.PackCommunicationElement;
import es.onebox.mgmt.packs.enums.PackTagType;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.channel.MsChannelDatasource;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelAgreement;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelAuditedTextBlock;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelContentClone;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelFormsResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelLiterals;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelTextBlock;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelTextBlockFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.UpdateChannelProfiledTextBlock;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.UpdateChannelTextBlocks;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.UpdateDefaultChannelForms;
import es.onebox.mgmt.datasources.ms.channel.dto.emailcontents.ChannelPurchaseContent;
import es.onebox.mgmt.datasources.ms.channel.dto.ticketcontent.ChannelTicketContent;
import es.onebox.mgmt.datasources.ms.channel.dto.whatsapptemplates.WhatsappTemplates;
import es.onebox.mgmt.events.enums.TicketCommunicationElementCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.Predicate;

@Repository
public class ChannelContentsRepository {

    private final MsChannelDatasource msChannelDatasource;

    @Autowired
    public ChannelContentsRepository(MsChannelDatasource msChannelDatasource) {
        this.msChannelDatasource = msChannelDatasource;
    }

    public void updateFormsByType(Long channelId, String formType, UpdateDefaultChannelForms body) {
        this.msChannelDatasource.updateFormsByType(channelId, formType, body);
    }

    public ChannelFormsResponse getFormsByType(Long channelId, String formType) {
        return this.msChannelDatasource.getFormsByType(channelId, formType);
    }

    public List<ChannelAgreement> getChannelAgreements(Long channelId) {
        return msChannelDatasource.getChannelAgreements(channelId);
    }

    public IdDTO createChannelAgreement(Long channelId, ChannelAgreement body) {
        return msChannelDatasource.createChannelAgreement(channelId, body);
    }

    public void updateChannelAgreement(Long channelId, Long channelAgreementId, ChannelAgreement body) {
        msChannelDatasource.updateChannelAgreement(channelId, channelAgreementId, body);
    }

    public void deleteChannelAgreement(Long channelId, Long agreementId) {
        msChannelDatasource.deleteChannelAgreement(channelId, agreementId);
    }

    public ChannelLiterals getChannelMasterLiterals(String appName, String languageCode, String key) {
        return this.msChannelDatasource.getChannelMasterLiterals(appName, languageCode, key);
    }

    public ChannelLiterals getChannelLiterals(Long channelId, String languageCode, String key, ChannelVersion channelVersion) {
        return this.msChannelDatasource.getChannelLiterals(channelId, languageCode, key, channelVersion);
    }

    public void createOrUpdateChannelMasterLiterals(String appName, String languageCode, ChannelLiterals body) {
        this.msChannelDatasource.createOrUpdateChannelMasterLiterals(appName, languageCode, body);
    }

    public void createOrUpdateChannelLiterals(Long channelId, String languageCode, ChannelLiterals body, ChannelVersion channelVersion) {
        this.msChannelDatasource.createOrUpdateChannelLiterals(channelId, languageCode, body, channelVersion);
    }

    public List<ChannelTextBlock> getChannelTextBlocks(Long channelId, ChannelTextBlockFilter filter) {
        return this.msChannelDatasource.getChannelTextBlocks(channelId, filter);
    }

    public List<ChannelAuditedTextBlock> getChannelTextBlockHistoricalData(Long channelId, Long blockId, String language) {
        return this.msChannelDatasource.getChannelTextBlocksHistoricalData(channelId, blockId, language);
    }

    public void updateChannelTextBlocks(Long channelId, UpdateChannelTextBlocks body) {
        this.msChannelDatasource.updateChannelTextBlocks(channelId, body);
    }

    public void updateProfiledChannelTextBlocks(Long channelId, Long contentId, List<UpdateChannelProfiledTextBlock> body) {
        this.msChannelDatasource.updateProfiledChannelTextBlocks(channelId, contentId, body);
    }

    public List<ChannelTicketContent> getChannelTicketPDFContent(Long channelId, String language, String type) {
        return this.msChannelDatasource.getChannelTicketPDFContent(channelId, language, type);
    }

    public void updateChannelTicketPDFContent(Long channelId, List<ChannelTicketContent> body) {
        this.msChannelDatasource.updateChannelTicketPDFContent(channelId, body);
    }

    public void deleteChannelTicketPDFContent(Long channelId, String language, String type) {
        this.msChannelDatasource.deleteChannelTicketPDFContent(channelId, language, type);
    }

    public List<ChannelTicketContent> getChannelTicketPassbookContent(Long channelId, String language, String type) {
        return this.msChannelDatasource.getChannelTicketPassbookContent(channelId, language, type);
    }

    public void updateChannelTicketPassbookContent(Long channelId, List<ChannelTicketContent> body) {
        this.msChannelDatasource.updateChannelTicketPassbookContent(channelId, body);
    }

    public void deleteChannelTicketPassbookContent(Long channelId, String language, String type) {
        this.msChannelDatasource.deleteChannelTicketPassbookContent(channelId, language, type);
    }

    public List<ChannelPurchaseContent> getChannelPurchaseContent(Long channelId, String language, List<String> types) {
        return this.msChannelDatasource.getChannelPurchaseContent(channelId, language, types);
    }

    public void updateChannelPurchaseContent(Long channelId, List<ChannelPurchaseContent> body) {
        this.msChannelDatasource.updateChannelPurchaseContent(channelId, body);
    }

    public void deleteChannelPurchaseContent(Long channelId, String language, String type) {
        this.msChannelDatasource.deleteChannelPurchaseContent(channelId, language, type);
    }

    public List<ChannelTicketContent> getChannelTicketPrinterContent(Long channelId, String language, String type) {
        return this.msChannelDatasource.getChannelTicketPrinterContent(channelId, language, type);
    }

    public void updateChannelTicketPrinterContent(Long channelId, List<ChannelTicketContent> body) {
        this.msChannelDatasource.updateChannelTicketPrinterContent(channelId, body);
    }

    public void deleteChannelTicketPrinterContent(Long channelId, String language, String type) {
        this.msChannelDatasource.deleteChannelTicketPrinterContent(channelId, language, type);
    }

    public void cloneTicketContents(Long channelId, ChannelContentClone body) {
        this.msChannelDatasource.cloneChannelTicketContents(channelId, body);
    }

    public void clonePurchaseContents(Long channelId, ChannelContentClone body) {
        this.msChannelDatasource.cloneChannelPurchaseContents(channelId, body);
    }

    public void cloneTextBlocksContents(Long channelId, ChannelContentClone body) {
        this.msChannelDatasource.cloneChannelTextBlocksContents(channelId, body);
    }

    public void cloneTextContents(Long channelId, ChannelContentClone body) {
        this.msChannelDatasource.cloneChannelTextContents(channelId, body);
    }

    public WhatsappTemplates getWhatsappTemplatesContents(Long channelId) {
        return this.msChannelDatasource.getWhatsappTemplatesContents(channelId);
    }

    public List<PackCommunicationElement> getPackCommunicationElements(Long channelId, Long packId, CommunicationElementFilter<PackTagType> filter, Predicate<PackTagType> tagType) {
        ChannelContentsUtils.addPackTagsToFilter(filter, tagType);
        return msChannelDatasource.getPackCommunicationElements(channelId, packId, filter);
    }

    public void updatePackCommunicationElements(Long channelId, Long packId, List<PackCommunicationElement> elements) {
        msChannelDatasource.updatePackCommunicationElements(channelId, packId, elements);
    }

    public List<ChannelTicketContent> getPackTicketContent(Long channelId, Long packId, String language, String type, TicketCommunicationElementCategory category) {
        return msChannelDatasource.getPackTicketContent(channelId, packId, language, type, category);
    }

    public void updatePackTicketContent(Long channelId, Long packId, List<ChannelTicketContent> body, TicketCommunicationElementCategory category) {
        msChannelDatasource.updatePackTicketContent(channelId, packId, body, category);
    }

    public void deletePackTicketContent(Long channelId, Long packId, String language, String type, TicketCommunicationElementCategory category) {
        msChannelDatasource.deletePackTicketContent(channelId, packId, language, type, category);
    }
}
