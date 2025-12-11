package es.onebox.mgmt.datasources.ms.channel.dto;

import es.onebox.mgmt.channels.dto.SurchargesSettingsDTO;
import es.onebox.mgmt.channels.enums.CustomerAssignationMode;
import es.onebox.mgmt.channels.enums.WhitelabelType;
import es.onebox.mgmt.datasources.ms.channel.dto.donations.DonationsConfig;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelBuild;
import es.onebox.mgmt.datasources.ms.channel.enums.ChannelStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelUpdateRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -2007222388549990610L;

    private String name;
    private List<Long> currencies;
    private Boolean channelPublic;
    private Boolean useMultiEvent;
    private ChannelStatus status;
    private Language languages;
    private ChannelBuild build;
    private String contactEntityManager;
    private String contactEntityOwner;
    private String contactWeb;
    private String contactName;
    private String contactSurname;
    private String contactJobPosition;
    private String contactEmail;
    private String contactPhone;
    private Integer ticketPurchaseMax;
    private Integer ticketBookingMax;
    private Integer ticketIssueMax;
    private Boolean automaticSeatSelection;
    private Boolean automaticSeatSelectionByPriceZone;
    private Boolean enableB2B;
    private SurchargesSettingsDTO surchargesSettings;
    private Boolean allowVouchers;
    private Boolean allowDataProtectionFields;
    private Boolean allowLinkedCustomers;
    private String passbookTemplate;
    private VirtualQueueConfig virtualQueueConfig;
    private Boolean useRobotIndexation;
    private Boolean allowB2BPublishing;
    private Boolean enableB2BEventCategoryFilter;
    private InvitationsSettings invitationsSettings;
    private SupportEmail supportEmail;
    private DonationsConfig donationsConfig;
    private WhatsappConfig whatsappConfig;
    private String domain;
    private Integer idReceiptTemplate;
    private Boolean useCurrencyExchange;
    private WhitelabelType whitelabelType;
    private Boolean forceSquarePictures;
    private String whitelabelPath;
    private String currencyDefaultExchange;
    private Boolean showCustomerAssignation;
    private Boolean enablePacksAndEventsCatalog;
    private Boolean robotsNoFollow;
    private CustomerAssignationMode customerAssignationMode;
    private Boolean hasDestinationChannel;
    private String destinationChannel;
    private String destinationChannelType;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getChannelPublic() {
        return channelPublic;
    }

    public void setChannelPublic(Boolean channelPublic) {
        this.channelPublic = channelPublic;
    }

    public Boolean getUseMultiEvent() {
        return useMultiEvent;
    }

    public void setUseMultiEvent(Boolean useMultiEvent) {
        this.useMultiEvent = useMultiEvent;
    }

    public ChannelStatus getStatus() {
        return status;
    }

    public void setStatus(ChannelStatus status) {
        this.status = status;
    }

    public Language getLanguages() {
        return languages;
    }

    public void setLanguages(Language languages) {
        this.languages = languages;
    }

    public ChannelBuild getBuild() {
        return build;
    }

    public void setBuild(ChannelBuild build) {
        this.build = build;
    }

    public String getContactEntityManager() {
        return contactEntityManager;
    }

    public void setContactEntityManager(String contactEntityManager) {
        this.contactEntityManager = contactEntityManager;
    }

    public String getContactEntityOwner() {
        return contactEntityOwner;
    }

    public void setContactEntityOwner(String contactEntityOwner) {
        this.contactEntityOwner = contactEntityOwner;
    }

    public String getContactWeb() {
        return contactWeb;
    }

    public void setContactWeb(String contactWeb) {
        this.contactWeb = contactWeb;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactSurname() {
        return contactSurname;
    }

    public void setContactSurname(String contactSurname) {
        this.contactSurname = contactSurname;
    }

    public String getContactJobPosition() {
        return contactJobPosition;
    }

    public void setContactJobPosition(String contactJobPosition) {
        this.contactJobPosition = contactJobPosition;
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

    public Boolean getEnableB2B() {
        return enableB2B;
    }

    public void setEnableB2B(Boolean enableB2B) {
        this.enableB2B = enableB2B;
    }


    public SurchargesSettingsDTO getSurchargesSettings() {
        return surchargesSettings;
    }

    public void setSurchargesSettings(SurchargesSettingsDTO surchargesSettings) {
        this.surchargesSettings = surchargesSettings;
    }

    public Boolean getAllowVouchers() {
        return allowVouchers;
    }

    public void setAllowVouchers(Boolean allowVouchers) {
        this.allowVouchers = allowVouchers;
    }

    public Boolean getAllowDataProtectionFields() {
        return allowDataProtectionFields;
    }

    public void setAllowDataProtectionFields(Boolean allowDataProtectionFields) {
        this.allowDataProtectionFields = allowDataProtectionFields;
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

    public void setUseRobotIndexation(Boolean useRobotIndexation) {
        this.useRobotIndexation = useRobotIndexation;
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

    public InvitationsSettings getInvitationsSettings() {
        return invitationsSettings;
    }

    public void setInvitationsSettings(InvitationsSettings invitationsSettings) {
        this.invitationsSettings = invitationsSettings;
    }

    public List<Long> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Long> currencies) {
        this.currencies = currencies;
    }

    public SupportEmail getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(SupportEmail supportEmail) {
        this.supportEmail = supportEmail;
    }

    public DonationsConfig getDonationsConfig() {
        return donationsConfig;
    }

    public void setDonationsConfig(DonationsConfig donationsConfig) {
        this.donationsConfig = donationsConfig;
    }

    public WhatsappConfig getWhatsappConfig() {
        return whatsappConfig;
    }

    public void setWhatsappConfig(WhatsappConfig whatsappConfig) {
        this.whatsappConfig = whatsappConfig;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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

    public Boolean getEnablePacksAndEventsCatalog() {
        return enablePacksAndEventsCatalog;
    }

    public void setEnablePacksAndEventsCatalog(Boolean enablePacksAndEventsCatalog) {
        this.enablePacksAndEventsCatalog = enablePacksAndEventsCatalog;
    }

    public Boolean getRobotsNoFollow() {
        return robotsNoFollow;
    }

    public void setRobotsNoFollow(Boolean robotsNoFollow) {
        this.robotsNoFollow = robotsNoFollow;
    }

    public CustomerAssignationMode getCustomerAssignationMode() {
        return customerAssignationMode;
    }

    public void setCustomerAssignationMode(CustomerAssignationMode customerAssignationMode) {
        this.customerAssignationMode = customerAssignationMode;
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
