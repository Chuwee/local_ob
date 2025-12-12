package es.onebox.common.datasources.webhook.dto.fever;

import java.io.Serializable;
import java.util.List;

public class ChannelFormDetailDTO implements Serializable {

    private List<ChannelFormFieldDTO> purchase;
    private List<ChannelFormFieldDTO> booking;
    private List<ChannelFormFieldDTO> issue;
    private List<ChannelFormFieldDTO> member;
    private List<ChannelFormFieldDTO> newMember;

    public List<ChannelFormFieldDTO> getPurchase() {
        return purchase;
    }

    public void setPurchase(List<ChannelFormFieldDTO> purchase) {
        this.purchase = purchase;
    }

    public List<ChannelFormFieldDTO> getBooking() {
        return booking;
    }

    public void setBooking(List<ChannelFormFieldDTO> booking) {
        this.booking = booking;
    }

    public List<ChannelFormFieldDTO> getIssue() {
        return issue;
    }

    public void setIssue(List<ChannelFormFieldDTO> issue) {
        this.issue = issue;
    }

    public List<ChannelFormFieldDTO> getMember() {
        return member;
    }

    public void setMember(List<ChannelFormFieldDTO> member) {
        this.member = member;
    }

    public List<ChannelFormFieldDTO> getNewMember() {
        return newMember;
    }

    public void setNewMember(List<ChannelFormFieldDTO> newMember) {
        this.newMember = newMember;
    }

}
