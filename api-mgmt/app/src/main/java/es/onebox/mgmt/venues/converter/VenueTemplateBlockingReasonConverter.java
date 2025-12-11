package es.onebox.mgmt.venues.converter;

import es.onebox.mgmt.datasources.ms.venue.dto.template.BlockingReason;
import es.onebox.mgmt.datasources.ms.venue.dto.template.BlockingReasonCode;
import es.onebox.mgmt.datasources.ms.venue.dto.template.BlockingReasonRequest;
import es.onebox.mgmt.venues.dto.BlockingReasonDTO;
import es.onebox.mgmt.venues.dto.BlockingReasonRequestDTO;
import es.onebox.mgmt.venues.enums.BlockingReasonCodeDTO;

public class VenueTemplateBlockingReasonConverter {

    private VenueTemplateBlockingReasonConverter() {
    }

    public static BlockingReasonDTO fromMsEvent(BlockingReason blockingReason) {
        if (blockingReason == null) {
            return null;
        }

        BlockingReasonDTO blockingReasonDTO = new BlockingReasonDTO();
        blockingReasonDTO.setId(blockingReason.getId());
        blockingReasonDTO.setName(blockingReason.getName());
        blockingReasonDTO.setColor(blockingReason.getColor());
        blockingReasonDTO.setDefault(blockingReason.getDefault());
        if(blockingReason.getCode() != null) {
            blockingReasonDTO.setCode(BlockingReasonCodeDTO.valueOf(blockingReason.getCode().name()));
        }

        return blockingReasonDTO;
    }
    
    public static BlockingReasonRequest toMsEvent(BlockingReasonRequestDTO reasonRequest) {
        if(reasonRequest == null) {
            return null;
        }

        BlockingReasonRequest blockingReason = new BlockingReasonRequest();
        blockingReason.setName(reasonRequest.getName());
        blockingReason.setColor(reasonRequest.getColor());
        blockingReason.setDefault(reasonRequest.getDefault());
        if(reasonRequest.getCode() != null) {
            blockingReason.setCode(BlockingReasonCode.byName(reasonRequest.getCode().name()));
        }
        
        return blockingReason;
    }
}
