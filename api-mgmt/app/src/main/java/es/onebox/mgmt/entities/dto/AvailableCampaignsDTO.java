package es.onebox.mgmt.entities.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;

public class AvailableCampaignsDTO extends HashSet<CampaignDTO> implements Serializable {
    @Serial
    private static final long serialVersionUID = -7950807776825018825L;

}
