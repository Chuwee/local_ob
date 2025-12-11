package es.onebox.event.events.avetrestrictions.converter;

import es.onebox.event.events.avetrestrictions.dto.AvetSectorRestrictionCreateDTO;
import es.onebox.event.events.avetrestrictions.dto.AvetSectorRestrictionDetailDTO;
import es.onebox.event.events.avetrestrictions.dto.AvetSectorRestrictionsDTO;
import es.onebox.event.events.avetrestrictions.dto.UpdateAvetSectorRestrictionDTO;
import es.onebox.event.products.dao.couch.AvetSectorRestriction;

import java.util.List;
import java.util.UUID;

public class AvetSectorRestrictionConverter {
    private AvetSectorRestrictionConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static AvetSectorRestrictionDetailDTO toAvetSectorRestrictionDTO(AvetSectorRestriction avetSectorRestriction,
                                                                            Boolean fullPayload) {
        AvetSectorRestrictionDetailDTO avetSectorRestrictionDetailDTO = new AvetSectorRestrictionDetailDTO();
        avetSectorRestrictionDetailDTO.setSid(avetSectorRestriction.getSid());
        avetSectorRestrictionDetailDTO.setType(avetSectorRestriction.getType());
        avetSectorRestrictionDetailDTO.setName(avetSectorRestriction.getName());
        avetSectorRestrictionDetailDTO.setActivated(avetSectorRestriction.getActivated());

        if (Boolean.TRUE.equals(fullPayload)) {
            avetSectorRestrictionDetailDTO.setFields(avetSectorRestriction.getFields());
            avetSectorRestrictionDetailDTO.setTranslations(avetSectorRestriction.getTranslations());
            avetSectorRestrictionDetailDTO.setVenueTemplateSectors(avetSectorRestriction.getVenueTemplateSectors());
        }

        return avetSectorRestrictionDetailDTO;
    }

    public static AvetSectorRestrictionsDTO toAvetSectorRestrictionsDTO(List<AvetSectorRestriction> avetSectorRestrictions,
                                                                        Boolean fullPayload) {
        AvetSectorRestrictionsDTO avetSectorRestrictionsDTO = new AvetSectorRestrictionsDTO();
        for (AvetSectorRestriction avetSectorRestriction : avetSectorRestrictions) {
            AvetSectorRestrictionDetailDTO avetSectorRestrictionDTO = toAvetSectorRestrictionDTO(avetSectorRestriction, fullPayload);
            avetSectorRestrictionsDTO.add(avetSectorRestrictionDTO);
        }

        return avetSectorRestrictionsDTO;
    }

    public static AvetSectorRestrictionDetailDTO toAvetSectorRestrictionDetailDTO(AvetSectorRestriction avetSectorRestriction) {
        if (avetSectorRestriction ==  null) {
            return null;
        }

        AvetSectorRestrictionDetailDTO avetSectorRestrictionDetailDTO = new AvetSectorRestrictionDetailDTO();

        avetSectorRestrictionDetailDTO.setSid(avetSectorRestriction.getSid());
        avetSectorRestrictionDetailDTO.setType(avetSectorRestriction.getType());
        avetSectorRestrictionDetailDTO.setName(avetSectorRestriction.getName());
        avetSectorRestrictionDetailDTO.setActivated(avetSectorRestriction.getActivated());
        avetSectorRestrictionDetailDTO.setVenueTemplateSectors(avetSectorRestriction.getVenueTemplateSectors());
        avetSectorRestrictionDetailDTO.setTranslations(avetSectorRestriction.getTranslations());
        avetSectorRestrictionDetailDTO.setFields(avetSectorRestriction.getFields());

        return avetSectorRestrictionDetailDTO;
    }

    public static AvetSectorRestriction toAvetSectorRestriction(AvetSectorRestrictionCreateDTO avetSectorRestrictionCreateDTO) {
        AvetSectorRestriction avetSectorRestriction = new AvetSectorRestriction();
        avetSectorRestriction.setSid(UUID.randomUUID().toString());
        avetSectorRestriction.setType(avetSectorRestrictionCreateDTO.getType());
        avetSectorRestriction.setName(avetSectorRestrictionCreateDTO.getName());
        avetSectorRestriction.setActivated(Boolean.FALSE);

        return avetSectorRestriction;
    }

    public static void updateFields(AvetSectorRestriction avetSectorRestriction, UpdateAvetSectorRestrictionDTO updateAvetSectorRestrictionDTO) {
        if (updateAvetSectorRestrictionDTO.getName() != null) {
            avetSectorRestriction.setName(updateAvetSectorRestrictionDTO.getName());
        }
        if (updateAvetSectorRestrictionDTO.getActivated() != null) {
            avetSectorRestriction.setActivated(updateAvetSectorRestrictionDTO.getActivated());
        }
        if (updateAvetSectorRestrictionDTO.getFields() != null) {
            avetSectorRestriction.setFields(updateAvetSectorRestrictionDTO.getFields());
        }
        if (updateAvetSectorRestrictionDTO.getVenueTemplateSectors() != null) {
            avetSectorRestriction.setVenueTemplateSectors(updateAvetSectorRestrictionDTO.getVenueTemplateSectors());
        }
        if (updateAvetSectorRestrictionDTO.getTranslations() != null) {
            avetSectorRestriction.setTranslations(updateAvetSectorRestrictionDTO.getTranslations());
        }
    }
}
