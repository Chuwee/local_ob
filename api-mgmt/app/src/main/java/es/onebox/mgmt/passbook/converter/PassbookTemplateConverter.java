package es.onebox.mgmt.passbook.converter;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.ticket.dto.AvailablePassbookField;
import es.onebox.mgmt.datasources.ms.ticket.dto.BasePassbookTemplate;
import es.onebox.mgmt.datasources.ms.ticket.dto.CreatePassbookTemplate;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookDesign;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookField;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookRequestFilter;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookTemplate;
import es.onebox.mgmt.datasources.ms.ticket.dto.PassbookTemplateList;
import es.onebox.mgmt.datasources.ms.ticket.dto.UpdatePassbookTemplate;
import es.onebox.mgmt.datasources.ms.ticket.enums.PassbookTemplateType;
import es.onebox.mgmt.events.dto.LanguagesDTO;
import es.onebox.mgmt.passbook.dto.AvailablePassbookFieldDTO;
import es.onebox.mgmt.passbook.dto.BasePassbookTemplateDTO;
import es.onebox.mgmt.passbook.dto.CreatePassbookTemplateDTO;
import es.onebox.mgmt.passbook.dto.PassbookDesignDTO;
import es.onebox.mgmt.passbook.dto.PassbookFieldDTO;
import es.onebox.mgmt.passbook.dto.PassbookRequestFilterDTO;
import es.onebox.mgmt.passbook.dto.PassbookTemplateDTO;
import es.onebox.mgmt.passbook.dto.PassbookTemplateListDTO;
import es.onebox.mgmt.passbook.dto.UpdatePassbookTemplateDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PassbookTemplateConverter {

    private PassbookTemplateConverter() {
        throw new UnsupportedOperationException("Try to instantiate utilities class");
    }

    public static PassbookRequestFilter convert(PassbookRequestFilterDTO dto) {
        PassbookRequestFilter filter = new PassbookRequestFilter();
        if (dto == null) {
            return filter;
        }
        filter.setLimit(dto.getLimit());
        filter.setOffset(dto.getOffset());
        filter.setEntityId(dto.getEntityId());
        filter.setSort(dto.getSort());
        filter.setQ(dto.getQ());
        filter.setOperatorId(dto.getOperatorId());
        filter.setCreateDate(dto.getCreateDate());
        filter.setDefaultTemplate(dto.getDefaultTemplate());
        filter.setEntityAdminId(dto.getEntityAdminId());
        if (dto.getType() != null) {
            filter.setType(PassbookTemplateType.valueOf(dto.getType().name()));
        }
        return filter;
    }


    public static PassbookTemplateListDTO convert(PassbookTemplateList source) {
        PassbookTemplateListDTO target = new PassbookTemplateListDTO();
        target.setMetadata(source.getMetadata());
        target.setData(Optional.ofNullable(source.getData()).orElse(new ArrayList<>()).stream()
                .map(PassbookTemplateConverter::convert).collect(Collectors.toList()));
        return target;
    }

    public static BasePassbookTemplateDTO convert(BasePassbookTemplate source) {
        if (source == null) {
            return new BasePassbookTemplateDTO();
        }
        BasePassbookTemplateDTO target = new BasePassbookTemplateDTO();
        setBaseFields(target, source);
        return target;
    }

    private static void setBaseFields(BasePassbookTemplateDTO target, BasePassbookTemplate source) {
        target.setCode(source.getCode());
        target.setEntityId(source.getEntityId());
        target.setOperatorId(source.getOperatorId());
        target.setName(source.getName());
        target.setUpdateDate(source.getUpdateDate());
        target.setCreateDate(source.getCreateDate());
        target.setDefaultPassbook(source.isDefaultPassbook());
        target.setObfuscateBarcode(source.isObfuscateBarcode());
        target.setDescription(source.getDescription());
        target.setPassbookDesign(convert(source.getPassbookDesign()));
        if (source.getType() != null) {
            target.setType(es.onebox.mgmt.passbook.dto.PassbookTemplateType.valueOf(source.getType().name()));
        }
    }

    private static PassbookDesignDTO convert(PassbookDesign passbookDesign) {
        if (passbookDesign == null) {
            return null;
        }
        switch (passbookDesign) {
            case STRIP:
                return PassbookDesignDTO.STRIP;
            case BACKGROUND_THUMB:
                return PassbookDesignDTO.BACKGROUND_THUMB;
            default:
                return null;
        }
    }

    public static PassbookTemplateDTO convert(PassbookTemplate source) {
        PassbookTemplateDTO target = new PassbookTemplateDTO();
        if (source == null) {
            return target;
        }
        setBaseFields(target, source);

        target.setPrimaryField(convert(source.getPrimaryField()));
        target.setAuxiliaryFields(convert(source.getAuxiliaryFields()));
        target.setBackFields(convert(source.getBackFields()));
        target.setHeader(convert(source.getHeader()));
        target.setSecondaryFields(convert(source.getSecondaryFields()));
        target.setLanguages(new LanguagesDTO());
        if (source.getDefaultLanguage() != null) {
            target.getLanguages().setDefaultLanguage(ConverterUtils.toLanguageTag(source.getDefaultLanguage()));
        }
        if (source.getLanguages() != null) {
            target.getLanguages().setSelected(source.getLanguages().stream()
                    .map(ConverterUtils::toLanguageTag).collect(Collectors.toList()));
        }
        return target;
    }

    private static List<PassbookFieldDTO> convert(List<PassbookField> fields) {
        if (fields == null || fields.isEmpty()) {
            return new ArrayList<>();
        }
        return fields.stream()
                .map(PassbookTemplateConverter::convert)
                .collect(Collectors.toList());
    }

    private static PassbookFieldDTO convert(PassbookField field) {
        if (field == null) {
            return new PassbookFieldDTO();
        }
        PassbookFieldDTO dto = new PassbookFieldDTO();
        dto.setLabel(field.getLabel());
        dto.setValue(field.getValue());
        dto.setKey(field.getKey());
        dto.setGroup(field.getGroup());
        return dto;
    }

    private static List<PassbookField> convertToEntity(List<PassbookFieldDTO> fields) {
        if (fields == null || fields.isEmpty()) {
            return new ArrayList<>();
        }
        return fields.stream()
                .map(PassbookTemplateConverter::convertToEntity)
                .collect(Collectors.toList());
    }

    private static PassbookField convertToEntity(PassbookFieldDTO field) {
        if (field == null) {
            return null;
        }
        PassbookField dto = new PassbookField();
        dto.setLabel(field.getLabel());
        dto.setGroup(field.getGroup());
        dto.setValue(field.getValue());
        dto.setKey(field.getKey());
        return dto;
    }

    public static CreatePassbookTemplate convert(CreatePassbookTemplateDTO dto) {
        if (dto == null) {
            return null;
        }
        CreatePassbookTemplate entity = new CreatePassbookTemplate();
        entity.setEntityId(dto.getEntityId());
        entity.setOperatorId(dto.getOperatorId());
        entity.setName(dto.getName());
        entity.setTemplateCodeToCopy(dto.getTemplateCodeToCopy());
        entity.setOriginEntityId(dto.getOriginEntityId());
        return entity;
    }

    public static UpdatePassbookTemplate convert(UpdatePassbookTemplateDTO source) {
        UpdatePassbookTemplate target = new UpdatePassbookTemplate();
        if (source == null) {
            return target;
        }
        target.setHeader(convertToEntity(source.getHeader()));
        target.setPrimaryField(convertToEntity(source.getPrimaryField()));
        target.setSecondaryFields(convertToEntity(source.getSecondaryFields()));
        target.setAuxiliaryFields(convertToEntity(source.getAuxiliaryFields()));
        target.setBackFields(convertToEntity(source.getBackFields()));
        target.setDefaultPassbook(source.isDefaultPassbook());
        target.setName(source.getName());
        target.setObfuscateBarcode(source.isObfuscateBarcode());
        target.setDescription(source.getDescription());
        if (source.getLanguages() != null) {
            target.setLanguages(source.getLanguages().getSelected());
            target.setDefaultLanguage(source.getLanguages().getDefaultLanguage());
        }
        target.setPassbookDesign(convert(source.getPassbookDesign()));
        return target;
    }

    public static PassbookDesign convert(PassbookDesignDTO source) {
        if (source == null) {
            return null;
        }
        switch (source) {
            case STRIP:
                return PassbookDesign.STRIP;
            case BACKGROUND_THUMB:
                return PassbookDesign.BACKGROUND_THUMB;
            default:
                return null;
        }
    }

    public static List<AvailablePassbookFieldDTO> convertAvailableFields(List<AvailablePassbookField> fields) {
        if (fields == null || fields.isEmpty()) {
            return new ArrayList<>();
        }
        return fields.stream()
                .map(PassbookTemplateConverter::convertAvailableFields)
                .collect(Collectors.toList());
    }

    public static PassbookTemplateType convertType(es.onebox.mgmt.passbook.dto.PassbookTemplateType type) {
        if (type == null) {
            return null;
        }
        return PassbookTemplateType.valueOf(type.name());
    }

    public static es.onebox.mgmt.passbook.dto.PassbookTemplateType convertType(PassbookTemplateType type) {
        if (type == null) {
            return null;
        }
        return es.onebox.mgmt.passbook.dto.PassbookTemplateType.valueOf(type.name());
    }


    private static AvailablePassbookFieldDTO convertAvailableFields(AvailablePassbookField field) {
        if (field == null) {
            return new AvailablePassbookFieldDTO();
        }
        AvailablePassbookFieldDTO dto = new AvailablePassbookFieldDTO();
        dto.setLabel(field.getLabel());
        dto.setValue(field.getValue());
        dto.setKey(field.getKey());
        dto.setGroup(field.getGroup());
        dto.setType(convertType(field.getType()));
        return dto;
    }
}
