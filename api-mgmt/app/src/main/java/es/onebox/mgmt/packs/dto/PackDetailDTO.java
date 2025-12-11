package es.onebox.mgmt.packs.dto;

import es.onebox.core.serializer.dto.common.IdNameDTO;

public class PackDetailDTO extends PackDTO {

    private PackSettingsDTO settings;
    private IdNameDTO tax;

    public PackSettingsDTO getSettings() {
        return settings;
    }

    public void setSettings(PackSettingsDTO settings) {
        this.settings = settings;
    }

    public IdNameDTO getTax() {
        return tax;
    }

    public void setTax(IdNameDTO tax) {
        this.tax = tax;
    }
}
