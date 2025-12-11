package es.onebox.event.common.utils;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.function.Consumer;
import es.onebox.core.serializer.dto.request.ZonedDateTimeWithRelative;
import es.onebox.core.utils.common.CommonUtils;

public class ConverterUtils {

    private ConverterUtils() {
    }

    public static Timestamp zonedDateTimeRelativeToTimestamp(ZonedDateTimeWithRelative zonedDateTime, Timestamp recordDate) {
        if(zonedDateTime == null) {
            return null;
        }
        ZonedDateTime target = resolveZonedRelativeDateTimeValue(zonedDateTime, recordDate);
        if (target != null) {
            return CommonUtils.zonedDateTimeToTimestamp(target);
        }
        return recordDate;
    }

    public static ZonedDateTime resolveZonedRelativeDateTimeValue(ZonedDateTimeWithRelative zonedDateTime, Timestamp recordDate) {
        ZonedDateTime target = null;
        if(zonedDateTime != null) {
            if (zonedDateTime.isAbsolute()) {
                target = zonedDateTime.absolute();
            } else if (zonedDateTime.isRelative()) {
                if(recordDate == null) {
                    return null;
                }
                target = zonedDateTime.relative().calculate(CommonUtils.timestampToZonedDateTime(recordDate));
            }
        }
        return target;
    }


    public static <T> void updateField(Consumer<T> target, T sourceField) {
        if (sourceField != null) {
            target.accept(sourceField);
        }
    }

    public static Byte isTrueAsByte(Boolean value) {
        if (value == null) {
            return null;
        }
        return (byte) (value ? 1 : 0);
    }

    public static Integer longToInt(Long value) {
        if (value == null) {
            return null;
        }
        return value.intValue();
    }

    public static Byte intToByte(Integer value) {
        if (value == null) {
            return null;
        }
        return value.byteValue();
    }

    public static Integer byteToInteger(Byte value) {
        if (value == null) {
            return null;
        }
        return value.intValue();
    }

    public static Boolean isByteAsATrue(Byte byteProperty) {
        if (byteProperty == null) {
            return Boolean.FALSE;
        }
        return  byteProperty.intValue() == 1 ? Boolean.TRUE : Boolean.FALSE;
    }
}
