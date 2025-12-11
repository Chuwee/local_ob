package es.onebox.mgmt.events.promotions.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.common.promotions.dto.PromotionCollectiveDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionDiscountConfigDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionLimitsDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionSurchargesDTO;
import es.onebox.mgmt.common.promotions.dto.PromotionValidityPeriodDTO;
import es.onebox.mgmt.common.promotions.enums.PromotionStatus;
import es.onebox.mgmt.validation.annotation.Promotion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serial;
import java.io.Serializable;

@Valid
@Promotion
public class UpdateEventPromotionDetailDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1097045223800405034L;

    @Size(min = 1, max = 40, message = "name must have between 1 and 40 characters")
    private String name;
    private PromotionStatus status;
    private PromotionDiscountConfigDTO discount;
    @JsonProperty("usage_limits")
    private PromotionLimitsDTO limits;
    @JsonProperty("validity_period")
    private PromotionValidityPeriodDTO validityPeriod;
    private PromotionSurchargesDTO surcharges;
    @JsonProperty("show_discount_name_ticket")
    private Boolean showDiscountNameticket;
    @JsonProperty("show_ticket_price_without_discount")
    private Boolean showTicketPriceWithoutDiscount;
    @JsonProperty("access_control_restricted")
    private Boolean accesControlRestricted;
    private Boolean combinable;
    private PromotionCollectiveDTO collective;
    private Boolean presale;
    @JsonProperty("block_secondary_market_sale")
    private Boolean blockSecondaryMarketSale;
    @JsonProperty("applicable_conditions")
    private UpdatePromotionConditionsDTO conditions;

    public Boolean getShowDiscountNameticket() {
        return showDiscountNameticket;
    }

    public void setShowDiscountNameticket(Boolean showDiscountNameticket) {
        this.showDiscountNameticket = showDiscountNameticket;
    }

    public Boolean getShowTicketPriceWithoutDiscount() {
        return showTicketPriceWithoutDiscount;
    }

    public void setShowTicketPriceWithoutDiscount(Boolean showTicketPriceWithoutDiscount) {
        this.showTicketPriceWithoutDiscount = showTicketPriceWithoutDiscount;
    }

    public PromotionDiscountConfigDTO getDiscount() {
        return discount;
    }

    public void setDiscount(PromotionDiscountConfigDTO discount) {
        this.discount = discount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PromotionStatus getStatus() {
        return status;
    }

    public void setStatus(PromotionStatus status) {
        this.status = status;
    }

    public Boolean getAccesControlRestricted() {
        return accesControlRestricted;
    }

    public void setAccesControlRestricted(Boolean accesControlRestricted) {
        this.accesControlRestricted = accesControlRestricted;
    }

    public Boolean getCombinable() {
        return combinable;
    }

    public void setCombinable(Boolean combinable) {
        this.combinable = combinable;
    }

    public PromotionLimitsDTO getLimits() {
        return limits;
    }

    public void setLimits(PromotionLimitsDTO limits) {
        this.limits = limits;
    }

    public PromotionValidityPeriodDTO getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(PromotionValidityPeriodDTO validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public void setSurcharges(PromotionSurchargesDTO surcharges) {
        this.surcharges = surcharges;
    }

    public PromotionSurchargesDTO getSurcharges() {
        return surcharges;
    }

    public PromotionCollectiveDTO getCollective() {
        return collective;
    }

    public void setCollective(PromotionCollectiveDTO collective) {
        this.collective = collective;
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

    public Boolean getPresale() {
        return presale;
    }

    public void setPresale(Boolean presale) {
        this.presale = presale;
    }

    public Boolean getBlockSecondaryMarketSale() {
        return blockSecondaryMarketSale;
    }

    public void setBlockSecondaryMarketSale(Boolean blockSecondaryMarketSale) {
        this.blockSecondaryMarketSale = blockSecondaryMarketSale;
    }

    public UpdatePromotionConditionsDTO getConditions() {
        return conditions;
    }

    public void setConditions(UpdatePromotionConditionsDTO conditions) {
        this.conditions = conditions;
    }
}
