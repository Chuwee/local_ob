package es.onebox.mgmt.datasources.ms.channel.dto.voucher;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Set;

public class VoucherGroup implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String name;
    private String description;
    private VoucherStatus status;
    private VoucherGroupType type;
    private Long entityId;
    private ChannelsScope channelsScope;
    private Set<Long> channelIds;
    private Set<IdNameDTO> channels;
    private VoucherValidationMethod validationMethod;
    private VoucherExpirationType expirationType;
    private ZonedDateTime fixedExpirationDate;
    private Long relativeExpirationAmount;
    private VoucherExpirationTimePeriod expirationTimePeriod;
    private Boolean enableUsageLimit;
    private Long usageLimit;
    private Long currencyId;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public VoucherStatus getStatus() {
        return status;
    }

    public void setStatus(VoucherStatus status) {
        this.status = status;
    }

    public VoucherGroupType getType() {
        return type;
    }

    public void setType(VoucherGroupType type) {
        this.type = type;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public ChannelsScope getChannelsScope() {
        return channelsScope;
    }

    public void setChannelsScope(ChannelsScope channelsScope) {
        this.channelsScope = channelsScope;
    }

    public Set<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(Set<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public Set<IdNameDTO> getChannels() {
        return channels;
    }

    public void setChannels(Set<IdNameDTO> channels) {
        this.channels = channels;
    }

    public VoucherValidationMethod getValidationMethod() {
        return validationMethod;
    }

    public void setValidationMethod(VoucherValidationMethod voucherValidationMethod) {
        this.validationMethod = voucherValidationMethod;
    }

    public VoucherExpirationType getExpirationType() {
        return expirationType;
    }

    public void setExpirationType(VoucherExpirationType expirationType) {
        this.expirationType = expirationType;
    }

    public ZonedDateTime getFixedExpirationDate() {
        return fixedExpirationDate;
    }

    public void setFixedExpirationDate(ZonedDateTime fixedExpirationDate) {
        this.fixedExpirationDate = fixedExpirationDate;
    }

    public Long getRelativeExpirationAmount() {
        return relativeExpirationAmount;
    }

    public void setRelativeExpirationAmount(Long relativeExpirationAmount) {
        this.relativeExpirationAmount = relativeExpirationAmount;
    }

    public VoucherExpirationTimePeriod getExpirationTimePeriod() {
        return expirationTimePeriod;
    }

    public void setExpirationTimePeriod(VoucherExpirationTimePeriod expirationTimePeriod) {
        this.expirationTimePeriod = expirationTimePeriod;
    }

    public Boolean getEnableUsageLimit() {
        return enableUsageLimit;
    }

    public void setEnableUsageLimit(Boolean enableUsageLimit) {
        this.enableUsageLimit = enableUsageLimit;
    }

    public Long getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Long usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
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
