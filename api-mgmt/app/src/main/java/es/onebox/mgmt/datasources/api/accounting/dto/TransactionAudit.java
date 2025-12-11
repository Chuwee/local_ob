package es.onebox.mgmt.datasources.api.accounting.dto;

import java.io.Serializable;

public class TransactionAudit implements Serializable {

    private Boolean status;
    private Integer providerId;
    private Integer amount;
    private String currencyCode;
    private String username;
    private Integer clientId;
    private MovementType movementType;
    private TransactionSupportType transactionType;
    private String transactionId;
    private String comment;
    private Integer channelId;
    private String locator;
    private Integer oldBalance;
    private Integer newBalance;
    private Integer oldUsedCredit;
    private Integer newUsedCredit;
    private Integer oldMaxCredit;
    private Integer newMaxCredit;
    private String movementId;
    private Long timestamp;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public TransactionSupportType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionSupportType transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getLocator() {
        return locator;
    }

    public void setLocator(String locator) {
        this.locator = locator;
    }

    public Integer getOldBalance() {
        return oldBalance;
    }

    public void setOldBalance(Integer oldBalance) {
        this.oldBalance = oldBalance;
    }

    public Integer getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(Integer newBalance) {
        this.newBalance = newBalance;
    }

    public Integer getOldUsedCredit() {
        return oldUsedCredit;
    }

    public void setOldUsedCredit(Integer oldUsedCredit) {
        this.oldUsedCredit = oldUsedCredit;
    }

    public Integer getNewUsedCredit() {
        return newUsedCredit;
    }

    public void setNewUsedCredit(Integer newUsedCredit) {
        this.newUsedCredit = newUsedCredit;
    }

    public Integer getOldMaxCredit() {
        return oldMaxCredit;
    }

    public void setOldMaxCredit(Integer oldMaxCredit) {
        this.oldMaxCredit = oldMaxCredit;
    }

    public Integer getNewMaxCredit() {
        return newMaxCredit;
    }

    public void setNewMaxCredit(Integer newMaxCredit) {
        this.newMaxCredit = newMaxCredit;
    }

    public String getMovementId() {
        return movementId;
    }

    public void setMovementId(String movementId) {
        this.movementId = movementId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
