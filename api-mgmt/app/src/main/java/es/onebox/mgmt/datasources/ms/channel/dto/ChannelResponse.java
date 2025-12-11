package es.onebox.mgmt.datasources.ms.channel.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.channels.enums.ChannelSendEmailMode;
import es.onebox.mgmt.channels.enums.CustomerAssignationMode;
import es.onebox.mgmt.datasources.ms.channel.dto.donations.DonationsConfig;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelBuild;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelIntegrationStatus;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelMode;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelScope;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelStatus;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelSubtype;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelType;
import es.onebox.mgmt.datasources.ms.channel.enums.WhitelabelType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.util.List;

public class ChannelResponse extends IdNameDTO {

    @Serial
    private static final long serialVersionUID = 1506028515353465985L;

    private Boolean channelPublic;
    private ChannelBuild build;
    private Long entityId;
    private String entityName;
    private String entityLogo;
    private Long operatorId;
    private String domain;
    private String url;
    private ChannelType type;
    private ChannelSubtype subtype;
    private WhitelabelType whitelabelType;
    private Boolean forceSquarePictures;
    private String whitelabelPath;
    private ChannelStatus status;
    private ChannelIntegrationStatus integrationStatus;
    private ChannelMode mode;
    private ChannelScope scope;
    private Language languages;
    private Boolean useMultiEvent;
    private String contactName;
    private String contactSurname;
    private String contactEmail;
    private String contactPhone;
    private String contactWeb;
    private String contactEntityOwner;
    private String contactEntityManager;
    private String contactJobPosition;
    private Integer ticketPurchaseMax;
    private Integer ticketBookingMax;
    private Integer ticketIssueMax;
    private Boolean automaticSeatSelection;
    private Boolean automaticSeatSelectionByPriceZone;
    private Boolean enableB2B;
    private Boolean allowB2BPublishing;
    private Boolean enableB2BEventCategoryFilter;
    private Boolean hasActivePromotion;
    private InvitationsSettings invitationsSettings;
    private Boolean allowDataProtectionFields;
    private ChannelSendEmailMode channelSendEmailMode;
    private SurchargesSettings surchargesSettings;
    private Boolean allowLinkedCustomers;
    private String passbookTemplate;
    private VirtualQueueConfig virtualQueueConfig;
    private Boolean useRobotIndexation;
    private Boolean robotsNoFollow;
    private List<Long> currencies;
    private DonationsConfig donationsConfig;
    private Boolean v4Enabled;
    private Boolean v4ConfigEnabled;
    private String v4PreviewToken;
    private Boolean allowDownloadPassbook;
    private SupportEmail supportEmail;
    private WhatsappConfig whatsappConfig;
    private Boolean activePromotion;
    private Integer idReceiptTemplate;
    private Boolean useCurrencyExchange;
    private String currencyDefaultExchange;
    private Boolean showCustomerAssignation;
    private CustomerAssignationMode customerAssignationMode;
    private Boolean enablePacksAndEventsCatalog;
    private Boolean hasDestinationChannel;
    private String destinationChannel;
    private String destinationChannelType;

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Boolean getChannelPublic() {
        return channelPublic;
    }

    public void setChannelPublic(Boolean channelPublic) {
        this.channelPublic = channelPublic;
    }

    public String getContactSurname() {
        return contactSurname;
    }

    public void setContactSurname(String contactSurname) {
        this.contactSurname = contactSurname;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactWeb() {
        return contactWeb;
    }

    public void setContactWeb(String contactWeb) {
        this.contactWeb = contactWeb;
    }

    public String getContactEntityOwner() {
        return contactEntityOwner;
    }

    public void setContactEntityOwner(String contactEntityOwner) {
        this.contactEntityOwner = contactEntityOwner;
    }

    public String getContactEntityManager() {
        return contactEntityManager;
    }

    public void setContactEntityManager(String contactEntityManager) {
        this.contactEntityManager = contactEntityManager;
    }

    public String getContactJobPosition() {
        return contactJobPosition;
    }

    public void setContactJobPosition(String contactJobPosition) {
        this.contactJobPosition = contactJobPosition;
    }

    public void setStatus(ChannelStatus status) {
        this.status = status;
    }

    public ChannelStatus getStatus() {
        return status;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getEntityId() {
        return entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ChannelType getType() {
        return type;
    }

    public void setType(ChannelType type) {
        this.type = type;
    }

    public ChannelIntegrationStatus getIntegrationStatus() {
        return integrationStatus;
    }

    public void setIntegrationStatus(ChannelIntegrationStatus integrationStatus) {
        this.integrationStatus = integrationStatus;
    }

    public ChannelMode getMode() {
        return mode;
    }

    public void setMode(ChannelMode mode) {
        this.mode = mode;
    }

    public ChannelScope getScope() {
        return scope;
    }

    public void setScope(ChannelScope scope) {
        this.scope = scope;
    }

    public Language getLanguages() {
        return languages;
    }

    public void setLanguages(Language languages) {
        this.languages = languages;
    }

    public Boolean getUseMultiEvent() {
        return useMultiEvent;
    }

    public void setUseMultiEvent(Boolean useMultiEvent) {
        this.useMultiEvent = useMultiEvent;
    }

    public ChannelSubtype getSubtype() {
        return subtype;
    }

    public void setSubtype(ChannelSubtype subtype) {
        this.subtype = subtype;
    }

    public WhitelabelType getWhitelabelType() {
        return whitelabelType;
    }

    public void setWhitelabelType(WhitelabelType whitelabelType) {
        this.whitelabelType = whitelabelType;
    }

    public Boolean getForceSquarePictures() {return forceSquarePictures;}

    public void setForceSquarePictures(Boolean forceSquarePictures) {this.forceSquarePictures = forceSquarePictures;}

    public String getWhitelabelPath() {
        return whitelabelPath;
    }

    public void setWhitelabelPath(String whitelabelPath) {
        this.whitelabelPath = whitelabelPath;
    }

    public ChannelBuild getBuild() {
        return build;
    }

    public void setBuild(ChannelBuild build) {
        this.build = build;
    }

    public ChannelSendEmailMode getChannelSendEmailMode() {
        return channelSendEmailMode;
    }

    public void setChannelSendEmailMode(ChannelSendEmailMode channelSendEmailMode) {
        this.channelSendEmailMode = channelSendEmailMode;
    }

    public String getEntityLogo() {
        return entityLogo;
    }

    public void setEntityLogo(String entityLogo) {
        this.entityLogo = entityLogo;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getTicketPurchaseMax() {
        return ticketPurchaseMax;
    }

    public void setTicketPurchaseMax(Integer ticketPurchaseMax) {
        this.ticketPurchaseMax = ticketPurchaseMax;
    }

    public Integer getTicketBookingMax() {
        return ticketBookingMax;
    }

    public void setTicketBookingMax(Integer ticketBookingMax) {
        this.ticketBookingMax = ticketBookingMax;
    }

    public Integer getTicketIssueMax() {
        return ticketIssueMax;
    }

    public void setTicketIssueMax(Integer ticketIssueMax) {
        this.ticketIssueMax = ticketIssueMax;
    }


    public Boolean getAutomaticSeatSelection() {
        return automaticSeatSelection;
    }

    public void setAutomaticSeatSelection(Boolean automaticSeatSelection) {
        this.automaticSeatSelection = automaticSeatSelection;
    }

    public Boolean getAutomaticSeatSelectionByPriceZone() {
        return automaticSeatSelectionByPriceZone;
    }

    public void setAutomaticSeatSelectionByPriceZone(Boolean automaticSeatSelectionByPriceZone) {
        this.automaticSeatSelectionByPriceZone = automaticSeatSelectionByPriceZone;
    }

    public Boolean getAllowDataProtectionFields() {
        return allowDataProtectionFields;
    }

    public void setAllowDataProtectionFields(Boolean allowDataProtectionFields) {
        this.allowDataProtectionFields = allowDataProtectionFields;
    }

    public Boolean getEnableB2B() {
        return enableB2B;
    }

    public void setEnableB2B(Boolean enableB2B) {
        this.enableB2B = enableB2B;
    }

    public Boolean getAllowB2BPublishing() {
        return allowB2BPublishing;
    }

    public void setAllowB2BPublishing(Boolean allowB2BPublishing) {
        this.allowB2BPublishing = allowB2BPublishing;
    }

    public Boolean getEnableB2BEventCategoryFilter() {
        return enableB2BEventCategoryFilter;
    }

    public void setEnableB2BEventCategoryFilter(Boolean enableB2BEventCategoryFilter) {
        this.enableB2BEventCategoryFilter = enableB2BEventCategoryFilter;
    }

    public Boolean getHasActivePromotion() {
        return hasActivePromotion;
    }

    public void setHasActivePromotion(Boolean hasActivePromotion) {
        this.hasActivePromotion = hasActivePromotion;
    }

    public InvitationsSettings getInvitationsSettings() {
        return invitationsSettings;
    }

    public void setInvitationsSettings(InvitationsSettings invitationsSettings) {
        this.invitationsSettings = invitationsSettings;
    }

    public SurchargesSettings getSurchargesSettings() {
        return surchargesSettings;
    }

    public void setSurchargesSettings(SurchargesSettings surchargesSettings) {
        this.surchargesSettings = surchargesSettings;
    }

    public Boolean getAllowLinkedCustomers() {
        return allowLinkedCustomers;
    }

    public void setAllowLinkedCustomers(Boolean allowLinkedCustomers) {
        this.allowLinkedCustomers = allowLinkedCustomers;
    }

    public String getPassbookTemplate() {
        return passbookTemplate;
    }

    public void setPassbookTemplate(String passbookTemplate) {
        this.passbookTemplate = passbookTemplate;
    }

    public VirtualQueueConfig getVirtualQueueConfig() {
        return virtualQueueConfig;
    }

    public void setVirtualQueueConfig(VirtualQueueConfig virtualQueueConfig) {
        this.virtualQueueConfig = virtualQueueConfig;
    }

    public Boolean getUseRobotIndexation() {
        return useRobotIndexation;
    }

    public Boolean getRobotsNoFollow() {
        return robotsNoFollow;
    }

    public void setRobotsNoFollow(Boolean robotsNoFollow) {
        this.robotsNoFollow = robotsNoFollow;
    }

    public void setUseRobotIndexation(Boolean useRobotIndexation) {
        this.useRobotIndexation = useRobotIndexation;
    }

    public List<Long> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Long> currencies) {
        this.currencies = currencies;
    }

    public DonationsConfig getDonationsConfig() {
        return donationsConfig;
    }

    public void setDonationsConfig(DonationsConfig donationsConfig) {
        this.donationsConfig = donationsConfig;
    }

    public Boolean getV4Enabled() {
        return v4Enabled;
    }

    public void setV4Enabled(Boolean v4Enabled) {
        this.v4Enabled = v4Enabled;
    }

    public Boolean getV4ConfigEnabled() {
        return v4ConfigEnabled;
    }

    public void setV4ConfigEnabled(Boolean v4ConfigEnabled) {
        this.v4ConfigEnabled = v4ConfigEnabled;
    }

    public String getV4PreviewToken() {
        return v4PreviewToken;
    }

    public void setV4PreviewToken(String v4PreviewToken) {
        this.v4PreviewToken = v4PreviewToken;
    }

    public Boolean getAllowDownloadPassbook() {
        return allowDownloadPassbook;
    }

    public void setAllowDownloadPassbook(Boolean allowDownloadPassbook) {
        this.allowDownloadPassbook = allowDownloadPassbook;
    }

    public SupportEmail getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(SupportEmail supportEmail) {
        this.supportEmail = supportEmail;
    }

    public WhatsappConfig getWhatsappConfig() {
        return whatsappConfig;
    }

    public void setWhatsappConfig(WhatsappConfig whatsappConfig) {
        this.whatsappConfig = whatsappConfig;
    }

    public Boolean getActivePromotion() {
        return activePromotion;
    }

    public void setActivePromotion(Boolean activePromotion) {
        this.activePromotion = activePromotion;
    }

    public Integer getIdReceiptTemplate() {
        return idReceiptTemplate;
    }

    public void setIdReceiptTemplate(Integer idReceiptTemplate) {
        this.idReceiptTemplate = idReceiptTemplate;
    }

    public Boolean getUseCurrencyExchange() {
        return useCurrencyExchange;
    }

    public void setUseCurrencyExchange(Boolean useCurrencyExchange) {
        this.useCurrencyExchange = useCurrencyExchange;
    }

    public String getCurrencyDefaultExchange() {
        return currencyDefaultExchange;
    }

    public void setCurrencyDefaultExchange(String currencyDefaultExchange) {
        this.currencyDefaultExchange = currencyDefaultExchange;
    }

    public Boolean getShowCustomerAssignation() {
        return showCustomerAssignation;
    }

    public void setShowCustomerAssignation(Boolean showCustomerAssignation) {
        this.showCustomerAssignation = showCustomerAssignation;
    }

    public CustomerAssignationMode getCustomerAssignationMode() {
        return customerAssignationMode;
    }

    public void setCustomerAssignationMode(CustomerAssignationMode customerAssignationMode) {
        this.customerAssignationMode = customerAssignationMode;
    }

    public Boolean getEnablePacksAndEventsCatalog() {
        return enablePacksAndEventsCatalog;
    }

    public void setEnablePacksAndEventsCatalog(Boolean enablePacksAndEventsCatalog) {
        this.enablePacksAndEventsCatalog = enablePacksAndEventsCatalog;
    }

    public Boolean hasDestinationChannel() {
        return hasDestinationChannel;
    }

    public void setHasDestinationChannel(Boolean hasDestinationChannel) {
        this.hasDestinationChannel = hasDestinationChannel;
    }

    public String getDestinationChannel() {
        return destinationChannel;
    }

    public void setDestinationChannel(String destinationChannel) {
        this.destinationChannel = destinationChannel;
    }

    public String getDestinationChannelType() {
        return destinationChannelType;
    }

    public void setDestinationChannelType(String destinationChannelType) {
        this.destinationChannelType = destinationChannelType;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
