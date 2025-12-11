package es.onebox.mgmt.packs.dto.channels;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;

import java.util.List;

public class PackChannelsDTO extends BaseResponseCollection<PackChannelDTO, Metadata> {

    private static final long serialVersionUID = -5262397422621445506L;

    public PackChannelsDTO() {
    }

    public PackChannelsDTO(List<PackChannelDTO> data, Metadata metadata) {
        super(data, metadata);
    }

}
