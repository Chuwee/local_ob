package es.onebox.internal.xmlsepa.validator.exception;

public class SEPAValidatorBICFormatException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private BicFormatViolation formatViolation;

    private Object expected;
    private Object actual;

    public SEPAValidatorBICFormatException() {
        super();
    }

    public SEPAValidatorBICFormatException(final String s) {
        super(s);
    }

    public SEPAValidatorBICFormatException(final String s, final Throwable t) {
        super(s, t);
    }

    public SEPAValidatorBICFormatException(BicFormatViolation violation,
                              Object actual, Object expected, final String s) {
        super(s);
        this.actual = actual;
        this.expected = expected;
        this.formatViolation = violation;
    }

    public SEPAValidatorBICFormatException(BicFormatViolation violation, final String s) {
        super(s);
        this.formatViolation = violation;
    }

    public SEPAValidatorBICFormatException(BicFormatViolation violation, Object actual, final String s) {
        super(s);
        this.actual = actual;
        this.formatViolation = violation;
    }

    public SEPAValidatorBICFormatException(final Throwable t) {
        super(t);
    }

    public BicFormatViolation getFormatViolation() {
        return formatViolation;
    }

    public Object getExpected() {
        return expected;
    }

    public Object getActual() {
        return actual;
    }

    public static enum BicFormatViolation {
        UNKNOWN,

        BIC_NOT_NULL,
        BIC_NOT_EMPTY,
        BIC_LENGTH_8_OR_11,
        BIC_ONLY_UPPER_CASE_LETTERS,

        BRANCH_CODE_ONLY_LETTERS_OR_DIGITS,
        LOCATION_CODE_ONLY_LETTERS_OR_DIGITS,
        BANK_CODE_ONLY_LETTERS,
        COUNTRY_CODE_ONLY_UPPER_CASE_LETTERS
    }
}