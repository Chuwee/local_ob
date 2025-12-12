package es.onebox.common.datasources.catalog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.common.datasources.catalog.dto.common.Promotion;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class ChannelEventDetail extends ChannelEvent {

    private static final long serialVersionUID = -7411757538499100325L;

    private ChannelEventEntity entity;
    private ChannelEventEntity promoter;
    @JsonProperty("use_promoter_fiscal_data")
    private Boolean usePromoterFiscalData;
    @JsonProperty("sold_out")
    private Boolean soldOut;
    private List<Promotion> promotions;


    public ChannelEventEntity getEntity() {
        return entity;
    }

    public void setEntity(ChannelEventEntity entity) {
        this.entity = entity;
    }

    public ChannelEventEntity getPromoter() {
        return promoter;
    }

    public void setPromoter(ChannelEventEntity promoter) {
        this.promoter = promoter;
    }

    public Boolean getUsePromoterFiscalData() {
        return usePromoterFiscalData;
    }

    public void setUsePromoterFiscalData(Boolean usePromoterFiscalData) {
        this.usePromoterFiscalData = usePromoterFiscalData;
    }

    public Boolean getSoldOut() {
        return soldOut;
    }

    public void setSoldOut(Boolean soldOut) {
        this.soldOut = soldOut;
    }

    public List<Promotion> getPromotions() {
        return promotions;
    }

    public void setPromotions(List<Promotion> promotions) {
        this.promotions = promotions;
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
