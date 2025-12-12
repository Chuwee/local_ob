package es.onebox.ath.integration.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class TransferSeatDTO implements Serializable {

    private static final long serialVersionUID = 7922489243317370672L;

    @JsonProperty("transfer_match")
    private TransferMatchDTO transferMatch;
    private String surnames;
    private String receiver;
    private String email;
    private String name;
    @JsonProperty("transfer_seat_id")
    private String transferSeatId;
    private String state;
    private String channel;

    public TransferMatchDTO getTransferMatch() {
        return transferMatch;
    }

    public void setTransferMatch(TransferMatchDTO transferMatch) {
        this.transferMatch = transferMatch;
    }

    public String getSurnames() {
        return surnames;
    }

    public void setSurnames(String surnames) {
        this.surnames = surnames;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTransferSeatId() {
        return transferSeatId;
    }

    public void setTransferSeatId(String transferSeatId) {
        this.transferSeatId = transferSeatId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
