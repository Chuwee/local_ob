package es.onebox.mgmt.datasources.ms.event.dto.packs.channel;

import es.onebox.core.serializer.dto.response.BaseResponseCollection;
import es.onebox.core.serializer.dto.response.Metadata;

import java.util.List;

public class PackChannels extends BaseResponseCollection<PackChannel, Metadata> {

    private static final long serialVersionUID = -5262397422621445506L;

    public PackChannels() {
    }

    public PackChannels(List<PackChannel> data, Metadata metadata) {
        super(data, metadata);
    }

}
