package es.onebox.mgmt.secondarymarket.dto;


public class SecondaryMarketConfigDTO extends SecondaryMarketBaseConfigDTO {

    private SecondaryMarketType type;

    public SecondaryMarketType getType() {
        return type;
    }

    public void setType(SecondaryMarketType type) {
        this.type = type;
    }
}



