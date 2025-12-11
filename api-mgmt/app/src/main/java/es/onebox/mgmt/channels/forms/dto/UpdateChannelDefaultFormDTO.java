package es.onebox.mgmt.channels.forms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.onebox.mgmt.validation.annotation.ChannelForms;
import jakarta.validation.Valid;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

@ChannelForms
public class UpdateChannelDefaultFormDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Valid
    private List<UpdateChannelFormFieldDTO> purchase;
    @Valid
    private List<UpdateChannelFormFieldDTO> booking;
    @Valid
    private List<UpdateChannelFormFieldDTO> issue;
    @Valid
    private List<UpdateChannelFormFieldDTO> member;
    @Valid
    @JsonProperty("new_member")
    private List<UpdateChannelFormFieldDTO> newMember;
    @Valid
    private List<UpdateChannelFormFieldDTO> tutor;

    public List<UpdateChannelFormFieldDTO> getPurchase() {
        return purchase;
    }

    public void setPurchase(List<UpdateChannelFormFieldDTO> purchase) {
        this.purchase = purchase;
    }

    public List<UpdateChannelFormFieldDTO> getBooking() {
        return booking;
    }

    public void setBooking(List<UpdateChannelFormFieldDTO> booking) {
        this.booking = booking;
    }

    public List<UpdateChannelFormFieldDTO> getIssue() {
        return issue;
    }

    public void setIssue(List<UpdateChannelFormFieldDTO> issue) {
        this.issue = issue;
    }

    public List<UpdateChannelFormFieldDTO> getMember() {
        return member;
    }

    public void setMember(List<UpdateChannelFormFieldDTO> member) {
        this.member = member;
    }

    public List<UpdateChannelFormFieldDTO> getNewMember() {
        return newMember;
    }

    public void setNewMember(List<UpdateChannelFormFieldDTO> newMember) {
        this.newMember = newMember;
    }

    public List<UpdateChannelFormFieldDTO> getTutor() {
        return tutor;
    }

    public void setTutor(List<UpdateChannelFormFieldDTO> tutor) {
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
