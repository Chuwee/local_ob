package es.onebox.mgmt.channels.forms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;



public class ChannelDefaultFormDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ChannelFormFieldDTO> purchase;
    private List<ChannelFormFieldDTO> booking;
    private List<ChannelFormFieldDTO> issue;
    private List<ChannelFormFieldDTO> member;
    @JsonProperty("new_member")
    private List<ChannelFormFieldDTO> newMember;
    private List<ChannelFormFieldDTO> tutor;


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

    public List<ChannelFormFieldDTO> getTutor() {
        return tutor;
    }

    public void setTutor(List<ChannelFormFieldDTO> tutor) {
        this.tutor = tutor;
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
