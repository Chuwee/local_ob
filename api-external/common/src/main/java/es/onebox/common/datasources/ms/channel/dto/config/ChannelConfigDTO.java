package es.onebox.common.datasources.ms.channel.dto.config;

import es.onebox.common.datasources.ms.channel.dto.CodeNameDTO;
import es.onebox.common.datasources.ms.channel.dto.RefundPeriodDTO;
import es.onebox.common.datasources.ms.channel.enums.WhitelabelType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ChannelConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -2048317998800390594L;

    private Long id;
    private String name;
    private String url;
    private Integer channelType;
    private WhitelabelType whitelabelType;
    private String portalBuild;
    private String timeZone;
    private String currency;
    private String currencyISO;
    private String defaultLanguageCode;

    private Boolean usePortal3;
    private Boolean useRest2;
    private Boolean useMultiSessionCart;
    private Boolean useOneboxMailServer;
    private Boolean useBlacklist;
    private Boolean useZopimChat;
    private Boolean useUserLogin;
    private Boolean use3dVenueModule;
    private Boolean use3dVenueModuleV2;
    private Boolean useSeat3dView;
    private Boolean useSector3dView;
    private Boolean showSessionsList;
    private Boolean allowPrintTickets;
    private Boolean allowCommercialMailing;
    private Boolean allowAutomaticSeatSelection;
    private Boolean allowPurchaseRetries;
    private Boolean commercialMailingNegativeAuth;
    private Boolean includeTaxes;
    private Boolean keepSalesCode;
    private Boolean forceLogin;

    private Integer maxTicketsPerSale;
    private Integer maxSessionsInList;
    private Integer defaultView;
    private Integer channelGroupId;

    private List<CodeNameDTO> channelLanguages;
    private Map<String, CodeNameDTO> channelRedirectionPolicy;
    private Map<String, Boolean> channelComponentVisibility;

    private String operator;
    private Long entityId;
    private String entityName;
    private String entityNif;
    private String userName;
    private String userPassword;
    private String userOperator;

    private RefundPeriodDTO refundPeriod;

    private Map<String, Object> additionalProperties;
    private List<CustomGroupValidation> customGroupValidation;
    private CustomPromotionalCodeValidation customPromotionalCodeValidation;
    private String externalReference;

    private String customEventConverter;
    private Boolean v4Enabled;
    private DomainSettings domainSettings;
    private String apiKey;

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

    public WhitelabelType getWhitelabelType() { return whitelabelType; }

    public void setWhitelabelType(WhitelabelType whitelabelType) { this.whitelabelType = whitelabelType; }

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

    public Boolean getUsePortal3() {
        return usePortal3;
    }

    public void setUsePortal3(Boolean usePortal3) {
        this.usePortal3 = usePortal3;
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

    public Boolean getAllowPrintTickets() {
        return allowPrintTickets;
    }

    public void setAllowPrintTickets(Boolean allowPrintTickets) {
        this.allowPrintTickets = allowPrintTickets;
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

    public Boolean getAllowPurchaseRetries() {
        return allowPurchaseRetries;
    }

    public void setAllowPurchaseRetries(Boolean allowPurchaseRetries) {
        this.allowPurchaseRetries = allowPurchaseRetries;
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

    public Map<String, CodeNameDTO> getChannelRedirectionPolicy() {
        return channelRedirectionPolicy;
    }

    public void setChannelRedirectionPolicy(Map<String, CodeNameDTO> channelRedirectionPolicy) {
        this.channelRedirectionPolicy = channelRedirectionPolicy;
    }

    public Map<String, Boolean> getChannelComponentVisibility() {
        return channelComponentVisibility;
    }

    public void setChannelComponentVisibility(Map<String, Boolean> channelComponentVisibility) {
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

    public RefundPeriodDTO getRefundPeriod() {
        return refundPeriod;
    }

    public void setRefundPeriod(RefundPeriodDTO refundPeriod) {
        this.refundPeriod = refundPeriod;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public List<CustomGroupValidation> getCustomGroupValidation() {
        return customGroupValidation;
    }

    public void setCustomGroupValidation(List<CustomGroupValidation> customGroupValidation) {
        this.customGroupValidation = customGroupValidation;
    }

    public CustomPromotionalCodeValidation getCustomPromotionalCodeValidation() {
        return customPromotionalCodeValidation;
    }

    public void setCustomPromotionalCodeValidation(CustomPromotionalCodeValidation customPromotionalCodeValidation) {
        this.customPromotionalCodeValidation = customPromotionalCodeValidation;
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getCustomEventConverter() {
        return customEventConverter;
    }

    public void setCustomEventConverter(String customEventConverter) {
        this.customEventConverter = customEventConverter;
    }

    public Boolean getV4Enabled() {
        return v4Enabled;
    }

    public void setV4Enabled(Boolean v4Enabled) {
        this.v4Enabled = v4Enabled;
    }

    public DomainSettings getDomainSettings() {
        return domainSettings;
    }

    public void setDomainSettings(DomainSettings domainSettings) {
        this.domainSettings = domainSettings;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Long getEntityId() { return entityId; }

    public void setEntityId(Long entityId) { this.entityId = entityId; }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
