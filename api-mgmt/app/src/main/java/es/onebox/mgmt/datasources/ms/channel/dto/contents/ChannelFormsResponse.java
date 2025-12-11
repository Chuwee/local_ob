package es.onebox.mgmt.datasources.ms.channel.dto.contents;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

public class ChannelFormsResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ChannelFormField> purchase;
    private List<ChannelFormField> booking;
    private List<ChannelFormField> issue;
    private List<ChannelFormField> member;
    private List<ChannelFormField> newMember;
    private List<ChannelFormField> tutor;

    public List<ChannelFormField> getPurchase() {
        return purchase;
    }

    public void setPurchase(List<ChannelFormField> purchase) {
        this.purchase = purchase;
    }

    public List<ChannelFormField> getBooking() {
        return booking;
    }

    public void setBooking(List<ChannelFormField> booking) {
        this.booking = booking;
    }

    public List<ChannelFormField> getIssue() {
        return issue;
    }

    public void setIssue(List<ChannelFormField> issue) {
        this.issue = issue;
    }

    public List<ChannelFormField> getMember() {
        return member;
    }

    public void setMember(List<ChannelFormField> member) {
        this.member = member;
    }

    public List<ChannelFormField> getNewMember() {
        return newMember;
    }

    public void setNewMember(List<ChannelFormField> newMember) {
        this.newMember = newMember;
    }

    public List<ChannelFormField> getTutor() {
        return tutor;
    }

    public void setTutor(List<ChannelFormField> tutor) {
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
