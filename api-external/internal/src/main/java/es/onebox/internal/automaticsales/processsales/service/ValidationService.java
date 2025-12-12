package es.onebox.internal.automaticsales.processsales.service;

import es.onebox.internal.automaticsales.processsales.dto.SaleDTO;
import es.onebox.internal.automaticsales.processsales.enums.AttendantSaleField;
import es.onebox.common.datasources.ms.channel.dto.ChannelFormField;
import es.onebox.common.datasources.ms.channel.dto.ChannelFormsResponse;
import es.onebox.common.datasources.ms.channel.repository.ChannelRepository;
import es.onebox.common.datasources.ms.event.dto.AttendantField;
import es.onebox.common.datasources.ms.event.dto.AttendantsConfigDTO;
import es.onebox.common.datasources.ms.event.dto.AttendantsFields;
import es.onebox.common.datasources.ms.event.repository.MsEventRepository;
import es.onebox.common.exception.ApiExternalErrorCode;
import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.core.exception.OneboxRestException;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ValidationService {

    private final static String DEFAULT_FORM = "default";
    private final ChannelRepository channelRepository;
    private final MsEventRepository msEventRepository;

    public ValidationService(ChannelRepository channelRepository, MsEventRepository msEventRepository) {
        this.channelRepository = channelRepository;
        this.msEventRepository = msEventRepository;
    }

    public void validateEventFields(List<SaleDTO> sales, Long eventId, Long channelId) {
        AttendantsConfigDTO attendantsConfig = msEventRepository.getAttendantsConfig(eventId);
        if (attendantsConfig == null || !BooleanUtils.isTrue(attendantsConfig.getActive())) {
            return;
        }
        if (BooleanUtils.isTrue(attendantsConfig.getAllChannelsActive()) || attendantsConfig.getActiveChannels().contains(channelId)) {
            AttendantsFields attendantsFields = msEventRepository.getAttendantsFields(eventId);
            for (AttendantField field: attendantsFields.getData()) {
                AttendantSaleField eventField = AttendantSaleField.fromEventFieldName(field.getKey());
                if (eventField != null && BooleanUtils.isTrue(field.getMandatory()) && !isValidField(sales, eventField)) {
                    throw ExceptionBuilder.build(ApiExternalErrorCode.INPUT_EVENT_NULL_VALUES, field.getKey());
                }
            }
        }
    }

    public void validateChannelFields(List<SaleDTO> sales, Long channelId) {
        ChannelFormsResponse channelForms = channelRepository.getChannelFormByType(channelId, DEFAULT_FORM);
        for (ChannelFormField field: channelForms.getPurchase()) {
            AttendantSaleField channelField = AttendantSaleField.fromChannelFieldName(field.getKey());
            if (channelField != null && BooleanUtils.isTrue(field.getMandatory()) && !isValidField(sales, channelField)) {
                throw ExceptionBuilder.build(ApiExternalErrorCode.INPUT_CHANNEL_NULL_VALUES, field.getName());
            }
        }
    }

    private boolean isValidField(List<SaleDTO> sales, AttendantSaleField field) {
        for (SaleDTO sale: sales) {
            if (field.getExtractors().stream().anyMatch(extractor -> StringUtils.isBlank(extractor.apply(sale)))) {
                return false;
            }
        }
        return true;
    }
}
