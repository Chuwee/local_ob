package es.onebox.mgmt.vouchers.converter;

import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageFilter;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.datasources.common.dto.BaseCommunicationElement;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.vouchers.dto.VoucherGiftCardContentTextFilter;
import es.onebox.mgmt.vouchers.enums.VoucherGiftCardContentImageInternalType;
import es.onebox.mgmt.vouchers.enums.VoucherGiftCardContentImageType;
import es.onebox.mgmt.vouchers.enums.VoucherGiftCardContentTextInternalType;
import es.onebox.mgmt.vouchers.enums.VoucherGiftCardContentTextType;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static es.onebox.mgmt.common.ConverterUtils.toLocale;

public class VouchersGroupGiftCardContentsConverter {

    private VouchersGroupGiftCardContentsConverter(){}

    public static CommunicationElementFilter<VoucherGiftCardContentTextInternalType> fromVoucherFilter(VoucherGiftCardContentTextFilter original,
                                                                                              MasterdataService masterdataService) {
        if (original == null) {
            return null;
        }
        CommunicationElementFilter<VoucherGiftCardContentTextInternalType> commFilter = new CommunicationElementFilter<>();
        if (original.getLanguage() != null) {
            commFilter.setLanguageId(masterdataService.getLanguageByCode(toLocale(original.getLanguage())));
        }
        if (original.getType() != null) {
            commFilter.setTags(new HashSet<>(Collections.singletonList(VoucherGiftCardContentTextInternalType.valueOf(original.getType().getInternalName()))));
        } else {
            commFilter.setTags(Arrays.stream(VoucherGiftCardContentTextInternalType.values()).collect(Collectors.toSet()));
        }
        return commFilter;
    }

    public static ChannelContentTextListDTO<VoucherGiftCardContentTextType> fromMsChannelText(List<BaseCommunicationElement> elements) {
        return new ChannelContentTextListDTO<>(elements.stream()
                .map(e -> {
                    ChannelContentTextDTO<VoucherGiftCardContentTextType> dto = new ChannelContentTextDTO<>();
                    dto.setLanguage(ConverterUtils.toLanguageTag(e.getLanguage()));
                    dto.setType(VoucherGiftCardContentTextType.getByInternalName(e.getTag()));
                    dto.setValue(e.getValue());
                    return dto;
                })
                .collect(Collectors.toList()));
    }

    public static List<BaseCommunicationElement> toMsChannelText(List<ChannelContentTextDTO<VoucherGiftCardContentTextType>> elements) {
        return elements.stream()
                .map(element -> {
                    BaseCommunicationElement dto = new BaseCommunicationElement();
                    dto.setLanguage(element.getLanguage());
                    dto.setTag(element.getType().getInternalName());
                    dto.setValue(element.getValue());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public static CommunicationElementFilter<VoucherGiftCardContentImageInternalType> fromVoucherFilter(
            ChannelContentImageFilter<VoucherGiftCardContentImageType> original,
            MasterdataService masterdataService) {
        if (original == null) {
            return null;
        }
        CommunicationElementFilter<VoucherGiftCardContentImageInternalType> commFilter = new CommunicationElementFilter<>();
        if (original.getLanguage() != null) {
            commFilter.setLanguageId(masterdataService.getLanguageByCode(toLocale(original.getLanguage())));
        }
        if (original.getType() != null) {
            commFilter.setTags(Collections.singleton(VoucherGiftCardContentImageInternalType.valueOf(original.getType().getInternalName())));
        } else {
            commFilter.setTags(Arrays.stream(VoucherGiftCardContentImageInternalType.values()).collect(Collectors.toSet()));
        }
        return commFilter;
    }

    public static ChannelContentImageListDTO<VoucherGiftCardContentImageType> fromMsChannelImage(List<BaseCommunicationElement> elements) {
        return new ChannelContentImageListDTO<>(elements.stream()
                .map(element -> {
                    ChannelContentImageDTO<VoucherGiftCardContentImageType> dto = new ChannelContentImageDTO<>();
                    dto.setLanguage(ConverterUtils.toLanguageTag(element.getLanguage()));
                    dto.setType(VoucherGiftCardContentImageType.getByInternalName(element.getTag()));
                    dto.setImageUrl(element.getValue());
                    dto.setAltText(element.getAltText());
                    return dto;
                })
                .collect(Collectors.toList()));
    }

    public static List<BaseCommunicationElement> toMsChannelImageList(List<ChannelContentImageDTO<VoucherGiftCardContentImageType>> elements) {
        return elements.stream()
                .map(element -> {
                    BaseCommunicationElement dto = new BaseCommunicationElement();
                    dto.setLanguage(element.getLanguage());
                    dto.setTag(element.getType().getInternalName());
                    dto.setImageBinary(Optional.of(element.getImageBinary()));
                    dto.setAltText(element.getAltText());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public static BaseCommunicationElement buildBaseCommunicationElementToDelete(String language, String type,
                                                                                 Map<String, Long> languages) {
        String locale = ConverterUtils.checkLanguage(language, languages);
        BaseCommunicationElement dto = new BaseCommunicationElement();
        dto.setTag(type);
        dto.setLanguage(locale);
        dto.setImageBinary(Optional.empty());
        return dto;
    }
}
