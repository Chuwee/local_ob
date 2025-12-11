package es.onebox.event.catalog.elasticsearch.builder;

import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEvent;
import es.onebox.event.catalog.elasticsearch.dto.channelevent.ChannelEventCommunicationElement;
import es.onebox.event.catalog.elasticsearch.enums.CommElementType;
import es.onebox.event.events.dao.record.CommElementRecord;
import es.onebox.event.events.dao.record.EmailCommElementRecord;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChannelEventComElementsBuilder {

    private final ChannelEvent data;

    protected List<EmailCommElementRecord> eventChannelBannerPromoter;
    protected List<EmailCommElementRecord> eventChannelBannerHeader;
    protected List<EmailCommElementRecord> eventChannelBannerChannel;
    protected List<EmailCommElementRecord> eventChannelChannelBannerLink;
    protected List<EmailCommElementRecord> channelBannerHeader;
    protected List<EmailCommElementRecord> channelBannerChannel;
    protected List<EmailCommElementRecord> channelBannerLink;
    protected List<CommElementRecord> eventChannelBannerSquare;

    private ChannelEventComElementsBuilder(ChannelEvent data) {
        super();
        this.data = data;
    }

    public static ChannelEventComElementsBuilder builder(ChannelEvent data) {
        return new ChannelEventComElementsBuilder(data);
    }

    public ChannelEventComElementsBuilder eventChannelBannerPromoter(final List<EmailCommElementRecord> eventChannelBannerPromoter) {
        this.eventChannelBannerPromoter = eventChannelBannerPromoter;
        return this;
    }

    public ChannelEventComElementsBuilder eventChannelBannerHeader(final List<EmailCommElementRecord> eventChannelBannerHeader) {
        this.eventChannelBannerHeader = eventChannelBannerHeader;
        return this;
    }

    public ChannelEventComElementsBuilder eventChannelBannerChannel(final List<EmailCommElementRecord> eventChannelBannerChannel) {
        this.eventChannelBannerChannel = eventChannelBannerChannel;
        return this;
    }

    public ChannelEventComElementsBuilder eventChannelBannerChannelLink(List<EmailCommElementRecord> eventChannelChannelBannerLink) {
        this.eventChannelChannelBannerLink = eventChannelChannelBannerLink;
        return this;
    }

    public ChannelEventComElementsBuilder channelBannerChannel(final List<EmailCommElementRecord> channelBannerChannel) {
        this.channelBannerChannel = channelBannerChannel;
        return this;
    }

    public ChannelEventComElementsBuilder channelBannerChannelLink(List<EmailCommElementRecord> channelBannerLink) {
        this.channelBannerLink = channelBannerLink;
        return this;
    }

    public ChannelEventComElementsBuilder channelBannerHeader(final List<EmailCommElementRecord> channelBannerHeader) {
        this.channelBannerHeader = channelBannerHeader;
        return this;
    }

    public ChannelEventComElementsBuilder eventChannelBannerSquare(List<CommElementRecord> ecBannerSquare) {
        this.eventChannelBannerSquare = ecBannerSquare;
        return this;
    }

    public void buildComElements() {
        data.setCommunicationElements(buildCommElements());
    }

    private List<ChannelEventCommunicationElement> buildCommElements() {
        List<ChannelEventCommunicationElement> commElements = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(eventChannelBannerPromoter)) {
            List<ChannelEventCommunicationElement> banners = buildChannelEventCommElement(eventChannelBannerChannel, CommElementType.PROMOTER_BANNER);
            commElements.addAll(banners);
        }

        if (CollectionUtils.isNotEmpty(eventChannelBannerChannel)) {
            List<ChannelEventCommunicationElement> banners = buildChannelEventCommElement(eventChannelBannerChannel, CommElementType.CHANNEL_BANNER);
            var links = buildChannelEventCommElement(eventChannelChannelBannerLink, CommElementType.CHANNEL_BANNER_LINK);
            if (CollectionUtils.isNotEmpty(banners) && CollectionUtils.isNotEmpty(links)) {
                banners = banners.stream().map(banner -> {
                    var link = links.stream().filter(l -> l.getLanguageCode().equalsIgnoreCase(banner.getLanguageCode())).findFirst().orElse(null);
                    banner.setLinkUrl(link != null ? link.getValue() : null);
                    return banner;
                }).collect(Collectors.toList());
            }
            commElements.addAll(banners);
        } else if (CollectionUtils.isNotEmpty(channelBannerChannel)) {
            List<ChannelEventCommunicationElement> banners = buildChannelEventCommElement(channelBannerChannel, CommElementType.CHANNEL_BANNER);
            var links = buildChannelEventCommElement(channelBannerLink, CommElementType.CHANNEL_BANNER_LINK);
            if (CollectionUtils.isNotEmpty(banners) && CollectionUtils.isNotEmpty(links)) {
                banners = banners.stream().map(banner -> {
                    var link = links.stream().filter(l -> l.getLanguageCode().equalsIgnoreCase(banner.getLanguageCode())).findFirst().orElse(null);
                    banner.setLinkUrl(link != null ? link.getValue() : null);
                    return banner;
                }).collect(Collectors.toList());
            }
            commElements.addAll(banners);
        }

        if (CollectionUtils.isNotEmpty(eventChannelBannerHeader)) {
            List<ChannelEventCommunicationElement> banners = buildChannelEventCommElement(eventChannelBannerHeader, CommElementType.HEADER_BANNER);
            commElements.addAll(banners);
        } else if (CollectionUtils.isNotEmpty(channelBannerHeader)) {
            List<ChannelEventCommunicationElement> banners = buildChannelEventCommElement(channelBannerHeader, CommElementType.HEADER_BANNER);
            commElements.addAll(banners);
        }

        if (CollectionUtils.isNotEmpty(eventChannelBannerSquare)) {
            List<ChannelEventCommunicationElement> banners =
                    buildChannelEventCommElementSquare(eventChannelBannerSquare, CommElementType.IMG_SQUARE_BANNER_WEB);
            commElements.addAll(banners);
        }

        return commElements;
    }

    protected static List<ChannelEventCommunicationElement> buildChannelEventCommElement(List<EmailCommElementRecord> records, CommElementType type) {
        return records.stream()
                .map(r -> {
                    ChannelEventCommunicationElement result = new ChannelEventCommunicationElement();
                    result.setLanguageId(r.getIdIdioma());
                    result.setItemId(r.getIdItem());
                    result.setType(type);
                    result.setLanguageCode(r.getLanguageCode());
                    result.setValue(r.getValue());
                    if(type.isImage()){
                        result.setAltText(r.getAltText());
                    }
                    return result;
                })
                .collect(Collectors.toList());
    }

    protected static List<ChannelEventCommunicationElement> buildChannelEventCommElementSquare(
            List<CommElementRecord> records, CommElementType type) {
        return records.stream()
                .map(r -> {
                    ChannelEventCommunicationElement result = new ChannelEventCommunicationElement();
                    result.setLanguageId(r.getIdIdioma());
                    result.setItemId(r.getIdItem());
                    result.setType(type);
                    result.setLanguageCode(r.getLanguageCode());
                    result.setValue(r.getValue());
                    result.setPosition(r.getPosition());
                    if(type.isImage()){

                    }
                    return result;
                })
                .collect(Collectors.toList());
    }

}
