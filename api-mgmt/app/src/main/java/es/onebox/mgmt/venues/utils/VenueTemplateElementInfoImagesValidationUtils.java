package es.onebox.mgmt.venues.utils;

import es.onebox.mgmt.common.FileUtils;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementAggregatedInfoDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementDefaultInfoUpdateDTO;
import es.onebox.mgmt.venues.dto.elementsinfo.VenueTemplateElementImageDTO;
import es.onebox.mgmt.venues.enums.VenueTemplateElementImageTypeDTO;
import org.apache.commons.collections4.MapUtils;

import java.util.List;

public class VenueTemplateElementInfoImagesValidationUtils {

    public static void validateImages(VenueTemplateElementDefaultInfoUpdateDTO in) {
        if (in != null) {
            validateImages(in.getDefaultInfo());
        }
    }

    public static void validateImages(VenueTemplateElementAggregatedInfoDTO in) {
        if (in != null
                && in.getImageSettings() != null
                && MapUtils.isNotEmpty(in.getImageSettings())) {

            in.getImageSettings().forEach((imageType, imageSettings) -> {
                if (MapUtils.isNotEmpty(imageSettings.getImages())) {
                    imageSettings.getImages().values().forEach(imageList -> validateImages(imageList, imageType));
                }
            });
        }
    }

    private static void validateImages(List<VenueTemplateElementImageDTO> in, VenueTemplateElementImageTypeDTO type) {
        in.forEach(image -> validateImages(image, type));
    }

    private static void validateImages(VenueTemplateElementImageDTO in, VenueTemplateElementImageTypeDTO type) {
        FileUtils.checkImage(in.getImage(), type.getImageWidth(), type.getImageHeight(), type.getImageSize(), type.name());
        if (VenueTemplateElementImageTypeDTO.SLIDER.equals(type)) {
            FileUtils.checkImage(in.getThumbnail(), type.getThumbnailWidth(), type.getThumbnailHeight(),
                    type.getThumbnailSize(), type.name() + " thumbnail");
        }
    }

}


