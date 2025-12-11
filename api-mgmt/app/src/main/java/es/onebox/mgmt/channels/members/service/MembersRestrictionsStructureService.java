package es.onebox.mgmt.channels.members.service;

import es.onebox.mgmt.channels.enums.RestrictionType;
import es.onebox.mgmt.channels.members.converter.MembersRestrictionConverter;
import es.onebox.mgmt.common.restrictions.dto.RestrictionsStructureDTO;
import es.onebox.mgmt.members.RestrictionTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MembersRestrictionsStructureService {

    @Autowired
    public MembersRestrictionsStructureService() {
    }

    public List<RestrictionsStructureDTO> getMemberConfigRestrictionStructure(RestrictionType restrictionType) {
        List<RestrictionsStructureDTO> filtered;
        if (restrictionType != null) {
            RestrictionTypes result = RestrictionTypes.valueOf(restrictionType.name());
            filtered = MembersRestrictionConverter.toStructure(result, null);
        } else {
            RestrictionTypes[] restrictionTypes = RestrictionTypes.values();
            filtered = MembersRestrictionConverter.toStructure(restrictionTypes, null);
        }
        if (restrictionType != null) {
            return filtered.stream().filter(fi -> fi.getRestrictionType() == null || (fi.getRestrictionType() != null && fi.getRestrictionType().equals(restrictionType))).collect(Collectors.toList());
        } else {
            return filtered;
        }
    }

}
