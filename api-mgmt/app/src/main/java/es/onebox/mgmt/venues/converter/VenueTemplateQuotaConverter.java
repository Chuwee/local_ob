package es.onebox.mgmt.venues.converter;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.common.ConverterUtils;
import es.onebox.mgmt.common.LimitlessValueDTO;
import es.onebox.mgmt.datasources.common.dto.IdCapacity;
import es.onebox.mgmt.datasources.common.dto.QuotaCapacity;
import es.onebox.mgmt.datasources.ms.venue.dto.template.PriceType;
import es.onebox.mgmt.datasources.ms.venue.dto.template.Quota;
import es.onebox.mgmt.venues.dto.QuotaDTO;
import es.onebox.mgmt.venues.dto.capacity.IdCapacityDTO;
import es.onebox.mgmt.venues.dto.capacity.QuotaCapacityDTO;
import es.onebox.mgmt.venues.dto.capacity.QuotaCapacityListDTO;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class VenueTemplateQuotaConverter {

    private VenueTemplateQuotaConverter() {
    }

    public static QuotaDTO fromMsEvent(Quota quota) {
        if (quota == null) {
            return null;
        }

        QuotaDTO quotaDTO = new QuotaDTO();
        quotaDTO.setId(quota.getId());
        quotaDTO.setName(quota.getName());
        quotaDTO.setCode(quota.getCode());
        quotaDTO.setColor(quota.getColor());
        quotaDTO.setDefault(quota.getDefault());

        return quotaDTO;
    }

    public static List<QuotaCapacityDTO> from(List<QuotaCapacity> source, Map<Long, Quota> availableQuotas, Map<Long, PriceType> venueConfigPriceTypes) {
        return fromMsDTO(source).stream()
                .map(q -> {
                    if (MapUtils.isNotEmpty(availableQuotas) && availableQuotas.containsKey(q.getId())) {
                        var quota = availableQuotas.get(q.getId());
                        q.setName(quota.getName());
                        q.setCode(quota.getCode());
                        q.setDefaultQuota(quota.getDefault());
                    }
                    q.getPriceTypes().forEach(pt -> {
                        if(MapUtils.isNotEmpty(venueConfigPriceTypes) && venueConfigPriceTypes.containsKey(pt.getId())) {
                            var priceType = venueConfigPriceTypes.get(pt.getId());
                            pt.setName(priceType.getName());
                            pt.setCode(priceType.getCode());
                        }
                    });
                    return q;
                }).collect(Collectors.toList());
    }

    public static List<QuotaCapacityDTO> fromMsDTO(List<QuotaCapacity> source) {
        if (source == null || source.isEmpty()) {
            return new ArrayList<>();
        }
        List<QuotaCapacityDTO> target = new ArrayList<>();
        target.addAll(source.stream()
                .map(s -> {
                    QuotaCapacityDTO capacity = new QuotaCapacityDTO();
                    capacity.setId(s.getId());
                    capacity.setMaxCapacity(new LimitlessValueDTO(s.getMaxCapacity()));
                    capacity.setPriceTypes(s.getPriceTypes().stream().map(p -> {
                        IdCapacityDTO priceType = new IdCapacityDTO();
                        priceType.setId(p.getId());
                        if (p.getOnSale() != null) {
                            priceType.setOnSale(p.getOnSale());
                        }
                        priceType.setCapacity(new LimitlessValueDTO(p.getCapacity()));
                        return priceType;
                    }).collect(Collectors.toList()));
                    return capacity;
                })
                .collect(Collectors.toList()));
        return target;
    }

    public static List<QuotaCapacity> toMsDTO(QuotaCapacityListDTO requestListDTO) {
        List<QuotaCapacity> requestList = new ArrayList<>();
        for (QuotaCapacityDTO requestDTO : requestListDTO) {
            QuotaCapacity request = new QuotaCapacity();
            request.setId(requestDTO.getId());
            if (requestDTO.getMaxCapacity() != null) {
                request.setMaxCapacity(ConverterUtils.getLimitlessValue(requestDTO.getMaxCapacity()));
            }
            if (!CommonUtils.isEmpty(requestDTO.getPriceTypes())) {
                request.setPriceTypes(requestDTO.getPriceTypes().stream().
                        map(p -> {
                            IdCapacity capacity = new IdCapacity();
                            capacity.setId(p.getId());
                            if (p.getOnSale() != null) {
                                capacity.setOnSale(p.getOnSale());
                            }
                            if (p.getCapacity() != null) {
                                capacity.setCapacity(ConverterUtils.getLimitlessValue(p.getCapacity()));
                            }
                            return capacity;
                        }).collect(Collectors.toList()));
            }
            requestList.add(request);
        }
        return requestList;
    }
}
