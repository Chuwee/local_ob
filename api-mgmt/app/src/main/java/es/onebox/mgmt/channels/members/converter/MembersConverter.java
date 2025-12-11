package es.onebox.mgmt.channels.members.converter;

import es.onebox.mgmt.channels.dto.MemberRestrictionCreateRequestDTO;
import es.onebox.mgmt.channels.dto.MemberRestrictionDTO;
import es.onebox.mgmt.channels.dto.MemberRestrictionDetailDTO;
import es.onebox.mgmt.channels.dto.MemberRestrictionRequestDTO;
import es.onebox.mgmt.channels.dto.TranslationsDTO;
import es.onebox.mgmt.channels.dto.TranslationsRequestDTO;
import es.onebox.mgmt.channels.members.dto.AforoInfoDTO;
import es.onebox.mgmt.channels.members.dto.PeriodicityDTO;
import es.onebox.mgmt.channels.members.dto.RolPartnerInfoDTO;
import es.onebox.mgmt.channels.members.dto.TermDTO;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.AforosList;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.RolInfoList;
import es.onebox.mgmt.datasources.integration.dispatcher.dto.TermInfoList;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberConfigDTO;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberRestriction;
import es.onebox.mgmt.datasources.ms.entity.dto.MemberRestrictionFields;
import es.onebox.mgmt.datasources.ms.entity.dto.Periodicity;
import es.onebox.mgmt.datasources.ms.entity.dto.RoleTranslation;
import es.onebox.mgmt.datasources.ms.entity.dto.Translations;
import es.onebox.mgmt.members.MembersRestrictionTypesFields;
import es.onebox.mgmt.members.RestrictionTypes;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class MembersConverter {

    public static List<PeriodicityDTO> toPeriodicities(TermInfoList termInfos) {
        if (CollectionUtils.isEmpty(termInfos)) {
            return null;
        }
        List<PeriodicityDTO> result = termInfos.stream().map(termInfo -> new PeriodicityDTO(termInfo.getPeriodicityId(), termInfo.getPeriodicity())).collect(Collectors.toList());
        return result
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public static TranslationsRequestDTO toPeriodicitiesCommunication(MemberConfigDTO memberConfigDTO, Long periodicityId) {
        TranslationsRequestDTO translationsRequestDTO = new TranslationsRequestDTO();
        if(memberConfigDTO.getPeriodicityTranslations() != null && !memberConfigDTO.getPeriodicityTranslations().isEmpty()) {
            Optional<Periodicity> optPeriodicityCommunication = memberConfigDTO.getPeriodicityTranslations().stream().filter(pe -> pe.getId().equals(periodicityId)).findFirst();
            if(optPeriodicityCommunication.isPresent()) {
                Map<String, Translations> translations = optPeriodicityCommunication.get().getTranslations();
                for (Map.Entry<String, Translations> entry : translations.entrySet()) {
                    translationsRequestDTO.put(entry.getKey(), new TranslationsDTO(entry.getValue().getName(), entry.getValue().getDescription(), entry.getValue().getLink(), entry.getValue().getLinkText()));
                }
            }
        }
        return translationsRequestDTO;
    }

    public static List<TermDTO> toTerms(TermInfoList termInfos) {
        if (CollectionUtils.isEmpty(termInfos)) {
            return null;
        }
        List<TermDTO> result = termInfos.stream().map(termInfo -> new TermDTO(termInfo.getTermId(), termInfo.getTerm())).collect(Collectors.toList());
        return result
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<RolPartnerInfoDTO> toRoles(RolInfoList rolInfos) {
        if (CollectionUtils.isEmpty(rolInfos)) {
            return null;
        }
        List<RolPartnerInfoDTO> result = rolInfos.stream().map(rolInfo -> new RolPartnerInfoDTO(rolInfo.getIdTipo(), rolInfo.getNombre())).collect(Collectors.toList());
        return result
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public static List<AforoInfoDTO> toAforos(AforosList aforosList) {
        if (CollectionUtils.isEmpty(aforosList.getAforo())) {
            return null;
        }
        List<AforoInfoDTO> result = aforosList.getAforo().stream().map(aforo -> new AforoInfoDTO(aforo.getDescription(), aforo.getIdAforo())).collect(Collectors.toList());
        return result
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }

    public static TranslationsRequestDTO toRolesCommunication(MemberConfigDTO memberConfigDTO, Long roleId) {
        TranslationsRequestDTO translationsRequestDTO = new TranslationsRequestDTO();
        if(memberConfigDTO.getRoleTranslations() != null && !memberConfigDTO.getRoleTranslations().isEmpty()) {
            Optional<RoleTranslation> optRoleCommunication = memberConfigDTO.getRoleTranslations().stream().filter(pe -> pe.getId().equals(roleId)).findFirst();
            if(optRoleCommunication.isPresent()) {
                Map<String, Translations> translations = optRoleCommunication.get().getTranslations();
                for (Map.Entry<String, Translations> entry : translations.entrySet()) {
                    translationsRequestDTO.put(entry.getKey(), new TranslationsDTO(entry.getValue().getName(), entry.getValue().getDescription(), entry.getValue().getLink(), entry.getValue().getLinkText()));
                }
            }
        }
        return translationsRequestDTO;
    }

    public static List<MemberRestrictionDTO> toRestrictionsDTO(List<MemberRestriction> memberRestrictions) {
        return memberRestrictions.stream().map(MembersConverter::toRestrictionDTO).collect(Collectors.toList());
    }

    public static MemberRestrictionDetailDTO toRestrictionDetailDTO(MemberRestriction memberRestriction) {
        MemberRestrictionDetailDTO memberRestrictionDetailDTO = new MemberRestrictionDetailDTO();
        memberRestrictionDetailDTO.setSid(memberRestriction.getSid());
        memberRestrictionDetailDTO.setRestrictionName(memberRestriction.getName());
        memberRestrictionDetailDTO.setRestrictionType(memberRestriction.getRestrictionType());
        memberRestrictionDetailDTO.setTranslations(memberRestriction.getTranslations());
        memberRestrictionDetailDTO.setActivated(memberRestriction.getActivated());
        memberRestrictionDetailDTO.setMemberPeriods(memberRestriction.getMemberPeriods());
        memberRestrictionDetailDTO.setVenueTemplateSectors(memberRestriction.getVenueTemplateSectors());
        if(memberRestriction.getFields() != null && !memberRestriction.getFields().isEmpty()) {
            mapFieldsDTO(memberRestriction, memberRestrictionDetailDTO);
        }
        return memberRestrictionDetailDTO;
    }

    private static MemberRestrictionDetailDTO mapFieldsDTO(MemberRestriction memberRestriction, MemberRestrictionDetailDTO memberRestrictionDetailDTO) {
        RestrictionTypes restrictionType = RestrictionTypes.valueOf(memberRestrictionDetailDTO.getRestrictionType().name());
        Map<String, Object> restrictionDetailFields = new HashMap<>();
        for(MembersRestrictionTypesFields restrictionTypesField : restrictionType.getFields()) {
            String fieldName = restrictionTypesField.getFieldName();
            Map<String, Object> field = (Map<String, Object>) memberRestriction.getFields();
            if(field.containsKey(fieldName)) {
                Object fieldValue = field.get(fieldName);
                restrictionDetailFields.put(fieldName, fieldValue);
            }
        }
        memberRestrictionDetailDTO.setFields(restrictionDetailFields);
        return memberRestrictionDetailDTO;
    }

    public static MemberRestrictionDTO toRestrictionDTO(MemberRestriction memberRestriction) {
        MemberRestrictionDTO memberRestrictionDTO = new MemberRestrictionDTO();
        memberRestrictionDTO.setSid(memberRestriction.getSid());
        memberRestrictionDTO.setRestrictionName(memberRestriction.getName());
        memberRestrictionDTO.setRestrictionType(memberRestriction.getRestrictionType());
        memberRestrictionDTO.setActivated(memberRestriction.getActivated());
        return memberRestrictionDTO;
    }

    public static MemberRestriction toNewRestriction(MemberRestrictionCreateRequestDTO memberRestrictionCreateRequestDTO) {
        MemberRestriction memberRestriction = new MemberRestriction();
        memberRestriction.setSid(memberRestrictionCreateRequestDTO.getSid());
        memberRestriction.setName(memberRestrictionCreateRequestDTO.getRestrictionName());
        memberRestriction.setRestrictionType(memberRestrictionCreateRequestDTO.getRestrictionType());
        memberRestriction.setActivated(false);
        return memberRestriction;
    }

    public static MemberRestriction toRestriction(MemberRestrictionRequestDTO memberRestrictionRequestDTO, MemberRestriction memberRestriction) {
        if(memberRestrictionRequestDTO.getRestrictionName() != null) {
            memberRestriction.setName(memberRestrictionRequestDTO.getRestrictionName());
        }
        if(memberRestrictionRequestDTO.getTranslations() != null) {
            memberRestriction.setTranslations(memberRestrictionRequestDTO.getTranslations());
        }
        if(memberRestrictionRequestDTO.getActivated() != null) {
            memberRestriction.setActivated(memberRestrictionRequestDTO.getActivated());
        }
        if(memberRestrictionRequestDTO.getMemberPeriods() != null) {
            memberRestriction.setMemberPeriods(memberRestrictionRequestDTO.getMemberPeriods());
        }
        if(memberRestrictionRequestDTO.getVenueTemplateSectors() != null) {
            memberRestriction.setVenueTemplateSectors(memberRestrictionRequestDTO.getVenueTemplateSectors());
        }
        if(memberRestrictionRequestDTO.getFields() != null) {
            mapFields(memberRestrictionRequestDTO, memberRestriction);
        }
        return memberRestriction;
    }

    private static MemberRestriction mapFields(MemberRestrictionRequestDTO memberRestrictionRequestDTO, MemberRestriction memberRestriction) {
        RestrictionTypes restrictionType = RestrictionTypes.valueOf(memberRestriction.getRestrictionType().name());
        MemberRestrictionFields memberRestrictionFields = new MemberRestrictionFields();
        Map<String, Object> restrictionFields = new HashMap<>();
        for(MembersRestrictionTypesFields restrictionTypesField : restrictionType.getFields()) {

            String fieldName = restrictionTypesField.getFieldName();
            if(memberRestrictionRequestDTO.getFields().containsKey(fieldName)) {
                Object fieldValue = memberRestrictionRequestDTO.getFields().get(fieldName);
                restrictionFields.put(fieldName, fieldValue);
            }
        }
        memberRestrictionFields.putAll(restrictionFields);
        memberRestriction.setFields(memberRestrictionFields);
        return memberRestriction;
    }

}
