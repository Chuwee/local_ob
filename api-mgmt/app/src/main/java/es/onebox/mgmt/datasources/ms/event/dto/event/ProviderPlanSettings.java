package es.onebox.mgmt.datasources.ms.event.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class ProviderPlanSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    // General Sync Settings
    @JsonProperty("sync_sessions_as_hidden")
    private Boolean syncSessionsAsHidden;
    
    @JsonProperty("sync_surcharges")
    private Boolean syncSurcharges;
    
    @JsonProperty("sync_session_labels")
    private Boolean syncSessionLabels;
    
    @JsonProperty("sync_session_pics")
    private Boolean syncSessionPics;
    
    @JsonProperty("sync_session_type_ordering")
    private Boolean syncSessionTypeOrdering;
    
    @JsonProperty("sync_session_type_details")
    private Boolean syncSessionTypeDetails;

    // Main Plan
    @JsonProperty("sync_main_plan_title")
    private Boolean syncMainPlanTitle;
    
    @JsonProperty("sync_main_plan_description")
    private Boolean syncMainPlanDescription;
    
    @JsonProperty("sync_main_plan_images")
    private Boolean syncMainPlanImages;

    public Boolean getSyncSessionsAsHidden() {
        return syncSessionsAsHidden;
    }

    public void setSyncSessionsAsHidden(Boolean syncSessionsAsHidden) {
        this.syncSessionsAsHidden = syncSessionsAsHidden;
    }

    public Boolean getSyncSurcharges() {
        return syncSurcharges;
    }

    public void setSyncSurcharges(Boolean syncSurcharges) {
        this.syncSurcharges = syncSurcharges;
    }

    public Boolean getSyncSessionLabels() {
        return syncSessionLabels;
    }

    public void setSyncSessionLabels(Boolean syncSessionLabels) {
        this.syncSessionLabels = syncSessionLabels;
    }

    public Boolean getSyncSessionPics() {
        return syncSessionPics;
    }

    public void setSyncSessionPics(Boolean syncSessionPics) {
        this.syncSessionPics = syncSessionPics;
    }

    public Boolean getSyncSessionTypeOrdering() {
        return syncSessionTypeOrdering;
    }

    public void setSyncSessionTypeOrdering(Boolean syncSessionTypeOrdering) {
        this.syncSessionTypeOrdering = syncSessionTypeOrdering;
    }

    public Boolean getSyncSessionTypeDetails() {
        return syncSessionTypeDetails;
    }

    public void setSyncSessionTypeDetails(Boolean syncSessionTypeDetails) {
        this.syncSessionTypeDetails = syncSessionTypeDetails;
    }

    public Boolean getSyncMainPlanTitle() {
        return syncMainPlanTitle;
    }

    public void setSyncMainPlanTitle(Boolean syncMainPlanTitle) {
        this.syncMainPlanTitle = syncMainPlanTitle;
    }

    public Boolean getSyncMainPlanDescription() {
        return syncMainPlanDescription;
    }

    public void setSyncMainPlanDescription(Boolean syncMainPlanDescription) {
        this.syncMainPlanDescription = syncMainPlanDescription;
    }

    public Boolean getSyncMainPlanImages() {
        return syncMainPlanImages;
    }

    public void setSyncMainPlanImages(Boolean syncMainPlanImages) {
        this.syncMainPlanImages = syncMainPlanImages;
    }
}
