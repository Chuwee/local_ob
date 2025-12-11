package es.onebox.mgmt.vouchers.service;

import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentImageListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.datasources.common.dto.BaseCommunicationElement;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroup;
import es.onebox.mgmt.datasources.ms.channel.repositories.VouchersRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.vouchers.converter.VouchersGroupGiftCardContentsConverter;
import es.onebox.mgmt.vouchers.dto.VoucherGiftCardContentImageFilter;
import es.onebox.mgmt.vouchers.dto.VoucherGiftCardContentTextFilter;
import es.onebox.mgmt.vouchers.enums.VoucherGiftCardContentImageType;
import es.onebox.mgmt.vouchers.enums.VoucherGiftCardContentTextType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class VoucherGroupGiftCardContentsService {

    private MasterdataService masterdataService;
    private VouchersRepository vouchersRepository;
    private EntitiesRepository entitiesRepository;
    private VoucherGroupsService voucherGroupsService;

    @Autowired
    public VoucherGroupGiftCardContentsService(MasterdataService masterdataService, VouchersRepository vouchersRepository,
                                               EntitiesRepository entitiesRepository, VoucherGroupsService voucherGroupsService) {
        this.masterdataService = masterdataService;
        this.vouchersRepository = vouchersRepository;
        this.entitiesRepository = entitiesRepository;
        this.voucherGroupsService = voucherGroupsService;
    }

    public ChannelContentTextListDTO<VoucherGiftCardContentTextType> getChannelContentTexts(Long voucherGroupId, VoucherGiftCardContentTextFilter filter) {
        voucherGroupsService.getAndCheckVoucherGroup(voucherGroupId);

        CommunicationElementFilter comElementsFilter =
                VouchersGroupGiftCardContentsConverter.fromVoucherFilter(filter, masterdataService);

        List<BaseCommunicationElement> comElements =
                vouchersRepository.getVoucherCommunicationElements(voucherGroupId, comElementsFilter);

        comElements.sort(Comparator.comparing(BaseCommunicationElement::getLanguage).
                thenComparing(BaseCommunicationElement::getTag));

        return VouchersGroupGiftCardContentsConverter.fromMsChannelText(comElements);
    }

    public void updateChannelContentTexts(Long voucherGroupId, List<ChannelContentTextDTO<VoucherGiftCardContentTextType>> texts) {
        VoucherGroup voucherGroup = voucherGroupsService.getAndCheckVoucherGroup(voucherGroupId);

        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Entity entity = entitiesRepository.getCachedEntity(voucherGroup.getEntityId());
        for (ChannelContentTextDTO element : texts) {
            element.setLanguage(ChannelContentsUtils.checkElementLanguageForEntity(entity, languages, element.getLanguage()));
        }

        vouchersRepository.updateVoucherGroupCommunicationElements(voucherGroupId, VouchersGroupGiftCardContentsConverter.toMsChannelText(texts));
    }

    public ChannelContentImageListDTO<VoucherGiftCardContentImageType> getChannelContentImages(Long voucherGroupId, VoucherGiftCardContentImageFilter filter) {
        voucherGroupsService.getAndCheckVoucherGroup(voucherGroupId);

        CommunicationElementFilter comElementsFilter =
                VouchersGroupGiftCardContentsConverter.fromVoucherFilter(filter, masterdataService);

        List<BaseCommunicationElement> comElements =
                vouchersRepository.getVoucherCommunicationElements(voucherGroupId, comElementsFilter);

        comElements.sort(Comparator.comparing(BaseCommunicationElement::getLanguage));

        return VouchersGroupGiftCardContentsConverter.fromMsChannelImage(comElements);
    }

    public void updateChannelContentImages(Long voucherGroupId, List<ChannelContentImageDTO<VoucherGiftCardContentImageType>> images) {
        VoucherGroup voucherGroup = voucherGroupsService.getAndCheckVoucherGroup(voucherGroupId);

        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Entity entity = entitiesRepository.getCachedEntity(voucherGroup.getEntityId());
        for (ChannelContentImageDTO element : images) {
            element.setLanguage(ChannelContentsUtils.checkElementLanguageForEntity(entity, languages, element.getLanguage()));
        }

        vouchersRepository.updateVoucherGroupCommunicationElements(voucherGroupId, VouchersGroupGiftCardContentsConverter.toMsChannelImageList(images));
    }

    public void deleteChannelContentImages(Long voucherGroupId, String language, VoucherGiftCardContentImageType type) {
        voucherGroupsService.getAndCheckVoucherGroup(voucherGroupId);

        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        BaseCommunicationElement dto = VouchersGroupGiftCardContentsConverter.buildBaseCommunicationElementToDelete(language, type.getInternalName(), languages);

        vouchersRepository.updateVoucherGroupCommunicationElements(voucherGroupId, Collections.singletonList(dto));
    }
}
