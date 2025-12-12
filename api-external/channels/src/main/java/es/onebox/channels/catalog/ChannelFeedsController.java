package es.onebox.channels.catalog;

import es.onebox.common.config.ApiConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({ApiConfig.ChannelsApiConfig.BASE_URL + "/catalog", ApiConfig.ChannelFeedsApiConfig.BASE_URL + "/catalog"})
public class ChannelFeedsController {

    private final ChannelFeedsService channelFeedsService;

    @Autowired
    public ChannelFeedsController(ChannelFeedsService channelFeedsService) {
        this.channelFeedsService = channelFeedsService;
    }

    @GetMapping
    public ChannelCatalog getCatalogFeeds(HttpServletRequest request) {
        return channelFeedsService.getChannelFeeds(request);
    }

}
