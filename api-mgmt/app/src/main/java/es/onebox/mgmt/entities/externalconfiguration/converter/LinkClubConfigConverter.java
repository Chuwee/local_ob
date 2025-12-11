package es.onebox.mgmt.entities.externalconfiguration.converter;

import es.onebox.mgmt.entities.externalconfiguration.dto.LinkClubConfigDTO;

public class LinkClubConfigConverter {

    public static es.onebox.mgmt.datasources.ms.entity.dto.LinkClubConfigDTO toMs(LinkClubConfigDTO dto) {
        if (dto == null) {
            return null;
        }
        es.onebox.mgmt.datasources.ms.entity.dto.LinkClubConfigDTO msDto = new es.onebox.mgmt.datasources.ms.entity.dto.LinkClubConfigDTO();
        msDto.setClubCode(dto.getClubCode());
        return msDto;
    }
}
