package es.onebox.event.events.converter;

import es.onebox.core.serializer.dto.common.IdNameDTO;
import es.onebox.event.common.utils.ConverterUtils;
import es.onebox.event.events.dao.record.ChannelEventB2BAssignationRecord;
import es.onebox.event.events.dto.ChannelEventB2BQuotaAssignationDTO;
import es.onebox.event.events.dto.ChannelEventB2BQuotaAssignationsDTO;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ChannelEventB2BConverter {

    private ChannelEventB2BConverter() {}

    public static ChannelEventB2BQuotaAssignationsDTO toDTO(List<ChannelEventB2BAssignationRecord> source) {
        return source.stream()
                .collect(Collectors.groupingBy(
                        ChannelEventB2BConverter::toDTO,
                        Collectors.mapping(elem -> elem.getClientId() != null ?
                                elem.getClientId().longValue() : null, Collectors.toList())))
                .entrySet().stream()
                .map(elem -> {
                    elem.getKey().setClients(elem.getValue().stream().filter(Objects::nonNull).toList());
                    return elem.getKey();
                })
                .collect(Collectors.toCollection(ChannelEventB2BQuotaAssignationsDTO::new));
    }


    private static ChannelEventB2BQuotaAssignationDTO toDTO(ChannelEventB2BAssignationRecord source) {
        ChannelEventB2BQuotaAssignationDTO target = new ChannelEventB2BQuotaAssignationDTO();
        target.setId(source.getIdcanalcupob2b().longValue());
        target.setAllClients(ConverterUtils.isByteAsATrue(source.getAllclients()));
        target.setQuota(new IdNameDTO(source.getQuotaId().longValue(), source.getQuotaDescription()));
        return target;
    }
}
