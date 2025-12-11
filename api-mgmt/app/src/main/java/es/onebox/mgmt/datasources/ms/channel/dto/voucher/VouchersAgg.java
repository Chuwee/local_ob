package es.onebox.mgmt.datasources.ms.channel.dto.voucher;

import java.io.Serializable;

public class VouchersAgg implements Serializable {

    public Long totalCodes = 0L;
    public Long activeCodes = 0L;
    public Long activeCodesWithRedeems = 0L;
    public Long activeCodesWithoutRedeems = 0L;
    public Long inactiveCodes = 0L;
    public Long inactiveCodesWithRedeems = 0L;
    public Long inactiveCodesWithoutRedeems = 0L;

    public Double totalBalance = 0D;
    public Double activeBalance = 0D;
    public Double activeRedeemedBalance = 0D;
    public Double activePendingBalance = 0D;
    public Double inactiveBalance = 0D;
    public Double inactiveRedeemedBalance = 0D;
    public Double inactivePendingBalance = 0D;

    public Long getTotalCodes() {
        return totalCodes;
    }

    public void setTotalCodes(Long totalCodes) {
        this.totalCodes = totalCodes;
    }

    public Long getActiveCodes() {
        return activeCodes;
    }

    public void setActiveCodes(Long activeCodes) {
        this.activeCodes = activeCodes;
    }

    public Long getActiveCodesWithRedeems() {
        return activeCodesWithRedeems;
    }

    public void setActiveCodesWithRedeems(Long activeCodesWithRedeems) {
        this.activeCodesWithRedeems = activeCodesWithRedeems;
    }

    public Long getActiveCodesWithoutRedeems() {
        return activeCodesWithoutRedeems;
    }

    public void setActiveCodesWithoutRedeems(Long activeCodesWithoutRedeems) {
        this.activeCodesWithoutRedeems = activeCodesWithoutRedeems;
    }

    public Long getInactiveCodes() {
        return inactiveCodes;
    }

    public void setInactiveCodes(Long inactiveCodes) {
        this.inactiveCodes = inactiveCodes;
    }

    public Long getInactiveCodesWithRedeems() {
        return inactiveCodesWithRedeems;
    }

    public void setInactiveCodesWithRedeems(Long inactiveCodesWithRedeems) {
        this.inactiveCodesWithRedeems = inactiveCodesWithRedeems;
    }

    public Long getInactiveCodesWithoutRedeems() {
        return inactiveCodesWithoutRedeems;
    }

    public void setInactiveCodesWithoutRedeems(Long inactiveCodesWithoutRedeems) {
        this.inactiveCodesWithoutRedeems = inactiveCodesWithoutRedeems;
    }

    public Double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(Double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public Double getActiveBalance() {
        return activeBalance;
    }

    public void setActiveBalance(Double activeBalance) {
        this.activeBalance = activeBalance;
    }

    public Double getActiveRedeemedBalance() {
        return activeRedeemedBalance;
    }

    public void setActiveRedeemedBalance(Double activeRedeemedBalance) {
        this.activeRedeemedBalance = activeRedeemedBalance;
    }

    public Double getActivePendingBalance() {
        return activePendingBalance;
    }

    public void setActivePendingBalance(Double activePendingBalance) {
        this.activePendingBalance = activePendingBalance;
    }

    public Double getInactiveBalance() {
        return inactiveBalance;
    }

    public void setInactiveBalance(Double inactiveBalance) {
        this.inactiveBalance = inactiveBalance;
    }

    public Double getInactiveRedeemedBalance() {
        return inactiveRedeemedBalance;
    }

    public void setInactiveRedeemedBalance(Double inactiveRedeemedBalance) {
        this.inactiveRedeemedBalance = inactiveRedeemedBalance;
    }

    public Double getInactivePendingBalance() {
        return inactivePendingBalance;
    }

    public void setInactivePendingBalance(Double inactivePendingBalance) {
        this.inactivePendingBalance = inactivePendingBalance;
    }
}
