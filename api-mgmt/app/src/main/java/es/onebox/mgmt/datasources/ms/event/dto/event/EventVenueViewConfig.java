package es.onebox.mgmt.datasources.ms.event.dto.event;

import es.onebox.mgmt.common.interactivevenue.dto.InteractiveVenueType;

import java.io.Serializable;

/**
 * eventConfig Couchbase fields related to 3D venue views
 */
public class EventVenueViewConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private String venue3dId;
    private InteractiveVenueType interactiveVenueType;
    private boolean use3dVenueModule;
    private boolean use3dVenueModuleV2;
    private boolean useVenue3dView;
    private boolean useSector3dView;
    private boolean useSeat3dView;

    public String getVenue3dId() {
        return venue3dId;
    }

    public void setVenue3dId(String venue3dId) {
        this.venue3dId = venue3dId;
    }

    public InteractiveVenueType getInteractiveVenueType() {
        return interactiveVenueType;
    }

    public void setInteractiveVenueType(InteractiveVenueType interactiveVenueType) {
        this.interactiveVenueType = interactiveVenueType;
    }

    public boolean isUse3dVenueModule() {
        return use3dVenueModule;
    }

    public void setUse3dVenueModule(boolean use3dVenueModule) {
        this.use3dVenueModule = use3dVenueModule;
    }

    public boolean isUse3dVenueModuleV2() {
        return use3dVenueModuleV2;
    }

    public boolean isUseVenue3dView() {
        return useVenue3dView;
    }

    public void setUseVenue3dView(boolean useVenue3dView) {
        this.useVenue3dView = useVenue3dView;
    }

    public void setUse3dVenueModuleV2(boolean use3dVenueModuleV2) {
        this.use3dVenueModuleV2 = use3dVenueModuleV2;
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
}
