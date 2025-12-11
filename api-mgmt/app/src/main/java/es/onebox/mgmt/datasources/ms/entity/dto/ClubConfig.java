package es.onebox.mgmt.datasources.ms.entity.dto;

import es.onebox.mgmt.common.DigitalTicketMode;
import es.onebox.mgmt.datasources.ms.entity.enums.AvetWSEnvironment;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ClubConfig implements Serializable {

    @Serial
    private static final long serialVersionUID = 4640640995615566249L;

    private String clubCode;

    private Integer entityId;
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
    private WSConnectionVersion wsConnectionVersion;
    private WSConnectionVersion wsSubscriberOperationsConnectionVersion;
    private Boolean pingRequestsBlocked;
    private Boolean membersEnabled;
    private String membersDrawServiceUrl;
    private Boolean generatePartnerTicket;
    private Boolean checkPartnerGrant;
    private List<Integer> partnerGrantCapacities;
    private Boolean checkPartnerPinRegexp;
    private String partnerPinRegexp;
    private Boolean checkPartnerAttributesGrant;
    private Boolean avoidSendPromoCode;
    private Boolean sendIdNumber;
    private Integer idNumberMaxLength;
    private AvetWSEnvironment avetWSEnvironment;
    private List<PartnerIdInfo> partnerIdInfos;
    private Long membersCapacityId;
    private DigitalTicketMode digitalTicketMode;
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

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    public String getClubCode() {
        return clubCode;
    }

    public void setClubCode(String clubCode) {
        this.clubCode = clubCode;
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

    public WSConnectionVersion getWsConnectionVersion() {
        return wsConnectionVersion;
    }

    public void setWsConnectionVersion(WSConnectionVersion wsConnectionVersion) {
        this.wsConnectionVersion = wsConnectionVersion;
    }

    public WSConnectionVersion getWsSubscriberOperationsConnectionVersion() {
        return wsSubscriberOperationsConnectionVersion;
    }

    public void setWsSubscriberOperationsConnectionVersion(WSConnectionVersion wsSubscriberOperationsConnectionVersion) {
        this.wsSubscriberOperationsConnectionVersion = wsSubscriberOperationsConnectionVersion;
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

    public Integer getIdNumberMaxLength() {
        return idNumberMaxLength;
    }

    public void setIdNumberMaxLength(Integer idNumberMaxLength) {
        this.idNumberMaxLength = idNumberMaxLength;
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

    public Long getMembersCapacityId() {
        return membersCapacityId;
    }

    public void setMembersCapacityId(Long membersCapacityId) {
        this.membersCapacityId = membersCapacityId;
    }

    public DigitalTicketMode getDigitalTicketMode() {
        return digitalTicketMode;
    }
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    public void setDigitalTicketMode(DigitalTicketMode digitalTicketMode) {
        this.digitalTicketMode = digitalTicketMode;
    }
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
