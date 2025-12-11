package es.onebox.mgmt.datasources.ms.promotion.dto;

import java.io.Serializable;

public class PromotionTickets implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean showDiscountNameTicket;
    private Boolean showTicketPriceWithoutDiscount;
    private PromotionLimit purchaseMinLimit;
    private PromotionLimit purchaseMaxLimit;
    private PromotionLimit promotionMaxLimit;
    private PromotionLimit sessionMaxLimit;
    private PromotionLimit packs;



    public Boolean getShowDiscountNameTicket() {
        return showDiscountNameTicket;
    }

    public void setShowDiscountNameTicket(Boolean showDiscountNameTicket) {
        this.showDiscountNameTicket = showDiscountNameTicket;
    }

    public Boolean getShowTicketPriceWithoutDiscount() {
        return showTicketPriceWithoutDiscount;
    }

    public void setShowTicketPriceWithoutDiscount(Boolean showTicketPriceWithoutDiscount) {
        this.showTicketPriceWithoutDiscount = showTicketPriceWithoutDiscount;
    }

    public PromotionLimit getPurchaseMinLimit() {
        return purchaseMinLimit;
    }

    public void setPurchaseMinLimit(PromotionLimit purchaseMinLimit) {
        this.purchaseMinLimit = purchaseMinLimit;
    }

    public PromotionLimit getPurchaseMaxLimit() {
        return purchaseMaxLimit;
    }

    public void setPurchaseMaxLimit(PromotionLimit purchaseMaxLimit) {
        this.purchaseMaxLimit = purchaseMaxLimit;
    }

    public PromotionLimit getPromotionMaxLimit() {
        return promotionMaxLimit;
    }

    public void setPromotionMaxLimit(PromotionLimit promotionMaxLimit) {
        this.promotionMaxLimit = promotionMaxLimit;
    }

    public PromotionLimit getSessionMaxLimit() {
        return sessionMaxLimit;
    }

    public void setSessionMaxLimit(PromotionLimit sessionMaxLimit) {
        this.sessionMaxLimit = sessionMaxLimit;
    }

    public PromotionLimit getPacks() {
        return packs;
    }

    public void setPacks(PromotionLimit packs) {
        this.packs = packs;
    }
}
