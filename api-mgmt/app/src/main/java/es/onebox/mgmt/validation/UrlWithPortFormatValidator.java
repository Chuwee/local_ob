package es.onebox.mgmt.validation;

import es.onebox.mgmt.validation.annotation.UrlWithPortFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlWithPortFormatValidator implements ConstraintValidator<UrlWithPortFormat, String> {

    private static final String URL_PATTERN = "(https?://)?(www\\.)?[a-zA-Z\\d][a-zA-Z\\d-.]{1,61}[a-zA-Z\\d]\\.[a-zA-Z\\d-]{2,}(/\\S*)?";
    private static final String URL_PATTERN2 = "^(https?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) {
            return true;
        }
        Pattern p = Pattern.compile(URL_PATTERN);
        Matcher m = p.matcher(value);

        Pattern p2 = Pattern.compile(URL_PATTERN2);
        Matcher m2 = p2.matcher(value);

        return m.matches() || m2.matches();
    }
}
