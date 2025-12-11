package es.onebox.event.priceengine.builders;

import es.onebox.event.priceengine.simulation.record.EventPromotionRecord;

public class EventPromotionRecordBuilder {

    private Integer eventPromotionTemplateId;
    private Integer promotionTemplateId;
    private Integer subtype;
    private Integer selectedChannels;
    private Integer selectedSessions;
    private Integer selectedRates;
    private Integer selectedPriceZones;
    private Integer discountType;
    private Double fixedDiscountValue;
    private Double percentualDiscountValue;
    private Boolean active;
    private Boolean applyChannelSpecificCharges;
    private Boolean applyPromoterSpecificCharges;
    private Boolean notCumulative;
    private String name;
    private String channels;
    private String priceZones;
    private String rates;
    private String ranges;

    private EventPromotionRecordBuilder(){}
    
    public static EventPromotionRecordBuilder buider(){return new EventPromotionRecordBuilder();}
    
    public EventPromotionRecord build(){
        EventPromotionRecord promo = new EventPromotionRecord();
        promo.setEventPromotionTemplateId(eventPromotionTemplateId);
        promo.setPromotionTemplateId(promotionTemplateId);
        promo.setSubtype(subtype);
        promo.setSelectedChannels(selectedChannels);
        promo.setSelectedSessions(selectedSessions);
        promo.setSelectedRates(selectedRates);
        promo.setSelectedPriceZones(selectedPriceZones);
        promo.setDiscountType(discountType);
        promo.setFixedDiscountValue(fixedDiscountValue);
        promo.setPercentualDiscountValue(percentualDiscountValue);
        promo.setActive(active);
        promo.setApplyChannelSpecificCharges(applyChannelSpecificCharges);
        promo.setApplyPromoterSpecificCharges(applyPromoterSpecificCharges);
        promo.setNotCumulative(notCumulative);
        promo.setName(name);
        promo.setChannels(channels);
        promo.setPriceZones(priceZones);
        promo.setRates(rates);
        promo.setRanges(ranges);
        return promo;
    }

    public EventPromotionRecordBuilder withEventPromotionTemplateId(Integer id){
        this.eventPromotionTemplateId = id;
        return this;
    }

    public EventPromotionRecordBuilder withpromotionTemplateId(Integer id){
        this.promotionTemplateId = id;
        return this;
    }

    public EventPromotionRecordBuilder withName(String name){
        this.name = name;
        return this;
    }

    public EventPromotionRecordBuilder withChannels(String channels){
        this.channels = channels;
        return this;
    }

    public EventPromotionRecordBuilder withPriceZones(String priceZones){
        this.priceZones = priceZones;
        return this;
    }

    public EventPromotionRecordBuilder withRates(String rates){
        this.rates = rates;
        return this;
    }

    public EventPromotionRecordBuilder withRanges(String ranges){
        this.ranges = ranges;
        return this;
    }

    public EventPromotionRecordBuilder withFixedDiscountValue(Double fixedDiscountValue){
        this.fixedDiscountValue = fixedDiscountValue;
        return this;
    }

    public EventPromotionRecordBuilder withPercentualDiscountValue(Double percentualDiscountValue){
        this.percentualDiscountValue = percentualDiscountValue;
        return this;
    }

    public EventPromotionRecordBuilder withActive(Boolean active){
        this.active = active;
        return this;
    }

    public EventPromotionRecordBuilder withApplyChannelSpecificCharges(Boolean applyChannelSpecificCharges){
        this.applyChannelSpecificCharges = applyChannelSpecificCharges;
        return this;
    }

    public EventPromotionRecordBuilder withApplyPromoterSpecificCharges(Boolean applyPromoterSpecificCharges){
        this.applyPromoterSpecificCharges = applyPromoterSpecificCharges;
        return this;
    }

    public EventPromotionRecordBuilder withNotCumulative(Boolean notCumulative){
        this.notCumulative = notCumulative;
        return this;
    }

    public EventPromotionRecordBuilder withSubtype(Integer subtype){
        this.subtype = subtype;
        return this;
    }

    public EventPromotionRecordBuilder withDiscountType(Integer discountType){
        this.discountType = discountType;
        return this;
    }

    public EventPromotionRecordBuilder withSelectedChannels(Integer selectedChannels){
        this.selectedChannels = selectedChannels;
        return this;
    }

    public EventPromotionRecordBuilder withSelectedSessions(Integer selectedSessions){
        this.selectedSessions = selectedSessions;
        return this;
    }

    public EventPromotionRecordBuilder withSelectedRates(Integer selectedRates){
        this.selectedRates = selectedRates;
        return this;
    }

    public EventPromotionRecordBuilder withSelectedPriceZones(Integer selectedPriceZones){
        this.selectedPriceZones = selectedPriceZones;
        return this;
    }

}
