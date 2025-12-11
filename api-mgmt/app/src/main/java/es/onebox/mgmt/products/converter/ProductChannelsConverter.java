package es.onebox.mgmt.products.converter;

import es.onebox.mgmt.channels.dto.ChannelLanguagesDTO;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannel;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductChannelResponse;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductChannels;
import es.onebox.mgmt.datasources.ms.event.dto.products.CreateProductChannelsResponse;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannelInfo;
import es.onebox.mgmt.datasources.ms.event.dto.products.ProductChannels;
import es.onebox.mgmt.datasources.ms.event.dto.products.UpdateProductChannel;
import es.onebox.mgmt.events.dto.channel.ChannelEntityDTO;
import es.onebox.mgmt.products.dto.ProductChannelDTO;
import es.onebox.mgmt.products.dto.CreateProductChannelResponseDTO;
import es.onebox.mgmt.products.dto.CreateProductChannelsDTO;
import es.onebox.mgmt.products.dto.CreateProductChannelsResponseDTO;
import es.onebox.mgmt.products.dto.ProductChannelInfoDTO;
import es.onebox.mgmt.products.dto.ProductChannelsDTO;
import es.onebox.mgmt.products.dto.UpdateProductChannelDTO;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Map;

public class ProductChannelsConverter {

    private ProductChannelsConverter() {
        throw new UnsupportedOperationException("Try to instantiate utilities class");
    }

    public static ProductChannelsDTO toDto(ProductChannels productChannels, Map<Long, ChannelLanguagesDTO> languagesByChannelId) {
        if (productChannels == null || productChannels.isEmpty()) {
            return new ProductChannelsDTO();
        }
        ProductChannelsDTO productChannelDTOS = new ProductChannelsDTO();
        for (ProductChannel productChannel : productChannels) {
            Long channelId = productChannel.getChannel().getId();
            productChannelDTOS.add(toDto(productChannel, languagesByChannelId.get(channelId)));
        }
        return productChannelDTOS;
    }

    public static ProductChannelDTO toDto(ProductChannel productChannel, ChannelLanguagesDTO languages) {
        if (productChannel == null) {
            return null;
        }

        ProductChannelDTO dto = new ProductChannelDTO();
        dto.setProduct(productChannel.getProduct());

        ProductChannelInfo channelEntity = productChannel.getChannel();
        ProductChannelInfoDTO channelDTO = null;

        if (channelEntity != null) {
            ChannelEntityDTO entityDto = null;

            if (channelEntity.getEntity() != null) {
                entityDto = new ChannelEntityDTO();
                entityDto.setId(channelEntity.getEntity().getId());
                entityDto.setName(channelEntity.getEntity().getName());
                entityDto.setLogo(channelEntity.getEntity().getLogo());
            }

            channelDTO = new ProductChannelInfoDTO(
                    channelEntity.getId(),
                    channelEntity.getName(),
                    entityDto,
                    channelEntity.getType()
            );
        }

        dto.setChannel(channelDTO);
        dto.setSaleRequestsStatus(productChannel.getSaleRequestsStatus());
        dto.setCheckoutSuggestionEnabled(productChannel.getCheckoutSuggestionEnabled());
        dto.setStandaloneEnabled(productChannel.getStandaloneEnabled());
        dto.setLanguages(languages);

        return dto;
    }

    public static CreateProductChannelsResponseDTO toDto(CreateProductChannelsResponse productChannels) {
        CreateProductChannelsResponseDTO productChannelDTOS = new CreateProductChannelsResponseDTO();
        if (CollectionUtils.isNotEmpty(productChannels)) {
            for (CreateProductChannelResponse productChannel : productChannels) {
                CreateProductChannelResponseDTO productChannelDTO = new CreateProductChannelResponseDTO();
                productChannelDTO.setProduct(productChannel.getProduct());
                productChannelDTO.setChannel(productChannel.getChannel());
                productChannelDTOS.add(productChannelDTO);
            }
        }
        return productChannelDTOS;
    }

    public static CreateProductChannels toMs(CreateProductChannelsDTO createProductChannelsDTO) {
        CreateProductChannels createProductChannels = new CreateProductChannels();
        if (createProductChannelsDTO != null) {
            createProductChannels.setChannelIds(createProductChannelsDTO.getChannelIds());
        }
        return createProductChannels;
    }

    public static UpdateProductChannel toMs(UpdateProductChannelDTO updateProductChannelDTO) {
        UpdateProductChannel updateProductChannel = new UpdateProductChannel();
        if (updateProductChannelDTO != null) {
            updateProductChannel.setStandaloneEnabled(updateProductChannelDTO.getStandaloneEnabled());
            updateProductChannel.setCheckoutSuggestionEnabled(updateProductChannelDTO.getCheckoutSuggestionEnabled());
        }
        return updateProductChannel;
    }
}
