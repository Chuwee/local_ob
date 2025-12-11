package es.onebox.mgmt.salerequests.enums;

import es.onebox.mgmt.datasources.ms.channel.salerequests.enums.MsValidityPeriodType;

import java.io.Serializable;

public enum ValidityPeriodType implements Serializable {
    ALL,
    PERIOD;

    private static final long serialVersionUID = 1L;

    ValidityPeriodType(){}

    public static MsValidityPeriodType toMsChannelEnum(ValidityPeriodType validityPeriodType){
        if(validityPeriodType == null){
            return null;
        }
        return valueOf(MsValidityPeriodType.class, validityPeriodType.name());
    }

    public static ValidityPeriodType fromMsChannelEnum(MsValidityPeriodType validityPeriodType){
        if(validityPeriodType == null){
            return null;
        }
        return valueOf(validityPeriodType.name());
    }
}