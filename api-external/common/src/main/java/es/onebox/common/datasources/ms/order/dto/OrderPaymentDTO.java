package es.onebox.common.datasources.ms.order.dto;

import es.onebox.dal.dto.couch.enums.PaymentType;

import java.io.Serializable;
import java.time.ZonedDateTime;

/**
 * Created by amiranda on 05/08/2015.
 */
public class OrderPaymentDTO implements Serializable {

    private Long id;
    private Double value;
    private Boolean isAdvance;
    private String cardSign;
    private String cardNumber;
    private String cardBinCode;
    private Integer userId;
    private String userName;
    private PaymentType paymentType;
    private String paymentReference;
    private String paymentExtraCode;
    private String merchant;
    private ZonedDateTime paymentDate;
    private ZonedDateTime transferDate;
    private String gatewaySid;
    private int products;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public Boolean getIsAdvance() {
        return isAdvance;
    }

    public void setIsAdvance(Boolean isAdvance) {
        this.isAdvance = isAdvance;
    }

    public String getCardSign() {
        return cardSign;
    }

    public void setCardSign(String cardSign) {
        this.cardSign = cardSign;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardBinCode() {
        return cardBinCode;
    }

    public void setCardBinCode(String cardBinCode) {
        this.cardBinCode = cardBinCode;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public ZonedDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(ZonedDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public ZonedDateTime getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(ZonedDateTime transferDate) {
        this.transferDate = transferDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getPaymentExtraCode() {
        return paymentExtraCode;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public void setPaymentExtraCode(String paymentExtraCode) {
        this.paymentExtraCode = paymentExtraCode;
    }

    public int getProducts() {
        return products;
    }

    public void setProducts(int products) {
        this.products = products;
    }

    public String getGatewaySid() {
        return gatewaySid;
    }

    public void setGatewaySid(String gatewaySid) {
        this.gatewaySid = gatewaySid;
    }
}
