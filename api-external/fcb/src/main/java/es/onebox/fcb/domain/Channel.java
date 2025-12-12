package es.onebox.fcb.domain;

import es.onebox.couchbase.annotations.CouchDocument;
import es.onebox.couchbase.annotations.Id;

import java.io.Serial;
import java.io.Serializable;

@CouchDocument
public class Channel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;

    @Id
    private Integer channelId;
    private String key;
    private String salesChannel;
    private String paymentMethod;

    public Channel() {
    }

    public Channel(String key, String salesChannel, String paymentMethod) {
        this.key = key;
        this.salesChannel = salesChannel;
        this.paymentMethod = paymentMethod;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSalesChannel() {
        return salesChannel;
    }

    public void setSalesChannel(String salesChannel) {
        this.salesChannel = salesChannel;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
