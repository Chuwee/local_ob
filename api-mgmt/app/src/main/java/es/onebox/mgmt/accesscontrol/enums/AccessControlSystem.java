package es.onebox.mgmt.accesscontrol.enums;

import java.util.Arrays;

public enum AccessControlSystem {
    skidataIFEMA("skidata-ifema"),
    skidataFLC("skidata-flc"),
    skidataMMO("skidata-mmo"),
    teamcardCHELSEA("teamcard-chelsea"),
    fortressBRISTOL("fortress-bristol");

    private String apiName;

    AccessControlSystem(String apiName) {
        this.apiName = apiName;
    }

    public String getApiName() {
        return apiName;
    }

    public static AccessControlSystem getFromApiName(String aName){
        return Arrays.stream(AccessControlSystem.values())
                .filter(v -> v.getApiName().equals(aName))
                .findAny().orElseThrow(IllegalArgumentException::new);
    }
}
