package es.onebox.mgmt.datasources.ms.channel.repositories;

import es.onebox.mgmt.datasources.ms.channel.MsChannelDatasource;
import es.onebox.mgmt.datasources.ms.channel.dto.faqs.ChannelFAQ;
import es.onebox.mgmt.datasources.ms.channel.dto.faqs.ChannelFAQUpsertRequest;
import es.onebox.mgmt.datasources.ms.channel.dto.faqs.ChannelFAQs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChannelFAQsRepository {

    private final MsChannelDatasource msChannelDatasource;

    @Autowired
    public ChannelFAQsRepository(MsChannelDatasource msChannelDatasource) {
        this.msChannelDatasource = msChannelDatasource;
    }


    public ChannelFAQs getChannelFAQs(Long channelId, String languageCode, List<String> tags, String q) {
        return msChannelDatasource.getChannelFAQs(channelId, languageCode, tags, q);
    }

    public ChannelFAQ getChannelFAQsItem(Long channelId, String key) {
        return msChannelDatasource.getChannelFAQsItem(channelId, key);
    }

    public void addChannelFAQ(Long channelId, ChannelFAQUpsertRequest faq) {
        msChannelDatasource.addChannelFAQ(channelId, faq);
    }

    public void updateChannelFAQs(Long channelId, ChannelFAQUpsertRequest faqs, String key) {
        msChannelDatasource.updateChannelFAQs(channelId, faqs, key);
    }

    public void bulkUpdateChannelFAQs(Long channelId, ChannelFAQs faqs) {
        msChannelDatasource.bulkUpdateChannelFAQs(channelId, faqs);
    }

    public void deleteChannelFAQ(Long channelId, String key) {
        msChannelDatasource.deleteChannelFAQ(channelId, key);
    }
}
