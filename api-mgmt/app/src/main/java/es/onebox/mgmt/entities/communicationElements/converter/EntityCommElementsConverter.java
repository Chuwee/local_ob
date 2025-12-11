package es.onebox.mgmt.entities.communicationElements.converter;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.datasources.ms.entity.dto.commElements.EntityCommElementsImage;
import es.onebox.mgmt.datasources.ms.entity.dto.commElements.EntityCommElementsImages;
import es.onebox.mgmt.datasources.ms.entity.dto.commElements.EntityCommElementsText;
import es.onebox.mgmt.datasources.ms.entity.dto.commElements.EntityCommElementsTexts;
import es.onebox.mgmt.entities.communicationElements.dto.EntityCommElementsImageDTO;
import es.onebox.mgmt.entities.communicationElements.dto.EntityCommElementsImageListDTO;
import es.onebox.mgmt.entities.communicationElements.dto.EntityCommElementsTextDTO;
import es.onebox.mgmt.entities.communicationElements.dto.EntityCommElementsTextListDTO;
import es.onebox.mgmt.entities.communicationElements.dto.UpdateEntityCommElementsImageDTO;
import es.onebox.mgmt.entities.enums.EntityImageContentResponseType;
import es.onebox.mgmt.entities.enums.EntityImageContentType;
import es.onebox.mgmt.entities.enums.EntityTextContentType;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class EntityCommElementsConverter {

    private EntityCommElementsConverter() {
        throw new UnsupportedOperationException("Try to instantiate utilities class");
    }

    public static EntityCommElementsTexts convertTexts(EntityCommElementsTextListDTO<EntityTextContentType> updateProductContentTextListDTO, Map<String, Long> languagesMap) {
        EntityCommElementsTexts entityCommElementsTexts = new EntityCommElementsTexts();
        for(EntityCommElementsTextDTO<EntityTextContentType> entityCommElementsTextDTO : updateProductContentTextListDTO) {
            String language = ConverterUtils.toLocale(entityCommElementsTextDTO.getLanguage());
            EntityCommElementsText commElementsText = new EntityCommElementsText();
            commElementsText.setType(entityCommElementsTextDTO.getType().name());
            commElementsText.setLanguage(language);
            commElementsText.setLanguageId(languagesMap.get(language));
            commElementsText.setValue(entityCommElementsTextDTO.getValue());
            entityCommElementsTexts.add(commElementsText);
        }
        return entityCommElementsTexts;
    }

    public static EntityCommElementsTextListDTO<EntityTextContentType> toDtoTexts(EntityCommElementsTexts entityCommElementsTexts) {
        if(Objects.isNull(entityCommElementsTexts)) {
            return null;
        }
        EntityCommElementsTextListDTO<EntityTextContentType> entityCommElementsTextDTOS = new EntityCommElementsTextListDTO<>();
        for(EntityCommElementsText entityCommElementsText : entityCommElementsTexts) {
            EntityCommElementsTextDTO<EntityTextContentType> entityCommElementsTextDTO = new EntityCommElementsTextDTO<>();
            entityCommElementsTextDTO.setType(EntityTextContentType.valueOf(entityCommElementsText.getType()));
            entityCommElementsTextDTO.setLanguage(ConverterUtils.toLanguageTag(entityCommElementsText.getLanguage()));
            entityCommElementsTextDTO.setValue(entityCommElementsText.getValue());
            entityCommElementsTextDTOS.add(entityCommElementsTextDTO);
        }
        return entityCommElementsTextDTOS;
    }

    public static EntityCommElementsImages convertImages(UpdateEntityCommElementsImageDTO entityCommElementsImageDTO, Map<String, Long> languagesMap) {
        EntityCommElementsImages entityCommElementsImages = new EntityCommElementsImages();
        for(EntityCommElementsImageDTO<EntityImageContentType> updateEntityCommElementsImage : entityCommElementsImageDTO) {
            EntityCommElementsImage entityCommElementsImage = new EntityCommElementsImage();
            entityCommElementsImage.setLanguage(ConverterUtils.toLocale(updateEntityCommElementsImage.getLanguage()));
            entityCommElementsImage.setLanguageId(languagesMap.get(updateEntityCommElementsImage.getLanguage()));
            entityCommElementsImage.setImageBinary(Optional.of(updateEntityCommElementsImage.getImageBinary()));
            entityCommElementsImage.setValue(updateEntityCommElementsImage.getImageUrl());
            entityCommElementsImage.setTagId(updateEntityCommElementsImage.getType().getTagId());
            entityCommElementsImage.setPosition(updateEntityCommElementsImage.getPosition());
            entityCommElementsImage.setType(updateEntityCommElementsImage.getType());
            entityCommElementsImage.setAltText(updateEntityCommElementsImage.getAltText());
            entityCommElementsImages.add(entityCommElementsImage);
        }
        return entityCommElementsImages;
    }

    public static EntityCommElementsImageListDTO<EntityImageContentType> toDtoImages(EntityCommElementsImages entityCommElementsImages) {
        if(Objects.isNull(entityCommElementsImages)) {
            return null;
        }
        EntityCommElementsImageListDTO<EntityImageContentType> entityCommElementsImageDTOS = new EntityCommElementsImageListDTO<>();
        for(EntityCommElementsImage entityCommElementsImage : entityCommElementsImages) {
            EntityCommElementsImageDTO<EntityImageContentType> entityCommElementsImageDTO = new EntityCommElementsImageDTO<>();
            entityCommElementsImageDTO.setPosition(entityCommElementsImage.getPosition());
            entityCommElementsImageDTO.setImageBinary(entityCommElementsImage.getImageBinary().isPresent() ? entityCommElementsImage.getImageBinary().get() : null);
            entityCommElementsImageDTO.setImageUrl(entityCommElementsImage.getValue());
            entityCommElementsImageDTO.setLanguage(ConverterUtils.toLanguageTag(entityCommElementsImage.getLanguage()));
            entityCommElementsImageDTO.setType(entityCommElementsImage.getType());
            entityCommElementsImageDTO.setAltText(entityCommElementsImage.getAltText());
            entityCommElementsImageDTOS.add(entityCommElementsImageDTO);
        }
        return entityCommElementsImageDTOS;
    }
}
