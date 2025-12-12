package es.onebox.event.priceengine.simulation.record;

import java.sql.Timestamp;

public class EventChannelRecord {

    private Long id;
    private Boolean allSaleGroups;
    private Long channelId;
    private String channelName;
    private Integer channelType;
    private Long entityId;
    private String entityName;
    private String entityLogoPath;

    private Long operatorId;

    private Long eventId;
    private Integer eventStatus;
    private Integer eventCurrencyId;

    private Integer requestStatus;

    private Integer saleStatus;
    private Integer saleType;
    private Integer releaseStatus;
    private Integer releaseType;

    private Boolean useEventDates;
    private Boolean releaseEnable;
    private Timestamp releaseDate;
    private String releaseDateTZ;

    private Boolean saleEnable;
    private Timestamp saleStartDate;
    private String saleStartDateTZ;
    private Timestamp saleEndDate;
    private String saleEndDateTZ;

    private Boolean bookingEnable;
    private Timestamp bookingStartDate;
    private String bookingStartDateTZ;
    private Timestamp bookingEndDate;
    private String bookingEndDateTZ;

    private Boolean useEventSurcharges;
    private Boolean recommendedChannelSurcharges;
    private Double minSurcharge;
    private Double maxSurcharge;

    private Boolean usePromotionEventSurcharges;
    private Boolean recommendedPromotionChannelSurcharges;
    private Double minPromotionSurcharge;
    private Double maxPromotionSurcharge;

    private Boolean recommendedInvChannelSurcharges;
    private Double minInvSurcharge;
    private Double maxInvSurcharge;
    private Boolean favorite;
    private Boolean allowChannelUseAlternativeCharges;
    private Integer individualTicketTemplate;
    private String providerPlanSettings;

    public Long getChannelId() {
        return channelId;
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Integer getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(Integer requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Integer getSaleStatus() {
        return saleStatus;
    }

    public void setSaleStatus(Integer saleStatus) {
        this.saleStatus = saleStatus;
    }

    public Integer getSaleType() {
        return saleType;
    }

    public void setSaleType(Integer saleType) {
        this.saleType = saleType;
    }

    public Integer getReleaseStatus() {
        return releaseStatus;
    }

    public void setReleaseStatus(Integer releaseStatus) {
        this.releaseStatus = releaseStatus;
    }

    public Integer getReleaseType() {
        return releaseType;
    }

    public void setReleaseType(Integer releaseType) {
        this.releaseType = releaseType;
    }

    public Boolean getUseEventDates() {
        return useEventDates;
    }

    public void setUseEventDates(Boolean useEventDates) {
        this.useEventDates = useEventDates;
    }

    public Boolean getReleaseEnable() {
        return releaseEnable;
    }

    public void setReleaseEnable(Boolean releaseEnable) {
        this.releaseEnable = releaseEnable;
    }

    public Timestamp getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Timestamp releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getReleaseDateTZ() {
        return releaseDateTZ;
    }

    public void setReleaseDateTZ(String releaseDateTZ) {
        this.releaseDateTZ = releaseDateTZ;
    }

    public Boolean getSaleEnable() {
        return saleEnable;
    }

    public void setSaleEnable(Boolean saleEnable) {
        this.saleEnable = saleEnable;
    }

    public Timestamp getSaleStartDate() {
        return saleStartDate;
    }

    public void setSaleStartDate(Timestamp saleStartDate) {
        this.saleStartDate = saleStartDate;
    }

    public String getSaleStartDateTZ() {
        return saleStartDateTZ;
    }

    public void setSaleStartDateTZ(String saleStartDateTZ) {
        this.saleStartDateTZ = saleStartDateTZ;
    }

    public Timestamp getSaleEndDate() {
        return saleEndDate;
    }

    public void setSaleEndDate(Timestamp saleEndDate) {
        this.saleEndDate = saleEndDate;
    }

    public String getSaleEndDateTZ() {
        return saleEndDateTZ;
    }

    public void setSaleEndDateTZ(String saleEndDateTZ) {
        this.saleEndDateTZ = saleEndDateTZ;
    }

    public Boolean getBookingEnable() {
        return bookingEnable;
    }

    public void setBookingEnable(Boolean bookingEnable) {
        this.bookingEnable = bookingEnable;
    }

    public Timestamp getBookingStartDate() {
        return bookingStartDate;
    }

    public void setBookingStartDate(Timestamp bookingStartDate) {
        this.bookingStartDate = bookingStartDate;
    }

    public String getBookingStartDateTZ() {
        return bookingStartDateTZ;
    }

    public void setBookingStartDateTZ(String bookingStartDateTZ) {
        this.bookingStartDateTZ = bookingStartDateTZ;
    }

    public Timestamp getBookingEndDate() {
        return bookingEndDate;
    }

    public void setBookingEndDate(Timestamp bookingEndDate) {
        this.bookingEndDate = bookingEndDate;
    }

    public String getBookingEndDateTZ() {
        return bookingEndDateTZ;
    }

    public void setBookingEndDateTZ(String bookingEndDateTZ) {
        this.bookingEndDateTZ = bookingEndDateTZ;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Integer getEventStatus() {
        return eventStatus;
    }

    public void setEventStatus(Integer eventStatus) {
        this.eventStatus = eventStatus;
    }

    public Integer getEventCurrencyId() {
        return eventCurrencyId;
    }

    public void setEventCurrencyId(Integer eventCurrencyId) {
        this.eventCurrencyId = eventCurrencyId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAllSaleGroups() {
        return allSaleGroups;
    }

    public void setAllSaleGroups(Boolean allSaleGroups) {
        this.allSaleGroups = allSaleGroups;
    }

    public String getEntityLogoPath() {
        return entityLogoPath;
    }

    public void setEntityLogoPath(String entityLogoPath) {
        this.entityLogoPath = entityLogoPath;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Integer getChannelType() {
        return channelType;
    }

    public void setChannelType(Integer channelType) {
        this.channelType = channelType;
    }

    public Boolean getUseEventSurcharges() {
        return useEventSurcharges;
    }

    public void setUseEventSurcharges(Boolean useEventSurcharges) {
        this.useEventSurcharges = useEventSurcharges;
    }

    public Boolean getRecommendedChannelSurcharges() {
        return recommendedChannelSurcharges;
    }

    public void setRecommendedChannelSurcharges(Boolean recommendedChannelSurcharges) {
        this.recommendedChannelSurcharges = recommendedChannelSurcharges;
    }

    public Double getMinSurcharge() {
        return minSurcharge;
    }

    public void setMinSurcharge(Double minSurcharge) {
        this.minSurcharge = minSurcharge;
    }

    public Double getMaxSurcharge() {
        return maxSurcharge;
    }

    public void setMaxSurcharge(Double maxSurcharge) {
        this.maxSurcharge = maxSurcharge;
    }

    public Boolean getUsePromotionEventSurcharges() {
        return usePromotionEventSurcharges;
    }

    public void setUsePromotionEventSurcharges(Boolean usePromotionEventSurcharges) {
        this.usePromotionEventSurcharges = usePromotionEventSurcharges;
    }

    public Boolean getRecommendedPromotionChannelSurcharges() {
        return recommendedPromotionChannelSurcharges;
    }

    public void setRecommendedPromotionChannelSurcharges(Boolean recommendedPromotionChannelSurcharges) {
        this.recommendedPromotionChannelSurcharges = recommendedPromotionChannelSurcharges;
    }

    public Double getMinPromotionSurcharge() {
        return minPromotionSurcharge;
    }

    public void setMinPromotionSurcharge(Double minPromotionSurcharge) {
        this.minPromotionSurcharge = minPromotionSurcharge;
    }

    public Double getMaxPromotionSurcharge() {
        return maxPromotionSurcharge;
    }

    public void setMaxPromotionSurcharge(Double maxPromotionSurcharge) {
        this.maxPromotionSurcharge = maxPromotionSurcharge;
    }

    public Boolean getRecommendedInvChannelSurcharges() {
        return recommendedInvChannelSurcharges;
    }

    public void setRecommendedInvChannelSurcharges(Boolean recommendedInvChannelSurcharges) {
        this.recommendedInvChannelSurcharges = recommendedInvChannelSurcharges;
    }

    public Double getMinInvSurcharge() {
        return minInvSurcharge;
    }

    public void setMinInvSurcharge(Double minInvSurcharge) {
        this.minInvSurcharge = minInvSurcharge;
    }

    public Double getMaxInvSurcharge() {
        return maxInvSurcharge;
    }

    public void setMaxInvSurcharge(Double maxInvSurcharge) {
        this.maxInvSurcharge = maxInvSurcharge;
    }

    public Boolean getFavorite() {
        return favorite;
    }

    public void setFavorite(Boolean favorite) {
        this.favorite = favorite;
    }

    public Boolean getAllowChannelUseAlternativeCharges() {
        return allowChannelUseAlternativeCharges;
    }

    public void setAllowChannelUseAlternativeCharges(Boolean allowChannelUseAlternativeCharges) {
        this.allowChannelUseAlternativeCharges = allowChannelUseAlternativeCharges;
    }

    public Integer getIndividualTicketTemplate() {
        return individualTicketTemplate;
    }

    public void setIndividualTicketTemplate(Integer individualTicketTemplate) {
        this.individualTicketTemplate = individualTicketTemplate;
    }

    public String getProviderPlanSettings() {
        return providerPlanSettings;
    }

    public void setProviderPlanSettings(String providerPlanSettings) {
        this.providerPlanSettings = providerPlanSettings;
    }
}
