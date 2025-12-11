package es.onebox.mgmt.entities.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.RolInfoList;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.TermInfoList;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityExternalConverter {

    private EntityExternalConverter() {
    }

    public static List<IdNameDTO> toPeriodicities(TermInfoList termInfos) {
        if (CollectionUtils.isEmpty(termInfos)) {
            return new ArrayList<>();
        }
        return termInfos.stream()
                .map(termInfo -> new IdNameDTO(termInfo.getPeriodicityId(), termInfo.getPeriodicity()))
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<IdNameDTO> toTerms(TermInfoList termInfos) {
        if (CollectionUtils.isEmpty(termInfos)) {
            return new ArrayList<>();
        }
        return termInfos.stream()
                .map(termInfo -> new IdNameDTO(termInfo.getTermId(), termInfo.getTerm()))
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<IdNameDTO> toRoles(RolInfoList rolInfos) {
        if (CollectionUtils.isEmpty(rolInfos)) {
            return new ArrayList<>();
        }
        return rolInfos.stream()
                .map(rolInfo -> new IdNameDTO(rolInfo.getIdTipo().longValue(), rolInfo.getNombre()))
                .distinct()
                .collect(Collectors.toList());
    }

}
