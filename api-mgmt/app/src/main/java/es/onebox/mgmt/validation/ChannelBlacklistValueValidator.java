package es.onebox.mgmt.validation;

import es.onebox.mgmt.channels.blacklists.enums.ChannelBlacklistType;
import es.onebox.mgmt.validation.annotation.ChannelBlacklistValue;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChannelBlacklistValueValidator implements ConstraintValidator<ChannelBlacklistValue, String> {

    private static final String NIF_PATTERN = "(\\d{1,8})([TRWAGMYFPDXBNJZSQVHLCKE])";
    private static final String NIE_PATTERN = "^([XYZ][0-9]{7})([A-Z])$";
    private static final String PASSPORT_PATTERN = "^[A-Z0-9]{6,9}$";
    private static final String EMAIL_PATTERN = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";

    private static final String CONTROL_LETTERS_NIF = "TRWAGMYFPDXBNJZSQVHLCKE";

    private static final Pattern nifPattern = Pattern.compile(NIF_PATTERN);
    private static final Pattern niePattern = Pattern.compile(NIE_PATTERN);
    private static final Pattern passportPattern = Pattern.compile(PASSPORT_PATTERN);

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (StringUtils.isEmpty(value)) {
            return true;
        }

        HttpServletRequest request  = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String[] uriSplit = request.getRequestURI().split("/");
        ChannelBlacklistType type = ChannelBlacklistType.valueOf(uriSplit[uriSplit.length - 1]);
        boolean result = validate(type, value.trim().toUpperCase());
        if (BooleanUtils.isFalse(result)) {
            context.buildConstraintViolationWithTemplate(String.format("%s is not valid", value));
        }
        return result;
    }

    private static boolean validate(ChannelBlacklistType type, String value) {
        if (ChannelBlacklistType.NIF.equals(type)) {
            return validateNIF(value);
        }
        if (ChannelBlacklistType.EMAIL.equals(type)) {
            return validateEmail(value.trim());
        }

        return true;
    }

    private static boolean validateNIF(String idValue) {
        Matcher nifMatcher = nifPattern.matcher(idValue);
        Matcher nieMatcher = niePattern.matcher(idValue);
        Matcher passportMatcher = passportPattern.matcher(idValue);
        if (nifMatcher.matches()) {
            String letter = nifMatcher.group(2);
            int number = Integer.parseInt(nifMatcher.group(1)) % 23;
            return validateControlLetter(letter, number);
        } else if (nieMatcher.matches()) {
            String letter = nieMatcher.group(2);
            String result = nieMatcher.group(1)
                    .replaceFirst("^X", "0")
                    .replaceFirst("^Y", "1")
                    .replaceFirst("^Z", "2");
            int number = Integer.parseInt(result) % 23;
            return validateControlLetter(letter, number);
        } else return passportMatcher.matches();
    }

    private static boolean validateControlLetter(String letterControl, Integer number) {
        int index = number % 23;
        String reference = CONTROL_LETTERS_NIF.substring(index, index + 1);
        return reference.equalsIgnoreCase(letterControl);
    }

    private static boolean validateEmail(String email) {
        Pattern p = Pattern.compile(EMAIL_PATTERN);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}
