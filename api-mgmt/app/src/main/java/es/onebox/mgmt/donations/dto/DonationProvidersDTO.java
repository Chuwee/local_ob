package es.onebox.mgmt.donations.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;

public class DonationProvidersDTO extends HashSet<DonationProviderDTO> implements Serializable {
    @Serial
    private static final long serialVersionUID = 5804895312456360401L;
}
