package es.onebox.event.catalog.elasticsearch.dto.session;

import es.onebox.event.catalog.elasticsearch.enums.VenueProviderVersion;
import es.onebox.event.common.enums.InteractiveVenueType;
import es.onebox.event.sessions.dao.ExternalPlugin;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class VenueProviderConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 2598922463471670479L;

    private String venueProviderCode;
    private String venueProviderMinimapCode;
    private List<ExternalPlugin> plugins;
    private boolean useVenue3dView;
    private boolean useSector3dView;
    private boolean useSeat3dView;
    private InteractiveVenueType interactiveVenueType;
    private VenueProviderVersion venueProviderVersion;

    public VenueProviderConfig(){}

    public VenueProviderConfig(String venueProviderCode,
                               String venueProviderMinimapCode,
                               List<ExternalPlugin> plugins,
                               boolean useVenue3dView,
                               boolean useSector3dView,
                               boolean useSeat3dView,
                               InteractiveVenueType interactiveVenueType,
                               VenueProviderVersion venueProviderVersion) {
        this.venueProviderCode = venueProviderCode;
        this.venueProviderMinimapCode = venueProviderMinimapCode;
        this.plugins = plugins;
        this.useVenue3dView = useVenue3dView;
        this.useSector3dView = useSector3dView;
        this.useSeat3dView = useSeat3dView;
        this.interactiveVenueType = interactiveVenueType;
        this.venueProviderVersion = venueProviderVersion;
    }

    public String getVenueProviderCode() {
        return venueProviderCode;
    }

    public void setVenueProviderCode(String venueProviderCode) {
        this.venueProviderCode = venueProviderCode;
    }

    public String getVenueProviderMinimapCode() {
        return venueProviderMinimapCode;
    }

    public void setVenueProviderMinimapCode(String venueProviderMinimapCode) {
        this.venueProviderMinimapCode = venueProviderMinimapCode;
    }

    public List<ExternalPlugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<ExternalPlugin> pluginIds) {
        this.plugins = pluginIds;
    }

    public boolean isUseVenue3dView() {
        return useVenue3dView;
    }

    public void setUseVenue3dView(boolean useVenue3dView) {
        this.useVenue3dView = useVenue3dView;
    }

    public boolean isUseSector3dView() {
        return useSector3dView;
    }

    public void setUseSector3dView(boolean useSector3dView) {
        this.useSector3dView = useSector3dView;
    }

    public boolean isUseSeat3dView() {
        return useSeat3dView;
    }

    public void setUseSeat3dView(boolean useSeat3dView) {
        this.useSeat3dView = useSeat3dView;
    }

    public InteractiveVenueType getInteractiveVenueType() {
        return interactiveVenueType;
    }

    public void setInteractiveVenueType(InteractiveVenueType interactiveVenueType) {
        this.interactiveVenueType = interactiveVenueType;
    }

    public VenueProviderVersion getVenueProviderVersion() {
        return venueProviderVersion;
    }

    public void setVenueProviderVersion(VenueProviderVersion venueProviderVersion) {
        this.venueProviderVersion = venueProviderVersion;
    }
}
