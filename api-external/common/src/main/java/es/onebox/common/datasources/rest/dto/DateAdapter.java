package es.onebox.common.datasources.rest.dto;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: MMolinero
 * Date: 9/03/12
 * Time: 15:57
 */
public class DateAdapter
{

    public static Date parseDate(String s)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"){
            public Date parse(String source,ParsePosition pos) {
                return super.parse(source.replaceFirst(":(?=[0-9]{2}$)",""),pos);
            }
        };
        try {
            return df.parse(s);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String printDate(Date dt)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"){
            @Override
            public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos) {
                return new StringBuffer(super.format(date, toAppendTo, pos).toString().replaceFirst(":(?=[0-9]{2}$)",""));
            }
        };
        return df.format(dt);
    }

}
