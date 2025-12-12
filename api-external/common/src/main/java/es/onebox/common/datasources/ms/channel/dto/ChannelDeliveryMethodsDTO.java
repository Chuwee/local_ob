package es.onebox.common.datasources.ms.channel.dto;

import es.onebox.common.datasources.ms.channel.enums.EmailMode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import jakarta.validation.Valid;
import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ChannelDeliveryMethodsDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Valid
    private List<ChannelDeliveryMethodDTO> deliveryMethods;
    private Boolean usePassbook;
    private Boolean downloadLink;
    private EmailMode emailMode;

    public List<ChannelDeliveryMethodDTO> getDeliveryMethods() {
        return deliveryMethods;
    }

    public void setDeliveryMethods(List<ChannelDeliveryMethodDTO> deliveryMethods) {
        this.deliveryMethods = deliveryMethods;
    }

    public Boolean getUsePassbook() {
        return usePassbook;
    }

    public void setUsePassbook(Boolean usePassbook) {
        this.usePassbook = usePassbook;
    }

    public Boolean getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(Boolean downloadLink) {
        this.downloadLink = downloadLink;
    }

    public EmailMode getEmailMode() {
        return emailMode;
    }

    public void setEmailMode(EmailMode emailMode) {
        this.emailMode = emailMode;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
