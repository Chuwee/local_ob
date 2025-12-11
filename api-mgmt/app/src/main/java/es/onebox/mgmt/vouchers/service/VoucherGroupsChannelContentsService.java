package es.onebox.mgmt.vouchers.service;

import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentTextListDTO;
import es.onebox.mgmt.common.channelcontents.ChannelContentsUtils;
import es.onebox.mgmt.datasources.common.dto.BaseCommunicationElement;
import es.onebox.mgmt.datasources.common.dto.CommunicationElementFilter;
import es.onebox.mgmt.datasources.ms.channel.dto.voucher.VoucherGroup;
import es.onebox.mgmt.datasources.ms.channel.repositories.VouchersRepository;
import es.onebox.mgmt.datasources.ms.entity.dto.Entity;
import es.onebox.mgmt.datasources.ms.entity.repository.EntitiesRepository;
import es.onebox.mgmt.vouchers.converter.VouchersGroupChannelContentsConverter;
import es.onebox.mgmt.vouchers.dto.VoucherChannelContentTextFilter;
import es.onebox.mgmt.vouchers.enums.VoucherChannelContentTextType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class VoucherGroupsChannelContentsService {

    private MasterdataService masterdataService;
    private VouchersRepository vouchersRepository;
    private EntitiesRepository entitiesRepository;
    private VoucherGroupsService voucherGroupsService;

    @Autowired
    public VoucherGroupsChannelContentsService(MasterdataService masterdataService, VouchersRepository vouchersRepository,
                                               EntitiesRepository entitiesRepository, VoucherGroupsService voucherGroupsService) {
        this.masterdataService = masterdataService;
        this.vouchersRepository = vouchersRepository;
        this.entitiesRepository = entitiesRepository;
        this.voucherGroupsService = voucherGroupsService;
    }

    public ChannelContentTextListDTO<VoucherChannelContentTextType> getChannelContentTexts(Long voucherGroupId, VoucherChannelContentTextFilter filter) {
        voucherGroupsService.getAndCheckVoucherGroup(voucherGroupId);

        CommunicationElementFilter comElementsFilter =
                VouchersGroupChannelContentsConverter.fromVoucherFilter(filter, masterdataService);

        List<BaseCommunicationElement> comElements =
                vouchersRepository.getVoucherCommunicationElements(voucherGroupId, comElementsFilter);

        comElements.sort(Comparator.comparing(BaseCommunicationElement::getLanguage).
                thenComparing(BaseCommunicationElement::getTag));

        return VouchersGroupChannelContentsConverter.fromMsChannelText(comElements);
    }

    public void updateChannelContentTexts(Long voucherGroupId, List<ChannelContentTextDTO<VoucherChannelContentTextType>> texts) {
        VoucherGroup voucherGroup = voucherGroupsService.getAndCheckVoucherGroup(voucherGroupId);

        Map<String, Long> languages = masterdataService.getLanguagesByIdAndCode();
        Entity entity = entitiesRepository.getCachedEntity(voucherGroup.getEntityId());
        for (ChannelContentTextDTO element : texts) {
            element.setLanguage(ChannelContentsUtils.checkElementLanguageForEntity(entity, languages, element.getLanguage()));
        }

        vouchersRepository.updateVoucherGroupCommunicationElements(voucherGroupId, VouchersGroupChannelContentsConverter.toMsChannelText(texts));
    }
}
