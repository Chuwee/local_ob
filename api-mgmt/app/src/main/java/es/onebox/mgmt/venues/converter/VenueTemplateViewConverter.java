package es.onebox.mgmt.venues.converter;

import es.onebox.core.utils.common.CommonUtils;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateView;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateViewBulk;
import es.onebox.mgmt.datasources.ms.venue.dto.template.UpdateVenueTemplateVipView;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateView;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateViews;
import es.onebox.mgmt.datasources.ms.venue.dto.template.VenueTemplateViewsFilter;
import es.onebox.mgmt.venues.dto.CreateVenueTemplateViewDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateViewBulkDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateViewDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateViewsDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateVipViewDTO;
import es.onebox.mgmt.venues.dto.UpdateVenueTemplateVipViewsDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateViewDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateViewsDTO;
import es.onebox.mgmt.venues.dto.VenueTemplateViewsFilterDTO;

import java.util.List;
import java.util.stream.Collectors;

public class VenueTemplateViewConverter {

    private VenueTemplateViewConverter() {
        throw new UnsupportedOperationException("Cannot instantiate utilities class");
    }

    public static VenueTemplateViewsDTO fromMs(VenueTemplateViews source, String baseUrl, String prefix) {
        VenueTemplateViewsDTO target = new VenueTemplateViewsDTO();
        target.setData(source.getData().stream().map(view -> fromMs(view, baseUrl, prefix)).collect(Collectors.toList()));
        target.setMetadata(source.getMetadata());
        return target;
    }

    public static VenueTemplateViewDTO fromMs(VenueTemplateView venueTemplate, String baseUrl, String prefix) {
        if (venueTemplate == null) {
            return null;
        }

        VenueTemplateViewDTO viewDTO = new VenueTemplateViewDTO();
        viewDTO.setId(venueTemplate.getId());
        viewDTO.setName(venueTemplate.getName());
        viewDTO.setCode(venueTemplate.getDescription());
        viewDTO.setVip(venueTemplate.getVip());
        viewDTO.setAggregatedView(venueTemplate.getAggregatedView());
        viewDTO.setDisplay3D(venueTemplate.getDisplay3D());
        viewDTO.setOrientation(VenueTemplateViewOrientationConverter.fromMs(venueTemplate.getOrientation()));

        if (venueTemplate.getUrl() != null) {
            String[] filePath = venueTemplate.getUrl().split("/");
            filePath[filePath.length - 1] = prefix + filePath[filePath.length - 1];
            viewDTO.setUrl(baseUrl + String.join("/", filePath));
        }
        viewDTO.setRoot(CommonUtils.isTrue(venueTemplate.getRoot()));

        VenueTemplateViewLinkConverter.fromMs(venueTemplate, viewDTO);

        return viewDTO;
    }

    public static VenueTemplateViewsFilter toMs(VenueTemplateViewsFilterDTO filter) {
        VenueTemplateViewsFilter target = new VenueTemplateViewsFilter();
        target.setLimit(filter.getLimit());
        target.setOffset(filter.getOffset());
        target.setSessionId(filter.getSessionId());
        target.setQ(filter.getQ());
        target.setSort(filter.getSort());
        return target;
    }

    public static UpdateVenueTemplateView toMs(UpdateVenueTemplateViewDTO dto) {
        UpdateVenueTemplateView out = new UpdateVenueTemplateView();
        out.setName(dto.getName());
        out.setDescription(dto.getCode());
        out.setAggregatedView(dto.getAggregatedView());
        out.setDisplay3D(dto.getDisplay3D());
        out.setRoot(dto.getRoot());
        out.setVip(dto.getVip());
        out.setOrientation(VenueTemplateViewOrientationConverter.toMs(dto.getOrientation()));
        return out;
    }

    public static UpdateVenueTemplateView toMs(CreateVenueTemplateViewDTO dto) {
        UpdateVenueTemplateView out = new UpdateVenueTemplateView();
        out.setName(dto.getName());
        out.setDescription(dto.getCode());
        out.setAggregatedView(dto.getAggregatedView());
        out.setDisplay3D(dto.getDisplay3D());
        out.setRoot(dto.getRoot());
        out.setVip(dto.getVip());
        out.setOrientation(VenueTemplateViewOrientationConverter.toMs(dto.getOrientation()));
        return out;
    }

    public static List<UpdateVenueTemplateVipView> toMs(UpdateVenueTemplateVipViewsDTO body, Long sessionId) {
        return body.stream().map(elem -> map(elem, sessionId)).collect(Collectors.toList());
    }

    public static List<UpdateVenueTemplateViewBulk> toMs(UpdateVenueTemplateViewsDTO dto) {
        return dto.stream().map(VenueTemplateViewConverter::map).collect(Collectors.toList());
    }

    private static UpdateVenueTemplateVipView map(UpdateVenueTemplateVipViewDTO in, Long sessionId) {
        UpdateVenueTemplateVipView out = new UpdateVenueTemplateVipView();
        out.setSessionId(sessionId);
        out.setViewId(in.getViewId());
        out.setVip(in.getVip());
        return out;
    }

    private static UpdateVenueTemplateViewBulk map(UpdateVenueTemplateViewBulkDTO in) {
        UpdateVenueTemplateViewBulk out = new UpdateVenueTemplateViewBulk();
        out.setId(in.getId());
        out.setName(in.getName());
        out.setDescription(in.getCode());
        out.setAggregatedView(in.getAggregatedView());
        out.setDisplay3D(in.getDisplay3D());
        out.setVip(in.getVip());
        out.setOrientation(VenueTemplateViewOrientationConverter.toMs(in.getOrientation()));
        return out;
    }
}
