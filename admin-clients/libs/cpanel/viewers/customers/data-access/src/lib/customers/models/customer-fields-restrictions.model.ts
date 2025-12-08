/* eslint-disable @typescript-eslint/no-duplicate-enum-values */
export enum CustomerFieldsRestrictions {
    customerNameMaxLength = 30,
    customerSurnameMaxLength = 100,
    customerEmailMaxLength = 100,
    customerPhoneMaxLength = 30,
    customerPhonePattern = '^[+]{0,1}[s0-9]*$',
    customerIbanPattern = '^[A-Z]{2}[A-Z0-9]{1,30}',
    customerPostalCodeMaxLength = 20,
    customerCityMaxLength = 30,
    customerAddressMaxLength = 100
}
