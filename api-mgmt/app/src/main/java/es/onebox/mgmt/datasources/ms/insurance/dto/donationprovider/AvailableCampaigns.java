package es.onebox.mgmt.datasources.ms.insurance.dto.donationprovider;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;

public class AvailableCampaigns extends HashSet<Campaign> implements Serializable {
    @Serial
    private static final long serialVersionUID = -3273799326665260301L;

}
