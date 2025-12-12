package es.onebox.internal.xmlsepa.sepa;

import es.onebox.internal.xmlsepa.format.SEPAFormatFilter;
import es.onebox.internal.xmlsepa.validator.SEPAValidatorBIC;
import es.onebox.internal.xmlsepa.validator.SEPAValidatorIBAN;
import es.onebox.internal.xmlsepa.validator.exception.SEPAValidatorIBANFormatException;

public class SEPABankAccount {
    private String creditorId;
    private String iban;
    private String bic;
    private String name;
    private String streetName;
    private String buildingNumber;
    private String postCode;
    private String townName;
    private String countryCode;

    public String getCreditorId() { return creditorId; }

    public String getIban() {
        return iban;
    }

    public String getBic() {
        return bic;
    }

    public String getName() {
        return name;
    }

    public String getStreetName() { return streetName; }

    public String getBuildingNumber() { return buildingNumber; }

    public String getPostCode() { return postCode; }

    public String getTownName() { return townName; }

    public String getCountryCode() { return countryCode; }

    public SEPABankAccount(String creditorId, String iban, String bic, String name) {
        this(creditorId, iban, bic, name, null, null, null, null, null);
    }

    public SEPABankAccount(String iban, String bic, String name, String streetName, String buildingNumber, String postCode, String townName, String countryCode) {
        this(null, iban, bic, name, streetName, buildingNumber, postCode, townName, countryCode);
    }

    public SEPABankAccount(String creditorId, String iban, String bic, String name, String streetName, String buildingNumber, String postCode, String townName, String countryCode) {
        if (SEPAValidatorIBAN.isValid(iban)) {
            this.iban = SEPAFormatFilter.filter(iban);
        } else {
            throw new SEPAValidatorIBANFormatException("Invalid IBAN: " + iban);
        }

        if (bic != null && SEPAValidatorBIC.isValid(bic)) {
            this.bic = SEPAFormatFilter.filterBIC(bic);
        }

        this.creditorId = creditorId;
        this.name = name;
        this.streetName = streetName;
        this.buildingNumber = buildingNumber;
        this.postCode = postCode;
        this.townName = townName;
        this.countryCode = countryCode;
    }
}
