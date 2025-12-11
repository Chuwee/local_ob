package es.onebox.mgmt.channels.members;

import es.onebox.core.exception.ExceptionBuilder;
import es.onebox.mgmt.exception.ApiMgmtErrorCode;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MembersSupport {

    public static void checkValueInt(int value, List<Integer> allowedValue, String fieldId) {
        if(!allowedValue.contains(Integer.valueOf(value))) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.CHANNEL_STRUCTURE_INVALID_VALUE, fieldId);
        }
    }

    public static void checkValueInts(String[] values, List<Integer> allowedValue, String fieldId) {
        for(String value : values) {
            if(!value.trim().isEmpty() && !allowedValue.contains(Integer.valueOf(value.trim()))) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.CHANNEL_STRUCTURE_INVALID_VALUE, fieldId);
            }
        }
    }

    public static void checkValueString(String value, List<Integer> allowedValue, String fieldId) {
        if(!value.trim().isEmpty() && !allowedValue.contains(Integer.valueOf(value.trim()))) {
            throw ExceptionBuilder.build(ApiMgmtErrorCode.CHANNEL_STRUCTURE_INVALID_VALUE, fieldId);
        }
    }

    public static void checkValueStrings(String[] values, List<Integer> allowedValue, String fieldId) {
        for(String value : values) {
            if(!allowedValue.contains(Integer.valueOf(value))) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.CHANNEL_STRUCTURE_INVALID_VALUE, fieldId);
            }
        }
    }

    public static void checkValueSource(Map<String, String> values, List<Integer> sourceAllowedValue, String fieldId) {
        for(String value : values.keySet()) {
            if(!sourceAllowedValue.contains(Integer.valueOf(value))) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.CHANNEL_STRUCTURE_INVALID_VALUE, fieldId);
            }
        }
    }

    public static void checkValueTarget(Map<String, String> values, List<Integer> targetAllowedValue, String fieldId) {
        for(String value : values.values()) {
            if(!targetAllowedValue.contains(Integer.valueOf(value))) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.CHANNEL_STRUCTURE_INVALID_VALUE, fieldId);
            }
        }
    }

    public static void chechValueMap(Map<String, String> values, List<Integer> sourceAllowedValue, List<Integer> targetAllowedValue, String fieldId) {
        for(String value : values.keySet()) {
            if(!sourceAllowedValue.contains(Integer.valueOf(value))) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.CHANNEL_STRUCTURE_INVALID_VALUE, fieldId);
            }
        }
        for(String value : values.values()) {
            if(!targetAllowedValue.contains(Integer.valueOf(value))) {
                throw ExceptionBuilder.build(ApiMgmtErrorCode.CHANNEL_STRUCTURE_INVALID_VALUE, fieldId);
            }
        }
    }

}
