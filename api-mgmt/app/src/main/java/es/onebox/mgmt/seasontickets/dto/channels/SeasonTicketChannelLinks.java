package es.onebox.mgmt.seasontickets.dto.channels;

import es.onebox.mgmt.common.BaseLinkDTO;

import java.io.Serializable;
import java.util.List;

public class SeasonTicketChannelLinks implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean published;
    private Boolean enabled;
    private List<BaseLinkDTO> links;

    public Boolean getPublished() {
        return published;
    }

    public void setPublished(Boolean published) {
        this.published = published;
    }

    public List<BaseLinkDTO> getLinks() {
        return links;
    }

    public void setLinks(List<BaseLinkDTO> links) {
        this.links = links;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
