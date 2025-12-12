package es.onebox.common.datasources.avetconfig.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by mmolinero on 28/05/18.
 */
public abstract class BaseClubConfig implements Serializable {

    private String ip;
    private String ipSubscriberOperations;
    private Integer port;
    private Integer portSubscriberOperations;
    private Boolean partnerIdToPersonId;
    private Boolean shortName;
    private String name;
    private Integer paymentMethod;
    private List<Integer> capacities;
    private Boolean scheduled;
    private String season;
    private Integer fixedDelayMs;
    private String protocol;
    private String protocolSubscriberOperations;
    private Boolean connectionBySocket;
    private String username;
    private String usernameSubscriberOperations;
    private String password;
    private String passwordSubscriberOperations;
    private Boolean pingRequestsBlocked;
    private Boolean membersEnabled;
    private String membersDrawServiceUrl;
    private Boolean generatePartnerTicket;
    private Boolean checkPartnerGrant;
    private List<Integer> partnerGrantCapacities;
    private Boolean checkPartnerPinRegexp;
    private String partnerPinRegexp;
    private Boolean checkPartnerIdRegexp;
    private String partnerIdRegexp;
    private Boolean checkPartnerAttributesGrant;
    private Boolean avoidSendPromoCode;
    private Boolean sendIdNumber;
    private AvetWSEnvironment avetWSEnvironment;

    private List<PartnerIdInfo> partnerIdInfos;

    private Boolean enableResilience4J;

    private Map<String, Object> additionalProperties;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getIpSubscriberOperations() {
        return ipSubscriberOperations;
    }

    public void setIpSubscriberOperations(String ipSubscriberOperations) {
        this.ipSubscriberOperations = ipSubscriberOperations;
    }

    public Integer getPortSubscriberOperations() {
        return portSubscriberOperations;
    }

    public void setPortSubscriberOperations(Integer portSubscriberOperations) {
        this.portSubscriberOperations = portSubscriberOperations;
    }

    public Boolean isPartnerIdToPersonId() {
        return partnerIdToPersonId;
    }

    public void setPartnerIdToPersonId(Boolean partnerIdToPersonId) {
        this.partnerIdToPersonId = partnerIdToPersonId;
    }

    public Boolean getShortName() {
        return shortName;
    }

    public void setShortName(Boolean shortName) {
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<Integer> getCapacities() {
        return capacities;
    }

    public void setCapacities(List<Integer> capacities) {
        this.capacities = capacities;
    }

    public Boolean getScheduled() {
        return scheduled;
    }

    public void setScheduled(Boolean scheduled) {
        this.scheduled = scheduled;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public Integer getFixedDelayMs() {
        return fixedDelayMs;
    }

    public void setFixedDelayMs(Integer fixedDelayMs) {
        this.fixedDelayMs = fixedDelayMs;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocolSubscriberOperations() {
        return protocolSubscriberOperations;
    }

    public void setProtocolSubscriberOperations(String protocolSubscriberOperations) {
        this.protocolSubscriberOperations = protocolSubscriberOperations;
    }

    public Boolean getConnectionBySocket() {
        return connectionBySocket;
    }

    public void setConnectionBySocket(Boolean connectionBySocket) {
        this.connectionBySocket = connectionBySocket;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsernameSubscriberOperations() {
        return usernameSubscriberOperations;
    }

    public void setUsernameSubscriberOperations(String usernameSubscriberOperations) {
        this.usernameSubscriberOperations = usernameSubscriberOperations;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordSubscriberOperations() {
        return passwordSubscriberOperations;
    }

    public void setPasswordSubscriberOperations(String passwordSubscriberOperations) {
        this.passwordSubscriberOperations = passwordSubscriberOperations;
    }

    public Boolean getPingRequestsBlocked() {
        return pingRequestsBlocked;
    }

    public void setPingRequestsBlocked(Boolean pingRequestsBlocked) {
        this.pingRequestsBlocked = pingRequestsBlocked;
    }

    public Boolean getMembersEnabled() {
        return membersEnabled;
    }

    public void setMembersEnabled(Boolean membersEnabled) {
        this.membersEnabled = membersEnabled;
    }

    public String getMembersDrawServiceUrl() {
        return membersDrawServiceUrl;
    }

    public void setMembersDrawServiceUrl(String membersDrawServiceUrl) {
        this.membersDrawServiceUrl = membersDrawServiceUrl;
    }

    public Boolean getGeneratePartnerTicket() {
        return generatePartnerTicket;
    }

    public void setGeneratePartnerTicket(Boolean generatePartnerTicket) {
        this.generatePartnerTicket = generatePartnerTicket;
    }

    public Boolean getCheckPartnerGrant() {
        return checkPartnerGrant;
    }

    public void setCheckPartnerGrant(Boolean checkPartnerGrant) {
        this.checkPartnerGrant = checkPartnerGrant;
    }

    public List<Integer> getPartnerGrantCapacities() {
        return partnerGrantCapacities;
    }

    public void setPartnerGrantCapacities(List<Integer> partnerGrantCapacities) {
        this.partnerGrantCapacities = partnerGrantCapacities;
    }

    public Boolean getCheckPartnerPinRegexp() {
        return checkPartnerPinRegexp;
    }

    public void setCheckPartnerPinRegexp(Boolean checkPartnerPinRegexp) {
        this.checkPartnerPinRegexp = checkPartnerPinRegexp;
    }

    public String getPartnerPinRegexp() {
        return partnerPinRegexp;
    }

    public void setPartnerPinRegexp(String partnerPinRegexp) {
        this.partnerPinRegexp = partnerPinRegexp;
    }

    public Boolean getCheckPartnerIdRegexp() {
        return checkPartnerIdRegexp;
    }

    public void setCheckPartnerIdRegexp(Boolean checkPartnerIdRegexp) {
        this.checkPartnerIdRegexp = checkPartnerIdRegexp;
    }

    public String getPartnerIdRegexp() {
        return partnerIdRegexp;
    }

    public void setPartnerIdRegexp(String partnerIdRegexp) {
        this.partnerIdRegexp = partnerIdRegexp;
    }

    public Boolean getCheckPartnerAttributesGrant() {
        return checkPartnerAttributesGrant;
    }

    public void setCheckPartnerAttributesGrant(Boolean checkPartnerAttributesGrant) {
        this.checkPartnerAttributesGrant = checkPartnerAttributesGrant;
    }

    public Boolean getPartnerIdToPersonId() {
        return partnerIdToPersonId;
    }

    public Boolean getAvoidSendPromoCode() {
        return avoidSendPromoCode;
    }

    public void setAvoidSendPromoCode(Boolean avoidSendPromoCode) {
        this.avoidSendPromoCode = avoidSendPromoCode;
    }

    public Boolean getSendIdNumber() {
        return sendIdNumber;
    }

    public void setSendIdNumber(Boolean sendIdNumber) {
        this.sendIdNumber = sendIdNumber;
    }

    public List<PartnerIdInfo> getPartnerIdInfos() {
        return partnerIdInfos;
    }

    public void setPartnerIdInfos(List<PartnerIdInfo> partnerIdInfos) {
        this.partnerIdInfos = partnerIdInfos;
    }

    public AvetWSEnvironment getAvetWSEnvironment() {
        return avetWSEnvironment;
    }

    public void setAvetWSEnvironment(AvetWSEnvironment avetWSEnvironment) {
        this.avetWSEnvironment = avetWSEnvironment;
    }

    public abstract WSConnectionVersion getWsConnectionVersion();

    public abstract void setWsConnectionVersion(WSConnectionVersion wsConnectionVersion);

    public abstract WSConnectionVersion getWsSubscriberOperationsConnectionVersion();

    public abstract void setWsSubscriberOperationsConnectionVersion(WSConnectionVersion wsSubscriberOperationsConnectionVersion);

    public Boolean getEnableResilience4J() {
        return enableResilience4J;
    }

    public void setEnableResilience4J(Boolean enableResilience4J) {
        this.enableResilience4J = enableResilience4J;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
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

