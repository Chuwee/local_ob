package es.onebox.mgmt.datasources.ms.channel.dto;

import es.onebox.core.serializer.dto.common.CodeNameDTO;
import es.onebox.mgmt.common.interactivevenue.dto.InteractiveVenueType;
import es.onebox.mgmt.customdomains.common.dto.DomainSettings;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelHeaderText;
import es.onebox.mgmt.datasources.ms.channel.enums.WhitelabelType;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ChannelConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 1664058345239081673L;

    private Long id;
    private String name;
    private String url;
    private Integer channelType;
    private WhitelabelType whitelabelType;
    private String whitelabelPath;
    private String portalBuild;
    private String timeZone;
    private String currency;
    private String currencyISO;
    private String defaultLanguageCode;
    private RelatedChannelInfo relatedChannelInfo;
    private String relatedChannel;

    private Boolean usePortal3;
    private Boolean v4Enabled;
    private Boolean v4ConfigEnabled;
    private String v4PreviewToken;
    private Boolean useRest2;
    private Boolean useMultiSessionCart;
    private Boolean useOneboxMailServer;
    private Boolean useBlacklist;
    private Map<String, Boolean> blacklists;

    private Boolean useZopimChat;
    private Boolean useUserLogin;
    private Boolean use3dVenueModule;
    private Boolean use3dVenueModuleV2;
    private Boolean useVenue3dView;
    private List<InteractiveVenueType> interactiveVenueTypes;
    private Boolean useSeat3dView;
    private Boolean useSector3dView;
    private Boolean showSessionsList;
    private Boolean allowCommercialMailing;
    private Boolean allowAutomaticSeatSelection;
    private Boolean allowAutomaticSeatSelectionByPriceZone;
    private Boolean allowPurchaseRetries;
    private Boolean allowRefundRetries;
    private Boolean commercialMailingNegativeAuth;
    private Boolean includeTaxes;
    private Boolean keepSalesCode;
    private Boolean forceLogin;

    private Integer maxTicketsPerSale;
    private Integer maxSessionsInList;
    private Integer defaultView;
    private Integer channelGroupId;

    private List<CodeNameDTO> channelLanguages;
    private Map<String, ChannelPurchaseConfigLinkDestination> channelRedirectionPolicy;
    private Map<ChannelHeaderText, Boolean> channelComponentVisibility;

    private String operator;
    private String entityName;
    private String entityNif;
    private String userName;
    private String userPassword;
    private String userOperator;

    private Boolean allowPrivateAccessThirdParty;
    private Boolean allowAnonymousUser;

    private RefundPeriod refundPeriod;

    private Map<String, Object> additionalProperties;

    private BookingSettings bookingSettings;

    private CustomPromotionalCodeValidation customPromotionalCodeValidation;
    private VirtualQueueConfig virtualQueueConfig;
    private SharingSettings sharingSettings;
    private Boolean showAcceptAllOption;
    private Boolean allowDownloadPassbook;
    private Boolean allowB2BPublishing;
    private InvitationsSettings invitationsSettings;
    private SupportEmail supportEmail;
    private DomainSettings domainSettings;
    private WhatsappConfig whatsappConfig;

    private ChannelInvoiceSettings invoiceSettings;
    private LoyaltyProgram loyaltyProgram;
    private Boolean allowPriceTypeTagFilter;
    private PriceDisplay priceDisplaySettings;

    private Boolean hasDestinationChannel;
    private String destinationChannel;
    private String destinationChannelType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getChannelType() {
        return channelType;
    }

    public void setChannelType(Integer channelType) {
        this.channelType = channelType;
    }

    public WhitelabelType getWhitelabelType() {
        return whitelabelType;
    }

    public void setWhitelabelType(WhitelabelType whitelabelType) {
        this.whitelabelType = whitelabelType;
    }

    public String getWhitelabelPath() {
        return whitelabelPath;
    }

    public void setWhitelabelPath(String whitelabelPath) {
        this.whitelabelPath = whitelabelPath;
    }

    public String getPortalBuild() {
        return portalBuild;
    }

    public void setPortalBuild(String portalBuild) {
        this.portalBuild = portalBuild;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCurrencyISO() {
        return currencyISO;
    }

    public void setCurrencyISO(String currencyISO) {
        this.currencyISO = currencyISO;
    }

    public String getDefaultLanguageCode() {
        return defaultLanguageCode;
    }

    public void setDefaultLanguageCode(String defaultLanguageCode) {
        this.defaultLanguageCode = defaultLanguageCode;
    }

    public String getRelatedChannel() {
        return relatedChannel;
    }

    public void setRelatedChannel(String relatedChannel) {
        this.relatedChannel = relatedChannel;
    }

    public RelatedChannelInfo getRelatedChannelInfo() {
        return relatedChannelInfo;
    }

    public void setRelatedChannelInfo(RelatedChannelInfo relatedChannelInfo) {
        this.relatedChannelInfo = relatedChannelInfo;
    }

    public Boolean getUsePortal3() {
        return usePortal3;
    }

    public void setUsePortal3(Boolean usePortal3) {
        this.usePortal3 = usePortal3;
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

    public Boolean getUseRest2() {
        return useRest2;
    }

    public void setUseRest2(Boolean useRest2) {
        this.useRest2 = useRest2;
    }

    public Boolean getUseMultiSessionCart() {
        return useMultiSessionCart;
    }

    public void setUseMultiSessionCart(Boolean useMultiSessionCart) {
        this.useMultiSessionCart = useMultiSessionCart;
    }

    public Boolean getUseOneboxMailServer() {
        return useOneboxMailServer;
    }

    public void setUseOneboxMailServer(Boolean useOneboxMailServer) {
        this.useOneboxMailServer = useOneboxMailServer;
    }

    public Boolean getUseBlacklist() {
        return useBlacklist;
    }

    public void setUseBlacklist(Boolean useBlacklist) {
        this.useBlacklist = useBlacklist;
    }

    public Map<String, Boolean> getBlacklists() {
        return blacklists;
    }

    public void setBlacklists(Map<String, Boolean> blacklists) {
        this.blacklists = blacklists;
    }

    public Boolean getUseZopimChat() {
        return useZopimChat;
    }

    public void setUseZopimChat(Boolean useZopimChat) {
        this.useZopimChat = useZopimChat;
    }

    public Boolean getUseUserLogin() {
        return useUserLogin;
    }

    public void setUseUserLogin(Boolean useUserLogin) {
        this.useUserLogin = useUserLogin;
    }

    public Boolean getUse3dVenueModule() {
        return use3dVenueModule;
    }

    public void setUse3dVenueModule(Boolean use3dVenueModule) {
        this.use3dVenueModule = use3dVenueModule;
    }

    public Boolean getUse3dVenueModuleV2() {
        return use3dVenueModuleV2;
    }

    public void setUse3dVenueModuleV2(Boolean use3dVenueModuleV2) {
        this.use3dVenueModuleV2 = use3dVenueModuleV2;
    }

    public Boolean getUseVenue3dView() {
        return useVenue3dView;
    }

    public void setUseVenue3dView(Boolean useVenue3dView) {
        this.useVenue3dView = useVenue3dView;
    }

    public List<InteractiveVenueType> getInteractiveVenueTypes() {
        return interactiveVenueTypes;
    }

    public void setInteractiveVenueTypes(List<InteractiveVenueType> interactiveVenueTypes) {
        this.interactiveVenueTypes = interactiveVenueTypes;
    }

    public Boolean getUseSeat3dView() {
        return useSeat3dView;
    }

    public void setUseSeat3dView(Boolean useSeat3dView) {
        this.useSeat3dView = useSeat3dView;
    }

    public Boolean getUseSector3dView() {
        return useSector3dView;
    }

    public void setUseSector3dView(Boolean useSector3dView) {
        this.useSector3dView = useSector3dView;
    }

    public Boolean getShowSessionsList() {
        return showSessionsList;
    }

    public void setShowSessionsList(Boolean showSessionsList) {
        this.showSessionsList = showSessionsList;
    }

    public Boolean getAllowCommercialMailing() {
        return allowCommercialMailing;
    }

    public void setAllowCommercialMailing(Boolean allowCommercialMailing) {
        this.allowCommercialMailing = allowCommercialMailing;
    }

    public Boolean getAllowAutomaticSeatSelection() {
        return allowAutomaticSeatSelection;
    }

    public void setAllowAutomaticSeatSelection(Boolean allowAutomaticSeatSelection) {
        this.allowAutomaticSeatSelection = allowAutomaticSeatSelection;
    }

    public Boolean getAllowAutomaticSeatSelectionByPriceZone() {
        return allowAutomaticSeatSelectionByPriceZone;
    }

    public void setAllowAutomaticSeatSelectionByPriceZone(Boolean allowAutomaticSeatSelectionByPriceZone) {
        this.allowAutomaticSeatSelectionByPriceZone = allowAutomaticSeatSelectionByPriceZone;
    }

    public Boolean getAllowPurchaseRetries() {
        return allowPurchaseRetries;
    }

    public void setAllowPurchaseRetries(Boolean allowPurchaseRetries) {
        this.allowPurchaseRetries = allowPurchaseRetries;
    }

    public Boolean getAllowRefundRetries() {
        return allowRefundRetries;
    }

    public void setAllowRefundRetries(Boolean allowRefundRetries) {
        this.allowRefundRetries = allowRefundRetries;
    }

    public Boolean getCommercialMailingNegativeAuth() {
        return commercialMailingNegativeAuth;
    }

    public void setCommercialMailingNegativeAuth(Boolean commercialMailingNegativeAuth) {
        this.commercialMailingNegativeAuth = commercialMailingNegativeAuth;
    }

    public Boolean getIncludeTaxes() {
        return includeTaxes;
    }

    public void setIncludeTaxes(Boolean includeTaxes) {
        this.includeTaxes = includeTaxes;
    }

    public Boolean getKeepSalesCode() {
        return keepSalesCode;
    }

    public void setKeepSalesCode(Boolean keepSalesCode) {
        this.keepSalesCode = keepSalesCode;
    }

    public Boolean getForceLogin() {
        return forceLogin;
    }

    public void setForceLogin(Boolean forceLogin) {
        this.forceLogin = forceLogin;
    }

    public Integer getMaxTicketsPerSale() {
        return maxTicketsPerSale;
    }

    public void setMaxTicketsPerSale(Integer maxTicketsPerSale) {
        this.maxTicketsPerSale = maxTicketsPerSale;
    }

    public Integer getMaxSessionsInList() {
        return maxSessionsInList;
    }

    public void setMaxSessionsInList(Integer maxSessionsInList) {
        this.maxSessionsInList = maxSessionsInList;
    }

    public Integer getDefaultView() {
        return defaultView;
    }

    public void setDefaultView(Integer defaultView) {
        this.defaultView = defaultView;
    }

    public Integer getChannelGroupId() {
        return channelGroupId;
    }

    public void setChannelGroupId(Integer channelGroupId) {
        this.channelGroupId = channelGroupId;
    }

    public List<CodeNameDTO> getChannelLanguages() {
        return channelLanguages;
    }

    public void setChannelLanguages(List<CodeNameDTO> channelLanguages) {
        this.channelLanguages = channelLanguages;
    }

    public Map<String, ChannelPurchaseConfigLinkDestination> getChannelRedirectionPolicy() {
        return channelRedirectionPolicy;
    }

    public void setChannelRedirectionPolicy(Map<String, ChannelPurchaseConfigLinkDestination> channelRedirectionPolicy) {
        this.channelRedirectionPolicy = channelRedirectionPolicy;
    }

    public Map<ChannelHeaderText, Boolean> getChannelComponentVisibility() {
        return channelComponentVisibility;
    }

    public void setChannelComponentVisibility(Map<ChannelHeaderText, Boolean> channelComponentVisibility) {
        this.channelComponentVisibility = channelComponentVisibility;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityNif() {
        return entityNif;
    }

    public void setEntityNif(String entityNif) {
        this.entityNif = entityNif;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserOperator() {
        return userOperator;
    }

    public void setUserOperator(String userOperator) {
        this.userOperator = userOperator;
    }

    public Boolean getAllowPrivateAccessThirdParty() {
        return allowPrivateAccessThirdParty;
    }

    public void setAllowPrivateAccessThirdParty(Boolean allowPrivateAccessThirdParty) {
        this.allowPrivateAccessThirdParty = allowPrivateAccessThirdParty;
    }

    public Boolean getAllowAnonymousUser() {
        return allowAnonymousUser;
    }

    public void setAllowAnonymousUser(Boolean allowAnonymousUser) {
        this.allowAnonymousUser = allowAnonymousUser;
    }

    public RefundPeriod getRefundPeriod() {
        return refundPeriod;
    }

    public void setRefundPeriod(RefundPeriod refundPeriod) {
        this.refundPeriod = refundPeriod;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public BookingSettings getBookingSettings() {
        return bookingSettings;
    }

    public void setBookingSettings(BookingSettings bookingSettings) {
        this.bookingSettings = bookingSettings;
    }

    public CustomPromotionalCodeValidation getCustomPromotionalCodeValidation() {
        return customPromotionalCodeValidation;
    }

    public void setCustomPromotionalCodeValidation(CustomPromotionalCodeValidation customPromotionalCodeValidation) {
        this.customPromotionalCodeValidation = customPromotionalCodeValidation;
    }

    public VirtualQueueConfig getVirtualQueueConfig() {
        return virtualQueueConfig;
    }

    public void setVirtualQueueConfig(VirtualQueueConfig virtualQueueConfig) {
        this.virtualQueueConfig = virtualQueueConfig;
    }

    public SharingSettings getSharingSettings() {
        return sharingSettings;
    }

    public void setSharingSettings(SharingSettings sharingSettings) {
        this.sharingSettings = sharingSettings;
    }

    public Boolean getShowAcceptAllOption() {
        return showAcceptAllOption;
    }

    public void setShowAcceptAllOption(Boolean showAcceptAllOption) {
        this.showAcceptAllOption = showAcceptAllOption;
    }

    public Boolean getAllowDownloadPassbook() {
        return allowDownloadPassbook;
    }

    public void setAllowDownloadPassbook(Boolean allowDownloadPassbook) {
        this.allowDownloadPassbook = allowDownloadPassbook;
    }
    public Boolean getAllowB2BPublishing() { return allowB2BPublishing; }

    public void setAllowB2BPublishing(Boolean allowB2BPublishing) {
        this.allowB2BPublishing = allowB2BPublishing;
    }

    public InvitationsSettings getInvitationsSettings() {
        return invitationsSettings;
    }

    public void setInvitationSettings(InvitationsSettings invitationsSettings) {
        this.invitationsSettings = invitationsSettings;
    }

    public String getV4PreviewToken() {
        return v4PreviewToken;
    }

    public void setV4PreviewToken(String v4PreviewToken) {
        this.v4PreviewToken = v4PreviewToken;
    }

    public void setInvitationsSettings(InvitationsSettings invitationsSettings) {
        this.invitationsSettings = invitationsSettings;
    }

    public SupportEmail getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(SupportEmail supportEmail) {
        this.supportEmail = supportEmail;
    }

    public DomainSettings getDomainSettings() {
        return domainSettings;
    }

    public void setDomainSettings(DomainSettings domainSettings) {
        this.domainSettings = domainSettings;
    }

    public WhatsappConfig getWhatsappConfig() {
        return whatsappConfig;
    }

    public void setWhatsappConfig(WhatsappConfig whatsappConfig) {
        this.whatsappConfig = whatsappConfig;
    }

    public ChannelInvoiceSettings getInvoiceSettings() {
        return invoiceSettings;
    }

    public void setInvoiceSettings(ChannelInvoiceSettings invoiceSettings) {
        this.invoiceSettings = invoiceSettings;
    }

    public LoyaltyProgram getLoyaltyProgram() { return loyaltyProgram; }

    public void setLoyaltyProgram(LoyaltyProgram loyaltyProgram) { this.loyaltyProgram = loyaltyProgram; }

    public Boolean getAllowPriceTypeTagFilter() {
        return allowPriceTypeTagFilter;
    }

    public void setAllowPriceTypeTagFilter(Boolean allowPriceTypeTagFilter) {
        this.allowPriceTypeTagFilter = allowPriceTypeTagFilter;
    }

    public PriceDisplay getPriceDisplaySettings() {
        return priceDisplaySettings;
    }

    public void setPriceDisplaySettings(PriceDisplay priceDisplaySettings) {
        this.priceDisplaySettings = priceDisplaySettings;
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
}
