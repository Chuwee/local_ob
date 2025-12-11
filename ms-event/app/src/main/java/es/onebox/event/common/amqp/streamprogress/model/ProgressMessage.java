package es.onebox.event.common.amqp.streamprogress.model;

import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class ProgressMessage extends AbstractNotificationMessage {

    protected static final String ROUTING_KEY = "progress";

    private static final long serialVersionUID = 1L;
    
    private Long id;
    private Byte progress;
    private StatusMessage status;
    private String text;
    
    public ProgressMessage() {
        super();
    }

    public ProgressMessage(String messageName) {
        super(messageName, ROUTING_KEY);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Byte getProgress() {
        return progress;
    }

    public void setProgress(Byte progress) {
        this.progress = progress;
    }

    public StatusMessage getStatus() {
        return status;
    }

    public void setStatus(StatusMessage status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
