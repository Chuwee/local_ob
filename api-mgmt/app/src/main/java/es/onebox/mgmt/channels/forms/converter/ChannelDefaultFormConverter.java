package es.onebox.mgmt.channels.forms.converter;

import com.google.common.base.CaseFormat;
import es.onebox.core.exception.OneboxRestException;
import es.onebox.core.order.utils.common.BuyerDataUtils;
import es.onebox.mgmt.channels.forms.dto.ChannelDefaultFormDTO;
import es.onebox.mgmt.channels.forms.dto.ChannelFormFieldDTO;
import es.onebox.mgmt.channels.forms.dto.DynamicFormValidatorDTO;
import es.onebox.mgmt.channels.forms.dto.DynamicFormValidatorInfoDTO;
import es.onebox.mgmt.channels.forms.dto.UpdateChannelDefaultFormDTO;
import es.onebox.mgmt.channels.forms.dto.UpdateChannelFormFieldDTO;
import es.onebox.mgmt.channels.forms.enums.DynamicFormValidationRule;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelFormField;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.ChannelFormsResponse;
import es.onebox.mgmt.datasources.ms.channel.dto.contents.UpdateDefaultChannelForms;
import es.onebox.mgmt.exception.ApiMgmtChannelsErrorCode;
import org.apache.commons.collections.CollectionUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ChannelDefaultFormConverter {

    private ChannelDefaultFormConverter() {
    }

    public static ChannelFormFieldDTO toDto(ChannelFormField in) {
        ChannelFormFieldDTO out = new ChannelFormFieldDTO();
        out.setKey(in.getKey());
        out.setExternalKey(BuyerDataUtils.getFieldExternalKey(in.getKey()));
        out.setMandatory(in.getMandatory());
        out.setVisible(in.getVisible());
        out.setMutable(in.getMutable());
        out.setUneditable(in.getUneditable());
        out.setAvailableRules(Arrays.stream(DynamicFormValidationRule.values()).filter(rule -> rule.containsType(in.getType()))
                        .map(rule -> new DynamicFormValidatorInfoDTO(rule, rule.isValRequired(), rule.getValType()))
                .collect(Collectors.toList()));
        out.setAppliedRules(in.getRules());
        return out;
    }

    public static ChannelDefaultFormDTO toDto(ChannelFormsResponse in) {
        ChannelDefaultFormDTO out = new ChannelDefaultFormDTO();
        List<ChannelFormField> purchaseForm = in.getPurchase();
        if (CollectionUtils.isNotEmpty(purchaseForm)) {
            out.setPurchase(purchaseForm.stream().map(ChannelDefaultFormConverter::toDto).collect(Collectors.toList()));
        }
        List<ChannelFormField> bookingForm = in.getBooking();
        if (CollectionUtils.isNotEmpty(bookingForm)) {
            out.setBooking(bookingForm.stream().map(ChannelDefaultFormConverter::toDto).collect(Collectors.toList()));
        }
        List<ChannelFormField> issueForm = in.getIssue();
        if (CollectionUtils.isNotEmpty(issueForm)) {
            out.setIssue(issueForm.stream().map(ChannelDefaultFormConverter::toDto).collect(Collectors.toList()));
        }
        List<ChannelFormField> memberForm = in.getMember();
        if (CollectionUtils.isNotEmpty(memberForm)) {
            out.setMember(memberForm.stream().map(ChannelDefaultFormConverter::toDto).collect(Collectors.toList()));
        }
        List<ChannelFormField> newMemberForm = in.getNewMember();
        if (CollectionUtils.isNotEmpty(newMemberForm)) {
            out.setNewMember(newMemberForm.stream().map(ChannelDefaultFormConverter::toDto).collect(Collectors.toList()));
        }
        List<ChannelFormField> tutorForm = in.getTutor();
        if (CollectionUtils.isNotEmpty(tutorForm)) {
            out.setTutor(tutorForm.stream().map(ChannelDefaultFormConverter::toDto).collect(Collectors.toList()));
        }
        return out;
    }

    public static UpdateDefaultChannelForms toMS(UpdateChannelDefaultFormDTO in) {
        UpdateDefaultChannelForms out = new UpdateDefaultChannelForms();
        if (CollectionUtils.isNotEmpty(in.getPurchase())) {
            Set<ChannelFormField> fields = in.getPurchase().stream().map(ChannelDefaultFormConverter::toDto).collect(Collectors.toSet());
            out.setPurchase(fields);
        }
        if (CollectionUtils.isNotEmpty(in.getBooking())) {
            Set<ChannelFormField> fields = in.getBooking().stream().map(ChannelDefaultFormConverter::toDto).collect(Collectors.toSet());
            out.setBooking(fields);
        }
        if (CollectionUtils.isNotEmpty(in.getIssue())) {
            Set<ChannelFormField> fields = in.getIssue().stream().map(ChannelDefaultFormConverter::toDto).collect(Collectors.toSet());
            out.setIssue(fields);
        }
        if (CollectionUtils.isNotEmpty(in.getMember())) {
            Set<ChannelFormField> fields = in.getMember().stream().map(ChannelDefaultFormConverter::toDto).collect(Collectors.toSet());
            out.setMember(fields);
        }
        if (CollectionUtils.isNotEmpty(in.getNewMember())) {
            Set<ChannelFormField> fields = in.getNewMember().stream().map(ChannelDefaultFormConverter::toDto).collect(Collectors.toSet());
            out.setNewMember(fields);
        }
        if (CollectionUtils.isNotEmpty(in.getTutor())) {
            Set<ChannelFormField> fields = in.getTutor().stream().map(ChannelDefaultFormConverter::toDto).collect(Collectors.toSet());
            out.setTutor(fields);
        }
        return out;
    }

    private static ChannelFormField toDto(UpdateChannelFormFieldDTO in) {
        ChannelFormField out = new ChannelFormField();
        out.setKey(in.getKey());
        out.setMandatory(in.getMandatory());
        out.setVisible(in.getVisible());
        out.setUneditable(in.getUneditable());
        if(in.getRules() != null && !in.getRules().stream().map(DynamicFormValidatorDTO::getRule).allMatch(new HashSet<>()::add)){
            throw OneboxRestException.builder(ApiMgmtChannelsErrorCode.CHANNEL_VALIDATION_RULE_REPEATED).build();
        }
        out.setRules(in.getRules());
        return out;
    }

    public static String camelCaseToSnakeCase(String text) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, text);
    }
}
