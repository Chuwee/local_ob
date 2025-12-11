package es.onebox.mgmt.validation;

import es.onebox.mgmt.validation.annotation.UrlFormat;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlFormatValidator implements ConstraintValidator<UrlFormat, String> {

    private static final String URL_PATTERN = "(https?://)?(www\\.)?[a-zA-Z\\d][a-zA-Z\\d-.]{1,61}[a-zA-Z\\d]\\.[a-zA-Z\\d-]{2,}(/\\S*)?";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) {
            return true;
        }
        Pattern p = Pattern.compile(URL_PATTERN);
        Matcher m = p.matcher(value);
        return m.matches();
    }
}
