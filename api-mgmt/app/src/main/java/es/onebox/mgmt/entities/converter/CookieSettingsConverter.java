package es.onebox.mgmt.entities.converter;

import es.onebox.mgmt.datasources.ms.entity.dto.CookieSettings;
import es.onebox.mgmt.datasources.ms.entity.dto.CookieSettingsBase;
import es.onebox.mgmt.entities.dto.CookieSettingsBaseDTO;
import es.onebox.mgmt.entities.dto.CookieSettingsDTO;
import es.onebox.mgmt.entities.dto.CookieSettingsUpdateDTO;
import es.onebox.mgmt.entities.enums.CookiesChannelEnablingMode;

import java.util.ArrayList;
import java.util.List;

public class CookieSettingsConverter {

    public static CookieSettingsDTO toDTO(CookieSettings source) {
        CookieSettingsDTO target = new CookieSettingsDTO();
        if(source != null) {
            target.setEnableCustomIntegration(source.getEnableCustomIntegration());
            target.setAcceptIntegrationConditions(source.getAcceptIntegrationConditions());
            target.setChannelEnablingMode(toDtoEnablingMode(source.getChannelEnablingMode()));
            target.setCustomIntegrationChannelIds(source.getCustomIntegrationChannelIds());
            target.setDate(source.getDate());
            target.setHistory(toHistoryDTO(source.getHistory()));
        }
        return target;
    }

    public static CookiesChannelEnablingMode toDtoEnablingMode(es.onebox.mgmt.datasources.ms.entity.enums.CookiesChannelEnablingMode source) {
        return source == null  ?  null
                               :  switch (source) {
                                    case ALL -> CookiesChannelEnablingMode.ALL;
                                    case RESTRICTED -> CookiesChannelEnablingMode.RESTRICTED;
                               };
    }

    public static List<CookieSettingsBaseDTO> toHistoryDTO(List<CookieSettingsBase> sourceHistory) {
        if (sourceHistory == null) {
            return null;
        } else {
            List<CookieSettingsBaseDTO> history = new ArrayList<>();
            sourceHistory.forEach(sourceSetting -> history.add(toBaseDTO(sourceSetting)));
            return history;
        }
    }

    public static CookieSettingsBaseDTO toBaseDTO(CookieSettingsBase sourceSetting) {
        CookieSettingsBaseDTO historyItem = new CookieSettingsBaseDTO();
        historyItem.setEnableCustomIntegration(sourceSetting.getEnableCustomIntegration());
        historyItem.setAcceptIntegrationConditions(sourceSetting.getAcceptIntegrationConditions());
        historyItem.setChannelEnablingMode(toDtoEnablingMode(sourceSetting.getChannelEnablingMode()));
        historyItem.setCustomIntegrationChannelIds(sourceSetting.getCustomIntegrationChannelIds());
        historyItem.setDate(sourceSetting.getDate());
        return historyItem;
    }


    public static CookieSettingsBase toMs(CookieSettingsUpdateDTO source) {
        CookieSettingsBase target = new CookieSettingsBase();
        target.setEnableCustomIntegration(source.getEnableCustomIntegration());
        target.setAcceptIntegrationConditions(source.getAcceptIntegrationConditions());
        target.setChannelEnablingMode(toMsEnablingMode(source.getChannelEnablingMode()));
        target.setCustomIntegrationChannelIds(source.getCustomIntegrationChannelIds());
        return target;
    }

    private static es.onebox.mgmt.datasources.ms.entity.enums.CookiesChannelEnablingMode toMsEnablingMode(CookiesChannelEnablingMode source) {
        return source == null ? null : switch (source) {
            case ALL -> es.onebox.mgmt.datasources.ms.entity.enums.CookiesChannelEnablingMode.ALL;
            case RESTRICTED -> es.onebox.mgmt.datasources.ms.entity.enums.CookiesChannelEnablingMode.RESTRICTED;
        };
    }
}
