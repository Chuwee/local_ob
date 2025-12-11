package es.onebox.mgmt.datasources.ms.channel.dto.contents;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Set;

public class UpdateDefaultChannelForms implements Serializable {

    private static final long serialVersionUID = 1L;

    private Set<ChannelFormField> purchase;
    private Set<ChannelFormField> booking;
    private Set<ChannelFormField> issue;
    private Set<ChannelFormField> member;
    private Set<ChannelFormField> newMember;
    private Set<ChannelFormField> tutor;

    public Set<ChannelFormField> getPurchase() {
        return purchase;
    }

    public void setPurchase(Set<ChannelFormField> purchase) {
        this.purchase = purchase;
    }

    public Set<ChannelFormField> getBooking() {
        return booking;
    }

    public void setBooking(Set<ChannelFormField> booking) {
        this.booking = booking;
    }

    public Set<ChannelFormField> getIssue() {
        return issue;
    }

    public void setIssue(Set<ChannelFormField> issue) {
        this.issue = issue;
    }

    public Set<ChannelFormField> getMember() {
        return member;
    }

    public void setMember(Set<ChannelFormField> member) {
        this.member = member;
    }

    public Set<ChannelFormField> getNewMember() {
        return newMember;
    }

    public void setNewMember(Set<ChannelFormField> newMember) {
        this.newMember = newMember;
    }

    public Set<ChannelFormField> getTutor() {
        return tutor;
    }

    public void setTutor(Set<ChannelFormField> tutor) {
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
