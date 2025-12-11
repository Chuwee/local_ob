package es.onebox.mgmt.datasources.ms.entity.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;

public class DonationProviders extends HashSet<DonationProvider> implements Serializable {

    @Serial
    private static final long serialVersionUID = 887840195135946957L;
}
