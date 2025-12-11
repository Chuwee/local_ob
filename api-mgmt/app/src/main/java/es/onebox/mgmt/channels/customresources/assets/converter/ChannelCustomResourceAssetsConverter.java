package es.onebox.mgmt.channels.customresources.assets.converter;

import es.onebox.mgmt.channels.customresources.assets.dto.CreateCustomResourceAssetsDTO;
import es.onebox.mgmt.channels.customresources.assets.dto.CustomResourceAssetDTO;
import es.onebox.mgmt.channels.customresources.assets.dto.CustomResourceAssetsDTO;
import es.onebox.mgmt.channels.customresources.assets.dto.CustomResourceAssetsFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.assets.CreateCustomResourceAssetMsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.assets.CreateCustomResourceAssetsMsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.assets.CustomResourceAssetMsDTO;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.assets.CustomResourceAssetsFilterMs;
import es.onebox.mgmt.datasources.ms.channel.dto.customresources.assets.CustomResourceAssetsMsDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ChannelCustomResourceAssetsConverter {

    private ChannelCustomResourceAssetsConverter() {
    }

    public static CustomResourceAssetsFilterMs toMs(CustomResourceAssetsFilter customResourceAssetsFilter) {
        CustomResourceAssetsFilterMs customResourceAssetsFilterMs = new CustomResourceAssetsFilterMs();
        customResourceAssetsFilterMs.setQ(customResourceAssetsFilter.getQ());
        customResourceAssetsFilterMs.setOffset(customResourceAssetsFilter.getOffset());
        customResourceAssetsFilterMs.setLimit(customResourceAssetsFilter.getLimit());
        return customResourceAssetsFilterMs;
    }

    public static CustomResourceAssetsDTO fromMs(CustomResourceAssetsMsDTO msDTO) {
        CustomResourceAssetsDTO customResourceAssetsDTO = new CustomResourceAssetsDTO();
        customResourceAssetsDTO.setMetadata(msDTO.getMetadata());
        customResourceAssetsDTO.setData(fromMs(msDTO.getData()));
        return customResourceAssetsDTO;
    }

    private static List<CustomResourceAssetDTO> fromMs(List<CustomResourceAssetMsDTO> msDTOs) {
        return msDTOs.stream().map(msDTO -> new CustomResourceAssetDTO(msDTO.filename(), msDTO.url()))
                .collect(Collectors.toList());
    }

    public static CreateCustomResourceAssetsMsDTO toMs(CreateCustomResourceAssetsDTO body) {
        CreateCustomResourceAssetsMsDTO msDTO = new CreateCustomResourceAssetsMsDTO();
        body.forEach(requestDTO -> msDTO.add(new CreateCustomResourceAssetMsDTO(requestDTO.binary(), requestDTO.filename())));
        return msDTO;
    }
}
