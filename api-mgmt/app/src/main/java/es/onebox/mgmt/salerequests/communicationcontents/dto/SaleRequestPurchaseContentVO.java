package es.onebox.mgmt.salerequests.communicationcontents.dto;

import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentUrlListDTO;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestPurchaseImageContentResponseType;
import es.onebox.mgmt.salerequests.communicationcontents.enums.SaleRequestPurchaseUrlContentType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

public class SaleRequestPurchaseContentVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private ChannelContentImageListDTO<SaleRequestPurchaseImageContentResponseType> images;
    private ChannelContentUrlListDTO<SaleRequestPurchaseUrlContentType> urls;
    private SaleRequestPurchaseContentTextListDTO texts;

    public ChannelContentImageListDTO<SaleRequestPurchaseImageContentResponseType> getImages() {
        return images;
    }

    public void setImages(ChannelContentImageListDTO<SaleRequestPurchaseImageContentResponseType> images) {
        this.images = images;
    }

    public ChannelContentUrlListDTO<SaleRequestPurchaseUrlContentType> getUrls() {
        return urls;
    }

    public void setUrls(ChannelContentUrlListDTO<SaleRequestPurchaseUrlContentType> urls) {
        this.urls = urls;
    }

    public SaleRequestPurchaseContentTextListDTO getTexts() {
        return texts;
    }

    public void setTexts(SaleRequestPurchaseContentTextListDTO texts) {
        this.texts = texts;
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
