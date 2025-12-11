package es.onebox.mgmt.salerequests.contents;

import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.serializer.dto.common.IdDTO;
import es.onebox.mgmt.channels.ChannelsHelper;
import es.onebox.mgmt.channels.converter.ChannelConverter;
import es.onebox.mgmt.channels.utils.ChannelUtils;
import es.onebox.mgmt.common.MasterdataService;
import es.onebox.mgmt.datasources.ms.channel.dto.ChannelResponse;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.MsSaleRequestDTO;
import es.onebox.mgmt.datasources.ms.channel.salerequests.dto.SaleRequestAgreement;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsContentsRepository;
import es.onebox.mgmt.datasources.ms.channel.salerequests.repositories.SaleRequestsRepository;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import es.onebox.mgmt.salerequests.contents.converter.SaleRequestAgreementConverter;
import es.onebox.mgmt.salerequests.contents.dto.CreateSaleRequestAgreementDTO;
import es.onebox.mgmt.salerequests.contents.dto.SaleRequestAgreementDTO;
import es.onebox.mgmt.salerequests.contents.dto.UpdateSaleRequestAgreementDTO;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class SaleRequestAgreementsService {

    private final SaleRequestsContentsRepository saleRequestsContentsRepository;
    private final SaleRequestsRepository saleRequestsRepository;
    private final ChannelsHelper channelsHelper;
    private final MasterdataService masterdataService;

    public SaleRequestAgreementsService(SaleRequestsContentsRepository saleRequestsContentsRepository,
                                        SaleRequestsRepository saleRequestsRepository, ChannelsHelper channelsHelper, MasterdataService masterdataService) {
        this.saleRequestsContentsRepository = saleRequestsContentsRepository;
        this.saleRequestsRepository = saleRequestsRepository;
        this.channelsHelper = channelsHelper;
        this.masterdataService = masterdataService;
    }

    public List<SaleRequestAgreementDTO> getSaleRequestAgreements(Long saleRequestId) {
        validateAndGetChannel(saleRequestId);
        List<SaleRequestAgreement> response = saleRequestsContentsRepository.getSaleRequestAgreements(saleRequestId);
        return SaleRequestAgreementConverter.toResponse(response);
    }

    public IdDTO createSaleRequestAgreement(Long saleRequestId, CreateSaleRequestAgreementDTO body) {
        ChannelResponse channel = validateAndGetChannel(saleRequestId);
        validateLanguages(body.getTexts(), channel);
        SaleRequestAgreement out = SaleRequestAgreementConverter.toDTO(body);
        return saleRequestsContentsRepository.createSaleRequestAgreement(saleRequestId, out);
    }

    public void updateSaleRequestAgreement(Long saleRequestId, Long agreementId, UpdateSaleRequestAgreementDTO body) {
        ChannelResponse channel = validateAndGetChannel(saleRequestId);
        if (MapUtils.isNotEmpty(body.getTexts())) {
            validateLanguages(body.getTexts(), channel);
        }
        SaleRequestAgreement out = SaleRequestAgreementConverter.toDTO(body);
        saleRequestsContentsRepository.updateSaleRequestAgreement(saleRequestId, agreementId, out);
    }

    public void deleteSaleRequestAgreement(Long saleRequestId, Long agreementId) {
        validateAndGetChannel(saleRequestId);
        saleRequestsContentsRepository.deleteSaleRequestAgreement(saleRequestId, agreementId);
    }

    private ChannelResponse validateAndGetChannel(Long saleRequestId) {
        MsSaleRequestDTO response = saleRequestsRepository.getSaleRequestDetail(saleRequestId);
        ChannelResponse channelResponse = this.channelsHelper.getAndCheckChannel(response.getChannel().getId());
        ChannelUtils.validateOBChannel(channelResponse.getType());
        return channelResponse;
    }

    private void validateLanguages(Map<String, String> texts, ChannelResponse channel) {
        ChannelUtils.validateChannelLanguages(channel);
        Set<String> languageCodes = ChannelConverter.fromLanguageIdToLanguageCode(channel.getLanguages().getSelectedLanguages(),
                masterdataService.getLanguagesByIds());
        checkLanguages(texts.keySet(), languageCodes);
    }

    private static void checkLanguages(Set<String> languageKeys, Set<String> channelLanguages) {
        for (String languageKey : languageKeys) {
            if (channelLanguages.stream().noneMatch(l -> l.equals(languageKey))) {
                throw new OneboxRestException(ApiMgmtErrorCode.INVALID_LANG, "Invalid language " + languageKey, null);
            }
        }
    }
}
