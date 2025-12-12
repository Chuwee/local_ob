package es.onebox.internal.utils.progress.model;

import es.onebox.internal.utils.progress.enums.StatusMessage;
import es.onebox.message.broker.client.message.AbstractNotificationMessage;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serial;

public abstract class ProgressMessage extends AbstractNotificationMessage {

    protected static final String ROUTING_KEY = "progress";
    @Serial
    private static final long serialVersionUID = -5652311396078898270L;

    private Long id;
    private Byte progress;
    private StatusMessage status;
    private String text;
    private Integer errorCode;

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

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setError(Integer errorCode) {
        this.errorCode = errorCode;
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
