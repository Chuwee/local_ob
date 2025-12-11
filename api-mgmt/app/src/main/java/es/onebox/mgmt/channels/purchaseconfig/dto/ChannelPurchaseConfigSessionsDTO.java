package es.onebox.mgmt.channels.purchaseconfig.dto;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;
import java.io.Serializable;

public class ChannelPurchaseConfigSessionsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ChannelPurchaseConfigPromotionsDTO promotions;
    private ChannelPurchaseConfigVisualizationDTO visualization;

    public ChannelPurchaseConfigPromotionsDTO getPromotions() {
        return promotions;
    }

    public void setPromotions(ChannelPurchaseConfigPromotionsDTO promotions) {
        this.promotions = promotions;
    }

    public ChannelPurchaseConfigVisualizationDTO getVisualization() {
        return visualization;
    }

    public void setVisualization(ChannelPurchaseConfigVisualizationDTO visualization) {
        this.visualization = visualization;
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
